step_counter: int
step_counter = 0


def prompt_enter_value(prompt_subject):
    prompt = "> Please enter " + prompt_subject + ": "
    value = input(prompt)
    # Checking if nothing is entered then ask user to enter again
    while (not value.strip()):
        value = input(prompt)
    return value


def prompt_yesno_question(question: str) -> bool:
    prompt = "> " + question + " (yes/no): "
    value = input(prompt)
    while (value.strip() != 'yes' and value.strip() != 'no'):
        value = input(prompt)
    return value == 'yes'


def log_debug(print_message):
    print("[DEBUG] "+print_message)


def log_info(print_message):
    print("[INFO] "+print_message)


def log_info_dry(print_message):
    log_info("[DRY] "+print_message)


def log_error(print_message):
    print("[ERROR] "+print_message)


def log_step(description):
    global step_counter
    step_counter = step_counter + 1
    print("")
    print("************************************************")
    print("********** [STEP " + str(step_counter) + "] " + description)
    print("************************************************")
    print("")
