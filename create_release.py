import urllib.request
from pathlib import Path  
import os 
import sys
from git import Repo
import requests
import json
from scripts.github_issue_creation import make_github_issue
from lxml import etree
from scripts.branchname_validation import check_branch_validity
from scripts.git_authentication import authenticate_git_user
from scripts.branchname_validation import get_build_folder
from scripts.settings import init

# get command line arguments and initialise global booleans
# --dry-run -> do not change anything in git but print the steps
# --debug -> step by step execution

bool_dry = False
bool_debug = False

for o in sys.argv:
    if o == "--dry-run":
        bool_dry = True
        print ('--dry-run: No changes will be made on the Git repo.')
    elif o == "--debug":
        print ('--debug: The script will require user interactions.')
        bool_debug = True
    elif o == "--help":
        print ('This script helps deploying CobiGen modules.\n'
               '[WARNING]: The script will access and change the Github repository.\n'
               'Do not use it if you do not want to deploy anything.\n'
               'Otherwise use --dry-run option.\n\n'
               'Options: \n'
               '--dry-run: Instead of accessing Git th script will print each step to the console.\n'
               '--debug: Script stops after each automatic step and asks the user to continue.\n'
               '--help: Provides a short help about the intention and possible options.')
        sys.exit(0)
        
input("Please clean up your working copy before running \
the script, press any key if it is already done")

#call only once to initialze global variables
init()

#Authentication of user and creation of session
while "Invalid details" == authenticate_git_user():
	authenticate_git_user()
print("Authentication successful.")
	
#############################Step 1.1.1  
# Enter Branch Name-mandatory
branch_name = input("Enter branch name:")  
#Checking if nothing is entered then ask user to enter again
while (not (branch_name and branch_name.strip() and check_branch_validity(branch_name)!="invalid")):   
    branch_name = input("Enter branch name:")    
build_folder_name=get_build_folder(branch_name)

# Enter Release Number-optional 
release_issue_number = input("Enter release issue number\
 without # prefix in case you already created one. Otherwise leave it empty:")

# Enter Version to be released-mandatory
release_version = input("Enter version to released...")
while (not (release_version and release_version.strip())):   
    release_version = input("Enter version to released...")

#Splitting version entered by user into numeric
try:
    split_version_from_v=release_version.rindex("v")
except ValueError:
    release_version=release_version
else:
    release_version=release_version[split_version_from_v+1:]
    
# Enter Next Version Number 
next_version = input("Enter next version...")
while (not (next_version and next_version.strip())):   
   next_version = input("Enter next version...")
   
#############################Step 1.1.2  
# Checks if we are at correct path "workspaces/cobigen-master/tools-cobigen"
print("Checking current directory path")
current_directory_path=os.getcwd()
print("Current working directory is: "+current_directory_path)
if current_directory_path.find("workspaces\cobigen-master\tools-cobigen") == -1:
    print("EXIT MESSAGE: Please go to correct directory i.e 'workspaces/cobigen-master/tools-cobigen'");sys.exit();

#############################Step 1.1.3	
#Check if remote "origin" is "devonfw/tools-cobigen" (git remote -v) 
repo = Repo('.')
remote_refernce = repo.git.remote()
if not (str(remote_refernce) == "origin"):
	print("EXIT MESSAGE: Repo is not pointing to master origin, Please change to master branch ");sys.exit()
    
#############################Step 1.1.4
if repo.is_dirty():
    print("EXIT MESSAGE: working copy is not clean");sys.exit()
    
#############################Step 0         
yes = {'yes'}
no = {'no'}
print("Check branch build not failing in production line https://devon.s2-eu.capgemini.com/ : ")
value=input("Press yes/no: ").lower()
while (value !="yes" and value!="no"):   
    value=input("Press yes/no: ").lower()
if input in no:
    print("EXIT MESSAGE: Correct the build failures");sys.exit() 
else:
    print("Build is sucessful.")
       
#############################Step 0   
print("close all eclipse instances of cobigen for safety reasons of build errors in maven")
input("Press any key if done:")
   
#############################Step 0   
print("close sourcetree for git performance reason");
input("Press any key if done:")
  
#############################Step 2.1   
url="https://"+init.git_username+":"+init.git_password+"@api.github.com/repos/krashah/practice/milestones"
response_object= requests.get(url)
milestone_json_data = json.loads(response_object.text)

#Fetching Versions from Milestones and keeping it in a list
version_list=[];matched_branch_with_version="";
for i in range(len(milestone_json_data)):
    milestones_in_git=milestone_json_data[i]['title']
    if build_folder_name in milestones_in_git:
        matched_branch_with_version=milestones_in_git;milestoneNumber=milestone_json_data[i]['number'];break
     
if matched_branch_with_version != "":          
    split_version_from_v=matched_branch_with_version.rindex("-v");
    milestone_version_in_git=milestones_in_git[split_version_from_v+2:];print(milestone_version_in_git);
    if milestone_version_in_git!=release_version:
        print("Please check if you passed the correct version to be released or check whether you missed\
         to create a milestone for the release and create one before running the script.");sys.exit()
else:
     print("Please check if you passed the correct version to be released or check whether you missed\
         to create a milestone for the release and create one before running the script.");sys.exit()
      
#############################Step 2.2
def create_github_issue():
	issue_text="This issue has been automatically created. It serves as a container for all release related commits";
	if bool_dry == True:
		print ('dry-run: would create a new issue')
	else: 
		github_issue_creation.make_github_issue(build_folder_name,git_username,git_password,milestoneNumber,issue_text,[build_folder_name]);	
	return	
    
 #Search for the Release issue to be used , if not found, create one:
if(release_issue_number==""):
    print("You have not entered Issue,hence creating a new issue..");
    release_issue_number=create_github_issue()
else:
    url="https://"+init.git_username+":"+init.git_password+"@api.github.com/repos/krashah/practice/issues/"+release_issue_number
    response_object= requests.get(url)
    milestone_json_data = json.loads(response_object.text)
    try:
        if milestone_json_data['message'] =="Not Found":
            print("Issue not found hence creating a new issue..");
            release_issue_number=create_github_issue()
    except:
        print("Issue found.No need to create issue..")
#############################Step 3.1/3.2/3.3
'''navigate to correct module folder depending on #1'''
os.chdir(build_folder_name)
print("Current working directory changed to: "+os.getcwd())
print(repo.git.checkout())
print(repo.git.pull())
  
#############################Step 3.4
 
'''****Currently implemented for Cobigen-maven , have to change to work for all packages"" '''
print(repo.git.checkout(branchname))
#print(repo.active_branch)
repo.git.__init__()
pom = etree.parse("pom.xml")
  
for mapping in pom.findall('//{http://maven.apache.org/POM/4.0.0}properties'):                           
    name  = mapping.find('{http://maven.apache.org/POM/4.0.0}cobigen.maven.version')
    if not name.text== release_version+"-SNAPSHOT":
        print(name.text);
        new_version=release_version+"-SNAPSHOT";
        name.text=str(new_version);
        pom.write('pom.xml')
print("Current working directory changed to: "+os.getcwd())       
print("Performing git add.."+repo.git.add(["pom.xml"]))
print(repo.git.status())
print("Performing git commit.."+repo.git.commit(message='#'+release_issue_number+' set release snapshot version'))
print("Performing git push.."+repo.git.push())
