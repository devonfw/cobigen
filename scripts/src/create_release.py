import re
import os 
import sys
import git

import platform
import fileinput
import subprocess
from tools.config import Config
from tools.github import GitHub
from tools.git_repo import GitRepo
from tools.validation import exit_if_not_executed_in_ide_environment, exit_if_origin_is_not_correct,\
    exit_if_working_copy_is_not_clean, check_running_in_bash
from tools.user_interface import print_step, print_error, prompt_yesno_question, print_info, print_info_dry, print_debug
from tools.maven import Maven
from pprint import pprint
from tools.initialization import init_git_dependent_config, init_non_git_config
from git.exc import InvalidGitRepositoryError
from _winapi import CREATE_NEW_CONSOLE
from asyncio.subprocess import PIPE

#############################
print_step("Initialization...")
#############################

try:
    git.cmd.Git(".")
except InvalidGitRepositoryError:
    print_error("Path is not a git repository. Please go to valid git repository!")
    sys.exit()

check_running_in_bash()
exit_if_working_copy_is_not_clean()
exit_if_not_executed_in_ide_environment()

config = Config()
init_non_git_config(config)

if(config.debug):
    print_debug("Current config:")
    pprint(vars(config))
    if not prompt_yesno_question("Continue?"):
        sys.exit()

git_repo = GitRepo(config)
github = GitHub(config)
init_git_dependent_config(config, github)

exit_if_origin_is_not_correct(config)

if(config.debug):
    print_debug("Current config:")
    pprint(vars(config))
    if not prompt_yesno_question("Continue?"):
        sys.exit()
        
maven = Maven(config, github)

input("Please close all eclipse instances as of potential race conditions on maven builds causing errors. Press any key if done...")
input("Please close SourceTree for Git performance reasons. Press any key if done...")

report_messages=[]
    
#############################
print_step("Check for working CI build and tests...")
#############################
if not prompt_yesno_question("Are the tests on branch " + config.branch_to_be_released + " passing in CI?"):
    print_error("Please correct the build failures before releasing!");
    sys.exit() 
else:
    report_messages.append("User confirmed that tests are running on CI and the build is not failing.")
    print_info("Build is reported to be successful.")
  
#############################
print_step("Search for GitHub milestone...")
#############################   
milestone_number = github.find_milestone()
print_info("Found!")

#############################
print_step("Find or create the GitHub release issue...")
#############################
if not config.github_issue_no:
    if config.dry_run:
        print_info_dry("Would now create a new GitHub issue for the release.")
    else:
        issue_text="This issue has been automatically created. It serves as a container for all release related commits";
        config.github_issue_no = github.create_issue("Release " + config.expected_milestone_name)
elif not github.exists_issue(config.github_issue_no):
    print_error("Issue with number #"+config.github_issue_no+" not found!")
    sys.exit()

#############################
print_step("Navigate to branch " + config.branch_to_be_released + " and prepare workspace...")
#############################
git_repo.checkout(config.branch_to_be_released)
os.chdir(os.path.join(config.root_path, config.get_build_folder()))
git_repo.clean()

#############################
print_step("Set the SNAPSHOT version...")
#############################
print_info("Set snapshot version of target release")
maven.add_remove_snapshot_version_in_pom(True, config.release_version)
print_info("Git add and commit...")
git_repo.repo.git.add(["pom.xml"])
git_repo.commit("set release snapshot version")

if config.debug:
    prompt_yesno_question("POM changes committed. Continue?")

#############################
print_step("Upgrade dependencies of SNAPSHOT versions and committing it...")
#############################
core_version_in_eclipse_pom = maven.upgrade_snapshot_dependencies()
git_repo.repo.git.add(["pom.xml"])
git_repo.commit("upgrade SNAPSHOT dependencies")

#############################
print_step("Run integration tests...")
#############################
maven_process = subprocess.Popen([sys.executable, "-c", "mvn clean integration-test -Pp2-build-mars,p2-build-stable && read -n1 -r -p 'Press any key to continue...' key"], 
                                 creationflags=CREATE_NEW_CONSOLE, stdin=PIPE, stdout = PIPE, universal_newlines=True, bufsize=1)

while True:
    out = maven_process.stderr.read(1)
    if out == '' and maven_process.poll() != None:
        break
    if out != '':
        sys.stdout.write(out)
        sys.stdout.flush()

if maven_process.returncode == 1:
    print_error("Integration tests failed, please see create_release.py.log for logs located at current directory.");    
    git_repo.reset()
    sys.exit();

#############################
print_step("Update wiki submodule...")
#############################
continue_run = True
if config.test_run:
    continue_run = prompt_yesno_question("Would now update wiki submodule. Continue (yes) or skip (no)?")
        
if continue_run:    
    os.chdir(config.wiki_submodule_path())
    git_repo.repo.execute("git pull origin master")
    
    print_info("Changing the "+config.wiki_version_overview_page+" file, updating the version number...")
    version_decl = config.cobigenwiki_title_name
    new_version_decl = version_decl+" v"+config.release_version
    with fileinput.FileInput(config.wiki_version_overview_page, inplace=True) as file:
        for line in file:	
            line = re.sub(r''+version_decl+'.+',new_version_decl, line)
            sys.stdout.write(line)
    
    git_repo.repo.add([config.wiki_version_overview_page])
    git_repo.commit("update wiki docs")
    git_repo.push()
    
    if config.debug and not prompt_yesno_question("Wiki docs have been committed. Next would be merging to master. Continue?"):
        git_repo.reset()
        sys.exit() 

#############################
print_step("Merging " + config.branch_to_be_released + " to master...")
#############################
os.chdir(config.root_path)
git_repo.checkout("master")

