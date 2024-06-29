import {USER_CONTEXT_INITIAL_STATE} from "utils/constants/contextConstants";
import userReducer from "reducers/userReducer";
import React, {Context, createContext, Dispatch, ReactElement, ReactNode, useMemo, useReducer} from "react";
import {UserContext, UserReducerAction} from "utils/interfaces";

const UserStateContext: Context<UserContext | undefined> = createContext<UserContext | undefined>(undefined);
const UserDispatchContext: Context<Dispatch<UserReducerAction> | undefined> = createContext<Dispatch<UserReducerAction> | undefined>(undefined);

interface Props {
    children: ReactElement | ReactNode
}

export const UserProvider = ({children}: Props) => {
    const INITIAL_STATE: UserContext = useMemo(() => USER_CONTEXT_INITIAL_STATE, []);
    const [state, dispatch] = useReducer(userReducer, INITIAL_STATE);
    return (
        <UserStateContext.Provider value={state}>
            <UserDispatchContext.Provider value={dispatch}>
                {children}
            </UserDispatchContext.Provider>
        </UserStateContext.Provider>
    );
}

export const useUserState = () => {
    const context = React.useContext(UserStateContext);
    if (context === undefined) {
        throw new Error("useUserState must be used within a UserProvider");
    }
    return context;
}

export const useUserDispatch = () => {
    const context = React.useContext(UserDispatchContext);
    if (context === undefined) {
        throw new Error("useUserDispatch must be used within a UserProvider");
    }
    return context;
}
