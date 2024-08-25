import {useUserState} from "context/UserContext";
import * as React from "react";
import {useEffect, useMemo} from "react";
import {QueryClient, QueryClientProvider} from "react-query";
import {UserContext} from "utils/interfaces";

interface Props {
    children: React.ReactNode;
}

const queryClient = new QueryClient()

const PrivateAreaWrapper = ({children}: Props) => {
    const userContext: UserContext = useUserState();
    const isContextLoaded: boolean = useMemo(() => Object.keys(userContext.user).length !== 0, [userContext]);

    useEffect(() => {
        if (isContextLoaded && userContext.user.isExpired && window.location.pathname !== "/app/user-management") {
            window.location.assign("/app/user-management")
        }
    }, [userContext.user.isExpired, isContextLoaded]);

    return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
}

export default PrivateAreaWrapper;
