import getpass
import os
import sys

from github.GitRelease import GitRelease
from github.GitReleaseAsset import GitReleaseAsset
from github.GithubException import UnknownObjectException, GithubException, BadCredentialsException
from github.Issue import Issue
from github.MainClass import Github
from github.Milestone import Milestone
from github.PaginatedList import PaginatedList
from github.Repository import Repository

from tools.config import Config
from tools.github_cache import GitHubCache
from tools.logger import log_error, log_info, log_info_dry, log_debug
from tools.user_interface import prompt_yesno_question, prompt_enter_value


class GitHub:

    def __init__(self, config: Config) -> None:
        self.__config: Config = config

        self.__authenticate_git_user()
        self.__initialize_repository_object()

    def __initialize_repository_object(self):
        self.__cache = GitHubCache()
        try:
            org = self.__github.get_organization(self.__config.git_repo_org)
            if self.__config.debug:
                log_debug("Organization found.")
        except UnknownObjectException:
            if self.__config.debug:
                log_debug("Organization not found. Try interpreting " + self.__config.git_repo_org + " as user...")
            org = self.__github.get_user(self.__config.git_repo_org)
            if self.__config.debug:
                log_debug("User found.")

        self.__repo: Repository = org.get_repo(self.__config.git_repo_name)

    # This script is responsible for the authentication of git user
    def __authenticate_git_user(self):
        while True:
            if not hasattr(self.__config, "two_factor_authentication"):
                self.__config.two_factor_authentication = prompt_yesno_question(
                    "Are you using two-factor authentication on GitHub?")
            if self.__config.two_factor_authentication:
                self.__config.git_token = getpass.getpass("> Please enter your token: ")
                while not self.__config.git_token:
                    self.__config.git_token = getpass.getpass("> Please enter your token: ")
            else:
                if not hasattr(self.__config, "git_username"):
                    self.__config.git_username = prompt_enter_value("your git user name")
                else:
                    log_info("The stored Github username is {}".format(self.__config.git_username))
                self.__config.git_password = getpass.getpass("> Please enter your password: ")
                while not self.__config.git_password:
                    self.__config.git_password = getpass.getpass("> Please enter your password: ")
            try:
                self.__login()
                log_info("Authenticated.")
                break
            except BadCredentialsException:
                log_info("Authentication error, please try again.")
                continue

    def __login(self):
        if hasattr(self.__config, "git_token") and self.__config.git_token:
            self.__github = Github(self.__config.git_token)
        else:
            self.__github = Github(self.__config.git_username, self.__config.git_password)

    def find_issue(self, issue_number: int) -> Issue:
        '''Search for the Release issue to be used, if not found, exit'''
        # caching!
        if issue_number in self.__cache.issues:
            log_info("Issue with number " + str(issue_number) + " found.")
            return self.__cache.issues[issue_number]
        else:
            log_debug("Issue not found in cache, retrieving from GitHub...")

        try:
            self.__cache.issues.update({issue_number: self.__repo.get_issue(issue_number)})
            log_info("Issue with number " + str(issue_number) + " found.")
            return self.__cache.issues[issue_number]
        except UnknownObjectException:
            return None

    def create_issue(self, title: str, milestone: Milestone, body: str) -> int:
        '''Function creates an issue in git hub with title,milestone,body,labels passed'''
        if self.__config.dry_run:
            log_info_dry('Skipping creation of issue with title ' + str(title))
            return 0
        if self.__config.debug and not prompt_yesno_question(
            '[DEBUG] Would now create GitHub issue with title="' + title + '", milestone=' + str(
                milestone) + '. Continue?'):
            sys.exit()

        log_info('Create GitHub issue with title "' + title + '"...')

        try:
            issue: Issue = self.__repo.create_issue(title=title, body=body, milestone=milestone,
                                                    labels=[self.__config.issue_label_name, "CI/CD"],
                                                    assignee=self.__github.get_user().login)
            self.__config.github_issue_no = issue.number
            self.__cache.issues.update({issue.number: issue})
            return self.__config.github_issue_no
        except GithubException as e:
            print(str(e))
            return 0

    def __request_milestone_list(self) -> PaginatedList:
        # caching!
        try:
            return self.__cache.milestones
        except AttributeError:
            log_debug("Milestones not found in cache, retrieving from GitHub...")

        try:
            milestones: PaginatedList = self.__repo.get_milestones(state="all")
            self.__cache.milestones = milestones
            return milestones
        except GithubException as e:
            log_error('Could not retrieve milestones')
            print(str(e))
            sys.exit()

    def find_release_milestone(self) -> Milestone:
        milestones: PaginatedList = self.__request_milestone_list()

        for milestone in milestones:
            milestone_title_in_git = milestone.title
            if self.__config.expected_milestone_name in milestone_title_in_git:
                return milestone
        return None

    def find_milestone(self, module: str, version: str) -> Milestone:
        milestones: PaginatedList = self.__request_milestone_list()

        search_title = self.__config.expected_raw_milestone_names.get(module) + version
        log_debug("Trying to search milestone: " + search_title)
        for milestone in milestones:
            if milestone.title == search_title:
                return milestone
        return None

    def create_next_release_milestone(self) -> Milestone:
        new_mile_title = self.__config.expected_milestone_name.replace(self.__config.release_version,
                                                                       self.__config.next_version)
        if self.__config.dry_run:
            log_info_dry("Would now create a new milestone with title '" + new_mile_title + "'.")
            return None

        log_info("Creating milestone '" + new_mile_title + "' for next release...")
        try:
            milestone: Milestone = self.__repo.create_milestone(title=new_mile_title, state="open")
            log_info("New milestone created!")
            return milestone
        except GithubException as e:
            log_info("Could not create milestone!")
            print(str(e))
            return None

    def create_release(self, closed_milestone: Milestone, core_version_in_eclipse_pom: str) -> GitRelease:
        if self.__config.dry_run:
            log_info_dry("Would create a new GitHub release")
            return None

        url_milestone = self.__config.github_closed_milestone_url(closed_milestone.number)
        release_title = self.__config.cobigenwiki_title_name + " v" + self.__config.release_version
        release_text = "[ChangeLog](" + url_milestone + ")"
        if "eclipse" in self.__config.branch_to_be_released and core_version_in_eclipse_pom:
            cobigen_core_milestone: Milestone = self.find_milestone("dev_core", core_version_in_eclipse_pom)
            if cobigen_core_milestone.state == "closed":
                core_url_milestone = self.__config.github_closed_milestone_url(cobigen_core_milestone.number)
                release_text = release_text + "\n also includes \n" + "[ChangeLog CobiGen Core](" + core_url_milestone + ")"
            else:
                log_info("Core version " + core_version_in_eclipse_pom +
                         " is not yet released. This should be released before releasing cobigen-eclipse")
                sys.exit()

        try:
            release: GitRelease = self.__repo.create_git_release(self.__config.tag_name, release_title,
                                                                 release_text, draft=False, prerelease=False,
                                                                 target_commitish="master")

            content_type = "application/java-archive"
            if self.__config.branch_to_be_released == self.__config.branch_eclipseplugin:
                content_type = "application/zip"

            for root, dirs, files in os.walk(
                os.path.join(self.__config.build_folder_abs, self.__config.build_artifacts_root_search_path)):
                dirs[:] = [d for d in dirs if
                           d not in [".settings", "src", "repository", "repository-upload", "classes", "apidocs"]]
                for fname in files:
                    fpath = os.path.join(root, fname)
                    # To prevent uploading of unnecessary zip/jar files.
                    if (fname.endswith("jar") or fname.endswith("zip")) and self.__config.release_version in fname and 'nexus-staging' in fpath:
                        log_info("Uploading file " + fname + " from " + fpath + " ...")
                        try:
                            asset: GitReleaseAsset = release.upload_asset(path=fpath, label=fname,
                                                                          content_type=content_type)
                            log_info("Uploaded " + str(asset.size) + "kb!")
                        except GithubException as e:
                            log_error("Upload failed!")
                            if self.__config.debug:
                                print(str(e))

            # workaround as of https://github.com/PyGithub/PyGithub/issues/779
            self.__login()
            self.__initialize_repository_object()

            return release
        except GithubException as e:
            log_error("Could not create release.")
            print(str(e))
            sys.exit()
