import {BASE_URL, ENDPOINTS} from "utils/constants/serviceConstants";
import {makeRequest} from "utils/helpers/requestHelper";
import {REQUEST_TYPE} from "utils/interfaces";

export const listBets = (page: number, size: number) =>
    makeRequest(`${BASE_URL}${ENDPOINTS.FIFA.BASE}${ENDPOINTS.BET.BASE}`, REQUEST_TYPE.GET, {
        params: {size, page}
    });

export const deleteBet = (betId: string) =>
    makeRequest(`${BASE_URL}${ENDPOINTS.FIFA.BASE}${ENDPOINTS.BET.BASE}/${betId}`, REQUEST_TYPE.DELETE);
