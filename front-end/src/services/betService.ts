import {BASE_URL, ENDPOINTS} from "utils/constants/serviceConstants";
import {makeRequest} from "utils/helpers/requestHelper";
import {BetQueryFilters, REQUEST_TYPE} from "utils/interfaces";

export const listBetsForStrategy = (strategyId: string, filters: BetQueryFilters, page: number, size: number) =>
    makeRequest(`${BASE_URL}${ENDPOINTS.FIFA.BASE}${ENDPOINTS.BET.BASE}${ENDPOINTS.BET.STRATEGY}/${strategyId}`, REQUEST_TYPE.POST, {
        page, size, ...filters
    });

export const deleteBet = (betId: string) =>
    makeRequest(`${BASE_URL}${ENDPOINTS.FIFA.BASE}${ENDPOINTS.BET.BASE}/${betId}`, REQUEST_TYPE.DELETE);

export const getFifaBetStatistics = () =>
    makeRequest(`${BASE_URL}${ENDPOINTS.FIFA.BASE}${ENDPOINTS.BET.BASE}${ENDPOINTS.BET.STATISTICS}`, REQUEST_TYPE.GET);
