import {ButtonProps} from "@chakra-ui/react";
import {ReactElement} from "react";
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
}

export interface Plan {
    id: string;
    name: string;
    description: string;
    price: number | string;
    priceId: string;
    popular?: boolean;
}

export interface FeatureMap {
    [key: string]: number;
}

export interface Subscription {
    id: string;
    customerId: string;
    status: string;
    expiresAt: string | null;
    subscriptionId: string | null;
    features: FeatureMap;
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

export interface TelegramChat {
    id: string;
    name: string;
    chatId: string;
    status: string;
    delay: number;
    deliveryProbability: number;
    notDeliveredMessage: string;
    delayedAlertMessage: string;
    extraText: string;
}

export interface ExtraButton extends ButtonProps {
    callback: () => void;
    disbled?: boolean;
    label: string;
    colorScheme?: string;
    rightIcon?: ReactElement;
    closeOnAction?: boolean;
}