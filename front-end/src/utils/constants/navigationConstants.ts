import {NavigationLinkOnHeaderValue, NavigationModuleTypes} from "utils/interfaces";

export const APP_NAVIGATION: NavigationLinkOnHeaderValue[] = [
    {
        name: "Painel",
        path: "app/esoccer/dashboard",
        type: NavigationModuleTypes.REGULAR
    },
    {
        name: "Estratégias",
        path: "app/esoccer/strategies",
        type: NavigationModuleTypes.REGULAR
    },
    {
        name: "Tendências",
        path: "app/esoccer/trends",
        type: NavigationModuleTypes.REGULAR
    }
]
