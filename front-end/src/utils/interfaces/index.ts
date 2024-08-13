import {ButtonProps} from "@chakra-ui/react";
import {ReactElement} from "react";
import {USER_REDUCER_ACTION_TYPES} from "../constants/contextConstants";

export enum REQUEST_TYPE {
    GET = "GET",
    POST = "POST",
    PUT = "PUT",
    DELETE = "DELETE"
}

export interface NavigationLinkOnHeaderValue {
    name: string
    path: string
}

export interface LoginBody {
    email: string;
    password: string;
}

export interface User {
    id?: string;
    name?: string;
    email?: string;
}

export interface Plan {
    id: string;
    name: string;
    description: string;
    price: number | string;
    priceId: string;
    popular?: boolean;
}

export interface FeatureMap {
    [key: string]: number;
}

export interface Subscription {
    id: string;
    customerId: string;
    status: string;
    expiresAt: string | null;
    subscriptionId: string | null;
    features: FeatureMap;
}

export interface UserInfo extends User {
    isExpired?: boolean;
    subscription?: Subscription;
}

export interface UserCreationBody extends User {
    password?: string;
    passwordConfirmation?: string;
}

export interface PasswordChangeBody {
    currentPassword: string;
    password: string;
    passwordConfirmation: string;
}

export interface UserRecoveryBody {
    email: string;
    code: string;
    password: string;
    passwordConfirmation: string;
}

export interface UserContext {
    user: UserInfo;
}

export interface UserReducerAction {
    type: USER_REDUCER_ACTION_TYPES;
    payload: UserInfo;
}

export type ErrorDictionary = {
    title: string;
    description: string | null;
};

export type SuccessDictionary = {
    title: string;
    description: string | null;
};

export interface TelegramChat {
    id: string;
    name: string;
    chatId: string;
    status: string;
    delay: number;
    deliveryProbability: number;
    notDeliveredMessage: string;
    delayedAlertMessage: string;
    extraText: string;
}

export interface ExtraButton extends ButtonProps {
    callback: () => void;
    disbled?: boolean;
    label: string;
    colorScheme?: string;
    rightIcon?: ReactElement;
    closeOnAction?: boolean;
}

export type StrategyStatus = "ACTIVE" | "INACTIVE" | "PAPER_BET";

export type StrategyListItem = {
    id: string;
    name: string;
    status: StrategyStatus;
    bets: number;
    result: number;
    roi: number;
    activeResult: number;
    activeRoi: number;
}

export interface FifaLeagueResponse {
    id: string;
    integrationId: number;
    name: string;
    link: string;
}

export interface FifaPlayerResponse {
    id: string;
    leagueId?: string;
    name: string;
}

export enum FifaMarketTypes {
    MATCH_ODDS = "MATCH_ODDS",
    GOAL_LINE = "GOAL_LINE"
}

export enum FifaMarketSubTypes {
    WINNER = "WINNER",
    DRAW = "DRAW",
    OVER = "OVER",
    UNDER = "UNDER"
}

export const FifaMarketSubTypesParent: { [key in FifaMarketSubTypes]: FifaMarketTypes } = {
    [FifaMarketSubTypes.WINNER]: FifaMarketTypes.MATCH_ODDS,
    [FifaMarketSubTypes.DRAW]: FifaMarketTypes.MATCH_ODDS,
    [FifaMarketSubTypes.OVER]: FifaMarketTypes.GOAL_LINE,
    [FifaMarketSubTypes.UNDER]: FifaMarketTypes.GOAL_LINE
};

export enum FifaRuleTypes {
    MINIMUM_ODDS = "MINIMUM_ODDS",
    MINIMUM_JUICE = "MINIMUM_JUICE",
    MINIMUM_PROBABILITY = "MINIMUM_PROBABILITY",
    MINIMUM_MATCHES = "MINIMUM_MATCHES"
}

export enum FifaMatchupTypes {
    VS_ANYONE = "VS_ANYONE",
    VS_EACH_OTHER = "VS_EACH_OTHER"
}

export enum FifaStrategyScopeTypes {
    HOURS = "HOURS",
    DAYS = "DAYS",
    MATCHES = "MATCHES"
}

export interface MarketType {
    marketType: FifaMarketTypes;
    marketSubTypes: FifaMarketSubTypes[];
}

export enum RuleValueFormatTypes {
    INTEGER = "INTEGER",
    PERCENTAGE = "PERCENTAGE",
    ODD = "ODD"
}

export interface RuleTypeDetail {
    type: FifaRuleTypes;
    minValue: number;
    maxValue: number;
    defaultValue: number;
    format: RuleValueFormatTypes;
}

export interface StrategyParams {
    leagues: FifaLeagueResponse[];
    marketTypes: MarketType[];
    players: FifaPlayerResponse[];
    ruleTypes: RuleTypeDetail[];
    matchupTypes: FifaMatchupTypes[];
    scopeTypes: FifaStrategyScopeTypes[];
}

export interface Rule {
    id?: string;
    type: string;
    value: number;
}

export interface Scope {
    id?: string;
    matchup: string;
    type: string;
    value: number;
    rules: Rule[];
}

export interface StrategyCreationBody {
    id?: string;
    name: string;
    marketType: string | null;
    marketSubTypes: string[];
    leagues: string[];
    excludedPlayers: string[];
    scopes: Scope[];
}

export type Option = {
    value: string;
    label: string;
};
