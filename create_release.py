import re
import os 
import sys
import git
import json
import glob
import platform
import requests
import fileinput
import subprocess
from git import Repo
from lxml import etree
from pathlib import Path 
from scripts.settings import init
from scripts.github_issue_creation import make_github_issue
from scripts.branchname_validation import check_branch_validity
from scripts.git_authentication import authenticate_git_user
from scripts.branchname_validation import get_build_folder

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
               '--dry-run: Instead of accessing Git script will print each step to the console.\n'
               '--debug: Script stops after each automatic step and asks the user to continue.\n'
               '--help: Provides a short help about the intention and possible options.')
        sys.exit(0)
        
# Called only once to initialze global variables
init()

# Variables Initialization
repo = Repo('.')
git_cmd = git.cmd.Git(".")
git_url='@github.com/devonfw/tools-cobigen'
pl_url='https://devon.s2-eu.capgemini.com/'
wiki_name="tools-cobigen.wiki"
wiki_directory_path="cobigen-documentation/tools-cobigen.wiki"
wiki_version_overview_page="Cobigen.asciidoc"


# Authentication of user and creation of session
while authenticate_git_user(git_url) =="Invalid details":
	if("Authentication Successful"==authenticate_git_user(git_url)):
	    break
print("Authentication successful.")

# Methods performing git actions
def perform_git_reset():
	print("Executing git reset --hard HEAD.."+git_cmd.execute("git reset --hard HEAD"))

def perform_commit_with_issue_number():
	print("Executing git commit.."+repo.git.commit(message='#'+release_issue_number+' set release snapshot version'))

def perform_git_reset_pull_on_user_choice(user_choice):
	if user_choice=='N' | user_choice=='n':
         perform_git_reset();
         print("Executing git pull.."+repo.git.pull());
		 
# This Method is responsible for changing version number in pom.xml to new release version with SNAPSHOT	
def change_release_version_pom():
    if not name.text== release_version+"-SNAPSHOT":
        new_version=release_version+"-SNAPSHOT";
        name.text=str(new_version);
        pom.write('pom.xml')

#############################Step 1.1.1  
# Enter Branch Name-mandatory
branch_name = input("Enter branch name:")  
# Checking if nothing is entered then ask user to enter again

while (not check_branch_validity(branch_name,git_url) or not branch_name.strip()):
    user_input=input("Please enter valid branch name, press 1 to continue or 2 if you want to change the script:");
    if user_input== "1":
	    branch_name = input("Enter branch name:");
    else :
        print("You have opted for changing the branch name in script branchname_validation");
        sys.exit();

build_folder_name=get_build_folder(branch_name)
if build_folder_name=="invalid":
	bool_continue=input("Enter valid branch name:")
else:
	print('Branch is valid.')

# Enter Release Number-optional 
release_issue_number = input("Enter release issue number\
 without # prefix in case you already created one. Otherwise leave it empty:")

# Enter Version to be released-mandatory
release_version = input("Enter version number to be released...")
while (not (release_version and release_version.strip())):   
    release_version = input("Enter version number to be released...")

# Splitting version entered by user into numeric
try:
    split_version_from_v=release_version.rindex("v")
except ValueError:
    release_version=release_version
else:
    release_version=release_version[split_version_from_v+1:]
    
# Enter Next Version Number 
next_version = input("Enter next version number to be set after the release...")
while (not (next_version and next_version.strip())):   
   next_version = input("Enter next version number to be set after the release...")
   
#############################Step 1.1.2  
''' Checks if we are at correct path "workspaces/cobigen-master/tools-cobigen'''
print("Checking current directory path")
current_directory_path=os.getcwd()
print("Current working directory is: "+current_directory_path)
if "Windows" in platform.platform():
	tools_cobigen_path="workspaces\cobigen-master\tools-cobigen"
else:
	tools_cobigen_path="workspaces/cobigen-master/tools-cobigen"
	
