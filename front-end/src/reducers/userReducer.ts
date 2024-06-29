import {produce} from "immer";
import {UserContext, UserReducerAction} from "utils/interfaces";
import {USER_REDUCER_ACTION_TYPES} from "utils/constants/contextConstants";
import {Reducer} from "react";

const userReducer: Reducer<UserContext, UserReducerAction> = (state: UserContext, action: UserReducerAction) => {
    return produce(state, (draft: UserContext) => {
        switch (action.type) {
            case USER_REDUCER_ACTION_TYPES.SET_USER:
                draft.user = action.payload;
                break;
            default:
                throw new Error(`Invalid action type: ${action.type}`);
        }
    });
}

export default userReducer;
