import {FEATURES} from "utils/constants/featureConstants";
import {NavigationLinkOnHeaderValue, NavigationModuleTypes} from "utils/interfaces";

export const APP_NAVIGATION: NavigationLinkOnHeaderValue[] = [
    {
        name: "Estratégias",
        path: "app/esoccer/strategies",
        moduleText: "E-Soccer",
        moduleColor: "green",
        type: NavigationModuleTypes.REGULAR
    },
    {
        name: "Backtest",
        path: "app/esoccer/backtest",
        moduleText: "E-Soccer",
        moduleColor: "green",
        type: NavigationModuleTypes.COMING_SOON,
        feature: FEATURES.BACKTEST_MODULE
    }
]
