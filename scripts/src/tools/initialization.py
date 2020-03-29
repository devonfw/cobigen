import getopt
import os
import re
import sys

from git.cmd import Git
from git.exc import InvalidGitRepositoryError

from tools.config import Config
from tools.git_repo import GitRepo
from tools.github import GitHub
from tools.logger import log_info, log_error
from tools.user_interface import prompt_enter_value, prompt_yesno_question
from tools.validation import is_valid_branch


def init_non_git_config(config: Config):
    __process_params(config)
    def __check_path(path):
        if not os.path.exists(path):
            log_error("Path does not exist.")
        if not os.path.isdir(path):
            log_error("Path is not a directory.")
        return os.path.exists(path) & os.path.isdir(path)

    def __set_path(config: Config, attr : str):
        msg = {
            'root_path' : "path of the repository to work on",
        }
        while True:
            if not hasattr(config, attr) or not getattr(config, attr):
                path = prompt_enter_value(msg[attr])
            else:
                path = getattr(config, attr)  # set by script param

            if not __check_path(path):
                setattr(config, attr, "")
                continue

            if (attr == "root_path") & (os.path.realpath(__file__).startswith(os.path.abspath(path))):
                log_error(
                    "Please copy and run the create release script in another place outside of the repository and execute again.")
                sys.exit()

            try:
                Git(path)
            except InvalidGitRepositoryError:
                log_error("Path is not a git repository.")

            setattr(config,attr,path)

            info = {
                "root_path": "Executing release in path '",
            }
            log_info(info[attr] + str(getattr(config,attr)) + "'")
            break

    __set_path(config,"root_path")

    if not hasattr(config, 'github_repo'):
        config.github_repo = ""
    repo_pattern = re.compile(r'[a-zA-Z]+/[a-zA-Z]+')
    while(not repo_pattern.match(config.github_repo)):
        if config.github_repo:
            log_error("'" + config.github_repo + "' is not a valid GitHub repository name.")
        config.github_repo = prompt_enter_value("repository to be released (e.g. devonfw/cobigen)")
    log_info("Releasing against GitHub repository '"+config.github_repo+"'")
    config.git_repo_name = config.github_repo.split(sep='/')[1]
    config.git_repo_org = config.github_repo.split(sep='/')[0]


    if not config.oss:
        config.oss = prompt_yesno_question("Should the release been published to maven central as open source?")
    if config.oss:
        if not hasattr(config, "gpg_keyname"):
            config.gpg_keyname = prompt_enter_value("""Please provide your gpg.keyname for build artifact signing.
If you are unsure about this, please stop here and clarify, whether
  * you created a pgp key and
  * published it!
gpg.keyname = """)

        if not config.gpg_loaded:
            config.gpg_loaded = prompt_yesno_question("Make sure the gpg key '" + config.gpg_keyname + "' is loaded by tools like Kleopatra before continuing! Continue?")
            if not config.gpg_loaded:
                sys.exit()

        #Check whether the user has gpg2 installed
        if is_tool("gpg2"):
            if not hasattr(config, "gpg_executable"):
                log_info("gpg2 installation found")
                config.gpg_executable = "gpg2"
        elif is_tool("gpg"):
            if not hasattr(config, "gpg_executable"):
                log_info("gpg installation found")
                config.gpg_executable = "gpg"
        else:
            log_error("gpg2 nor gpg are installed. Please install them on your computer (system path) or either use command -Dgpg.executable='gpg2'")

def is_tool(name):
    """Check whether `name` is on PATH and marked as executable."""
    from shutil import which

    return which(name) is not None

