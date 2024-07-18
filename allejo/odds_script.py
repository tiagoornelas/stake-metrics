import os

from dotenv import load_dotenv
from google.cloud import error_reporting

from helpers.stake_metrics import GCP_PROJECT_NAME, SERVICE_NAME
from integration.bets_api import get_and_inform_upcoming_matches_with_odds

load_dotenv()

CURRENT_ENV = os.environ.get("ENV", "PRODUCTION")


def run():
    client = error_reporting.Client(project=GCP_PROJECT_NAME, service=SERVICE_NAME)

    try:
        get_and_inform_upcoming_matches_with_odds()
    except Exception as exc:
        print(f"An error occurred. {exc}")
        if CURRENT_ENV != "LOCAL":
            client.report_exception()


run()
