from tools.user_interface import prompt_enter_value, print_error, print_info
import os
import sys
from tools.validation import is_valid_branch
import re
from tools.config import Config
from tools.github import GitHub

def ask_user_and_fill_config(config: Config, github: GitHub):
    config.wiki_version_overview_page = "CobiGen.asciidoc"
    config.root_path = os.path.normpath(os.path.join(os.path.realpath(__file__), ".."+os.sep+".."+os.sep+".."+os.sep+".."+os.sep))
    print_info("Executing release in path " + str(config.root_path))
    
    config.dry_run = False
    config.debug = False
    config.test_run = False
    
    config.git_username = ""
    config.git_password_or_token = ""
    
    config.git_repo = ""
    repo_pattern = re.compile('[a-zA-Z]+/[a-zA-Z]+')
    while(not repo_pattern.match(config.git_repo)):
        config.git_repo = prompt_enter_value("repository to be released (e.g. devonfw/tools-cobigen)")
    config.git_repo_name = config.git_repo.split(sep='/')[1]
    config.git_repo_org = config.git_repo.split(sep='/')[0]
    
    while(True):
        config.branch_to_be_released = prompt_enter_value("the name of the branch to be released")
        if(is_valid_branch(config)):
            break
        
    config.build_folder = __get_build_folder(config)
    config.cobigenwiki_title_name = __get_cobigenwiki_title_name(config)
    config.tag_name = __get_tag_name(config)
    config.target_folder = __get_target_folder(config)

    while(True):
        config.github_issue_no = prompt_enter_value("release issue number without # prefix in case you already created one or type 'new' to create an issue automatically")
        if(config.github_issue_no == 'new'):
            config.github_issue_no = '' # to be processed as falsely in the script later on (create one automatically)
            break
        elif(github.find_issue(config.github_issue_no)):
            print_info("Issue found remotely!")
            break

    config.release_version = ""
    version_pattern = re.compile('[0-9]\.[0-9]\.[0-9]')
    while(not version_pattern.match(config.release_version)):
        config.release_version = prompt_enter_value("release version number without 'v' in front")
        
    config.next_version = ""
    while(not version_pattern.match(config.next_version)):
        config.next_version = prompt_enter_value("next version number (after releasing) without 'v' in front")
    
    config.expected_milestone_name = config.tag_name[:-2] + "-v" + config.config.release_version
    
    config.wiki_submodule_path = os.path.abspath(os.path.join(config.root_path, "cobigen-documentation", config.git_repo_name() + ".wiki"))
    
    __process_params(config)
    
def __get_build_folder(config: Config):
    '''This Method will be responsible for getting package folder name based on branch name'''
    
    build_folder = {
        'dev_core': os.path.join('cobigen','cobigen-core-parent'),
        'dev_mavenplugin': 'cobigen-maven',
        'dev_eclipseplugin': 'cobigen-eclipse',
        'dev_javaplugin': os.path.join('cobigen','cobigen-javaplugin-parent'),
        'dev_xmlplugin': os.path.join('cobigen','cobigen-xmlplugin'),
        'dev_htmlmerger': os.path.join('cobigen','cobigen-htmlplugin'),
        'dev_openapiplugin': os.path.join('cobigen','cobigen-openapiplugin'),
        'dev_tsplugin': os.path.join('cobigen','cobigen-tsplugin'),
        'dev_textmerger': os.path.join('cobigen','cobigen-textmerger'),
        'dev_propertyplugin': os.path.join('cobigen','cobigen-propertyplugin'),
        'dev_jsonplugin': os.path.join('cobigen','cobigen-jsonplugin'),
        'dev_tempeng_freemarker': os.path.join('cobigen','cobigen-templateengines','cobigen-tempeng-freemarker'),
        'dev_tempeng_velocity': os.path.join('cobigen','cobigen-templateengines','cobigen-tempeng-velocity'),
    }
    
    val = build_folder.get(config.branch_to_be_released, "")
    if not val:
        print_error('Branch name unknown to script. Please edit function get_build_folder in scripts/**/config.py');
        sys.exit()
    return val
    
def __get_cobigenwiki_title_name(config):
    '''This Method is responsible for fetching wiki document description based on branch name 
    for the purpose of updating version in CobiGen.asciidoc'''
    
    wiki_description_name = {
        'dev_core': 'CobiGen',
        'dev_mavenplugin': 'CobiGen - Maven Build Plug-in',
        'dev_eclipseplugin': 'CobiGen - Eclipse Plug-in',
        'dev_javaplugin': 'Cobigen - Java Plug-in',
        'dev_xmlplugin': 'CobiGen - XML Plug-in',
        'dev_htmlmerger': 'CobiGen - HTML Plug-in',
        'dev_openapiplugin': 'CobiGen - Open API Plug-in',
        'dev_tsplugin': 'CobiGen - TypeScript Plug-in',
        'dev_textmerger': 'CobiGen - Text Merger',
        'dev_propertyplugin': 'CobiGen - Property Plug-in',
        'dev_jsonplugin': 'CobiGen - JSON Plug-in',
        'dev_tempeng_freemarker': 'CobiGen - FreeMaker Template Engine',
        'dev_tempeng_velocity': 'CobiGen - Velocity Template Engine',
    }
    
    val = wiki_description_name.get(config.branch_to_be_released, "")
    if not val:
        print_error('Branch name unknown to script. Please edit function get_cobigenwiki_title_name in scripts/**/config.py');
        sys.exit()
    return val
    
def __get_tag_name(config):
    '''This Method is responsible for fetching wiki document description based on branch name 
    for the purpose of updating version in CobiGen.asciidoc'''

    tag_name = {
        'dev_core': 'cobigen-core/v',
        'dev_mavenplugin': 'cobigen-maven/v',
        'dev_eclipseplugin': 'cobigen-eclipse/v',
        'dev_javaplugin': 'cobigen-javaplugin/v',
        'dev_xmlplugin': 'cobigen-xmlplugin/v',
        'dev_htmlmerger': 'cobigen-htmlplugin/v',
        'dev_openapiplugin': 'cobigen-openapiplugin/v',
        'dev_tsplugin': 'cobigen-tsplugin/v',
        'dev_textmerger': 'cobigen-textmerger/v',
        'dev_propertyplugin': 'cobigen-propertyplugin/v',
        'dev_jsonplugin': 'cobigen-jsonplugin/v',
        'dev_tempeng_freemarker': 'cobigen-tempeng-freemarker/v',
        'dev_tempeng_velocity': 'cobigen-tempeng-velocity/v',
    }
       
    val = tag_name.get(config.branch_to_be_released, "")     
    if not val:
        print_error('Branch name unknown to script. Please edit function get_tag_name in scripts/**/config.py');
        sys.exit()
    return val

def __get_target_folder(config):
    target_folders = {
        'dev_javaplugin': 'cobigen-javaplugin/target',
        'dev_eclipseplugin': 'cobigen-eclipse-updatesite/target'
    }
    
    return target_folders.get(config.branch_to_be_released, "target")

def __process_params(config):
    for o in sys.argv:
        if not o:
            print_info("For further informations run >py create_release.py --help<")
        elif o == "--dry-run":
            config.dry_run = True
            print_info("--dry-run: No changes will be made on the Git repo.")
        elif o == "--test":
            print_info("--test: Script runs on a different repo for testing purpose. Does not require any user interaction to speed up.")
            config.test_run = True
        elif o == "--debug":
            print_info("--debug: The script will require user interactions for each"
                   "step. It will access git without --dry-run.")
            config.debug = True
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
        