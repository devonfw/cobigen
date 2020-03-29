import os
import sys
import logging
import yaml

from pprint import pprint
from github.Milestone import Milestone

from tools.config import Config
from tools.github import GitHub
from tools.git_repo import GitRepo
from tools.validation import exit_if_not_executed_in_ide_environment, exit_if_origin_is_not_correct, check_running_in_bash
from tools.user_interface import prompt_enter_value, prompt_yesno_question
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

#####################################################################
def __load_config_file(config : Config):
    def load_file(path):
        with open(path, 'r') as file:
            try:
                return yaml.safe_load(file)
            except yaml.YAMLError as exc:
                log_error(exc)

    config_dir = "config"
    non_git_config_path = os.path.join(config_dir,"non_git_config.yml")
    git_config_path = os.path.join(config_dir, "git_config.yml")
    if os.path.isfile(git_config_path) and os.path.isfile(git_config_path):
        msg = "[INFO ] We have found a configuration file from your previous execution," \
              " would you like to load it?"
        load_file_flag = prompt_yesno_question(msg)
        if load_file_flag:
            data = {**load_file(non_git_config_path), **load_file(git_config_path)}
            for attr in data:
                setattr(config,attr,data[attr])
            return True
    return False
#####################################################################

#####################################################################
def __store_config(config: Config):
    def config2dic(config, attrs):
        data = dict()
        for attr in attrs:
            if hasattr(config, attr):
                data[attr] = getattr(config, attr)
        return data

    def store_as_yaml(data,path,prefix=""):
        with open(path, 'w') as outfile:
            yaml.dump(data, outfile, default_flow_style=False)
        log_info("{}Git configuration has been stored in {}!".format(prefix,path))

    config_dir = "config"
    if not os.path.isdir(config_dir):
        os.makedirs(config_dir)
        log_info("Folder config has been created!")

    attrs = ["oss",
             "gpg_loaded",
             "gpg_keyname"]
    non_git_config_dic = config2dic(config, attrs)
    non_git_config_path = os.path.join(config_dir,"non_git_config.yml")
    store_as_yaml(non_git_config_dic, non_git_config_path, prefix="Non ")

    attrs = ["git_username",
             "two_factor_authentication",
             "branch_to_be_released",
             "release_version",
             "next_version",
             "github_issue_no"]
    git_config_dic = config2dic(config, attrs)
    git_config_path = os.path.join(config_dir, "git_config.yml")
    store_as_yaml(git_config_dic, git_config_path)

#####################################################################

#############################
log_step("Initialization...")
#############################

check_running_in_bash()
exit_if_not_executed_in_ide_environment()

config = Config()
loaded_config_flag = __load_config_file(config)
init_non_git_config(config)
__store_config(config)

git_repo = GitRepo(config)
git_repo.assure_clean_working_copy()

github = GitHub(config)
init_git_dependent_config(config, github, git_repo)
__store_config(config)
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
    log_info("Execute command '" + command + "'")
    returncode = maven.run_maven_process(execpath, command)

    if returncode == 1:
        log_error("Maven execution failed, please see create_release.py.log for logs located at current directory.")
        if prompt_yesno_question("Maven execution failed. Script is not able to recover from it by its own.\nCan you fix the problem right now? If so, would you like to retry the last maven execution and resume the script?"):
            run_maven_process_and_handle_error(command, execpath)
        else:
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
log_info("Pushing Upgrade SNAPSHOT dependencies.")
git_repo.push()

#############################
__log_step("Run integration tests...")
#############################
if not prompt_yesno_question("Do you want to run the tests? WARNING: Your tests must pass succesfully, only answer NO when you already have passed the tests in a previous execution."):
    run_maven_process_and_handle_error("mvn clean install -U -Dmaven.test.skip=true -Pp2-build-photon,p2-build-stable")
else:
    run_maven_process_and_handle_error("mvn clean install -U -Pp2-build-photon,p2-build-stable")

#############################
__log_step("Update documentation...")
#############################
continue_run = True
if config.test_run:
    continue_run = prompt_yesno_question("[TEST] Would now update documentation. Continue (yes) or skip (no)?")    

if continue_run:
    git_repo.update_documentation()
    git_repo.commit("update wiki docs")
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
log_info("TODO: if this step fails, it means you need to check that your " + config.branch_to_be_released + " or master needs to commit the latest changes")
# TODO: Sometimes the error happens when doing a checkout to master. With the checkout you are moving some files from the branch to release,
#  to master, and the script is not able to remove them.
os.chdir(config.root_path)
git_repo.merge(config.branch_to_be_released, "master")

