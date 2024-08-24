import {BASE_URL, ENDPOINTS} from "utils/constants/serviceConstants";
import {makeRequest} from "utils/helpers/requestHelper";
import {REQUEST_TYPE} from "utils/interfaces";

export const listBets = (page: number, size: number, showPaperBets: boolean) =>
    makeRequest(`${BASE_URL}${ENDPOINTS.FIFA.BASE}${ENDPOINTS.BET.BASE}?size=${size}&page=${page}&showPaperBets=${showPaperBets}`, REQUEST_TYPE.GET);

export const deleteBet = (betId: string) =>
    makeRequest(`${BASE_URL}${ENDPOINTS.FIFA.BASE}${ENDPOINTS.BET.BASE}/${betId}`, REQUEST_TYPE.DELETE);
