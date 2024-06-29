import {BASE_URL, ENDPOINTS} from "utils/constants/serviceConstants";
import {LoginBody, REQUEST_TYPE} from "utils/interfaces";
import {makePublicRequest, makeRequest} from "utils/helpers/requestHelper";

export const login = (form: LoginBody) => makePublicRequest(`${BASE_URL}${ENDPOINTS.LOGIN.BASE}`, REQUEST_TYPE.POST, form);

export const getUserDetails = (userId: string) => makeRequest(`${BASE_URL}${ENDPOINTS.USER.BASE}/${userId}`, REQUEST_TYPE.GET);