from integration.bets_api import get_results_since
from integration.stake_metrics import get_last_result_time, inform_results


def run():
    # client = error_reporting.Client(project="stakemetrics", service="gomez")

    try:
        last_result_time = get_last_result_time()
        new_results = get_results_since(last_result_time)
        inform_results(new_results)
    except Exception as exc:
        print(f"An error occurred. {exc}")


# client.report_exception()

run()
