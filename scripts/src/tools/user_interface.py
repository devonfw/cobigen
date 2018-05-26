def prompt_enter_value(prompt_subject):
    prompt = "\n> Please enter " + prompt_subject + ": "
    value = input(prompt)
    # Checking if nothing is entered then ask user to enter again
    while (not value.strip()):
        value = input(prompt)
    return value


def prompt_yesno_question(question: str) -> bool:
    prompt = "\n> " + question + " (yes/no): "
    value = input(prompt)
    while (value.strip() != 'yes' and value.strip() != 'no'):
        value = input(prompt)
    return value == 'yes'
