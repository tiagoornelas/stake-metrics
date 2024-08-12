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
        name: "Ativo",
        actionText: "Ativar"
    },
    PAPER_BET: {
        color: "yellow",
        name: "Paper Bet",
        actionText: "Mudar para Paper Bet"
    },
    INACTIVE: {
        color: "red",
        name: "Inativo",
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
    [FifaRuleTypes.MINIMUM_PROBABILITY]: "Chance maior que"
};

export const FifleRuleTypesFormatDict: { [key in RuleValueFormatTypes]: string } = {
    [RuleValueFormatTypes.PERCENTAGE]: "%",
    [RuleValueFormatTypes.ODD]: "Odd",
};

export const FifaMatchupTypesDict: { [key in FifaMatchupTypes]: string } = {
    [FifaMatchupTypes.VS_ANYONE]: "Qualquer",
    [FifaMatchupTypes.VS_EACH_OTHER]: "Mesmo"
};

export const FifaStrategyScopeTypesDict: { [key in FifaStrategyScopeTypes]: string } = {
    [FifaStrategyScopeTypes.HOURS]: "Horas",
    [FifaStrategyScopeTypes.DAYS]: "Dias",
    [FifaStrategyScopeTypes.MATCHES]: "Partidas"
};
