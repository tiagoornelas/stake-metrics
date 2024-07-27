import {BASE_URL, ENDPOINTS} from "utils/constants/serviceConstants";
import {makeRequest} from "utils/helpers/requestHelper";
import {REQUEST_TYPE, StrategyStatus} from "utils/interfaces";

export const changeStrategyStatus = (strategyId: string, status: StrategyStatus) =>
    makeRequest(`${BASE_URL}${ENDPOINTS.STRATEGY.BASE}${ENDPOINTS.STRATEGY.STATUS}/${strategyId}`, REQUEST_TYPE.PUT, {status});

export const deleteStrategy = (strategyId: string) =>
    makeRequest(`${BASE_URL}${ENDPOINTS.STRATEGY.BASE}/${strategyId}`, REQUEST_TYPE.DELETE);