def init_git_dependent_config(config: Config, github: GitHub, git_repo: GitRepo):

    if not hasattr(config,"branch_to_be_released"):
        while(True):
            config.branch_to_be_released = prompt_enter_value("the name of the branch to be released")
            if(is_valid_branch(config)):
                break
    else:
        log_info("Branch to be released: {}".format(config.branch_to_be_released))

    version_pattern = re.compile(r'[0-9]\.[0-9]\.[0-9]')
    if not hasattr(config,"release_version"):
        config.release_version = ""
        while not version_pattern.match(config.release_version):
            config.release_version = prompt_enter_value("release version number without 'v' in front")
    else:
        log_info("Release version: {}".format(config.release_version))

    if not hasattr(config, "next_version"):
        config.next_version = ""
        while not version_pattern.match(config.next_version):
            config.next_version = prompt_enter_value("next version number (after releasing) without 'v' in front")
    else:
        log_info("Next version: {}".format(config.next_version))

    config.build_folder = __get_build_folder(config)
    config.build_folder_abs = os.path.join(config.root_path, config.build_folder)
    config.build_artifacts_root_search_path = __get_build_artifacts_root_search_path(config)
    config.cobigenwiki_title_name = __get_cobigenwiki_title_name(config)
    config.tag_name = __get_tag_name(config) + config.release_version
    config.tag_core_name = __get_tag_name_specific_branch(config, config.branch_core)

    if git_repo.exists_tag(config.tag_name):
        log_error("Git tag " + config.tag_name + " already exists. Maybe you entered the wrong release version? Please fix the problem and try again.")
        sys.exit()

    config.issue_label_name = config.tag_name[:-7]

    config.expected_milestone_name = config.tag_name[:-7] + "-v" + config.release_version
    config.expected_core_milestone_name = config.tag_core_name[:-2] + "-v"
    milestone = github.find_release_milestone()
    if milestone:
        log_info("Milestone '"+milestone.title+"' found!")
    else:
        log_error("Milestone not found! Searched for milestone with name '" + config.expected_milestone_name+"'. Aborting...")
        sys.exit()
    if not hasattr(config,"github_issue_no"):
        while(True):
            github_issue_no: str = prompt_enter_value(
                "release issue number without # prefix in case you already created one or type 'new' to create an issue automatically")
            if github_issue_no == 'new':
                issue_text = "This issue has been automatically created. It serves as a container for all release related commits"
                config.github_issue_no = github.create_issue("Release " + config.expected_milestone_name, body=issue_text, milestone=milestone)
                if not config.github_issue_no:
                    log_error("Could not create issue! Aborting...")
                    sys.exit()
                else:
                    log_info('Successfully created issue #' + str(github_issue_no))
                break
            else:
                try:
                    if github.find_issue(int(github_issue_no)):
                        config.github_issue_no = int(github_issue_no)
                        log_info("Issue #" + str(config.github_issue_no) + " found remotely!")
                        break
                    else:
                        log_error("Issue with number #" + str(config.github_issue_no) + " not found! Typo?")
                except ValueError:
                    log_error("Please enter a number.")
    else:
        log_info("Github issue no: {}".format(config.github_issue_no))


def __get_build_folder(config: Config):
    build_folder = {
        config.branch_core: os.path.join('cobigen', 'cobigen-core-parent'),
        config.branch_mavenplugin: 'cobigen-maven',
        config.branch_eclipseplugin: 'cobigen-eclipse',
        config.branch_javaplugin: os.path.join('cobigen', 'cobigen-javaplugin-parent'),
        'dev_xmlplugin': os.path.join('cobigen', 'cobigen-xmlplugin'),
        'dev_htmlmerger': os.path.join('cobigen', 'cobigen-htmlplugin'),
        config.branch_openapiplugin: os.path.join('cobigen', 'cobigen-openapiplugin-parent'),
        'dev_tsplugin': os.path.join('cobigen', 'cobigen-tsplugin'),
        'dev_textmerger': os.path.join('cobigen', 'cobigen-textmerger'),
        'dev_propertyplugin': os.path.join('cobigen', 'cobigen-propertyplugin'),
        'dev_jsonplugin': os.path.join('cobigen', 'cobigen-jsonplugin'),
        'dev_tempeng_freemarker': os.path.join('cobigen', 'cobigen-templateengines', 'cobigen-tempeng-freemarker'),
        'dev_tempeng_velocity': os.path.join('cobigen', 'cobigen-templateengines', 'cobigen-tempeng-velocity'),
    }

    val = build_folder.get(config.branch_to_be_released, "")
    if not val:
        log_error('Branch name unknown to script. Please edit function get_build_folder in scripts/**/__config.py')
        sys.exit()
    return val


