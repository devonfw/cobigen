import os
import json
import requests

def make_github_issue(title,git_username,git_password,milestone=None,body=None, labels=None):
    '''Create an issue on github.com using the given parameters.'''
    # Our url to create issues via POST
    url = 'https://'+git_username+':'+git_password+'@api.github.com/repos/krashah/practice/issues'
    # Create an authenticated session to create the issue
    session = requests.Session()
    session.auth = (git_username, git_password)
    # Create our issue
    issue = {'title': title,
			 'milestone': milestone,
             'body': body,			 
             'labels': labels}
    # Add the issue to our repository
    r = session.post(url, json=issue)
    data=r.json()
    issueNumber=data["number"]
    if r.status_code == 201:
        print ('Successfully created Issue: '+title);
        return issueNumber
    else:
        print ('Could not create Issue: '+title) 
        print ('Response:', r.content)
        return
