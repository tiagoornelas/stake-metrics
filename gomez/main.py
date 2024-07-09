import time
from google.cloud import error_reporting

from integration.bets_api import get_results_since
from integration.stake_metrics import get_last_result_time, inform_results


def run():
    # client = error_reporting.Client(project="stakemetrics", service="gomez")

    loop_interval = 3600
    max_retries = 20
    retries = 0

    last_result_time = get_last_result_time()
    new_results = get_results_since(last_result_time)
    inform_results(new_results)

    # while True:
    #     try:
    #         last_result_time = get_last_result_time()
    #         new_results = get_results_since(last_result_time)
    #         print(new_results)
    #         # inform_results(new_results)
    #         retries = 0
    #     except Exception as e:
    #         retries += 1
    #         # client.report_exception()
    #         print(e)
    #
    #         if retries >= max_retries:
    #             time.sleep(loop_interval)
    #             retries = 0


if __name__ == '__main__':
    run()