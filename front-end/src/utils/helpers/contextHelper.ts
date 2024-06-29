import {Dispatch} from "react";
import {User, UserReducerAction} from "../interfaces";
import {USER_REDUCER_ACTION_TYPES} from "../constants/contextConstants";

export const setUser = (dispatch: Dispatch<UserReducerAction>, user: User) => dispatch({
    type: USER_REDUCER_ACTION_TYPES.SET_USER,
    payload: user
})

export const cleanUser = (dispatch: Dispatch<UserReducerAction>) => dispatch({
    type: USER_REDUCER_ACTION_TYPES.SET_USER,
    payload: {}
})