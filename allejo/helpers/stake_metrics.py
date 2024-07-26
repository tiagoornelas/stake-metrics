import os

from dotenv import load_dotenv

load_dotenv()

GCP_PROJECT_NAME = "stakemetrics"
SERVICE_NAME = "allejo"
LOCAL_STAKE_METRICS_URL = "localhost:8080"
PROD_STAKE_METRICS_URL = "stakemetrics.net"
LOCAL_INFORM_RESULTS_URL = "/service/fifa/match"
PROD_INFORM_RESULTS_URL = "/service/fifa/match/enqueue"
INFORM_ODD_URL = "/service/fifa/upcoming-match/odds"
CURRENT_ENV = os.environ.get("ENV", "PRODUCTION")


def get_stake_metrics_url():
    if CURRENT_ENV == "LOCAL":
        return LOCAL_STAKE_METRICS_URL
    else:
        return PROD_STAKE_METRICS_URL


def get_inform_results_url():
    if CURRENT_ENV == "LOCAL":
        return LOCAL_INFORM_RESULTS_URL
    else:
        return PROD_INFORM_RESULTS_URL


def get_inform_odds_url():
    return INFORM_ODD_URL


def get_market_type(market_id):
    match market_id:
        case "1_1":
            return "MATCH_ODDS"
        case "1_3":
            return "GOAL_LINE"
        case _:
            raise ValueError("Unknown market type")
