import {useEffect, useMemo} from "react";
import * as React from "react";
import {UserContext} from "../utils/interfaces";
import {useUserState} from "../context/UserContext";
interface Props {
    children: React.ReactNode;
}

const PrivateAreaWrapper = ({children}: Props) => {
    const userContext: UserContext = useUserState();
    const isContextLoaded: boolean = useMemo(() => Object.keys(userContext.user).length !== 0, [userContext]);

    useEffect(() => {
        if (isContextLoaded && userContext.user.isExpired && window.location.pathname !== "/user-management") {
            window.location.assign("/user-management")
        }
    }, [userContext.user.isExpired, isContextLoaded]);

    return <>{children}</>
}

export default PrivateAreaWrapper;
