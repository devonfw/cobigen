# This file is responsible for installing all necessary modules needed for running create_release.py script.
import os
import sys

os.chdir(os.path.dirname(sys.executable))
os.chdir("Scripts")
os.system('pip install gitpython')
os.system('pip install PyGithub')
os.system('pip install lxml')
os.system('pip install git')
os.system('pip install pathlib')
os.system('pip install github')
os.system('pip install uritemplate')
os.system('pip install requests')