if current_directory_path.find(tools_cobigen_path) == -1:
    print("EXIT MESSAGE: Please go to correct directory i.e 'workspaces/cobigen-master/tools-cobigen'");
    sys.exit()

#############################Step 1.1.3	
'''Check if remote "origin" is "devonfw/tools-cobigen" (git remote -v)''' 
remote_origin=git_cmd.execute("git remote -v")
if 'devonfw/tools-cobigen' not in remote_origin:
    print("EXIT MESSAGE: Remote origin is not 'devonfw/tools-cobigen', Please go to correct directory");
    sys.exit()

#############################Step 1.1.4
if repo.is_dirty():
    user_choice=input("Your working directory is not clean. Please clean it,\
	press 'Y' if it is done and you want to continue else any key to exit")
    if not user_choice =='Y':
        print("EXIT MESSAGE: working copy is not clean");
        sys.exit()
    
#############################Step 0         
yes = {'yes'}
no = {'no'}
print("Check branch build not failing in production line "+pl_url+" :")
value=input("Press yes/no: ").lower()
while (value !="yes" and value!="no"):   
    value=input("Press yes/no: ").lower()
if input in no:
    print("EXIT MESSAGE: Correct the build failures");
    sys.exit() 
else:
    print("Build is sucessful.")
       
#############################Step 0   
print("close all eclipse instances of cobigen for safety reasons of build errors in maven")
input("Press any key if done:")
   
#############################Step 0   
print("close sourcetree for git performance reason");
input("Press any key if done:")
  
#############################Step 2.1   
'''Prepare GitHub '''
url="https://"+init.git_username+":"+init.git_password+git_url+"/milestones"
response_object= requests.get(url)
milestone_json_data = json.loads(response_object.text)

# Fetching Versions from Milestones and keeping it in a list
version_list=[];matched_branch_with_version="";
for i in range(len(milestone_json_data)):
    milestones_in_git=milestone_json_data[i]['title']
    if build_folder_name in milestones_in_git:
        matched_branch_with_version=milestones_in_git;
        milestoneNumber=milestone_json_data[i]['number'];
        break
     
if matched_branch_with_version != "":          
    split_version_from_v=matched_branch_with_version.rindex("-v");
    milestone_version_in_git=milestones_in_git[split_version_from_v+2:];
    print(milestone_version_in_git);
    if milestone_version_in_git!=release_version:
        print("Please check if you passed the correct version to be released or check whether you missed\
        to create a milestone for the release and create one before running the script.");
        sys.exit()
else:
    print("Please check if you passed the correct version to be released or check whether you missed\
	to create a milestone for the release and create one before running the script.");
    sys.exit()
      
#############################Step 2.2
def create_github_issue():
	issue_text="This issue has been automatically created. It serves as a container for all release related commits";
	if bool_dry:
		print ('dry-run: would create a new issue with issue number 999')
	else: 
		github_issue_creation.make_github_issue(build_folder_name,git_url,git_username,git_password,milestoneNumber,issue_text,[build_folder_name]);	
	return
    
 # Search for the Release issue to be used , if not found, create one:
if release_issue_number=="":
    print("You have not entered an issue number of an existing issue, hence creating a new issue...");
    release_issue_number=create_github_issue()
else:
    url="https://"+init.git_username+":"+init.git_password+git_url+"/issues/"+release_issue_number
    response_object= requests.get(url)
    milestone_json_data = json.loads(response_object.text)
    try:
        if milestone_json_data['message'] =="Not Found":
            print("Issue not found hence creating a new issue..");
            release_issue_number=create_github_issue()
    except:
        print("Issue found.No need to create issue..")
		
if bool_dry:
		# Any random number
		release_issue_number="999"
#############################Step 3.1/3.2/3.3
'''Update Versions'''
'''navigate to correct module folder depending on #1'''
print(build_folder_name)
os.chdir(build_folder_name)
print("Current working directory changed to: "+os.getcwd())
print(repo.git.checkout())
print(repo.git.pull())
  
