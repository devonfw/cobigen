import requests
import getpass
#This script is responsible for the authentication of git user
def git_user_authentication():
	git_username = input("Enter Your Git User Name....")
	#getpass is responsible for hiding password in command prompt
	git_password = getpass.getpass("Enter Your Git Password.....")
	url="https://"+git_username+":"+git_password+"@api.github.com"
	session = requests.Session()
	print("Authenticating User....")
	try:
		response_object = session.get(url)
		if(response_object.status_code in [201,200]):
			return "Authentication Successful"
		else:
			raise Exception()
	except:
		print ('Wrong Git Credentials')
		return "Invalid Details"