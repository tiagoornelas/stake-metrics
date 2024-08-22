import {InfiniteQueryObserverResult, useInfiniteQuery} from "react-query";
import {listBets} from "services/strategyService";
import {PaginatedResponse} from "utils/interfaces";

const useBetQuery = (): InfiniteQueryObserverResult<PaginatedResponse> => {
    return useInfiniteQuery(
        'bets',
        ({pageParam = 0}) => listBets(pageParam, 30),
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