#############################Step 3.4 
'''****Currently implemented for Cobigen-maven , have to change to work for all packages"" '''
print('Checking out branch '+branch_name+'.')
repo.git.checkout(branch_name)
repo.git.__init__()
pom = etree.parse("pom.xml")

# For dev_mavenplugin branch
if branch_name == 'dev_mavenplugin':
    for mapping in pom.findall('//{http://maven.apache.org/POM/4.0.0}properties'):                           
        name  = mapping.find('{http://maven.apache.org/POM/4.0.0}cobigen.maven.version')
        try:
            change_release_version_pom()
        except:
	        continue
			
# For dev_core branch
elif branch_name == 'dev_core':
    for mapping in pom.findall('//{http://maven.apache.org/POM/4.0.0}properties'):                           
        name  = mapping.find('{http://maven.apache.org/POM/4.0.0}cobigencore.version')
        try:
            change_release_version_pom()
        except:
	        continue
			
# For dev_htmlmerger , dev_eclipseplugin, dev_eclipseplugin, dev_eclipseplugin, dev_jssenchaplugin branch
else:
    name  = pom.find('{http://maven.apache.org/POM/4.0.0}version')
    change_release_version_pom()

print("Current working directory changed to: "+os.getcwd())

if bool_dry:
    print ('dry-run: would add,commit,push pom.xml in git')
else:   
	print("Executing git add.."+repo.git.add(["pom.xml"]))
	print(repo.git.status())
	perform_commit_with_issue_number()
	print("Executing git push.."+repo.git.push())

#############################Step 3.5
for dname, dirs, files in os.walk("."):
    for fname in files:
	    fpath = os.path.join(dname, fname)	    
	    if "pom.xml" in fname:
		    print(fname)
		    with open(fpath) as file:
			    print(fpath)
			    pom = etree.parse(file)
			    for mapping in pom.findall('//{http://maven.apache.org/POM/4.0.0}dependency'):
				    name  = mapping.find('{http://maven.apache.org/POM/4.0.0}version');
				    try:
					    if "-SNAPSHOT" in name.text:					        
					        new_version=name.text.split("-");
					        name.text=str(new_version[0])
					        pom.write(fpath)
					    else:
					        continue
				    except:
					    continue
					
print('Removing ''SNAPSHOT'' from dependencies in Pom.xml and committing it')

if bool_dry:
    print ('dry-run: would add,commit,push pom.xml after removing suffix - in git')
else:
	print("Executing git add.."+repo.git.add(["pom.xml"]))
	print(repo.git.status())
	perform_commit_with_issue_number()
	print("Executing git push.."+repo.git.push())

############################Step 4 
'''mvn clean integration-test -> check if everything is # fine, otherwise abort 
(git reset --hard HEAD~2 && git pull) '''
print("Testing maven integeration..")
print("If maven clean integration-test fails,git reset --hard will be executed to revoke last commits and operation will be revoked")
maven_process= subprocess.Popen('mvn clean integration-test --log-file log.txt', shell=True,stdout = subprocess.PIPE)
stdout, stderr = maven_process.communicate()
if maven_process.returncode == "1":
    print("Maven clean integeration fails, please see log.txt for logs located at current directory ");
    if bool_dry:
        print ('dry-run: would perform git reset and pull')
    else: 
        print("Executing git reset --hard HEAD~2.."+git_cmd.execute("git reset --hard HEAD~2"));
        print("Executing git pull.."+repo.git.pull());	
    sys.exit();
   
	
############################Step 5
'''Update the wiki submodule and commit the latest version to target the updated release version of the wiki'''
os.chdir("..")
os.chdir()
print("Executing git pull origin master.."+git_cmd.execute("git pull origin master"));

print("Changing the CobiGen.asciidoc file , updating the version number")
title=get_cobigenwiki_title_name(branch_name)
with fileinput.FileInput(wiki_version_overview_page, inplace=True) as file:
	for line in file:
		line = re.sub('r'+title+'.,r'+title+''+version, line)
		sys.stdout.write(line)

