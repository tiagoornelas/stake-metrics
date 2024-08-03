import {BASE_URL, ENDPOINTS} from "utils/constants/serviceConstants";
import {makeRequest} from "utils/helpers/requestHelper";
import {REQUEST_TYPE} from "utils/interfaces";

export const getPricingTableInfo = (darkMode: boolean) => makeRequest(`${BASE_URL}${ENDPOINTS.SUBSCRIPTION.BASE}${ENDPOINTS.SUBSCRIPTION.PRICING_TABLE}?darkMode=${darkMode}`, REQUEST_TYPE.GET);

export const createPortalSession = () => makeRequest(`${BASE_URL}${ENDPOINTS.SUBSCRIPTION.BASE}${ENDPOINTS.SUBSCRIPTION.CREATE_PORTAL_SESSION}`, REQUEST_TYPE.POST);
