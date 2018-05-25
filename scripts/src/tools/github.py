import requests
from tools.config import Config
import sys
import os
from tools.user_interface import prompt_yesno_question, print_error, print_info_dry, print_info
import json
from uritemplate.template import URITemplate
from tools.git_repo import GitRepo

class GitHub:
    
    def __init__(self, config: Config, git_repo: GitRepo):
        self.config = config
        self.git_repo = git_repo
        self.session = requests.Session()
        self.session.auth = (self.config.git_username, self.config.git_password_or_token)
        
    def find_issue(self, issue_number):
        '''Search for the Release issue to be used, if not found, exit'''
        response_object= requests.get(self.config.github_issue_url(issue_number))
        issue_json_data = json.loads(response_object.text)
        if issue_json_data["message"] == "Not Found":
            print_error("Issue with number " + issue_number + " not found. Aborting...");
            return False
        else:
            print_info("Issue with number " + issue_number + " found.");
            return True
        
    def create_issue(self, version_decl, milestone=None, body=None, labels=None):
        '''Function creates an issue in git hub with version_decl,milestone,body,labels passed'''
        
        issue = {'version_decl': version_decl,
             'milestone': milestone,
             'body': body,             
             'labels': labels}
        
        if self.config.dry_run:
            print_info_dry('Skipping creation of issue:' + os.linesep + str(issue))
            return
        if self.config.debug and not prompt_yesno_question('Would now create GitHub issue as follows:' + os.linesep + str(issue) + os.linesep + "Continue?"):
            sys.exit()
            
        print_info('Create GitHub issue with version_decl "' + version_decl + '"...')
        
        response_object = self.session.post(self.config.github_issues_url(), json=issue)
        data = response_object.json()
        self.config.github_issue_no = data["number"]
        if response_object.status_code == 201:
            print_info('Successfully created issue #' + self.config.github_issue_no)
            return str(self.config.github_issue_no)
        else:
            print_error('Could not create new issue')
            sys.exit()
    
    def __request_milestone_list(self):
        response_object= requests.get(self.config.github_milestones_url())
        milestone_json_data = json.loads(response_object.text)
        if response_object.status_code == 200:
            return milestone_json_data
        else:
            print_error('Could not retrieve data from "'+self.config.github_milestones_url()+". Got "+response_object.status_code);
            sys.exit()
            
    
    def find_milestone(self) -> int:
        milestone_json_data = self.__request_milestone_list()
        
        # Fetching Versions from Milestones and keeping it in a list
        milestone_number = ""
        for i in range(len(milestone_json_data)):
            milestone_title_in_git = milestone_json_data[i]["version_decl"];
            if self.config.expected_milestone_name in milestone_title_in_git:
                milestone_number = milestone_json_data[i]["number"];
                break
             
        if milestone_number:
            print_info("Milestone with version_decl " + self.config.expected_milestone_name + " found")          
            return milestone_number 
        else:
            print_error("Milestone with version_decl " + self.config.expected_milestone_name + " not found! Aborting...")
            sys.exit()
    
    def find_cobigen_core_milestone(self, version:str, milestone_json_data=None):
        if not milestone_json_data:
            milestone_json_data = self.__request_milestone_list()
        
        for i in range(len(milestone_json_data)):
            if "cobigen-core/v"+version == milestone_json_data[i]["version_decl"]:
                return milestone_json_data[i]
        
        print_error("Could not find milestone for cobigen-core v"+version+". This must be an script error, please check.")
        sys.exit()
        
    
    def create_release(self, closed_milestone_number, core_version_in_eclipse_pom):
        if self.config.dry_run:
            print_info_dry("Would create a new GitHub release")
            return
        
        milestone_json_data = self.__request_milestone_list()
        
        url_milestone = self.config.github_closed_milestone_url(closed_milestone_number)
        release_title = self.config.cobigenwiki_title_name
        release_text = "[ChangeLog](" + url_milestone + ")"
        if "eclipse" in self.config.branch_to_be_released:
            cobigen_core_milestone = self.find_cobigen_core_milestone(core_version_in_eclipse_pom, milestone_json_data)
            if cobigen_core_milestone["state"] == "closed":
                core_url_milestone = self.config.github_closed_milestone_url(str(cobigen_core_milestone["number"]))
                release_text = release_text + "\n also includes \n"+ "[ChangeLog CobiGen Core](" + core_url_milestone + ")"
            else:
                print_info("Core version " + core_version_in_eclipse_pom + " is not yet released. This should be released before releasing cobigen-eclipse");
                sys.exit()
            
        try:
            response = self.git_repo.repo.create_git_release(self.config.tag_name, release_title, release_text, draft=False, prerelease=False, target_commitish="master");
            if response.status_code != 201:
                print_error("An error occurred during upload. Status Code "+response.status_code)
                sys.exit()
                
            upload_url=response.upload_url
            uri_template = URITemplate(upload_url)
            
            content_type="application/java-archive"
            if self.config.branch_to_be_released == "dev_eclipseplugin":
                content_type="application/zip"
            os.chdir(self.config.target_folder)
            
            for root, dirs, files in os.walk("."):
                dirs[:] = [d for d in dirs if d not in [".settings","src",]]
                for fname in files:
                    fpath = os.path.join(root, fname);
                    # To prevent uploading of unnecessary zip/jar files.
                    if ("jar" in fname or "zip" in fname) and self.config.release_version in fname:
                        print_info("Uploading file "+fname+"...")
                        asset_url = uri_template.expand(name=fname)
                        r = requests.post(asset_url, auth=(self.config.git_username,self.config.git_password_or_token) ,headers={'Content-Type':content_type}, files={'file': (fname, open(fpath, 'rb'), 'application/octet-stream')})
                        if r.status_code in [201,200]:
                            print_info("Uploaded!")
                        else:
                            print_error("Upload failed :/ Status Code: "+r.status_code)
        except Exception as e:
            print_error(str(e))
