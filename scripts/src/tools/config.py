import os
import sys
from tools.logger import log_error


class Config():

    def __init__(self):
        self.wiki_version_overview_page: str = "CobiGen.asciidoc"
        self.root_path: str

        self.oss: bool = False
        self.gpg_keyname: str

        self.dry_run: bool = False
        self.debug: bool = False
        self.test_run: bool = False
        self.cleanup_silently: bool = False

        self.git_username: str
        self.git_password: str
        self.git_token: str

        self.github_repo: str
        self.git_repo_name: str
        self.git_repo_org: str

        self.branch_to_be_released: str
        self.branch_core: str = "dev_core"
        self.branch_eclipseplugin: str = "dev_eclipseplugin"
        self.branch_mavenplugin: str = "dev_mavenplugin"
        self.branch_javaplugin: str = "dev_javaplugin"

        self.groupid_cobigen: str = "com.devonfw.cobigen"
        self.artifactid_core: str = "core"

        self.build_folder: str
        self.build_folder_abs: str
        self.cobigenwiki_title_name: str
        self.tag_name: str
        self.issue_label_name: str
        self.build_artifacts_root_search_path: str

        self.github_issue_no: int
        self.release_version: str
        self.next_version: str

        self.expected_milestone_name: str

        self.wiki_submodule_path: str

    def github_closed_milestone_url(self, milestone_number: int):
        return "https://github.com/" + self.github_repo + "/milestone/" + str(milestone_number)+"?closed=1"
