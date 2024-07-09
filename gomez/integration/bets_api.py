import http.client
import json
import os
import time
from datetime import datetime, timedelta

from dotenv import load_dotenv

from helpers.bets_api import add_extra_properties_to_result
from integration.stake_metrics import get_leagues

load_dotenv()
BETS_API_TOKEN = os.environ.get('BETS_API_TOKEN')


def get_results_since(last_result_time):
    if last_result_time is None:
        two_months_ago = datetime.now() - timedelta(days=61)
        start_date = two_months_ago
    else:
        start_date = datetime.fromtimestamp(last_result_time)

    leagues = get_leagues()
    today = datetime.now().date()
    start_date = datetime.combine(start_date, datetime.min.time()).date()
    date_range = [start_date + timedelta(days=x) for x in range((today - start_date).days + 1)]

    results = []

    for league in leagues:
        for date in date_range:
            formatted_date = date.strftime("%Y%m%d")
            page = 1

            while True:
                endpoint = f"/v3/events/ended?sport_id=1&league_id={league['integrationId']}&day={formatted_date}&page={page}"
                json_data = fetch_bets_api(endpoint)
                print(f"Fetching league {league['name']} for date {formatted_date} and page {page}...")
                total_results = json_data['pager']['total']

                results.extend(json_data['results'])

                if page * json_data['pager']['per_page'] < total_results:
                    page += 1
                else:
                    break

    formatted_results = []

    for match in results:
        if 'scores' in match and '2' in match['scores']:
            if 'home' in match['scores']['2'] and 'away' in match['scores']['2']:
                if 'time_status' in match and match['time_status'] == '3':
                    formatted_match = add_extra_properties_to_result(match)
                    formatted_results.append(formatted_match)

    return formatted_results


def fetch_bets_api(endpoint):
    time.sleep(1)
    conn = http.client.HTTPSConnection("api.b365api.com")
    payload = ''
    headers = {}
    conn.request("GET", endpoint + f"&token={BETS_API_TOKEN}", payload, headers)
    res = conn.getresponse()
    data = res.read().decode("utf-8")
    json_data = json.loads(data)
    return json_data
