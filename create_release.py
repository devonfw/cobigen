import re
import os 
import sys
import git
import json
import platform
import requests
import fileinput
import subprocess
from git import Repo
from lxml import etree
from pathlib import Path 
from github import Github
from scripts.settings import init
from uritemplate import URITemplate, expand
from scripts.github_issue_creation import make_github_issue
from scripts.git_authentication import authenticate_git_user
from scripts.branchname_validation import get_build_folder
from scripts.branchname_validation import get_cobigenwiki_title_name

# get command line arguments and initialise global booleans
# --dry-run -> do not change anything in git but print the steps
# --debug -> step by step execution

bool_dry = False
bool_debug = False
bool_test = False

#This method is used to print statements with INFO prefix
def print_info(print_message):
    print("[INFO] "+print_message)

for o in sys.argv:
    if not o:
        print_info("For further informations run >cobigen_script.py --help<")
    elif o == "--dry-run":
        bool_dry = True
        print_info("--dry-run: No changes will be made on the Git repo.")
    elif o == "--debug":
        print_info("--debug: The script will require user interactions for each"
               "step. It will access git without --dry-run.")
        bool_debug = True
    elif o == "--test":
        print_info("--test: Script runs on a different repo for testing purpose. Does not require any user interaction to speed up.")
        bool_test = True
    elif o == "--help":
        print_info("This script helps deploying CobiGen modules.\n"
               "[WARNING]: The script will access and change the Github"
               " repository.\n Do not use it unless you want to deploy "
               "modules.\n Use --dry-run option to test the sript.\n\n"
               "Options: \n"
               "--dry-run: Instead of accessing Git th script will print each"
               "step to the console.\n"
               "--debug: Script stops after each automatic step and asks the"
               " user to continue.\n"
               "--test: Script runs on a different repo for testing purpose. It also uses \npredefined names and variables to shorten up the process.\n"
               "--help: Provides a short help about the intention and possible options.")
        sys.exit(0)
        
# Called only once to initialze global variables
init()

# Variables Initialization
repo = Repo(".")
git_cmd = git.cmd.Git(".")
if bool_test:
    git_url="@api.github.com/repos/TODO/TODO"
else:
    git_url="@api.github.com/repos/devonfw/tools-cobigen"
pl_url="https://devon.s2-eu.capgemini.com/"
wiki_name="tools-cobigen.wiki"
wiki_version_overview_page="CobiGen.asciidoc"
user_acceptance_messages=[]
root_path = os.path.dirname(os.path.realpath(__file__))

# Authentication of user and creation of session
while authenticate_git_user(bool_test) =="Invalid details":
	if("Authentication Successful"==authenticate_git_user(bool_test)):
	    break
print_info("Authentication successful.")

g = Github(init.git_username, init.git_password)
user = g.get_user()

if bool_test:
    rep = user.get_repo("TODO")
else:
    org = g.get_organization("devonfw")
    rep = org.get_repo("tools-cobigen")

	
# Methods performing git actions
def perform_git_reset():
	print_info("Executing git reset --hard HEAD.."+git_cmd.execute("git reset --hard HEAD"))

def perform_commit_with_issue_number(commit_message):
    try:
        print_info("Executing git commit.."+repo.git.commit(message=commit_message))
        print_info("Executing git push.."+repo.git.push())
    except Exception as e:
	    if "no changes added to commit" in str(e):
	        print_info("No File is changed, Nothing to commit..")
		

def perform_git_reset_pull_on_user_choice(user_choice):
	if user_choice=="no":
         perform_git_reset();
         print_info("Executing git pull.."+repo.git.pull());
		 
# This method is responsible for changing version number in pom.xml to new release version with SNAPSHOT	
def add_snapshot_in_version(name,pom,version_to_change):
    if not name.text== version_to_change+"-SNAPSHOT":
        new_version=version_to_change+"-SNAPSHOT";
        name.text=str(new_version);
        pom.write("pom.xml")

