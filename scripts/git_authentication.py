import requests
import getpass
from scripts.settings import init
from logging import exception

#This script is responsible for the authentication of git user
def authenticate_git_user():	
	init.git_username = input("Enter Your Git User Name....")
	while ( (init.git_username in "" )):   
		init.git_username = input("Enter Your Git User Name....")
		
	token_or_password = input("How do you want to authenticate yourself? press t for token or p for password")
	while (not (token_or_password in ['t','p'] )):   
		token_or_password = input("How do you want to authenticate yourself? press t for token or p for password")
	if token_or_password =='t':
		init.git_password = getpass.getpass("Enter Your Git Token.....")
	else:
		init.git_password = getpass.getpass("Enter Your Git Password..")
	url = 'https://'+init.git_username+':'+init.git_password+'@api.github.com/repos/krashah/practice/issues'
	session = requests.Session()
	message=""
	try:
		response_object=session.get(url)
		if (response_object.status_code in [201,200]):
			message= "Authentication Successful" 
		else:
			raise Exception()
	except:
		print ('Wrong Git Credentials');message= "Invalid Details"
	return message
    
		