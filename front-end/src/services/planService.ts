import {REQUEST_TYPE} from "../utils/interfaces";
import {makeRequest} from "../utils/helpers/requestHelper";
import {BASE_URL, ENDPOINTS} from "../utils/constants/serviceConstants";

export const getPricingTableInfo = () => makeRequest(`${BASE_URL}${ENDPOINTS.SUBSCRIPTION.BASE}${ENDPOINTS.SUBSCRIPTION.PRICING_TABLE}`, REQUEST_TYPE.GET);

export const createPortalSession = () => makeRequest(`${BASE_URL}${ENDPOINTS.SUBSCRIPTION.BASE}${ENDPOINTS.SUBSCRIPTION.CREATE_PORTAL_SESSION}`, REQUEST_TYPE.POST);