def remove_snapshot_in_version(name,pom,version_to_change):
    if name.text== version_to_change+"-SNAPSHOT":
        new_version=version_to_change;
        name.text=str(new_version);
        pom.write("pom.xml")

def get_exit_message_milestone():
    print("[ERROR]Please check if you passed the correct version to be released or check whether you missed \
	to create a milestone for the release and create one before running the script.");
    sys.exit()

def call_add_remove_snapshot_method(name,pom,bool_add_snapshot,version_to_change):
    if bool_add_snapshot:
        add_snapshot_in_version(name,pom,version_to_change)
    else:
	    remove_snapshot_in_version(name,pom,version_to_change)

# This method is responsible for adding SNAPSHOT version if not already added
def add_remove_snapshot_version_in_pom(bool_add_snapshot,commit_message,version_to_change):
    print("Checking out branch for adding SNAPSHOT version: "+branch_name+".")
    #repo.git.checkout(branch_name)
    repo.git.__init__()
    pom = etree.parse("pom.xml")

    # For dev_mavenplugin branch
    if branch_name == "dev_mavenplugin":
        for mapping in pom.findall("//{http://maven.apache.org/POM/4.0.0}properties"):                           
            name  = mapping.find("{http://maven.apache.org/POM/4.0.0}cobigen.maven.version")
            try:
                call_add_remove_snapshot_method(name,pom,bool_add_snapshot,version_to_change)
            except:
	            continue
			
    # For dev_core branch
    elif branch_name == "dev_core":
        for mapping in pom.findall("//{http://maven.apache.org/POM/4.0.0}properties"):                           
            name  = mapping.find("{http://maven.apache.org/POM/4.0.0}cobigencore.version")
            try:
                call_add_remove_snapshot_method(name,pom,bool_add_snapshot,version_to_change)
            except:
	            continue
			
    # For dev_htmlmerger , dev_eclipseplugin, dev_eclipseplugin, dev_eclipseplugin, dev_jssenchaplugin branch
    else:    	
        name  = pom.find("{http://maven.apache.org/POM/4.0.0}version")
        call_add_remove_snapshot_method(name,pom,bool_add_snapshot,version_to_change)

    print_info("Current working directory changed to: "+os.getcwd())

    if bool_dry:
        print_info("dry-run: would add,commit,push pom.xml in git")
    else:   
	    print_info("Executing git add.."+repo.git.add(["pom.xml"]))
	    print(repo.git.status())
	    perform_commit_with_issue_number(commit_message)

#This Method is responsible for Checking branches in repository with branch entered by user
def check_branch_validity(branch_name):
    if git_cmd.execute("git ls-remote --heads origin dev_eclipseplugin | wc -l") == "0":
	    is_branch_valid=False
    else:
	    is_branch_valid=True;
    return is_branch_valid

############################Step 1.1.1  
# Enter Branch Name-mandatory
if bool_test:
    branch_name="TODO"
else:
    branch_name = input("Enter branch name:")  

    #Checking if nothing is entered then ask user to enter again   
    while (not branch_name.strip() or not check_branch_validity(branch_name)):
        user_input=input("You have entered branch which doesn't exists, Please enter valid branch name");
        branch_name = input("Enter branch name:")   

build_folder_name=get_build_folder(branch_name)

if build_folder_name=="invalid":
	print("[ERROR]You most probably entered a new branch, which is not yet known to this script.\
	Please check the script function get_build_folder in scripts/branchname_validation.py and adapt properly");
	sys.exit();
else:
	print_info("Branch is valid.")

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
 
# Removing cobigen/ from the build folder name for milestone title and tag name"
if "cobigen/" in build_folder_name:
    tag_name=build_folder_name.split("/")[1]+"/v"+release_version
    build_folder_without_cobigen=build_folder_name.split("/")[1]
else:
	tag_name=build_folder_name+"/v"+release_version
	build_folder_without_cobigen=build_folder_name
	