#############################
__log_step("Validate merge commit...")
#############################
list_of_changed_files = git_repo.get_changed_files_of_last_commit()
is_pom_changed = False
for file_name in list_of_changed_files:
    file_name = file_name.replace('/', os.sep)
    if not file_name.startswith(config.build_folder):
        if not prompt_yesno_question("Changed file " + file_name + " is outside the component folder path "+config.build_folder+".\nThis should not be the normal case! Please check these changes are necessary. Continue?"):
            git_repo.reset()
            sys.exit()
        report_messages.append("User has accepted to continue when found that some files were outside of build folder name")
        if file_name.endswith("pom.xml"):
            is_pom_changed = True

if is_pom_changed:
    if not prompt_yesno_question("Any pom.xml has been changed, please check for new and upgraded dependencies and update dependency tracking wiki page accordingly! Continue?"):
        git_repo.reset()
        sys.exit()
    report_messages.append("User has accepted to continue on found pom.xml changes")

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

def __deploy_m2_as_p2(oss: bool, execpath: str=config.build_folder_abs):
    activation_str = ""
    if oss:
        activation_str = "-Poss -Dgpg.keyname="+config.gpg_keyname + " -Dgpg.executable="+config.gpg_executable
    run_maven_process_and_handle_error("mvn clean package -U bundle:bundle -Pp2-bundle -Dmaven.test.skip=true", execpath=execpath)
    run_maven_process_and_handle_error("mvn install -U bundle:bundle -Pp2-bundle p2:site -Dmaven.test.skip=true", execpath=execpath)
    run_maven_process_and_handle_error("mvn deploy -U "+activation_str+" -Dmaven.test.skip=true -Dbintray.repository=cobigen.p2", execpath=execpath)


def __deploy_m2_only(oss: bool, execpath: str=config.build_folder_abs):
    activation_str = ""
    if oss:
        activation_str = "-Poss -Dgpg.keyname="+config.gpg_keyname + " -Dgpg.executable="+config.gpg_executable    
    run_maven_process_and_handle_error("mvn clean -Dmaven.test.skip=true deploy -U "+activation_str, execpath=execpath)


def __deploy_p2(oss: bool, execpath: str=config.build_folder_abs):
    run_maven_process_and_handle_error("mvn clean -Dmaven.test.skip=true deploy -U -Pp2-build-stable,p2-build-photon -Dbintray.repository=cobigen.p2", execpath=execpath)


if config.dry_run or config.test_run:
    log_info_dry("Would now deploy to maven central/OSS & updatesite. Skipping...")
else:
    if config.branch_to_be_released not in [config.branch_eclipseplugin, config.branch_mavenplugin, config.branch_core, config.branch_javaplugin, config.branch_openapiplugin]:
        __deploy_m2_only(config.oss)
        __deploy_m2_as_p2(config.oss)
    elif config.branch_to_be_released == config.branch_javaplugin:
        __deploy_m2_only(config.oss, os.path.join(config.build_folder_abs, "cobigen-javaplugin-model"))
        __deploy_m2_as_p2(config.oss, os.path.join(config.build_folder_abs, "cobigen-javaplugin"))
    elif config.branch_to_be_released == config.branch_openapiplugin:
        __deploy_m2_only(config.oss, os.path.join(config.build_folder_abs, "cobigen-openapiplugin-model"))
        __deploy_m2_as_p2(config.oss, os.path.join(config.build_folder_abs, "cobigen-openapiplugin"))
    elif config.branch_to_be_released == config.branch_eclipseplugin:
        __deploy_p2(config.oss)
    else:  # core + maven
        __deploy_m2_only(config.oss)

    if config.branch_to_be_released != config.branch_core and config.branch_to_be_released != config.branch_mavenplugin and not prompt_yesno_question("Please check installation of module from update site! Was the installation of the newly deployed bundle successful?"):
        log_info("Aborting release...")
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
        if milestone.description is None:
            #TODO: milestone.description is sometimes None
            milestone.edit(milestone.title, "closed", "Void description error")
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
        log_info("Failed to create the next release milestone (is it already created?), please create it manually...")
        if not prompt_yesno_question("Do you still want to continue the execution?"):
            git_repo.reset()
            sys.exit()

#############################
__log_step("Merge master branch to "+config.branch_to_be_released+"...")
##############################
git_repo.merge("master", config.branch_to_be_released)
git_repo.push(True)

#############################
__log_step("Set next release version...")
#############################
git_repo.checkout(config.branch_to_be_released)
changed_files = maven.set_version(config.next_version + "-SNAPSHOT")
git_repo.add(changed_files)
git_repo.commit("Set next development version")
git_repo.push(True)

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
    log_info("Closed issue #" + str(release_issue.number) + ": " + release_issue.title)

log_info("Congratz! A new release! Script executed successfully!")
