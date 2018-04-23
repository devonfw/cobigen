import os
import json
import requests
import scripts.settings

# Function creates an issue in git hub with title,milestone,body,labels passed
def make_github_issue(title,git_url,milestone=None,body=None, labels=None):
    '''Create an issue on github.com using the given parameters.'''
    url = 'https://'+init.git_username+':'+init.git_password+git_url+'issues'
    session = requests.Session()
    session.auth = (init.git_username, init.git_password)
    issue = {'title': title,
			 'milestone': milestone,
             'body': body,			 
             'labels': labels}
    response_object = session.post(url, json=issue)
    data=response_object.json()
    issue_number=data["number"]
    if response_object.status_code == 201:
        print ('Successfully created issue with title: '+title);
        return issue_number
    else:
        print ('Could not create new issue with title: '+title) 
        return