release_version_with_v = "v" + release_version

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
remote_origin=git_cmd.execute("git remote -v")
if "devonfw/tools-cobigen" not in remote_origin:
    print("EXIT MESSAGE: Remote origin is not 'devonfw/tools-cobigen', Please go to correct directory");
    sys.exit()

#############################Step 1.1.4
if repo.is_dirty():
    user_choice=input("Your working directory is not clean. Please clean it,\
    press 'yes' if it is done and you want to continue else any key to exit:").lower()
    if not user_choice =="yes":
        user_acceptance_messages.append("User cleaned working directory and allowed script to run further.")
        print("[ERROR]EXIT MESSAGE: working copy is not clean");
        sys.exit()
    
#############################Step 0      
print_info("Check branch build not failing in production line "+pl_url+" :")
value=input("Press yes/no: ").lower()
while (value !="yes" and value!="no"):   
    value=input("Press yes/no: ").lower()
if input == "no":
    print("[ERROR]EXIT MESSAGE: Correct the build failures");
    sys.exit() 
else:
    user_acceptance_messages.append("User confirmed that build in production line was not failing.")
    print_info("Build is sucessful.")
       
#############################Step 0
print_info("close all eclipse instances of cobigen for safety reasons of build errors in maven")
input("Press any key if done:")
   
#############################Step 0
print_info("close sourcetree for git performance reason");
input("Press any key if done:")
  
#############################Step 2.1   
'''Search for the Milestone to be released (based on #3) -> abort if not found'''
url="https://"+init.git_username+":"+init.git_password+git_url+"/milestones"
response_object= requests.get(url)
milestone_json_data = json.loads(response_object.text)

# Fetching Versions from Milestones and keeping it in a list
matched_milestone_title="";
for i in range(len(milestone_json_data)):
    milestone_title_in_git=milestone_json_data[i]["title"];
    if build_folder_without_cobigen in milestone_title_in_git:
        matched_milestone_title=milestone_title_in_git;
        milestone_number=milestone_json_data[i]["number"];
        break
     
if matched_milestone_title != "":          
    split_version_from_v=matched_milestone_title.rindex("-v");
    milestone_version_in_git=milestone_title_in_git[split_version_from_v+2:];
    if milestone_version_in_git!=release_version:
        get_exit_message_milestone()
else:
    get_exit_message_milestone()
      
#############################Step 2.2
'''Search for the Release issue to be used (based on #2) -> if not found, create one:'''
def create_github_issue():
    issue_text="This issue has been automatically created. It serves as a container for \
	all release related commits";
    if bool_dry:
        print_info("dry-run: would create a new issue with issue number 999")
        release_issue_number="999"
    else: 
        release_issue_number=make_github_issue("Release "+build_folder_without_cobigen+"-"+release_version_with_v,git_url,\
        milestone_number,issue_text,[build_folder_without_cobigen]);	
    return release_issue_number
    
 # Search for the Release issue to be used , if not found, create one:
if release_issue_number=="":
    print_info(" You have not entered an issue number of an existing issue, hence creating a new issue...");
    release_issue_number=create_github_issue()
else:
    url="https://"+init.git_username+":"+init.git_password+git_url+"/issues/"+release_issue_number
    response_object= requests.get(url)
    issue_json_data = json.loads(response_object.text)
    try:
        if issue_json_data["message"] =="Not Found":
            print_info(" Issue not found hence creating a new issue..");
            release_issue_number=create_github_issue()
    except:
        print_info("Issue found.No need to create issue..")

#############################Step 3.1/3.2/3.3
'''Update Versions'''
'''navigate to correct module folder depending on #1'''
os.chdir(build_folder_name)
print_info("Current working directory changed to: "+os.getcwd())
print_info("Performing git checkout.."+repo.git.checkout())
print_info("Performing git pull.."+repo.git.pull())
  
#############################Step 3.4 
'''Set the SNAPSHOT version'''
print_info("Set snapshot version of target release")
commit_message="#"+str(release_issue_number)+" set release snapshot version"
add_remove_snapshot_version_in_pom(True,commit_message,release_version)

