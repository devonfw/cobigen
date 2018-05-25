step_counter: int
step_counter = 0

# This Method will be responsible for getting package folder name based on branch name
def prompt_enter_value(prompt_subject):
    prompt = "> Please enter " + prompt_subject + ": "
    value = input(prompt)
    #Checking if nothing is entered then ask user to enter again   
    while (not value.strip()):
        value = input(prompt)
    return value

def prompt_yesno_question(question: str) -> bool:
    prompt = "> " + question + " (yes/no): "
    value = input(prompt)
    while (value.strip() != 'yes' and value.strip() != 'no'):
        value = input(prompt)
    return value == 'yes'

def print_debug(print_message):
    print("[DEBUG] "+print_message)

def print_info(print_message):
    print("[INFO] "+print_message)

def print_info_dry(print_message):
    print_info("[DRY] "+print_message)    
    
def print_error(print_message):
    print("[ERROR] "+print_message)

def print_step(description):
    global step_counter
    step_counter = step_counter + 1
    print_info("[STEP " + str(step_counter) + "] " + description)
    