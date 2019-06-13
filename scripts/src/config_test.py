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

import yaml

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
#exit_if_not_executed_in_ide_environment()

config = Config()

loaded_config_flag = __load_config_file(config)


init_non_git_config(config)

git_repo = GitRepo(config)
#git_repo.assure_clean_working_copy()

github = GitHub(config)

init_git_dependent_config(config, github, git_repo)
__store_config(config)