#############################Step 3.5
'''Check relevant poms based on #1 for dependencies!\
 (not pom version itself) declaring SNAPSHOT versions'''

print_info("Removing ''SNAPSHOT'' from dependencies in Pom.xml and committing it")

if bool_dry:
    print_info("dry-run: would add,commit,push pom.xml after removing suffix - in git")
else:
    for dname, dirs, files in os.walk("."):
        for fname in files:
            fpath = os.path.join(dname, fname)	    
            if "pom.xml" in fname:
                with open(fpath) as file:
                    pom = etree.parse(file)
                    for mapping in pom.findall("//{http://maven.apache.org/POM/4.0.0}dependency"):
                        name  = mapping.find("{http://maven.apache.org/POM/4.0.0}version")
                        artifactId  = pom.find("{http://maven.apache.org/POM/4.0.0}artifactId")
                        try:
                            if "-SNAPSHOT" in name.text:
                                new_version=name.text.split("-")
                                name.text=str(new_version[0])
                                pom.write(fpath)
                                if artifactId =="cobigen-core":
                                    core_version_in_eclipse_pom=name.text
                            else:
                                continue
                        except:
                            continue
    print_info("Executing git add.."+repo.git.add(["pom.xml"]))
    print(repo.git.status())
    commit_message="#"+str(release_issue_number)+" Removing SNAPSHOT suffix from dependencies"
    perform_commit_with_issue_number(commit_message)
    

############################Step 4 
'''mvn clean integration-test -> check if everything is # fine, otherwise abort 
(git reset --hard HEAD~2 && git pull) '''
print_info("Testing maven integeration..")
print_info("If maven clean integration-test fails,git reset --hard will be executed to revoke last commits and operation will be revoked")
maven_process= subprocess.Popen("mvn clean integration-test -Pp2-build-mars,p2-build-stable --log-file create_release.py.log", shell=True,stdout = subprocess.PIPE)
stdout, stderr = maven_process.communicate()
if maven_process.returncode == 1:
    print_info("Maven clean integeration fails, please see create_release.py.log for logs located at current directory ");
    if bool_dry:
        print_info("dry-run: would perform git reset and pull")
    else: 
        print_info("Executing git reset --hard HEAD~2.."+git_cmd.execute("git reset --hard HEAD~2"));
        print_info("Executing git pull.."+repo.git.pull());	
    sys.exit();
   
	
############################Step 5
'''Update the wiki submodule and commit the latest version to target the updated release version of the wiki'''
filepath = os.path.abspath(os.path.join(root_path, "cobigen-documentation", "tools-cobigen.wiki"))
print_info("Executing git pull origin master.."+git_cmd.execute("git pull origin master"));
os.chdir(filepath)
print_info("Changing the "+wiki_version_overview_page+" file, updating the version number")
title=get_cobigenwiki_title_name(branch_name)
new_title=title+" "+release_version_with_v
with fileinput.FileInput(wiki_version_overview_page, inplace=True) as file:
	for line in file:	
		line = re.sub(r''+title+'.+',new_title, line)
		sys.stdout.write(line)
		
if bool_dry:
    print_info("dry-run: would perform git add, commit and push of wiki page")
else:
    print_info("Executing git add.."+repo.git.add([wiki_version_overview_page]))
    print_info("Executing git commit of tools-cobigen.wiki.."+perform_commit_with_issue_number("#"+release_issue_number+" update wiki docs"))   

#############################Step 6
'''Merge development branch into master'''
print_info("Executing git pull..."+repo.git.pull());
if bool_dry:
    print("dry-run: would perform git merge")
else:
    repo.git.checkout("master")
    try:
	    print_info("Executing git merge..."+repo.git.merge());
    except:
	    print_info("Exception occured..")
	    print_info("Executing git merge --abort.."+git_cmd.execute("git merge --abort"));
	    perform_git_reset();
	
