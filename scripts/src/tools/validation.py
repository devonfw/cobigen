import git
import os
import sys
from tools.user_interface import print_error, print_info
import subprocess

def exit_if_not_executed_in_ide_environment():
    '''This part checks if environment variables is set or not.'''
    if not ("ICSD_FILESERVER_USER" and "ICSD_FILESERVER_PASSWD") in os.environ:
        print_error("Please use CobiGen IDE initialized console and set the variables in the variables-customized.bat.")
        sys.exit()

def is_valid_branch(config) -> bool:
    '''This Method is responsible for checking branches in repository with branch entered by user'''
    
    if git.cmd.Git(".").execute("git ls-remote --heads origin "+config.branch_to_be_released+" | wc -l") == "":
        print_info("Branch is not known remotely.")
        is_branch_valid=False
    else:
        print_info("Branch is valid.")
        is_branch_valid=True    
    return is_branch_valid

def exit_if_origin_is_not_correct(config):
    remote_origin=git.cmd.Git(".").execute("git remote -v")
    if config.git_repo not in remote_origin:
        print_error("Origin of the current repository is not '" + config.git_repo + "', Please go to correct directory.");
        sys.exit()
        
def exit_if_working_copy_is_not_clean():
    repo = git.cmd.Git(".")
    if repo.execute("git diff --shortstat") != "" or repo.execute("git status --porcelain") != "":
        print_error("Working copy is not clean");
        sys.exit()
    
def check_running_in_bash():
    try:
        FNULL = open(os.devnull, 'w')
        subprocess.call("ls", stdout=FNULL)
    except:
        print_error("Please run the script in a linux like environment (e.g. git bash)")
        sys.exit()