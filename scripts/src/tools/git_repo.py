import os
import re
import sys
from fileinput import FileInput
from typing import List

from git.exc import GitCommandError, InvalidGitRepositoryError
from git.repo.base import Repo

from tools.config import Config
from tools.logger import log_debug, log_info, log_error, log_info_dry
from tools.user_interface import prompt_yesno_question


class GitRepo:

    def __init__(self, config: Config, path: str = None) -> None:
        self.__config: Config = config
        try:
            if not path:
                self.__repo = Repo(config.root_path)
            else:
                self.__repo = Repo(path)
            assert not self.__repo.bare
            self.origin = self.__repo.remote('origin')
        except InvalidGitRepositoryError:
            log_error("Path is not a git repository. Please go to valid git repository!")
            sys.exit()

    def pull(self, branch_name: str = None):
        if not branch_name:
            branch = self.__repo.active_branch.name
        else:
            branch = branch_name
        try:
            log_info('Pull changes from origin ...')
            self.__repo.git.execute("git pull origin {}".format(branch).split(" "))
        except GitCommandError:
            log_error("Pull from origin/" + branch + " on " + self.__repo.working_tree_dir +
                      " is not possible as you might have uncommitted or untracked files. Fix the working tree, and then try again.")
            if not prompt_yesno_question("Did you fix the issue manually? Resume script?"):
                self.reset()
                sys.exit()

    def reset(self):
        if (self.__config.cleanup_silently or prompt_yesno_question(
                'Should the repository and file system to be reset automatically?\nThis will reset the entire repository inlcuding latest commits to comply to remote.\nThis will also delete untrackted files!')):
            # arbitrary 20, but extensive enough to reset all hopefully
            log_info("Executing reset (git reset --hard HEAD~20)")
            self.__repo.git.reset('--hard', 'HEAD~20')
            self.update_and_clean()

    def update_and_clean(self):
        log_info("Executing update and cleanup (git pull origin && git clean -fd)")
        self.pull()
        self.__repo.git.clean("-fd")
        if not self.is_working_copy_clean():
            log_error("Reset and cleanup did not work out. Other branches have local commits not yet pushed:")
            log_info("\n" + self.__list_unpushed_commits())
            if not prompt_yesno_question(
                    "Something went wrong during cleanup. Please check if you can perform the cleanup on your own. Resume the script?"):
                self.reset()
                sys.exit()

    def checkout(self, branch_name):
        log_info("Checkout " + branch_name)
        self.__repo.git.checkout(branch_name)
        self.update_and_clean()

    def commit(self, commit_message: str):
        try:
            if self.__list_uncommitted_files() != "":
                log_info("Committing ...")
                self.__repo.index.commit("#" + str(self.__config.github_issue_no) + " " + commit_message)
            else:
                log_info("Nothing to commit.")
        except Exception as e:
            if "no changes added to commit" in str(e):
                log_info("No File is changed, Nothing to commit..")

    def push(self, force: bool = False):
        ''' Boolean return type states, whether to continue process or abort'''
        if (not force and not self.has_unpushed_commits()):
            log_info("Nothing to be pushed.")
            return

        if (self.__config.test_run or self.__config.debug) and not prompt_yesno_question(
                "[DEBUG] Changes will be pushed now. Continue?"):
            self.reset()
            sys.exit()
        if self.__config.dry_run:
            log_info_dry('Skipping pushing of changes.')
            return

        try:
            log_info(
                "Pushing to origin/" + self.__repo.active_branch.name + " in " + self.__repo.working_tree_dir + "  ...")
            self.__repo.git.execute("git push origin " + self.__repo.active_branch.name + " --tags")
        except Exception as e:
            if "no changes added to commit" in str(e):
                log_info("No file is changed, nothing to commit.")
            else:
                if not prompt_yesno_question(
                        "Something went wrong during pushing. Please check if you can perform pushing on your own. Resume the script?"):
                    self.reset()

    def add(self, files: List[str], consider_as_build_folder_path: bool = True) -> None:
        files_to_add: List[str]
        if consider_as_build_folder_path:
            files_to_add = [os.path.join(self.__config.build_folder, i) for i in files]
        else:
            files_to_add = files

        self.__repo.index.add([i for i in files_to_add if self.__is_tracked_and_dirty(i)])

    def merge(self, source: str, target: str) -> None:
        if self.__config.dry_run:
            log_info_dry("Would merge from " + source + " to " + target)
            return

        try:
            self.checkout(target)
            log_info("Executing git pull...")
            self.pull()
            log_info("Merging...")
            self.__repo.git.execute("git merge " + self.__config.branch_to_be_released)
            log_info("Adapting automatically generated merge commit message to include issue no.")
            automatic_commit_message = self.__repo.git.execute("git log -1 --pretty=%B")
            if "Merge" in automatic_commit_message and str(
                    self.__config.github_issue_no) not in automatic_commit_message:
                self.__repo.git.execute('git commit --amend -m"#' + str(
                    self.__config.github_issue_no) + ' ' + automatic_commit_message + '"')
        except Exception as ex:
            log_error("Something went wrong, please check if merge conflicts exist and solve them.")
            if self.__config.debug:
                print(ex)
            if not prompt_yesno_question(
                    "If there were conflicts you solved and committed, would you like to resume the script?"):
                self.__repo.git.execute("git merge --abort")
                self.reset()
                sys.exit()

    def update_documentation(self) -> None:
        self.checkout('master')
        self.pull()

        log_info("Changing the " + self.__config.wiki_version_overview_page + " file, updating the version number...")
        version_decl = self.__config.cobigenwiki_title_name
        new_version_decl = version_decl + " v" + self.__config.release_version
        modified_file = os.path.join(self.__config.root_path, "documentation", self.__config.wiki_version_overview_page)
        with FileInput(modified_file,
                       inplace=True) as file:
            for line in file:
                line = re.sub(r'' + version_decl + r'\s+v[0-9]\.[0-9]\.[0-9]', new_version_decl, line)
                sys.stdout.write(line)

        self.add([modified_file], False)

    def exists_tag(self, tag_name) -> bool:
        return tag_name in self.__repo.tags

    def get_changed_files_of_last_commit(self) -> List[str]:
        return str(self.__repo.git.execute("git diff HEAD^ HEAD --name-only".split(" "))).strip().splitlines()

    def create_tag_on_last_commit(self) -> None:
        self.__repo.create_tag(self.__config.tag_name)
        log_info("Git tag " + self.__config.tag_name + " created!")

    def assure_clean_working_copy(self) -> None:
        if not self.is_working_copy_clean(True):
            log_error("Working copy is not clean!")
            if self.__config.cleanup_silently or prompt_yesno_question(
                    "Should I clean the repo for you? This will delete all untracked files and hardly reset the repository!"):
                self.reset()
            else:
                log_info("Please cleanup your working copy first. Then run the script again.")
                sys.exit()
        else:
            log_info("Working copy clean.")

    def is_working_copy_clean(self, check_all_branches=False) -> bool:
        return self.__repo.git.execute(
            "git diff --shortstat".split(" ")) == "" and not self.has_uncommitted_files() and not self.has_unpushed_commits()

    def __list_uncommitted_files(self) -> str:
        return self.__repo.git.execute("git status --porcelain".split(" "))

    def has_uncommitted_files(self) -> bool:
        return self.__list_uncommitted_files() != ""

    def __list_unpushed_commits(self) -> str:
        return self.__repo.git.execute("git log --branches --not --remotes".split(" "))

    def has_unpushed_commits(self) -> bool:
        return self.__list_unpushed_commits() != ""

    def __is_tracked_and_dirty(self, path: str) -> bool:
        changed = [item.a_path for item in self.__repo.index.diff(None)]
        changedAbs = [os.path.abspath(os.path.join(self.__repo.working_tree_dir, item.a_path)) for item in
                      self.__repo.index.diff(None)]
        log_debug("Untracked and Dirty files: " + str(changed))
        if path in changed or path in changedAbs:
            # modified
            return True
        else:
            return False
