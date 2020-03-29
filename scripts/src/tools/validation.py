import os
import subprocess
import sys

import git

from tools.config import Config
from tools.logger import log_error, log_info


def exit_if_not_executed_in_ide_environment():
    '''This part checks if environment variables is set or not.'''
    if not ("OSSRH_USER" and "OSSRH_PASSWD" and "GPG_SIGNING_PASSWD" and "BINTRAY_USER" and "BINTRAY_TOKEN") in os.environ:
        log_error("Please use CobiGen IDE initialized console and set the variables OSSRH_USER, OSSRH_PASSWD, GPG_SIGNING_PASSWD, BINTRAY_USER, and BINTRAY_TOKEN in the variables-customized.bat.")
        sys.exit()


def is_valid_branch(config: Config) -> bool:
    '''This Method is responsible for checking branches in repository with branch entered by user'''

    if git.cmd.Git(config.root_path).execute(
        ["git", "ls-remote", "--heads", "origin", config.branch_to_be_released, "|", "wc", "-l"]) == "":
        log_info("Branch is not known remotely.")
        is_branch_valid = False
    else:
        log_info("Branch is valid.")
        is_branch_valid = True
    return is_branch_valid


def exit_if_origin_is_not_correct(config: Config):
    remote_origin = git.cmd.Git(config.root_path).execute("git remote -v".split(" "))
    if config.github_repo not in remote_origin:
        log_error("Origin of the current repository is not '" + config.github_repo + "', Please go to correct directory.")
        sys.exit()


def check_running_in_bash():
    try:
        FNULL = open(os.devnull, 'w')
        subprocess.call("ls", stdout=FNULL)
    except:
        log_error("Please run the script in a linux like environment (e.g. git bash)")
        sys.exit()
