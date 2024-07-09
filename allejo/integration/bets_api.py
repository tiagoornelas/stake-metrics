import http.client
import json
import os
import time
from datetime import datetime, timedelta

from dotenv import load_dotenv

load_dotenv()
BETS_API_TOKEN = os.environ.get('BETS_API_TOKEN')


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

