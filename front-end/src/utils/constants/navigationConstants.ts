import {NavigationLinkOnHeaderValue, NavigationModuleTypes} from "utils/interfaces";

export const APP_NAVIGATION: NavigationLinkOnHeaderValue[] = [
    {
        name: "Painel",
        path: "app/esoccer/dashboard",
        moduleText: "E-Soccer",
        moduleColor: "green",
        type: NavigationModuleTypes.REGULAR
    },
    {
        name: "Estratégias",
        path: "app/esoccer/strategies",
        moduleText: "E-Soccer",
        moduleColor: "green",
        type: NavigationModuleTypes.REGULAR
    },
    {
        name: "Tendências",
        path: "app/esoccer/trends",
        moduleText: "E-Soccer",
        moduleColor: "green",
        type: NavigationModuleTypes.BETA
    }
]
