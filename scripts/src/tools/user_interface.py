from tools.logger import log_info


def prompt_enter_value(prompt_subject: str, defaultOnEmpty: str = ""):
    prompt = "\n> Please enter " + prompt_subject + ": "
    value = input(prompt)
    # Checking if nothing is entered then ask user to enter again
    while (not value.strip() and not defaultOnEmpty):
        value = input(prompt)
    log_info("[USER-PROMPT] "+prompt.strip())
    if not value:
        value = defaultOnEmpty
    log_info("[USER-ANSWER] "+value)
    return value


def prompt_yesno_question(question: str) -> bool:
    prompt = "\n> " + question + " (yes/no): "
    value = input(prompt)
    while (value.strip() != 'yes' and value.strip() != 'no'):
        value = input(prompt)
    log_info("[USER-PROMPT] "+prompt.strip())
    log_info("[USER-ANSWER] "+value)
    return value == 'yes'