#############################Step 7
'''validation of merge commit'''
print("Please check all the changed file paths which is to be released")
list_of_changed_files=str(git_cmd.execute("git diff --name-only")).strip().split("\\n+")
is_pom_changed=False
if bool_dry:
    print("dry-run: would check if anything outside build folder is changed.If yes, user is asked to continue or not")
else:
    for file_name in list_of_changed_files:
        if "pom.xml" in file_name:
            is_pom_changed=True;
        if not file_name.startswith(build_folder_name):
            print(file_name +" does not starts with "+build_folder_name);
            user_choice=input("Some Files are outside the folder "+build_folder_name+". Do you want to continue merge? Press 'no' else any other key to continue")
            perform_git_reset_pull_on_user_choice(user_choice)
            user_acceptance_messages.append("User has accepted to continue when found that some files were outside of build folder name")
			
    '''check if all nothing changed in any pom'''
    if is_pom_changed:
        user_choice=input("Pom is changed, please check dependency tracking wiki page,press 'yes' to continue else 'no' to abort:")
        perform_git_reset_pull_on_user_choice(user_choice)
        user_acceptance_messages.append("User has accepted to continue when found that pom has been changed")
	
#############################Step 8
'''Set the Release version (without snapshot) and commit using "<#2>:\
 set release version" message'''
if bool_dry:
    print("dry-run: would set the new release version in pom")
else:
    print("Setting the release version "+release_version+" in the pom.xml and committing it.")
    commit_message="#"+str(release_issue_number)+" Removing snapshot from version"
    add_remove_snapshot_version_in_pom(False,commit_message,release_version)

############################Step 9
'''deploy''' 
if build_folder_name!="cobigen-eclipse":
	os.system("start cmd.exe @cmd /k \" echo 1) *****************Executing maven clean package*****************\
	& mvn clean package --update-snapshots bundle:bundle -Pp2-bundle  -Dmaven.test.skip=true & echo 2) *****************\
	Executing maven install***************** & mvn install bundle:bundle -Pp2-bundle p2:site -Dmaven.test.skip=true & echo 3)\
	*****************Executing maven deploy***************** & mvn deploy -Pp2-upload-stable -Dmaven.test.skip=true -Dp2.upload=stable\"")	
else:
    os.system("start cmd.exe @cmd /k \"mvn clean deploy -Pp2-build-stable,p2-upload-stable,p2-build-mars -Dp2.upload=stable\"")	

user_choice=input("Please check installation of module from update site,Do you want to continue? Press 'yes' for continue or 'no' for abort:")
if user_choice=="no":
	sys.exit()

############################Step 10
'''Create Tag'''
print_info("Creating Tag: "+tag_name)
if bool_dry:
    print("dry-run:would create a new tag")
else:
    repo.create_tag(tag_name)
    print_info("Pushing git tags.."+git_cmd.execute("git push --tags"))

#############################Step 11.1
'''Process GitHub Milestone and Create Release'''
release_milestone = rep.get_milestone(milestone_number)

if bool_dry:
    print_info("dry-run: would close the milestone:", release_milestone.title)
else:
    if (release_milestone.state == "closed"):
        print_info("Milestone >>", release_milestone.title, "<< is already closed, please check.")
    else:
        release_milestone.edit(release_milestone.title, "closed", release_milestone.description)
        print_info("New status of Milestone >>", release_milestone.title, "<< is:", release_milestone.state )

#############################Step 11.2
'''create a new release'''
if bool_dry:
    print_info("dry-run: would create a new release")
