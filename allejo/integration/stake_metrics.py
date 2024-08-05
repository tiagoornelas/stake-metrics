import http
import http.client
import json
import os

from dotenv import load_dotenv

from helpers.stake_metrics import get_stake_metrics_url, get_inform_odds_url, get_inform_results_url, get_market_type

load_dotenv()
STAKE_METRICS_URL = get_stake_metrics_url()
INFORM_RESULT_URL = get_inform_results_url()
INFORM_ODD_URL = get_inform_odds_url()
SERVICE_TOKEN = os.environ.get('SERVICE_TOKEN')
CURRENT_ENV = os.environ.get("ENV", "PRODUCTION")


def get_last_result_time():
    response = fetch_stake_metrics(STAKE_METRICS_URL, "/service/fifa/last-result-time")
    return response["lastResultTime"]


def get_leagues():
    response = fetch_stake_metrics(STAKE_METRICS_URL, "/service/fifa/leagues")
    return response["leagues"]


def fetch_stake_metrics(base_endpoint, path):
    headers = {
        'Authorization': f'Bearer {SERVICE_TOKEN}'
    }
    conn = get_connection(base_endpoint)
    conn.request("GET", path, headers=headers)
    res = conn.getresponse()
    data = res.read().decode("utf-8")
    conn.close()

    if 200 <= res.status < 300:
        if data:
            json_data = json.loads(data)
            return json_data
        else:
            print("Received an empty response body.")
            return {}
    else:
        print(f"Request failed with status code {res.status}: {res.reason}")
        return {}


def get_connection(base_endpoint):
    if CURRENT_ENV == "LOCAL":
        return http.client.HTTPConnection(base_endpoint)
    else:
        return http.client.HTTPSConnection(base_endpoint)


def inform_results(results):
    formatted_results = format_results_for_stake_metrics(results)

    for formatted_result in formatted_results:
        payload = json.dumps(formatted_result)
        headers = {
            'Content-Type': 'application/json',
            'Authorization': f'Bearer {SERVICE_TOKEN}'
        }

        conn = http.client.HTTPConnection(STAKE_METRICS_URL)
        conn.request("POST", INFORM_RESULT_URL, body=payload, headers=headers)
        response = conn.getresponse()
        if 200 <= response.status < 300:
            print(f"Successfully sent result {formatted_result['integrationId']}")
        else:
            print(f"Failed to send result {formatted_result['integrationId']}, status code: {response.status}")
        conn.close()


def format_results_for_stake_metrics(results):
    formatted_results = []

    for result in results:
        if '1' in result["scores"]:
            total_goals_at_half_time = (int(result["scores"]["1"]["home"] or 0)) + (
                int(result["scores"]["1"]["away"] or 0))
            home_goals_at_half_time = int(result["scores"]["1"]["home"] or 0)
            away_goals_at_half_time = int(result["scores"]["1"]["away"] or 0)
        else:
            total_goals_at_half_time = 0
            home_goals_at_half_time = 0
            away_goals_at_half_time = 0

        total_goals_at_full_time = int(result["scores"]["2"]["home"]) + int(result["scores"]["2"]["away"])

        winner = None if not result["winner"] else result["winner"]["player_name"]

        formatted_result = {
            "integrationId": int(result["id"]),
            "time": int(result["time"]),
            "status": int(result["time_status"]),
            "leagueId": int(result["league"]["id"]),
            "home": result["home"]["player_name"],
            "away": result["away"]["player_name"],
            "homeGoalsAtHalfTime": home_goals_at_half_time,
            "homeGoalsAtFullTime": int(result["scores"]["2"]["home"]),
            "awayGoalsAtHalfTime": away_goals_at_half_time,
            "awayGoalsAtFullTime": int(result["scores"]["2"]["away"]),
            "totalGoalsAtHalfTime": total_goals_at_half_time,
            "totalGoalsAtFullTime": total_goals_at_full_time,
            "winner": winner
        }
        formatted_results.append(formatted_result)

    return formatted_results


def inform_upcoming_match(match):
    formatted_match = format_upcoming_match(match)
    payload = json.dumps(formatted_match)
    match_identifier = f"{match['id']} - {formatted_match['homePlayerName']} x {formatted_match['awayPlayerName']}"

    headers = {
        'Content-Type': 'application/json',
        'Authorization': f'Bearer {SERVICE_TOKEN}'
    }

    conn = http.client.HTTPConnection(STAKE_METRICS_URL)
    conn.request("POST", INFORM_ODD_URL, body=payload, headers=headers)
    response = conn.getresponse()
    if 200 <= response.status < 300:
        print(f"Successfully sent match {match_identifier}")
    else:
        print(f"Failed to send match {match_identifier}, status code: {response.status}")
    conn.close()


def format_upcoming_match(match):
    return {
        "leagueIntegrationId": int(match["league"]["id"]),
        "homePlayerName": match["home"]["player_name"],
        "awayPlayerName": match["away"]["player_name"],
        "odds": get_odds_from_match(match)
    }


def get_odds_from_match(match):
    formatted_odds = []
    for market_id, odds_array in match["sportsbook_odds"]["odds"].items():
        latest_odds = None
        latest_add_time = -1
        for odds_entry in odds_array:
            if int(odds_entry["add_time"]) > latest_add_time:
                latest_add_time = int(odds_entry["add_time"])
                latest_odds = odds_entry
        if latest_odds:
            try:
                market_type = get_market_type(market_id)
                strategy = get_strategy(market_type)
                if strategy:
                    odds_dict = {
                        "marketType": market_type,
                        "updateTime": latest_add_time,
                    }
                    odds_dict.update(strategy.get_properties(latest_odds))
                    formatted_odds.append(odds_dict)
            except ValueError:
                continue
    return formatted_odds


class OddsStrategy:
    def get_properties(self, odds_entry):
        pass


class MatchOddsStrategy(OddsStrategy):
    def get_properties(self, odds_entry):
        return {
            "home": float(odds_entry.get("home_od")),
            "draw": float(odds_entry.get("draw_od")),
            "away": float(odds_entry.get("away_od"))
        }


class GoalLineStrategy(OddsStrategy):
    def get_properties(self, odds_entry):
        return {
            "handicap": format_handicap(odds_entry.get("handicap")),
            "over": float(odds_entry.get("over_od")),
            "under": float(odds_entry.get("under_od"))
        }


def get_strategy(market_type):
    if market_type == "MATCH_ODDS":
        return MatchOddsStrategy()
    elif market_type == "GOAL_LINE":
        return GoalLineStrategy()
    else:
        return None


def format_handicap(line):
    if ',' in line:
        line_array = line.split(',')
        return (float(line_array[0]) + float(line_array[1])) / 2
    else:
        return float(line)
