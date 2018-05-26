import sys

from git.exc import GitCommandError, InvalidGitRepositoryError
from git.repo.base import Repo
from typing import List

from tools.user_interface import log_info, log_error, log_info_dry, prompt_yesno_question
from tools.config import Config
from fileinput import FileInput
import re
import os


class GitRepo:

    def __init__(self, config: Config) -> None:
        self.__config: Config = config

        try:
            self.__repo = Repo(config.root_path)
            assert not self.__repo.bare
            self.origin = self.__repo.remote('origin')
        except InvalidGitRepositoryError:
            log_error("Path is not a git repository. Please go to valid git repository!")
            sys.exit()

    def pull(self):
        try:
            log_info('Pull changes from origin ...')
            self.origin.pull()
        except GitCommandError:
            log_error(
                "Pull is not possible because you have unmerged files. Fix them up in the work tree, and then try again.")
            sys.exit()

    def reset(self):
        if(prompt_yesno_question('Should the repository and file system to be reset automatically before exiting?')):
            # arbitrary 20, but extensive enough to reset all hopefully
            log_info("Executing reset (git reset --hard HEAD~20)")
            self.__repo.git.reset('--hard HEAD~20')
            self.update_and_clean()

    def update_and_clean(self):
        log_info("Executing update and cleanup (git pull origin && git submodule update && git clean -fd)")
        self.origin.pull()
        self.__repo.submodule_update()
        self.__repo.git.clean("-fd")

    def checkout(self, branch_name):
        log_info("Checkout " + branch_name)
        self.__repo.git.checkout(branch_name)

    def commit(self, commit_message: str):
        try:
            log_info("Committing ...")
            self.__repo.git.commit(message="#" + str(self.__config.github_issue_no) + " " + commit_message)
        except Exception as e:
            if "no changes added to commit" in str(e):
                log_info("No File is changed, Nothing to commit..")

    def push(self):
        ''' Boolean return type states, whether to continue process or abort'''
        if(self.__config.debug):
            if not prompt_yesno_question("Changes will be pushed now. Continue?"):
                self.reset()
                sys.exit()
        if(self.__config.dry_run):
            log_info_dry('Skipping pushing of changes.')
            return

        try:
            log_info("Pushing ...")
            self.origin.push(tags=True)
        except Exception as e:
            if "no changes added to commit" in str(e):
                log_info("No file is changed, nothing to commit.")
            else:
                raise e

    def add(self, files: List[str]) -> None:
        self.__repo.git.add(files)

    def merge(self, source: str, target: str) -> None:
        if self.__config.dry_run:
            log_info_dry("Would merge from "+source+" to " + target)
            return
        if self.__config.debug and not prompt_yesno_question("Would now merge "+source+" to " + target + ". Continue?"):
            self.reset()
            sys.exit()

        try:
            log_info("Checkout "+source)
            self.__repo.git.checkout(source)
            log_info("Executing git pull before merging development branch to master...")
            self.pull()
            log_info("Merging...")
            self.__repo.git.execute("git merge " + self.__config.branch_to_be_released)
            self.commit("Merged "+source+" to " + target)
        except Exception as ex:
            log_error("Something went wrong, please check if merge conflicts exist and solve them.")
            if self.__config.debug:
                print(ex)
            if not prompt_yesno_question("If there were conflicts you solved and committed, would you like to resume the script?"):
                self.__repo.git.execute("git merge --abort")
                self.reset()
                sys.exit()

    def update_submodule(self, submodule_path: str) -> None:
        os.chdir(submodule_path)
        self.__repo.git.execute("git pull origin master")

        log_info("Changing the "+self.__config.wiki_version_overview_page + " file, updating the version number...")
        version_decl = self.__config.cobigenwiki_title_name
        new_version_decl = version_decl+" v"+self.__config.release_version
        with FileInput(self.__config.wiki_version_overview_page, inplace=True) as file:
            for line in file:
                line = re.sub(r''+version_decl+'.+', new_version_decl, line)
                sys.stdout.write(line)

        self.add([self.__config.wiki_version_overview_page])
        self.commit("update wiki docs")
        self.push()

    def get_changed_files_of_last_commit(self) -> List[str]:
        return str(self.__repo.git.execute("git diff HEAD^ HEAD --name-only")).strip().split("\n+")

    def create_tag_on_last_commit(self) -> None:
        self.__repo.create_tag(self.__config.tag_name)
