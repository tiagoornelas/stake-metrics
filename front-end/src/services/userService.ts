import {BASE_URL, ENDPOINTS} from "utils/constants/serviceConstants";
import {PasswordChangeBody, REQUEST_TYPE, User, UserCreationBody} from "utils/interfaces";
import {makePublicRequest, makeRequest} from "utils/helpers/requestHelper";

export const createUser = (user: User) => makePublicRequest(`${BASE_URL}${ENDPOINTS.USER.BASE}`, REQUEST_TYPE.POST, user);

export const editUser = (userId: String, userEditionBody: UserCreationBody) => makeRequest(`${BASE_URL}${ENDPOINTS.USER.BASE}/${userId}`, REQUEST_TYPE.PUT, userEditionBody);

export const changePassword = (userId: String, passwordChangeBody: PasswordChangeBody) => makeRequest(`${BASE_URL}${ENDPOINTS.USER.BASE}${ENDPOINTS.USER.CHANGE_PASSWORD}/${userId}`, REQUEST_TYPE.PUT, passwordChangeBody);

