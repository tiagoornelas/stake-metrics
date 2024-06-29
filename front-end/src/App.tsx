import Subscription from "containers/public/Subscription";
import * as React from "react"
import {Dispatch, useEffect, useMemo} from "react"
import {ChakraProvider, extendTheme} from "@chakra-ui/react"
import Login from "containers/public/Login"
import {getCustomThemeColors} from "utils/helpers/themeColorHelper";
import {useCookies} from "react-cookie";
import {getUserDetails} from "services/loginService";
import Header from "components/Header";
import {createBrowserRouter, RouterProvider,} from "react-router-dom";
import Workspace from "containers/workspace/Workspace";
import CreateAccount from "containers/public/CreateAccount";
import AccountRecovery from "containers/public/AccountRecovery";
import {setUser} from "utils/helpers/contextHelper";
import {useUserDispatch, UserProvider, useUserState} from "context/UserContext";
import {UserContext, UserReducerAction} from "./utils/interfaces";
import {ErrorBoundary} from "components/ErrorBoundary";
import PublicAreaWrapper from "components/PublicAreaWrapper";
import PrivateAreaWrapper from "components/PrivateAreaWrapper";
import UserManagement from "./containers/user/UserManagement";

const publicRouter = createBrowserRouter([
    {
        path: "/create-account",
        element: <CreateAccount/>,
    },
    {
        path: "/recover-account",
        element: <AccountRecovery/>,
    },
    {
        path: "/*",
        element: <Login/>,
    }
]);

const unsubscribedRouter = createBrowserRouter([
    {
        path: "/*",
        element: <Subscription/>,
    }
]);

const appRouter = createBrowserRouter([
    {
        path: "/user-management",
        element: <UserManagement/>
    },
    {
        path: "/*",
        element: <Workspace/>,
    },
]);

const AppContent = () => {
    const [cookies] = useCookies(["userId"]);
    const isLoggedIn = useMemo(() => cookies.userId, [cookies]);

    const dispatch: Dispatch<UserReducerAction> = useUserDispatch();
    const userContext: UserContext = useUserState();

    useEffect(() => {
        const {userId} = cookies;

        const saveUserDetailsToContext = async () => {
            const {user} = await getUserDetails(userId);
            const parsedUser = {
                ...user,
                isExpired: user.subscription?.status !== "ACTIVE"
            }
            setUser(dispatch, parsedUser);
        }

        if (isLoggedIn) saveUserDetailsToContext();
    }, [cookies, dispatch, isLoggedIn]);

    if (!isLoggedIn) return (<PublicAreaWrapper>
        <RouterProvider router={publicRouter}/>
    </PublicAreaWrapper>);

    if (userContext.user.isExpired) return (
        <PrivateAreaWrapper>
            <RouterProvider router={unsubscribedRouter}/>
        </PrivateAreaWrapper>
    )

    return (<PrivateAreaWrapper>
        <Header/>
        <RouterProvider router={appRouter}/>
    </PrivateAreaWrapper>);

}

export const App = () => {
    const extendedTheme = extendTheme({
        colors: getCustomThemeColors()
    });

    return (
        <ChakraProvider theme={extendedTheme}>
            <UserProvider>
                <ErrorBoundary>
                    <AppContent/>
                </ErrorBoundary>
            </UserProvider>
        </ChakraProvider>
    );
}