else:
    url_milestone = "https://github.com/"+ rep.full_name + "/milestone/" + str(milestone_number)+"?closed=1"
    release_title = new_title.replace(" -","")
    release_text = "[ChangeLog](" + url_milestone + ")"
    if "eclipse" in branch_name:
	    for i in range(len(milestone_json_data)):
		    if "cobigen-core-v"+core_version_in_eclipse_pom == milestone_json_data[i]["title"]:
			    if not milestone_json_data[i]["state"] =="closed":
				    print_info("Core version"+core_version_in_eclipse_pom+" is not released yet,	This should be released before releasing cobigen-eclipse");
				    sys.exit()
			    else:
				    core_url_milestone = "https://github.com/"+ rep.full_name + "/milestone/" + str(milestone_json_data[i]["number"])+"?closed=1"
				    release_text=release_text+ "\n also includes \n"+ "[ChangeLog CobiGen Core](" + url_milestone + ")"					
	    
    try:
        content_type="application/java-archive"
        response=rep.create_git_release(tag_name, release_title, release_text, draft=False, prerelease=False, target_commitish="master");
        upload_url=response.upload_url
        uri_template = URITemplate(upload_url)
        if branch_name in ["dev_openapiplugin","dev_xmlplugin","dev_propertyplugin","dev_jsonplugin","dev_tempeng_velocity","dev_textmerger","dev_htmlmerger","dev_tsplugin","dev_jssenchaplugin"]:
            os.chdir(build_folder_name+"/target")            			
        elif branch_name=="dev_javaplugin":
            os.chdir(build_folder_name+"/cobigen-javaplugin/target")
        elif branch_name in ["dev_core","dev_mavenplugin"]:
            os.chdir(build_folder_name)
        elif branch_name=="dev_eclipseplugin":
            content_type="application/zip"
            os.chdir(build_folder_name+"/cobigen-eclipse-updatesite/target")
        else:
            print_info("New branch is not added in the script, please add it");
            sys.exit()
        for root, dirs, files in os.walk("."):
            dirs[:] = [d for d in dirs if d not in [".settings","src",]]
            for fname in files:
                fpath = os.path.join(root, fname);
				# To prevent uploading of unnecessary zip/jar files.
                if ("jar" in fname or "zip" in fname) and version in fname:
                    print("Uploading file "+fname+"..")
                    asset_url = uri_template.expand(name=fname)
                    r = requests.post(asset_url, auth=(init.git_username,init.git_password) ,headers={'Content-Type':content_type}, files={'file': (fname, open(fpath, 'rb'), 'application/octet-stream')})
    except Exception as e:
        print("[ERROR]"+str(e))
    else:
        print_info("Created a new release")

#############################Step 11.3
'''create a new milestone based on the name of\
 the previous closed milestone with the new version #4'''
if bool_dry:
    print_info("dry-run: would create a new milestone")
else:
    new_mile_title =release_milestone.title.strip(" Release").strip(release_version) + next_version
    new_mile_description = release_milestone.description
    print_info("Creating the next milestone..")
    try:
        rep.create_milestone(new_mile_title, "open", new_mile_description)
    except github.GithubException as e:
        print_info("Could not create milestone, does it already exists?")
    else:
        print_info("Created a new milestone")

#############################Step 12
'''Merge master to development branch'''
if bool_dry:
    print_info("dry-run: would merge from master to ", branch_name)
else:
    try:
        head = rep.get_branch("master")
        base = rep.get_branch(branch_name)

        merge_to_devbranch = rep.merge(base.name, head.commit.sha, "merge to dev_branch")
        print_info("Merged master into " + base.name)

    except Exception as ex:
        print("[ERROR]Something went wrong, please check if merge conflicts exist and solve them.")
        if bool_debug or bool_test:
            print(ex)

#############################Step 13
'''set next release version'''
if bool_dry:
    print_info("dry-run: would set next version")
else:
    commit_message="setting snapshot version for next release"
    add_remove_snapshot_version_in_pom(True,commit_message,next_version)

#############################Step 14
'''Close issue number'''
release_issue = rep.get_issue(int(release_issue_number))
if bool_dry:
    print_info("dry-run: would close the release issue")
else:
    #will never find closed issues
    closing_comment = "Automatically processed."
    release_issue.create_comment(closing_comment)
    release_issue.edit(state="closed")
    print_info("Closed issue >>", release_issue.title, "<<")
	
print("Script has been executed successfully, below are the points that user accepted during script execution: ")

'''The decisions taken by the developer and the context of the decisions throughout the script'''
for message in user_acceptance_messages:
    print_info(""+message)
