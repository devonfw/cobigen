import logging
import os

step_counter: int = 0

log_formatter = logging.Formatter("%(asctime)s [%(levelname)-5.5s]  %(message)s")
logger = logging.getLogger()

log_path = os.path.normpath(os.path.join(os.path.join(os.path.dirname(os.path.realpath(__file__)), ".."),  "create_release.log"))
file_handler = logging.FileHandler(log_path)
file_handler.setFormatter(log_formatter)
logger.addHandler(file_handler)

consoleHandler = logging.StreamHandler()
consoleHandler.setFormatter(log_formatter)
logger.addHandler(consoleHandler)


def log_debug(print_message: str) -> None:
    logger.info("[DEBUG] "+print_message)


def log_info(print_message: str) -> None:
    logger.info(print_message)


def log_info_dry(print_message: str) -> None:
    logger.info("[DRY] "+print_message)


def log_error(print_message: str) -> None:
    logger.error(print_message)


def log_step(description: str) -> None:
    global step_counter
    step_counter = step_counter + 1
    logger.info("")
    logger.info("***************************************************************************")
    logger.info("********** [STEP " + str(step_counter) + "] " + description)
    logger.info("***************************************************************************")
