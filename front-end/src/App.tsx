import {ChakraProvider, extendTheme} from "@chakra-ui/react"
import {ErrorBoundary} from "components/ErrorBoundary";
import PrivateAreaWrapper from "components/PrivateAreaWrapper";
import PublicAreaWrapper from "components/PublicAreaWrapper";
import RouterLayout from "components/RouterLayout";
import FifaModule from "containers/fifa/FifaModule";
import AccountRecovery from "containers/public/AccountRecovery";
import CreateAccount from "containers/public/CreateAccount";
import Login from "containers/public/Login"
import Subscription from "containers/public/Subscription";
import {UserProvider, useUserDispatch, useUserState} from "context/UserContext";
import * as React from "react"
import {Dispatch, useEffect, useMemo} from "react"
import {useCookies} from "react-cookie";
import {createBrowserRouter, RouterProvider,} from "react-router-dom";
import {getUserDetails} from "services/loginService";
import {setUser} from "utils/helpers/contextHelper";
import {getCustomThemeColors} from "utils/helpers/themeColorHelper";
import {UserContext, UserReducerAction} from "utils/interfaces";
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
        element: <RouterLayout><UserManagement/></RouterLayout>
    },
    {
        path: "/fifa",
        element: <RouterLayout><FifaModule/></RouterLayout>

    },
    {
        path: "/*",
        element: <RouterLayout><FifaModule/></RouterLayout>
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
