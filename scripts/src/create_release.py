import os
import sys
import logging

from pprint import pprint
from github.Milestone import Milestone

from tools.config import Config
from tools.github import GitHub
from tools.git_repo import GitRepo
from tools.validation import exit_if_not_executed_in_ide_environment, exit_if_origin_is_not_correct, check_running_in_bash
from tools.user_interface import prompt_yesno_question
from tools.maven import Maven
from tools.initialization import init_git_dependent_config, init_non_git_config
from tools.logger import log_step, log_debug, log_error, log_info, log_info_dry

# This log level is a global log level of the logging framework.
# Has to be set within the script as it starts logging everything also from used frameworks
# which is too much for testing purposes only
logging.getLogger('').setLevel(logging.INFO)

#####################################################################


def __log_step(message: str):
    log_step(message)

    global git_repo
    global config
    if config.debug and not prompt_yesno_question("[DEBUG] Continue with next step '"+message+"'?"):
        if git_repo:
            git_repo.reset()
            sys.exit()
#####################################################################


#############################
log_step("Initialization...")
#############################

check_running_in_bash()
exit_if_not_executed_in_ide_environment()

config = Config()
init_non_git_config(config)

git_repo = GitRepo(config)
git_repo.assure_clean_working_copy()

github = GitHub(config)
init_git_dependent_config(config, github, git_repo)

exit_if_origin_is_not_correct(config)

if(config.debug):
    log_debug("Current config:")
    pprint(vars(config))
    if not prompt_yesno_question("[DEBUG] Continue?"):
        sys.exit()

maven = Maven(config, github)

input("\nPlease close all eclipse instances as of potential race conditions on maven builds causing errors. Press return if done...")
input("\nPlease close SourceTree for Git performance reasons. Press return if done...\n")

report_messages = []

#####################################################################


def run_maven_process_and_handle_error(command: str, execpath: str=config.build_folder_abs):
    returncode = maven.run_maven_process(execpath, command)

    if returncode == 1:
        log_error("Maven execution failed, please see create_release.py.log for logs located at current directory.")
        git_repo.reset()
        sys.exit()
#####################################################################


#############################
__log_step("Check for working CI build and tests...")
#############################
if not prompt_yesno_question("Are the tests on branch " + config.branch_to_be_released + " passing in CI?"):
    log_error("Please correct the build failures before releasing!")
    sys.exit()
else:
    report_messages.append("User confirmed that tests are running on CI and the build is not failing.")
    log_info("Build is reported to be successful.")

#############################
__log_step("Navigate to branch " + config.branch_to_be_released + " and prepare workspace...")
#############################
git_repo.checkout(config.branch_to_be_released)

#############################
__log_step("Set the SNAPSHOT version...")
#############################
log_info("Set SNAPSHOT version of target release")
changed_files = maven.set_version(config.release_version + "-SNAPSHOT")
log_info("Git add and commit...")
git_repo.add(changed_files)
git_repo.commit("set release snapshot version")

#############################
__log_step("Upgrade dependencies of SNAPSHOT versions and committing it...")
#############################
(core_version_in_eclipse_pom, changed_files) = maven.upgrade_snapshot_dependencies()
git_repo.add(changed_files)
git_repo.commit("upgrade SNAPSHOT dependencies")

#############################
__log_step("Run integration tests...")
#############################
run_maven_process_and_handle_error("mvn clean integration-test -Pp2-build-mars,p2-build-stable")

#############################
__log_step("Update wiki submodule...")
#############################
continue_run = True
if config.test_run:
    continue_run = prompt_yesno_question("[TEST] Would now update wiki submodule. Continue (yes) or skip (no)?")

if continue_run:
    git_repo.update_submodule(config.wiki_submodule_path)

git_repo.push()

#############################
__log_step("Check for working CI build and tests...")
#############################
if not prompt_yesno_question("Wait until the build of the last push ran through. Are the tests on branch " + config.branch_to_be_released + " passing in CI?"):
    log_error("Please correct the build failures before releasing!")
    sys.exit()
else:
    report_messages.append("User confirmed that tests are running on CI and the build is not failing after pushing release changes to development branch.")
    log_info("Build is reported to be successful.")

#############################
__log_step("Merging " + config.branch_to_be_released + " to master...")
#############################
os.chdir(config.root_path)
git_repo.merge(config.branch_to_be_released, "master")

