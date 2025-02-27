import { BASE_URL, ENDPOINTS } from "utils/constants/serviceConstants";
import { makeRequest } from "utils/helpers/requestHelper";
import { REQUEST_TYPE } from "utils/interfaces";


export const fetchAutoBettor = () => makeRequest(`${BASE_URL}${ENDPOINTS.INTEGRATION.AUTO_BETTOR}`, REQUEST_TYPE.GET);

export const saveAutoBettor = (integrationId: string) => makeRequest(`${BASE_URL}${ENDPOINTS.INTEGRATION.AUTO_BETTOR}`, REQUEST_TYPE.POST, { integrationId });

export const deleteAutoBettor = () => makeRequest(`${BASE_URL}${ENDPOINTS.INTEGRATION.AUTO_BETTOR}`, REQUEST_TYPE.DELETE);

export const activateAutoBettor = () => makeRequest(`${BASE_URL}${ENDPOINTS.INTEGRATION.AUTO_BETTOR}${ENDPOINTS.INTEGRATION.STATUS}`, REQUEST_TYPE.PUT, { status: "ACTIVE" });

export const deactivateAutoBettor = () => makeRequest(`${BASE_URL}${ENDPOINTS.INTEGRATION.AUTO_BETTOR}${ENDPOINTS.INTEGRATION.STATUS}`, REQUEST_TYPE.PUT, { status: "INACTIVE" });