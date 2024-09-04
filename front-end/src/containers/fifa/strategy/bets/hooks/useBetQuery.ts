import {InfiniteQueryObserverResult, useInfiniteQuery} from "react-query";
import {listBets} from "services/betService";
import {PaginatedResponse} from "utils/interfaces";

const useBetQuery = (showPaperBets: boolean): InfiniteQueryObserverResult<PaginatedResponse> => {
    return useInfiniteQuery(
        ['bets', {showPaperBets}],
        ({pageParam = 0}) => listBets(pageParam, 30, showPaperBets),
        {
            getNextPageParam: (lastPage) => {
                if (lastPage.number < lastPage.totalPages - 1) {
                    return lastPage.number + 1;
                }
                return undefined;
            }
        }
    );
}

export default useBetQuery;
