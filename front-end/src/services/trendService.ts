import {makeRequest} from "../utils/helpers/requestHelper";
import {BASE_URL, ENDPOINTS} from "../utils/constants/serviceConstants";
import {REQUEST_TYPE} from "../utils/interfaces";
import {DateIntervalTypes} from "../utils/constants/dateConstants";

export const getFifaGoalsTrend = (dateInterval: DateIntervalTypes) =>
  makeRequest(`${BASE_URL}${ENDPOINTS.TREND.BASE}${ENDPOINTS.TREND.FIFA.BASE}${ENDPOINTS.TREND.FIFA.GOALS}/${dateInterval}`, REQUEST_TYPE.GET);