import {BASE_URL, ENDPOINTS} from "../utils/constants/serviceConstants";
import {makeRequest} from "../utils/helpers/requestHelper";
import {REQUEST_TYPE} from "../utils/interfaces";

export const fetchTelegramChats = (userId: string) => makeRequest(`${BASE_URL}${ENDPOINTS.TELEGRAM.BASE}/${userId}`, REQUEST_TYPE.GET);

export const beginIntegration = (userId: string) => makeRequest(`${BASE_URL}${ENDPOINTS.TELEGRAM.BASE}${ENDPOINTS.TELEGRAM.BEGIN_PRIVATE_CHAT_INTEGRATION}/${userId}`, REQUEST_TYPE.POST);

export const integratePrivateChat = (userId: string) => makeRequest(`${BASE_URL}${ENDPOINTS.TELEGRAM.BASE}${ENDPOINTS.TELEGRAM.INTEGRATE_PRIVATE_CHAT}/${userId}`, REQUEST_TYPE.POST);

export const deletePrivateChat = (telegramChatId: string) => makeRequest(`${BASE_URL}${ENDPOINTS.TELEGRAM.BASE}/${telegramChatId}`, REQUEST_TYPE.DELETE);

export const testPrivateChat = (telegramChatId: string) => makeRequest(`${BASE_URL}${ENDPOINTS.TELEGRAM.BASE}${ENDPOINTS.TELEGRAM.TEST_MESSAGE}/${telegramChatId}`, REQUEST_TYPE.POST);

export const editIntegration = (telegramChatId: string, name: string, status: string, delay: number, deliveryProbability: number) => makeRequest(`${BASE_URL}${ENDPOINTS.TELEGRAM.BASE}`, REQUEST_TYPE.PUT, {
    id: telegramChatId,
    name,
    status,
    delay,
    deliveryProbability
});