def __get_cobigenwiki_title_name(config: Config):
    wiki_description_name = {
        config.branch_core: 'CobiGen',
        config.branch_mavenplugin: 'CobiGen - Maven Build Plug-in',
        config.branch_eclipseplugin: 'CobiGen - Eclipse Plug-in',
        config.branch_javaplugin: 'CobiGen - Java Plug-in',
        'dev_xmlplugin': 'CobiGen - XML Plug-in',
        'dev_htmlmerger': 'CobiGen - HTML Plug-in',
        config.branch_openapiplugin: 'CobiGen - Open API Plug-in',
        'dev_tsplugin': 'CobiGen - TypeScript Plug-in',
        'dev_textmerger': 'CobiGen - Text Merger',
        'dev_propertyplugin': 'CobiGen - Property Plug-in',
        'dev_jsonplugin': 'CobiGen - JSON Plug-in',
        'dev_tempeng_freemarker': 'CobiGen - FreeMaker Template Engine',
        'dev_tempeng_velocity': 'CobiGen - Velocity Template Engine',
    }

    val = wiki_description_name.get(config.branch_to_be_released, "")
    if not val:
        log_error('Branch name unknown to script. Please edit function get_cobigenwiki_title_name in scripts/**/__config.py')
        sys.exit()
    return val


def __get_tag_name(config: Config):
    tag_name = {
        config.branch_core: 'cobigen-core/v',
        config.branch_mavenplugin: 'cobigen-maven/v',
        config.branch_eclipseplugin: 'cobigen-eclipse/v',
        config.branch_javaplugin: 'cobigen-javaplugin/v',
        'dev_xmlplugin': 'cobigen-xmlplugin/v',
        'dev_htmlmerger': 'cobigen-htmlplugin/v',
        config.branch_openapiplugin: 'cobigen-openapiplugin/v',
        'dev_tsplugin': 'cobigen-tsplugin/v',
        'dev_textmerger': 'cobigen-textmerger/v',
        'dev_propertyplugin': 'cobigen-propertyplugin/v',
        'dev_jsonplugin': 'cobigen-jsonplugin/v',
        'dev_tempeng_freemarker': 'cobigen-tempeng-freemarker/v',
        'dev_tempeng_velocity': 'cobigen-tempeng-velocity/v',
    }

    val = tag_name.get(config.branch_to_be_released, "")
    if not val:
        log_error('Branch name unknown to script. Please edit function get_tag_name in scripts/**/__config.py')
        sys.exit()
    return val

def __get_tag_name_specific_branch(config: Config, branch_to_get_tag: str):
    tag_name = {
        config.branch_core: 'cobigen-core/v',
        config.branch_mavenplugin: 'cobigen-maven/v',
        config.branch_eclipseplugin: 'cobigen-eclipse/v',
        config.branch_javaplugin: 'cobigen-javaplugin/v',
        'dev_xmlplugin': 'cobigen-xmlplugin/v',
        'dev_htmlmerger': 'cobigen-htmlplugin/v',
        config.branch_openapiplugin: 'cobigen-openapiplugin/v',
        'dev_tsplugin': 'cobigen-tsplugin/v',
        'dev_textmerger': 'cobigen-textmerger/v',
        'dev_propertyplugin': 'cobigen-propertyplugin/v',
        'dev_jsonplugin': 'cobigen-jsonplugin/v',
        'dev_tempeng_freemarker': 'cobigen-tempeng-freemarker/v',
        'dev_tempeng_velocity': 'cobigen-tempeng-velocity/v',
    }

    val = tag_name.get(branch_to_get_tag, "")
    if not val:
        log_error('Branch name unknown to script. Please edit function get_tag_name in scripts/**/__config.py')
        sys.exit()
    return val