print_info("Executing git pull before merging development branch to master.")
git_repo.pull()
print_info("Merge...")
try:
    git_repo.repo.execute("git merge " + config.branch_to_be_released);
except:
    print_error("Exception occured, executing git merge --abort...")
    git_repo.repo.execute("git merge --abort");
    git_repo.reset()
    sys.exit()

#############################
print_step("Validate merge commit...")
#############################
print("Please check all the changed file paths which is to be released")
list_of_changed_files=str(git_repo.repo.execute("git diff --name-only")).strip().split("\\n+")
is_pom_changed=False
for file_name in list_of_changed_files:
    if "pom.xml" in file_name:
        is_pom_changed=True;
    if not file_name.startswith(config.get_build_folder()):
        print_info(file_name +" does not starts with "+config.get_build_folder())
        if not prompt_yesno_question("Some Files are outside the folder "+config.get_build_folder()+". Please check for file changes as this should not be the case in a normal scenario! Continue?"):
            git_repo.reset()
            sys.exit()
        report_messages.append("User has accepted to continue when found that some files were outside of build folder name")

if is_pom_changed:
    if not prompt_yesno_question("POM has been changed, please update dependency tracking wiki page! Continue?"):
        git_repo.reset()
        sys.exit()
    report_messages.append("User has accepted to continue on found POM changes")

#############################
print_step("Set release version...")
#############################
maven.add_remove_snapshot_version_in_pom(False,"Set release version")

#############################
print_step("Deploy artifacts to nexus and update sites...")
#############################
if config.dry_run or config.test_run:
    print_info_dry("Would not deploy to maven central & updatesite. Skipping...")
else:
    if config.get_build_folder() != "cobigen-eclipse":
        print("\n***************** (1) Executing maven clean package *****************\n")
        git_repo.repo.execute("mvn clean package --update-snapshots bundle:bundle -Pp2-bundle -Dmaven.test.skip=true")
        print("\n***************** (2) Executing maven install *****************\n")
        git_repo.repo.execute("mvn install bundle:bundle -Pp2-bundle p2:site -Dmaven.test.skip=true")
        print("\n***************** (3) Executing maven deploy *****************\n")
        git_repo.repo.execute("mvn deploy -Pp2-upload-stable -Dmaven.test.skip=true -Dp2.upload=stable")
    else:
        git_repo.repo.execute("mvn clean deploy -Pp2-build-stable,p2-upload-stable,p2-build-mars -Dp2.upload=stable")

    if not prompt_yesno_question("Please check installation of module from update site! Was the installation of the newly deployed bundle successful?"):
        git_repo.reset()
        sys.exit()

#############################
print_step("Create Tag...")
#############################
if config.dry_run:
    print_info_dry("Would create Git tag with name "+config.tag_name)
else:
    new_tag=git_repo.repo.create_tag(config.tag_name)
    git_repo.push()

#############################
print_step("Close GitHub Milestone...")
#############################
if config.dry_run:
    print_info_dry("Would close GitHub milestone with no "+milestone_number)
else:
    release_milestone = github.repo.get_milestone(milestone_number)
    
    if config.dry_run:
        print_info_dry("Would close the milestone: " + release_milestone.title)
    else:
        if (release_milestone.state == "closed"):
            print_info("Milestone >>", release_milestone.title, "<< is already closed, please check.")
        else:
            release_milestone.edit(release_milestone.title, "closed", release_milestone.description)
            print_info("New status of Milestone >>" +release_milestone.title+ "<< is: "+ release_milestone.state)

#############################
print_step("Create new GitHub elease...")
#############################
if config.dry_run:
    print_info_dry("Would create a new GitHub release")
else:
    github.create_release(release_milestone, core_version_in_eclipse_pom)

#############################
print_step("Create new GitHub milestone...")
#############################
if config.dry_run:
    print_info_dry("Would create a new milestone")
else:
    new_mile_title = config.expected_milestone_name.replace(config.release_version, config.next_version)
    new_mile_description = ""
    try:
        config.repo.create_milestone(new_mile_title, "open", new_mile_description)
    except github.GithubException as e:
        print_info("Could not create milestone, does it already exists?")
        if config.debug:
            print(str(e))
    else:
        print_info("New milestone created!")

#############################
print_step("Merge master branch to "+config.branch_to_be_released+"...")
#############################
if config.dry_run:
    print_info_dry("Would merge from master to "+ config.branch_to_be_released)
else:
    try:
        head = git_repo.repo.get_branch("master")
        base = git_repo.repo.get_branch(config.branch_to_be_released)

        merge_to_devbranch = git_repo.repo.merge(base.name, head.commit.sha, "merge to dev_branch")
        print_info("Merged master into " + base.name)

    except Exception as ex:
        print_error("Something went wrong, please check if merge conflicts exist and solve them.")
        if config.debug:
            print(ex)
        if not prompt_yesno_question("If there were conflicts, you solved and committed, would you like to resume the script?"):
            git_repo.reset()
            sys.exit()

#############################
print_step("Set next release version...")
#############################
maven.add_remove_snapshot_version_in_pom(True,"setting snapshot version for next release")

#############################
print_step("Close GitHub release issue...")
#############################
if config.dry_run:
    print_info_dry("Would close GitHub release issue with no #"+ config.github_issue_no)
else:
    release_issue = github.repo.get_issue(int(config.github_issue_no))
    #will never find closed issues
    closing_comment = "Automatically processed.\n\nThe decisions taken by the developer and the context of the decisions throughout the script:\n\n"
    for message in report_messages:
        closing_comment = closing_comment + "* "+message+"\n"
    release_issue.create_comment(closing_comment)
    release_issue.edit(state="closed")
    print_info("Closed issue #"+ release_issue.number +": "+ release_issue.title)

print_info("Script has been executed successfully!")