#############################
__log_step("Validate merge commit...")
#############################
list_of_changed_files = git_repo.get_changed_files_of_last_commit()
is_pom_changed = False
for file_name in list_of_changed_files:
    if "pom.xml" in file_name:
        is_pom_changed = True
    if not file_name.startswith(config.build_folder.replace(os.sep, '/')):
        log_info(file_name + " does not starts with " + config.build_folder)
        if not prompt_yesno_question("Some Files are outside the folder "+config.build_folder+". Please check for odd file changes as this should not be the case in a normal scenario! Continue?"):
            git_repo.reset()
            sys.exit()
        report_messages.append("User has accepted to continue when found that some files were outside of build folder name")

if is_pom_changed:
    if not prompt_yesno_question("POM has been changed, please update dependency tracking wiki page! Continue?"):
        git_repo.reset()
        sys.exit()
    report_messages.append("User has accepted to continue on found POM changes")

log_info("Validation finished.")

#############################
__log_step("Set release version...")
#############################
changed_files = maven.set_version(config.release_version)
git_repo.add(changed_files)
git_repo.commit("Set release version")

#############################
__log_step("Deploy artifacts to nexus and update sites...")
#############################
if config.dry_run or config.test_run:
    log_info_dry("Would now deploy to maven central & updatesite. Skipping...")
else:
    if config.branch_to_be_released not in ["dev_eclipseplugin", "dev_mavenplugin", "dev_core"]:
        run_maven_process_and_handle_error("mvn clean package bundle:bundle -Pp2-bundle -Dmaven.test.skip=true")
        run_maven_process_and_handle_error("mvn install bundle:bundle -Pp2-bundle p2:site -Dmaven.test.skip=true")
        run_maven_process_and_handle_error("mvn deploy -Dmaven.test.skip=true -Dp2.upload=stable")
    elif config.branch_to_be_released == "dev_eclipseplugin":
        run_maven_process_and_handle_error("mvn clean -Dmaven.test.skip=true deploy -Pp2-build-stable,p2-build-mars -Dp2.upload=stable")
    else:  # core + maven
        run_maven_process_and_handle_error("mvn clean -Dmaven.test.skip=true deploy")

    if not prompt_yesno_question("Please check installation of module from update site! Was the installation of the newly deployed bundle successful?"):
        git_repo.reset()
        sys.exit()

#############################
__log_step("Create Tag...")
#############################
if config.dry_run:
    log_info_dry("Would create Git tag with name "+config.tag_name)
else:
    git_repo.create_tag_on_last_commit()
    git_repo.push()

#############################
__log_step("Close GitHub Milestone...")
#############################
milestone: Milestone = github.find_release_milestone()
if config.dry_run:
    log_info_dry("Would close the milestone: " + milestone.title)
else:
    if (milestone.state == "closed"):
        log_info("Milestone '"+milestone.title + "' is already closed, please check.")
    else:
        milestone.edit(milestone.title, "closed", milestone.description)
        log_info("New status of Milestone '" + milestone.title + "' is: " + milestone.state)

#############################
__log_step("Create new GitHub release...")
#############################
github.create_release(milestone, core_version_in_eclipse_pom)

#############################
__log_step("Create new GitHub milestone...")
#############################
if config.dry_run:
    log_info_dry("Would create a new milestone")
else:
    if not github.create_next_release_milestone():
        log_info("Aborting...")
        git_repo.reset()
        sys.exit()

#############################
__log_step("Merge master branch to "+config.branch_to_be_released+"...")
##############################
git_repo.merge("master", config.branch_to_be_released)

#############################
__log_step("Set next release version...")
#############################
changed_files = maven.set_version(config.next_version + "-SNAPSHOT")
git_repo.add(changed_files)
git_repo.commit("Set next development version")
git_repo.push()

#############################
__log_step("Close GitHub release issue...")
#############################
if config.dry_run:
    log_info_dry("Would close GitHub release issue with no #" + str(config.github_issue_no))
else:
    release_issue = github.find_issue(config.github_issue_no)
    # will never find closed issues
    closing_comment = "Automatically processed.\n\nThe decisions taken by the developer and the context of the decisions throughout the script:\n\n"
    for message in report_messages:
        closing_comment = closing_comment + "* "+message+"\n"
    release_issue.create_comment(closing_comment)
    release_issue.edit(state="closed")
    log_info("Closed issue #" + release_issue.number + ": " + release_issue.title)

log_info("Script has been executed successfully!")