def __get_wiki_name_specific_branch(config: Config, branch_to_get_wikiname: str):
    wiki_name = {
        config.branch_core: 'cobigen-core',
        config.branch_mavenplugin: 'cobigen-maven',
        config.branch_eclipseplugin: 'cobigen-eclipse',
        config.branch_javaplugin: 'cobigen-javaplugin',
        'dev_xmlplugin': 'cobigen-xmlplugin',
        'dev_htmlmerger': 'cobigen-htmlplugin',
        config.branch_openapiplugin: 'cobigen-openapiplugin',
        'dev_tsplugin': 'cobigen-tsplugin',
        'dev_textmerger': 'cobigen-textmerger',
        'dev_propertyplugin': 'cobigen-propertyplugin',
        'dev_jsonplugin': 'cobigen-jsonplugin',
        'dev_tempeng_freemarker': 'cobigen-tempeng-freemarker',
        'dev_tempeng_velocity': 'cobigen-tempeng-velocity',
        'master': 'master-cobigen',
    }
    if branch_to_get_wikiname not in wiki_name:
        log_error('Branch name unknown to script. Please edit function get_tag_name in scripts/**/__config.py')
        sys.exit()
    return wiki_name[branch_to_get_wikiname]


def __get_build_artifacts_root_search_path(config: Config):
    target_folders = {
        config.branch_core: '',  # search for target folders in every submodule
        config.branch_javaplugin: '',  # search for target folders in every submodule
        config.branch_openapiplugin: '',  # search for target folders in every submodule
        config.branch_mavenplugin: '',  # search for target folders in every submodule
        config.branch_eclipseplugin: 'cobigen-eclipse-updatesite/target'
    }

    return target_folders.get(config.branch_to_be_released, "target")


def __process_params(config: Config):
    try:
        opts, args = getopt.getopt(sys.argv[1:], "cdg:hk:r:ty", ["cleanup-silently", "debug",
                                                                 "github-repo-id=", "help", "gpg-key=", "local-repo=", "test", "dry-run"])
    except getopt.GetoptError:
        __print_cmd_help()
        sys.exit(2)

    for opt, arg in opts:
        if opt in ("-c", "--cleanup-silently"):
            log_info("[ARGS] --cleanup-silently: Will silently reset/clean your working copy automatically. You will not be asked anymore. Use with caution!")
            config.cleanup_silently = True
        elif opt in ("-d", "--debug"):
            log_info("[ARGS] --debug: The script will require user interactions for each step.")
            config.debug = True
        elif opt in ("-g", "--github-repo-id"):
            log_info("[ARGS] GitHub repository to release against is set to " + arg)
            config.github_repo = arg
        elif opt in ("-h", "--help"):
            __print_cmd_help()
            sys.exit(0)
        elif opt in ("-k", "--gpg-key"):
            log_info("[ARGS] GPG key for code signing is set to " + arg)
            config.gpg_keyname = arg
        elif opt in ("-r", "--local-repo"):
            log_info("[ARGS] local repository set to " + arg)
            config.root_path = arg
        elif opt in ("-t", "--test"):
            log_info("[ARGS] --test: Script runs on a different repo for testing purpose. Does not require any user interaction to speed up.")
            config.test_run = True
        elif opt in ("-y", "--dry-run"):
            config.dry_run = True
            log_info("[ARGS] --dry-run: No changes will be made on the Git repo.")


def __print_cmd_help():
    log_info("""

This script automates the deployment of CobiGen modules.
[WARNING]: The script will access and change the Github repository. Do not use it unless you want to deploy modules.

Options:
  -c / --cleanup-silently [CAUTION] Will silently reset/clean your working copy automatically.
                          This will also delete non-tracked files from your local file system!
                          You will not be asked anymore!
  -d / --debug:           Script stops after each automatic step and asks the user to continue.
  -g / --github-repo-id   GitHub repository name to be released
  -h / --help:            Provides a short help about the intention and possible options.
  -k / --gpg-key          GPG key for code signing (for OSS release only)
  -r / --local-repo       Local repository clone to work on for the release
  -t / --test:            Script runs on a different repo for testing purpose. It also uses predefined
                          names and variables to shorten up the process.
  -y / --dry-run:         Will prevent from pushing to the remote repository, changing anything on GitHub
                          Issues/Milestones etc.
    """)
