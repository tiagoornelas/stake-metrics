import {
    FifaMarketSubTypes,
    FifaMarketTypes,
    FifaMatchupTypes,
    FifaRuleTypes,
    FifaStrategyScopeTypes,
    RuleValueFormatTypes
} from "../interfaces";

export const strategyStatusDict = {
    ACTIVE: {
        color: "green",
        name: "Ativa",
        actionText: "Ativar"
    },
    PAPER_BET: {
        color: "yellow",
        name: "Paper Bet",
        actionText: "Mudar para Paper Bet"
    },
    INACTIVE: {
        color: "red",
        name: "Inativa",
        actionText: "Desativar"
    },
};

export const FifaMarketTypesDict: { [key in FifaMarketTypes]: string } = {
    [FifaMarketTypes.MATCH_ODDS]: "Match Odds",
    [FifaMarketTypes.GOAL_LINE]: "Linha de Gols"
};

export const FifaMarketSubTypesDict: { [key in FifaMarketSubTypes]: string } = {
    [FifaMarketSubTypes.WINNER]: "Vencedor",
    [FifaMarketSubTypes.DRAW]: "Empate",
    [FifaMarketSubTypes.OVER]: "Over",
    [FifaMarketSubTypes.UNDER]: "Under"
};

export const FifaRuleTypesDict: { [key in FifaRuleTypes]: string } = {
    [FifaRuleTypes.MINIMUM_ODDS]: "Odd maior que",
    [FifaRuleTypes.MINIMUM_JUICE]: "Juice maior que",
    [FifaRuleTypes.MINIMUM_PROBABILITY]: "Chance maior que",
    [FifaRuleTypes.MINIMUM_MATCHES]: "Mínimo de partidas",
};

export const FifleRuleTypesFormatDict: { [key in RuleValueFormatTypes]: string } = {
    [RuleValueFormatTypes.INTEGER]: "N",
    [RuleValueFormatTypes.PERCENTAGE]: "%",
    [RuleValueFormatTypes.ODD]: "Odd"
};

export const FifaMatchupTypesDict: { [key in FifaMatchupTypes]: string } = {
    [FifaMatchupTypes.VS_ANYONE]: "Qualquer",
    [FifaMatchupTypes.VS_EACH_OTHER]: "Mesmo"
};

export const FifaStrategyScopeTypesDict: { [key in FifaStrategyScopeTypes]: string } = {
    [FifaStrategyScopeTypes.TWENTY_FOUR_HOURS]: "Últimas 24 horas",
    [FifaStrategyScopeTypes.THREE_DAYS]: "Últimos 3 dias",
    [FifaStrategyScopeTypes.ONE_WEEK]: "Última 1 semana",
    [FifaStrategyScopeTypes.TWO_WEEKS]: "Últimas 2 semanas",
    [FifaStrategyScopeTypes.ONE_MONTH]: "Último 1 mês",
    [FifaStrategyScopeTypes.TWO_MONTHS]: "Últimos 2 meses",
};
