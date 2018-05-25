import requests
import getpass
from tools.user_interface import prompt_enter_value, print_info, print_error, print_info_dry, prompt_yesno_question
from tools.config import Config
from git.exc import GitCommandError, InvalidGitRepositoryError
import sys
from github.MainClass import Github
import git


class GitRepo:

    def __init__(self, config: Config):
        self.config = config
        self.authenticate_git_user()
        self.init_git_repo()

        try:
            self.repo = git.cmd.Git(".")
        except InvalidGitRepositoryError:
            print_error("Path is not a git repository. Please go to valid git repository!")
            sys.exit()

    # This script is responsible for the authentication of git user
    def authenticate_git_user(self):    
        authenticated = False
        while not authenticated:
            self.config.git_username = prompt_enter_value("your git user name");
            self.config.git_password_or_token = getpass.getpass("Please enter your password or token: ")

            session = requests.Session()
            response_object = session.get(self.config.github_api_root_url)
            if (response_object.status_code in [201,200]):
                print_info("Authenticated.")
                authenticated = True
            else:
                print_info("Authentication failed.")
                authenticated = False
    
    def init_git_repo(self):
        g = Github(self.config.git_username, self.config.git_password_or_token)
        org = g.get_organization(self.config.git_repo_org)
        self.repo = org.get_repo(self.config.git_repo_name)
        self.origin = self.repo.remotes.origin
    
    def pull(self):
        try:
            print_info('Pull changes from origin ...');
            self.origin.pull()
        except GitCommandError:
            print_error("Pull is not possible because you have unmerged files. Fix them up in the work tree, and then try again.")
            sys.exit()
    
    def reset(self):
        if(prompt_yesno_question('Should the repository and file system to be reset automatically before exiting?')):
            # arbitrary 20, but extensive enough to reset all hopefully
            print_info("Executing reset (git reset --hard HEAD~20)") 
            self.repo.git.reset('--hard HEAD~20')
            self.clean()
    
    def update_and_clean(self):
        print_info("Executing update and cleanup (git pull origin && git submodule update && git clean -fd)")
        self.origin.pull()
        self.repo.execute("git submodule update")
        self.repo.execute("git clean -f -d")
    
    def checkout(self, branch_name):
        print_info("Checkout " + branch_name)
        self.repo.git.checkout(branch_name)
        
    def commit(self, commit_message: str):
        try:
            print_info("Committing ...")
            self.repo.git.commit(message="#" + str(self.config.github_issue_no) + " " + commit_message)
            self.push()
        except Exception as e:
            if "no changes added to commit" in str(e):
                print_info("No File is changed, Nothing to commit..")
    
    def push(self):
        if(self.config.debug):
            prompt_yesno_question("Changes will be pushed now. Continue?")
        if(self.config.dry_run):
            print_info_dry('Skipping pushing of changes.')
            return
        
        try:
            print_info("Pushing ...")
            self.origin.push(tags=True)
        except Exception as e:
            if "no changes added to commit" in str(e):
                print_info("No file is changed, nothing to commit.")
            else:
                raise e
        