import {InfiniteQueryObserverResult, useInfiniteQuery} from "react-query";
import {listBetsForStrategy} from "services/betService";
import {BetQueryFilters, PaginatedResponse} from "utils/interfaces";

const useBetQuery = (strategyId: string, filters: BetQueryFilters): InfiniteQueryObserverResult<PaginatedResponse> => {
    return useInfiniteQuery(
        ['bets', {strategyId, filters}],
        ({pageParam = 0}) => listBetsForStrategy(strategyId, filters, pageParam, 30),
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
