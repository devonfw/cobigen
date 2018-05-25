from tools.config import Config
import sys
import os
from tools.user_interface import prompt_yesno_question, print_error, print_info_dry, print_info, print_debug
from github.GithubException import UnknownObjectException, GithubException
from github.MainClass import Github
from github.Issue import Issue
from github.PaginatedList import PaginatedList
from github.Milestone import Milestone
from github.GitRelease import GitRelease

class GitHub:
    
    def __init__(self, config: Config):
        self.__config = config
        
        self.__github = Github(self.__config.git_username, self.__config.git_password_or_token)
        try:
            org = self.__github.get_organization(self.__config.git_repo_org)
            if self.__config.debug:
                print_debug("Organization found.")
        except UnknownObjectException:
            if self.__config.debug:
                print_debug("Organization not found. Try interpreting " + self.__config.git_repo_org + " as user...")
            org = self.__github.get_user(self.__config.git_repo_org)
            if self.__config.debug:
                print_debug("User found.")
            
        self.__repo = org.get_repo(self.__config.git_repo_name)
        
    def find_issue(self, issue_number:int) -> Issue:
        '''Search for the Release issue to be used, if not found, exit'''
        if self.cache.issues[issue_number]:
            return self.cache.issues[issue_number]
            
        try:
            self.cache.issues[issue_number]: Issue = self.__repo.get_issue(issue_number)
            print_info("Issue with number " + str(issue_number) + " found.");
            return self.cache.issues[issue_number]
        except UnknownObjectException:
            print_error("Issue with number " + str(issue_number) + " not found.");
            return None
        
    def exists_issue(self, issue_number:int) -> bool:
        '''Search for the Release issue to be used, if not found, exit'''
        if self.find_issue(issue_number):
            print_info("Issue with number " + issue_number + " found.");
            return True
        else:
            print_error("Issue with number " + issue_number + " not found.");
            return False
        
    def create_issue(self, title, milestone=None, body=None, labels=None) -> int:
        '''Function creates an issue in git hub with title,milestone,body,labels passed'''
        if self.__config.dry_run:
            print_info_dry('Skipping creation of issue with title ' + str(title))
            return
        if self.__config.debug and not prompt_yesno_question('Would now create GitHub issue with title="' + str(title) + '", milestone='+str(milestone)+'. Continue?'):
            sys.exit()
            
        print_info('Create GitHub issue with title "' + title + '"...')
        
        try:
            issue: Issue = self.__repo.create_issue(title=title, body=body, milestone=milestone, labels=labels)
            self.__config.github_issue_no = issue.number
            self.cache.issues[issue.number] = issue
            return self.__config.github_issue_no
        except GithubException as e:
            print(str(e))
            return None
    
    def __request_milestone_list(self) -> PaginatedList[Milestone]:
        # caching!
        if self.cache.milestones:
            return self.cache.milestones
        
        try:
            milestones: PaginatedList[Milestone] = self.__repo.get_milestones(state=all)
            self.cache.milestones = milestones
            return milestones
        except GithubException as e:
            print_error('Could not retrieve milestones');
            print(str(e))
            sys.exit()
            
    
    def find_release_milestone(self) -> Milestone:
        milestones: PaginatedList[Milestone] = self.__request_milestone_list()
        
        for milestone in milestones:
            milestone_title_in_git = milestone.title;
            if self.__config.expected_milestone_name in milestone_title_in_git:
                return milestone
        return None
    
    def find_cobigen_core_milestone(self, version:str) -> Milestone:
        milestones: PaginatedList[Milestone] = self.__request_milestone_list()
        
        for milestone in milestones:
            if "cobigen-core/v"+version == milestone.title:
                return milestone
        
        print_error("Could not find milestone for cobigen-core v"+version+". This must be an script error, please check.")
        sys.exit()
    
    def create_next_release_milestone(self) -> Milestone:
        if self.__config.dry_run:
            print_info_dry("Would create a new milestone")
            return None
        
        new_mile_title = self.__config.expected_milestone_name.replace(self.__config.release_version, self.__config.next_version) 
        try:
            milestone: Milestone = self.__repo.create_milestone(new_mile_title, "open")
            print_info("New milestone created!")
            return  milestone
        except GithubException as e:
            print_info("Could not create milestone!")
            print(str(e))
            return None
    
    def create_release(self, closed_milestone: Milestone, core_version_in_eclipse_pom) -> GitRelease:
        if self.__config.dry_run:
            print_info_dry("Would create a new GitHub release")
            return
        
        url_milestone = self.__config.github_closed_milestone_url(closed_milestone.number)
        release_title = self.__config.cobigenwiki_title_name
        release_text = "[ChangeLog](" + url_milestone + ")"
        if "eclipse" in self.__config.branch_to_be_released:
            cobigen_core_milestone: Milestone = self.find_cobigen_core_milestone(core_version_in_eclipse_pom)
            if cobigen_core_milestone.state == "closed":
                core_url_milestone = self.__config.github_closed_milestone_url(str(cobigen_core_milestone.number))
                release_text = release_text + "\n also includes \n"+ "[ChangeLog CobiGen Core](" + core_url_milestone + ")"
            else:
                print_info("Core version " + core_version_in_eclipse_pom + " is not yet released. This should be released before releasing cobigen-eclipse");
                sys.exit()
            
        try:
            release: GitRelease = self.__repo.create_git_release(self.__config.tag_name, release_title, release_text, draft=False, prerelease=False, target_commitish="master");
            
            content_type="application/java-archive"
            if self.__config.branch_to_be_released == "dev_eclipseplugin":
                content_type="application/zip"
            os.chdir(self.__config.target_folder)
            
            for root, dirs, files in os.walk("."):
                dirs[:] = [d for d in dirs if d not in [".settings","src",]]
                for fname in files:
                    fpath = os.path.join(root, fname);
                    # To prevent uploading of unnecessary zip/jar files.
                    if ("jar" in fname or "zip" in fname) and self.__config.release_version in fname:
                        print_info("Uploading file "+fname+"...")
                        try:
                            asset: GitReleaseAsset = release.upload_asset(fpath, content_type)
                            print_info("Uploaded "+asset.size+"kb!")
                        except GithubException as e:
                            print_error("Upload failed!")
                            if self.__config.debug:
                                print(str(e))
            return release
        except GithubException as e:
            print_error("Could not create release.")
            print(str(e))
            sys.exit()
