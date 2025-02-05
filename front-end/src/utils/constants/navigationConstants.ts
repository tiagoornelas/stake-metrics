import {NavigationLinkOnHeaderValue, NavigationModuleTypes} from "utils/interfaces";

export const APP_NAVIGATION: NavigationLinkOnHeaderValue[] = [
    {
        name: "Painel",
        path: "app/esoccer/dashboard",
        moduleText: "E-Soccer",
        moduleColor: "green",
        type: NavigationModuleTypes.BETA
    },
    {
        name: "Estratégias",
        path: "app/esoccer/strategies",
        moduleText: "E-Soccer",
        moduleColor: "green",
        type: NavigationModuleTypes.REGULAR
    }
]
