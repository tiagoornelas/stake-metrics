import {UserContext} from "../interfaces";

export const USER_CONTEXT_INITIAL_STATE: UserContext = {
    user: {}
};

export enum USER_REDUCER_ACTION_TYPES {
    SET_USER = "SET_USER"
}