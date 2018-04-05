from pathlib import Path  
import os 
import sys
from git import Repo
import urllib.request
import requests
import json
from scripts.github_issue_creation import make_github_issue
from lxml import etree
from scripts.branchname_validation import check_branch_validity
from scripts.git_authentication import git_user_authentication
from scripts.branchname_validation import get_build_folder

input("Please clean up your working copy before running \
the script, Press any key if it is already done")

#Authentication of user and creation of session
while "Invalid Details" == git_user_authentication():
	git_user_authentication()
print("Authentication Successfull...")
	
#############################Step 1.1.1  
# Enter Branch Name-mandatory
branch_name = input("Enter Branch Name....")  
#Checking if nothing is entered then ask user to enter again
while (not (branch_name and branch_name.strip() and check_branch_validity(branch_name,git_username,git_password)!="invalid")):   
    branch_name = input("Enter Branch Name....")    
build_folder_name=get_build_folder(branch_name)
# 
# while (not (branch_name and branch_name.strip())):   
#     branch_name = input("Enter Branch Name....")    
# build_folder_name=branch_name

# Enter Release Number-optional 
release_issue_number = input("Enter release issue number without # prefix in case you already created one. Otherwise leave it empty.")

# Enter Version to be released-mandatory
version = input("Enter Version to released...")
while (not (version and version.strip())):   
    version = input("Enter Version to released...")

#Splitting version entered by user into numeric
try:
    split_version_from_v=version.rindex("v")
except ValueError:
    version=version
else:
    version=version[split_version_from_v+1:]
    
# Enter Next Version Number 
next_version = input("Enter Next Version...")
while (not (next_version and next_version.strip())):   
   next_version = input("Enter Next Version...")
   
#############################Step 1.1.2  
# Checks if we are at correct path "workspaces/cobigen-master/tools-cobigen"
print("Checking current directory path")
current_directory_path=os.getcwd()
print("Current Working Directory is: "+current_directory_path)
# if current_directory_path.find("workspaces\cobigen-master\tools-cobigen") == -1:
#     print("EXIT MESSAGE: Please go to Correct Directory i.e 'workspaces/cobigen-master/tools-cobigen'");sys.exit();

#############################Step 1.1.3	
#Check if remote "origin" is "devonfw/tools-cobigen" (git remote -v) 
repo = Repo('.')
remote_refernce = repo.git.remote()
if not (str(remote_refernce) == "origin"):
	print("EXIT MESSAGE: Repo is not pointing to master origin, Please change to master branch ");sys.exit()
    
#############################Step 1.1.4
if repo.is_dirty():
    print("EXIT MESSAGE: Working Copy is not clean");sys.exit()
    
#############################Step 0         
yes = {'yes'}
no = {'no'}
print("Check branch build not failing in production line https://devon.s2-eu.capgemini.com/ : ")
value=input("Press yes/no: ").lower()
while (value !="yes" and value!="no"):   
    value=input("Press yes/no: ").lower()
if input in no:
    print("EXIT MESSAGE: Correct the Build failures");sys.exit() 
else:
    print("Build is sucessfull: Approved")
       
#############################Step 0   
print("close all eclipse instances of cobigen for safety reasons of build errors in maven")
input("Press any key if done:")
   
#############################Step 0   
print("close SourceTree for Git performance reason");
input("Press any key if done:")
  
#############################Step 2.1   
url="https://"+git_username+":"+git_password+"@api.github.com/repos/krashah/practice/milestones"
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
    if milestone_version_in_git!=version:
        print("Version to be Released is not found in Milestones list");sys.exit()
else:
     print("Version to be Released is not found in Milestones list");sys.exit()
      
#############################Step 2.2
issueText="This issue has been automatically created. It serves as a container for all release related commits";
def creation_of_issue():
    github_issue_creation.make_github_issue(build_folder_name,git_username,git_password,milestoneNumber,issueText,[build_folder_name]);    
    
 #Search for the Release issue to be used , if not found, create one:
if(release_issue_number==""):
    print("You have not entered Issue,hence Creating a new issue..");
    release_issue_number=creation_of_issue()
else:
    url="https://"+git_username+":"+git_password+"@api.github.com/repos/krashah/practice/issues/"+release_issue_number
    response_object= requests.get(url)
    milestone_json_data = json.loads(response_object.text)
    try:
        if milestone_json_data['message'] =="Not Found":
            print("Issue not Found hence Creating a new issue..");
            release_issue_number=creation_of_issue()
    except:
        print("Issue Found.No Need to Create Issue..")
#############################Step 3.1/3.2/3.3
'''navigate to correct module folder depending on #1'''
os.chdir(build_folder_name)
print("Current Working directory changed to: "+os.getcwd())
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
    if not name.text== version+"-SNAPSHOT":
        print(name.text);
        new_version=version+"-SNAPSHOT";
        name.text=str(new_version);
        pom.write('pom.xml')
print("Current Working directory changed to: "+os.getcwd())       
print("Performing git Add.."+repo.git.add(["pom.xml"]))
print(repo.git.status())
print("Performing git Commit.."+repo.git.commit(message='#'+release_issue_number+' set release snapshot version'))
print("Performing git Push.."+repo.git.push())
