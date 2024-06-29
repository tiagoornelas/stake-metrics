import {BASE_URL, ENDPOINTS} from "utils/constants/serviceConstants";
import {UserRecoveryBody, REQUEST_TYPE} from "utils/interfaces";
import {makePublicRequest} from "utils/helpers/requestHelper";

export const sendRecoveryCode = (email: string) => makePublicRequest(`${BASE_URL}${ENDPOINTS.RECOVER.BASE}`, REQUEST_TYPE.POST, {email});

export const changePasswordWithRecoveryCode = (form: UserRecoveryBody) => makePublicRequest(`${BASE_URL}${ENDPOINTS.RECOVER.BASE}`, REQUEST_TYPE.PUT, form);