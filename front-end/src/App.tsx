import {ChakraProvider, extendTheme} from "@chakra-ui/react"
import {ErrorBoundary} from "components/ErrorBoundary";
import PrivateAreaWrapper from "components/PrivateAreaWrapper";
import PrivateRouterLayout from "components/PrivateRouterLayout";
import PublicAreaLayout from "components/PublicAreaLayout";
import FifaStrategies from "containers/fifa/strategy/FifaStrategies";
import AccountRecovery from "containers/public/AccountRecovery";
import CreateAccount from "containers/public/CreateAccount";
import Login from "containers/public/Login"
import PublicPage from "containers/public/PublicPage";
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
import TranslationProvider from "providers/TranslationProvider";
import FifaDashboard from "containers/fifa/dashboard/FifaDashboard";

const publicRouter = createBrowserRouter([
    {
        path: "/create-account",
        element: <PublicAreaLayout><CreateAccount/></PublicAreaLayout>,
    },
    {
        path: "/recover-account",
        element: <PublicAreaLayout><AccountRecovery/></PublicAreaLayout>,
    },
    {
        path: "/login",
        element: <PublicAreaLayout><Login/></PublicAreaLayout>,
    },
    {
        path: "/*",
        element: <PublicPage/>,
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
        path: "/app/user-management",
        element: <PrivateRouterLayout><UserManagement/></PrivateRouterLayout>
    },
    {
        path: "/app/esoccer/dashboard",
        element: <PrivateRouterLayout><FifaDashboard/></PrivateRouterLayout>

    },
    {
        path: "/app/esoccer/strategies",
        element: <PrivateRouterLayout><FifaStrategies/></PrivateRouterLayout>
    },
    {
        path: "/app/*",
        element: <PrivateRouterLayout><FifaDashboard/></PrivateRouterLayout>
    },
    {
        path: "/*",
        element: <PublicPage/>,
    }
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
                isExpired: user.subscription?.status === "INACTIVE"
            }
            setUser(dispatch, parsedUser);
        }

        if (isLoggedIn) saveUserDetailsToContext();
    }, [cookies, dispatch, isLoggedIn]);

    if (!isLoggedIn) return <RouterProvider router={publicRouter}/>;

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
                    <TranslationProvider>
                        <AppContent/>
                    </TranslationProvider>
                </ErrorBoundary>
            </UserProvider>
        </ChakraProvider>
    );
}
