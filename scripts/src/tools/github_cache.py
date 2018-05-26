from github.Issue import Issue
from typing import List
from github.PaginatedList import PaginatedList


class GitHubCache:

    def __init__(self):
        self.issues: dict = {}
        self.milestones: PaginatedList
