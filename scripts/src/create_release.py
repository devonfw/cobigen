import os
import sys
import git

from pprint import pprint
from tools.initialization import init_git_dependent_config, init_non_git_config
from git.exc import InvalidGitRepositoryError
from github.Milestone import Milestone

from tools.config import Config
from tools.github import GitHub
from tools.git_repo import GitRepo
from tools.validation import exit_if_not_executed_in_ide_environment, exit_if_origin_is_not_correct,\
    exit_if_working_copy_is_not_clean, check_running_in_bash
from tools.user_interface import log_step, log_error, prompt_yesno_question, log_info, log_info_dry, log_debug
from tools.maven import Maven

#############################
log_step("Initialization...")
#############################

try:
    git.cmd.Git(".")
except InvalidGitRepositoryError:
    log_error("Path is not a git repository. Please go to valid git repository!")
    sys.exit()

check_running_in_bash()
exit_if_working_copy_is_not_clean()
exit_if_not_executed_in_ide_environment()

config = Config()
init_non_git_config(config)

if(config.debug):
    log_debug("Current config:")
    pprint(vars(config))
    if not prompt_yesno_question("[DEBUG] Continue?"):
        sys.exit()

git_repo = GitRepo(config)
github = GitHub(config)
init_git_dependent_config(config, github)

exit_if_origin_is_not_correct(config)

if(config.debug):
    log_debug("Current config:")
    pprint(vars(config))
    if not prompt_yesno_question("[DEBUG] Continue?"):
        sys.exit()

maven = Maven(config, github)

input("Please close all eclipse instances as of potential race conditions on maven builds causing errors. Press any key if done...")
input("Please close SourceTree for Git performance reasons. Press any key if done...")

report_messages = []

#############################
log_step("Check for working CI build and tests...")
#############################
if not prompt_yesno_question("Are the tests on branch " + config.branch_to_be_released + " passing in CI?"):
    log_error("Please correct the build failures before releasing!")
    sys.exit()
else:
    report_messages.append("User confirmed that tests are running on CI and the build is not failing.")
    log_info("Build is reported to be successful.")

#############################
log_step("Search for GitHub milestone...")
#############################
milestone: Milestone = github.find_release_milestone()
if milestone:
    log_info("Milestone '"+milestone.title+"' found!")
else:
    log_error("Milestone not found! Searched for milestone with name '" + config.expected_milestone_name+"'. Aborting...")
    git_repo.reset()
    sys.exit()

#############################
log_step("Find or create the GitHub release issue...")
#############################
if not config.github_issue_no:
    issue_text = "This issue has been automatically created. It serves as a container for all release related commits"
    config.github_issue_no = github.create_issue("Release " + config.expected_milestone_name, body=issue_text, milestone=milestone)
    if not config.github_issue_no:
        log_error("Could not create issue! Aborting...")
        git_repo.reset()
        sys.exit()
    else:
        log_info('Successfully created issue #' + str(config.github_issue_no))
elif not github.exists_issue(config.github_issue_no):
    log_error("Issue with number #" + str(config.github_issue_no) + " not found! Aborting...")
    git_repo.reset()
    sys.exit()
log_info("Issue #" + str(config.github_issue_no) + " found.")

#############################
log_step("Navigate to branch " + config.branch_to_be_released + " and prepare workspace...")
#############################
git_repo.checkout(config.branch_to_be_released)
os.chdir(os.path.join(config.root_path, config.build_folder))
git_repo.update_and_clean()

#############################
log_step("Set the SNAPSHOT version...")
#############################
log_info("Set snapshot version of target release")
maven.add_remove_snapshot_version_in_pom(True, config.release_version)
log_info("Git add and commit...")
git_repo.add(["pom.xml"])
git_repo.commit("set release snapshot version")

if config.debug:
    prompt_yesno_question("[DEBUG] POM changes committed. Continue?")

#############################
log_step("Upgrade dependencies of SNAPSHOT versions and committing it...")
#############################
core_version_in_eclipse_pom = maven.upgrade_snapshot_dependencies()
git_repo.add(["pom.xml"])
git_repo.commit("upgrade SNAPSHOT dependencies")

#############################
log_step("Run integration tests...")
#############################
run_maven_process_and_handle_error("mvn clean integration-test - Pp2-build-mars, p2-build-stable")

