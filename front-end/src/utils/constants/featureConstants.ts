export enum FEATURES {
    EARLY_ACCESS = "early-access",
    FIFA_STRATEGY = "fifa-strategy",
    MESSENGER_CHAT = "messenger-chat",
    SIMPLE_REPORT = "simple-report",
    DETAILED_REPORT = "detailed-report",
    NO_ADS_MESSAGES = "no-ads-messages"
}

export const FEATURE_LABELS = {
    [FEATURES.EARLY_ACCESS]: "Acesso Antecipado",
    [FEATURES.FIFA_STRATEGY]: "Estratégia de E-Soccer",
    [FEATURES.MESSENGER_CHAT]: "Chat do Telegram",
    [FEATURES.SIMPLE_REPORT]: "Relatório Simples",
    [FEATURES.DETAILED_REPORT]: "Relatório Detalhado",
    [FEATURES.NO_ADS_MESSAGES]: "Mensagens sem identificação da plataforma"
};
