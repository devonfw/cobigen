import scripts.settings

#This Method is responsible for Checking branches in repository with branch entered by user
def check_branch_validity(branch_name):
    url='https://'+git_username+':'+git_password+'@api.github.com/repos/krashah/practice/branches/'+branch_name
    response_object= requests.get(url)
    branch_repo_data = json.loads(response_object.text)
    try:
        if branch_repo_data['message'] =='Not Found':
            print('Branch name is not valid, Enter valid branch name.');
    except:
        print('Checking validity of branch is done.')
    return

# This Method will be responsible for checking validitiy of branch name
def get_build_folder(branch_name):
    build_folder_name = '' 
    if (branch_name == 'master'):
        build_folder_name = '' 
    elif (branch_name == 'dev_eclipseplugin'): 
        build_folder_name = 'cobigen-eclipse' 
    elif (branch_name == 'dev_htmlmerger'): 
        build_folder_name = 'cobigen/cobigen-htmlplugin' 
    elif (branch_name == 'dev_mavenplugin'):
        build_folder_name = 'cobigen-maven' 
    elif (branch_name == 'dev_tempeng_freemarker'):
        build_folder_name = 'cobigen/cobigen-templateengines/cobigen-tempeng-freemarker' 
    elif (branch_name == 'dev_tempeng_velocity'):
        build_folder_name = 'cobigen/cobigen-templateengines/cobigen-tempeng-velocity' 
    elif (branch_name == 'dev_core'):
        build_folder_name = 'cobigen/cobigen-core-parent' 
    elif(branch_name == 'dev_javaplugin'):
        build_folder_name = 'cobigen/cobigen-javaplugin-parent' 
    elif (branch_name == 'dev_jssenchaplugin'):
        build_folder_name = 'cobigen/cobigen-senchaplugin' 
    else:
        build_folder_name ='invalid';print('Please edit function get_build_folder in scripts/branchname_validation.py');
    return build_folder_name

