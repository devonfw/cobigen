class Config():

    def __init__(self):
        self.wiki_version_overview_page: str
        self.root_path: str

        self.dry_run: bool
        self.debug: bool
        self.test_run: bool

        self.git_username: str
        self.git_password: str
        self.git_token: str

        self.github_repo: str
        self.git_repo_name: str
        self.git_repo_org: str

        self.branch_to_be_released: str

        self.build_folder: str
        self.cobigenwiki_title_name: str 
        self.tag_name: str
        self.target_folder: str

        self.github_issue_no: int
        self.release_version: str
        self.next_version: str

        self.expected_milestone_name: str

        self.wiki_submodule_path: str

    def github_closed_milestone_url(self, milestone_number: int):
        return "https://github_repo.com/" + self.github_repo + "/milestone/" + str(milestone_number)+"?closed=1"