#############################
log_step("Update wiki submodule...")
#############################
continue_run = True
if config.test_run:
    continue_run = prompt_yesno_question("[TEST] Would now update wiki submodule. Continue (yes) or skip (no)?")

if continue_run:
    git_repo.update_submodule(config.wiki_submodule_path)

#############################
log_step("Merging " + config.branch_to_be_released + " to master...")
#############################
if config.debug and not prompt_yesno_question("[DEBUG] Wiki docs have been committed. Next would be merging to master. Continue?"):
    git_repo.reset()
    sys.exit()

os.chdir(config.root_path)
git_repo.merge(config.branch_to_be_released, "master")

#############################
log_step("Validate merge commit...")
#############################
list_of_changed_files = git_repo.get_changed_files_of_last_commit()
is_pom_changed = False
for file_name in list_of_changed_files:
    if "pom.xml" in file_name:
        is_pom_changed = True
    if not file_name.startswith(config.build_folder):
        log_info(file_name + " does not starts with " + config.build_folder)
        if not prompt_yesno_question("Some Files are outside the folder "+config.build_folder+". Please check for odd file changes as this should not be the case in a normal scenario! Continue?"):
            git_repo.reset()
            sys.exit()
        report_messages.append("User has accepted to continue when found that some files were outside of build folder name")

if is_pom_changed:
    if not prompt_yesno_question("POM has been changed, please update dependency tracking wiki page! Continue?"):
        git_repo.reset()
        sys.exit()
    report_messages.append(
        "User has accepted to continue on found POM changes")

#############################
log_step("Set release version...")
#############################
maven.add_remove_snapshot_version_in_pom(False, "Set release version")

#############################
log_step("Deploy artifacts to nexus and update sites...")
#############################
if config.dry_run or config.test_run:
    log_info_dry("Would now deploy to maven central & updatesite. Skipping...")
else:
    if config.build_folder != "cobigen-eclipse":
        run_maven_process_and_handle_error("mvn clean package --update-snapshots bundle:bundle -Pp2-bundle -Dmaven.test.skip=true")
        run_maven_process_and_handle_error("mvn install bundle:bundle -Pp2-bundle p2:site -Dmaven.test.skip=true")
        run_maven_process_and_handle_error("mvn deploy -Pp2-upload-stable -Dmaven.test.skip=true -Dp2.upload=stable")
    else:
        run_maven_process_and_handle_error("mvn clean deploy -Pp2-build-stable,p2-upload-stable,p2-build-mars -Dp2.upload=stable")

    if not prompt_yesno_question("Please check installation of module from update site! Was the installation of the newly deployed bundle successful?"):
        git_repo.reset()
        sys.exit()

#############################
log_step("Create Tag...")
#############################
if config.dry_run:
    log_info_dry("Would create Git tag with name "+config.tag_name)
else:
    git_repo.create_tag_on_last_commit()
    git_repo.push()

#############################
log_step("Close GitHub Milestone...")
#############################
if config.dry_run:
    log_info_dry("Would close the milestone: " + milestone.title)
else:
    if (milestone.state == "closed"):
        log_info("Milestone '"+milestone.title + "' is already closed, please check.")
    else:
        milestone.edit(milestone.title, "closed", milestone.description)
        log_info("New status of Milestone '" + milestone.title + "' is: " + milestone.state)

#############################
log_step("Create new GitHub release...")
#############################
github.create_release(milestone, core_version_in_eclipse_pom)

#############################
log_step("Create new GitHub milestone...")
#############################
if config.dry_run:
    log_info_dry("Would create a new milestone")
else:
    if not github.create_next_release_milestone():
        log_info("Aborting...")
        git_repo.reset()
        sys.exit()

#############################
log_step("Merge master branch to "+config.branch_to_be_released+"...")
##############################
git_repo.merge("master", config.branch_to_be_released)

#############################
log_step("Set next release version...")
#############################
maven.add_remove_snapshot_version_in_pom(True, config.next_version)
git_repo.add(["**/pom.xml"])
git_repo.commit("Set next development version")
git_repo.push()

#############################
log_step("Close GitHub release issue...")
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


def run_maven_process_and_handle_error(command: str):
    returncode = maven.run_maven_process(command)

    if returncode == 1:
        log_error("Integration tests failed, please see create_release.py.log for logs located at current directory.")
        git_repo.reset()
        sys.exit()
