import {USER_REDUCER_ACTION_TYPES} from "../constants/contextConstants";

export enum REQUEST_TYPE {
    GET = "GET",
    POST = "POST",
    PUT = "PUT",
    DELETE = "DELETE"
}

export interface NavigationLinkOnHeaderValue {
    name: string
    path: string
}

export interface LoginBody {
    email: string;
    password: string;
}

export interface User {
    id?: string;
    name?: string;
    email?: string;
    phone?: string;
}

export interface Plan {
    id: string;
    name: string;
    description: string;
    price: number | string;
    priceId: string;
    popular?: boolean;
}

export interface Subscription {
    id: string;
    customerId: string;
    status: string;
    expiresAt: string | null;
    subscriptionId: string | null;
}

export interface UserInfo extends User {
    isExpired?: boolean;
    subscription?: Subscription;
}

export interface UserCreationBody extends User {
    password?: string;
    passwordConfirmation?: string;
}

export interface PasswordChangeBody {
    currentPassword: string;
    password: string;
    passwordConfirmation: string;
}

export interface UserRecoveryBody {
    email: string;
    code: string;
    password: string;
    passwordConfirmation: string;
}

export interface UserContext {
    user: UserInfo;
}

export interface UserReducerAction {
    type: USER_REDUCER_ACTION_TYPES;
    payload: UserInfo;
}

export type ErrorDictionary = {
    title: string;
    description: string | null;
};

export type SuccessDictionary = {
    title: string;
    description: string | null;
};
