from scripts.settings import init
import requests
import json
import os


# This Method will be responsible for getting package folder name based on branch name
def get_build_folder(branch_name):
    build_folder_name = '' 
    if (branch_name == 'master'):
        build_folder_name = '' 
    elif (branch_name == 'dev_eclipseplugin'): 
        build_folder_name = 'cobigen-eclipse' 
    elif (branch_name == 'dev_htmlmerger'): 
        build_folder_name = os.path.join('cobigen','cobigen-htmlplugin') 
    elif (branch_name == 'dev_mavenplugin'):
        build_folder_name = 'cobigen-maven' 
    elif (branch_name == 'dev_tempeng_freemarker'):
        build_folder_name = os.path.join('cobigen','cobigen-templateengines','cobigen-tempeng-freemarker')
    elif (branch_name == 'dev_tempeng_velocity'):
        build_folder_name = os.path.join('cobigen','cobigen-templateengines','cobigen-tempeng-velocity')
    elif (branch_name == 'dev_core'):
        build_folder_name = os.path.join('cobigen','cobigen-core-parent')
    elif(branch_name == 'dev_javaplugin'):
        build_folder_name = os.path.join('cobigen','cobigen-javaplugin-parent')
    elif (branch_name == 'dev_jssenchaplugin'):
        build_folder_name = os.path.join('cobigen','cobigen-senchaplugin')
    elif (branch_name == 'dev_openapiplugin'):
	    build_folder_name = os.path.join('cobigen','cobigen-openapiplugin')
    elif (branch_name == 'dev_tsplugin'):
	    build_folder_name = os.path.join('cobigen','cobigen-tsplugin')
    elif (branch_name == 'dev_textmerger'):
	    build_folder_name = os.path.join('cobigen','cobigen-textmerger')
    elif (branch_name == 'dev_propertyplugin'):
	    build_folder_name = os.path.join('cobigen','cobigen-propertyplugin')
    elif (branch_name == 'dev_jsonplugin'):
	    build_folder_name = os.path.join('cobigen','cobigen-jsonplugin')
    elif (branch_name == 'dev_xmlplugin'):
	    build_folder_name = os.path.join('cobigen','cobigen-xmlplugin')
    else:
        build_folder_name ='invalid'
    return build_folder_name

# This Method is responsible for fetching wiki document description based \
#on branch name for the purpose of updating version in CobiGen.asciidoc
def get_cobigenwiki_title_name(branch_name):
    wiki_description_name = '' 
    if (branch_name == 'master'):
        wiki_description_name = 'CobiGen v' 
    elif (branch_name == 'dev_eclipseplugin'): 
        wiki_description_name = 'CobiGen - Eclipse Plug-in' 
    elif (branch_name == 'dev_htmlmerger'): 
        wiki_description_name = 'CobiGen - HTML Plug-in'
    elif (branch_name == 'dev_propertyplugin'): 
        wiki_description_name = 'CobiGen - Property Plug-in'
    elif (branch_name == 'dev_textmerger'):
        wiki_description_name = 'CobiGen - Text Merger'
    elif (branch_name == 'dev_jsonplugin'): 
        wiki_description_name = 'CobiGen - JSON Plug-in'  
    elif (branch_name == 'dev_openapiplugin'): 
        wiki_description_name = 'CobiGen - Open API Plug-in'		
    elif (branch_name == 'dev_mavenplugin'):
        wiki_description_name = 'CobiGen - Maven Build Plug-in' 
    elif (branch_name == 'dev_tempeng_freemarker'):
        wiki_description_name = 'CobiGen - FreeMaker Template Engine' 
    elif (branch_name == 'dev_tempeng_velocity'):
        wiki_description_name = 'CobiGen - Velocity Template Engine' 
    elif(branch_name == 'dev_javaplugin'):
        wiki_description_name = 'Cobigen - Java Plug-in'
    elif(branch_name == 'dev_xmlplugin'):
        wiki_description_name = 'CobiGen - XML Plug-in'
    elif (branch_name == 'dev_jssenchaplugin'):
        wiki_description_name = 'CobiGen - Sencha Plug-in' 
    else:
        wiki_description_name ='invalid';
        print('Please edit function get_cobigenwiki_title_name in scripts/branchname_validation.py');
    return wiki_description_name
