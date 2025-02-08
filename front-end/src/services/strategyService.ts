import {BASE_URL, ENDPOINTS} from "utils/constants/serviceConstants";
import {makeRequest} from "utils/helpers/requestHelper";
import {REQUEST_TYPE, StrategyCreationBody, StrategyStatus} from "utils/interfaces";

export const getFifaStrategyParams = () =>
    makeRequest(`${BASE_URL}${ENDPOINTS.FIFA.BASE}${ENDPOINTS.STRATEGY.BASE}${ENDPOINTS.STRATEGY.PARAMS}`, REQUEST_TYPE.GET);

export const changeStrategyStatus = (strategyId: string, status: StrategyStatus) =>
    makeRequest(`${BASE_URL}${ENDPOINTS.FIFA.BASE}${ENDPOINTS.STRATEGY.BASE}${ENDPOINTS.STRATEGY.STATUS}/${strategyId}`, REQUEST_TYPE.PUT, {status});

export const deleteStrategy = (strategyId: string) =>
    makeRequest(`${BASE_URL}${ENDPOINTS.FIFA.BASE}${ENDPOINTS.STRATEGY.BASE}/${strategyId}`, REQUEST_TYPE.DELETE);

export const restartStrategy = (strategyId: string) =>
    makeRequest(`${BASE_URL}${ENDPOINTS.FIFA.BASE}${ENDPOINTS.STRATEGY.BASE}${ENDPOINTS.STRATEGY.RESTART}/${strategyId}`, REQUEST_TYPE.POST);

export const saveStrategy = (strategy: StrategyCreationBody) =>
    makeRequest(`${BASE_URL}${ENDPOINTS.FIFA.BASE}${ENDPOINTS.STRATEGY.BASE}`, REQUEST_TYPE.POST, strategy);

export const getStrategy = (strategyId: string) =>
    makeRequest(`${BASE_URL}${ENDPOINTS.FIFA.BASE}${ENDPOINTS.STRATEGY.BASE}/${strategyId}`, REQUEST_TYPE.GET);

export const listStrategiesStatistics = () =>
    makeRequest(`${BASE_URL}${ENDPOINTS.FIFA.BASE}${ENDPOINTS.STRATEGY.BASE}${ENDPOINTS.STRATEGY.STATISTICS}`, REQUEST_TYPE.GET);

export const listStrategiesCumulativeProfit = (strategyId: string) =>
    makeRequest(`${BASE_URL}${ENDPOINTS.FIFA.BASE}${ENDPOINTS.STRATEGY.BASE}${ENDPOINTS.STRATEGY.PROFITS}/${strategyId}`, REQUEST_TYPE.GET);

export const getStrategySimpleReport = (strategyId: string) =>
    makeRequest(`${BASE_URL}${ENDPOINTS.FIFA.BASE}${ENDPOINTS.STRATEGY.BASE}${ENDPOINTS.STRATEGY.REPORT.BASE}${ENDPOINTS.STRATEGY.REPORT.SIMPLE}/${strategyId}`, REQUEST_TYPE.GET);