os.chdir("..")
print(os.getcwd(wiki_directory_path))
if bool_dry:
    print ('dry-run: would perform git add, commit and push of wiki page')
else:
    print("Executing git add.."+repo.git.add([wiki_name]))
    print("Executing git commit of tools-cobigen.wiki.."+repo.git.commit(message='#'+release_issue_number+' update docs'))
    print("Executing git push.."+repo.git.push())

#############################Step 6
'''Merge development branch into master'''
repo.git.checkout('master')
print("Executing git pull..."+repo.git.pull());
if bool_dry:
    print('dry-run: would perform git merge')
else:
    try:
	    print("Executing git merge..."+repo.git.merge());
    except:
	    print("Excepion occured..")
	    print("Executing git merge --abort.."+git_cmd.execute("git merge --abort"));
	    perform_git_reset();
	
#############################Step 7
'''validation of merge commit'''
user_input=input("Please check all the changed file paths which is to be released, press N[Not allowed]")
list_of_changed_files=str(git_cmd.execute("git diff --name-only")).strip().split("\\n+")
is_pom_changed=false;
for file_name in list_of_changed_files:
    if "pom.xml" in file_name:
        is_pom_changed=True;
    if not file_name.startswith(build_folder_name):
        print(file_name +" does not starts with "+build_folder_name);
        user_choice=input("Some Files are outside the folder "+build_folder_name+". Do you want to continue merge? Press N/n(No) else any other key to continue")
        perform_git_reset_pull_on_user_choice(user_choice)
			
'''check if all nothing changed in any pom'''
if is_pom_changed:
    user_choice=input("Pom is changed, please check dependency tracking wiki page,press Y(Yes) to continue else N(No) to abort:")
    perform_git_reset_pull_on_user_choice(user_choice)
	
#############################Step 8
'''Set the Release version (without snapshot) and commit using "<#2>:\
 set release version" message'''
print('Removing ''SNAPSHOT'' from Pom.xml and committing it')
for mapping in pom.findall('//{http://maven.apache.org/POM/4.0.0}properties'):                           
    name  = mapping.find('{http://maven.apache.org/POM/4.0.0}cobigen.maven.version')
    if name.text== release_version+"-SNAPSHOT":
        new_version=release_version;name.text=str(new_version);
        pom.write('pom.xml')
if bool_dry:
    print ('dry-run: would add,commit,push pom.xml after removing suffix -SNAPSHOT in git')
else:
	print("Executing git add.."+repo.git.add(["pom.xml"]))
	print(repo.git.status())
	perform_commit_with_issue_number()
	print("Executing git push.."+repo.git.push())

############################Step 9
'''deploy''' 
if build_folder_name!="cobigen-eclipse":
	os.system("start cmd.exe @cmd /k \" echo 1) *****************Executing maven clean package*****************\
	& mvn clean package --update-snapshots bundle:bundle -Pp2-bundle  -Dmaven.test.skip=true & echo 2) *****************\
	Executing maven install***************** & mvn install bundle:bundle -Pp2-bundle p2:site -Dmaven.test.skip=true & echo 3)\
	*****************Executing maven deploy***************** & mvn deploy -Pp2-upload-stable -Dmaven.test.skip=true -Dp2.upload=stable\"")	
else:
    os.system("start cmd.exe @cmd /k \"mvn clean deploy -Pp2-build-stable,p2-upload-stable,p2-build-mars -Dp2.upload=stable\"")	

user_choice=input("Please check installation of module from update site,Do you want to continue? Press Y(yes) for continue or N(No) for abort:")
if user_choice=='N' | user_choice=='n':
	sys.exit()

############################Step 10
'''Create Tag'''
# Removing cobigen/ from the build folder name for getting tag name"
if "cobigen/" in build_folder_name:
    tag_name=build_folder_name.split("/")[1]+"/v"+release_version
else:
	tag_name=build_folder_name+"/v"+release_version
print("Creating Tag: "+tag_name)
repo.create_tag(tag_name)
print("Pushing git tags.."+git_cmd.execute("git push --tags"))