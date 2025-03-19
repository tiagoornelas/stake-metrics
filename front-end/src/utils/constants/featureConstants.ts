export enum FEATURES {
    EARLY_ACCESS = "early-access",
    FIFA_STRATEGY = "fifa-strategy",
    MESSENGER_CHAT = "messenger-chat",
    SIMPLE_REPORT = "simple-report",
    DETAILED_REPORT = "detailed-report",
    NO_ADS_MESSAGES = "no-ads-messages",
    AUTO_BETTOR = "auto-bettor",
}

export const FEATURE_LABELS = {
    [FEATURES.EARLY_ACCESS]: "Acesso antecipado",
    [FEATURES.FIFA_STRATEGY]: "Estratégia de E-Soccer",
    [FEATURES.MESSENGER_CHAT]: "Chat do Telegram",
    [FEATURES.SIMPLE_REPORT]: "Relatório simples",
    [FEATURES.DETAILED_REPORT]: "Relatório detalhado",
    [FEATURES.NO_ADS_MESSAGES]: "Mensagens sem identificação da plataforma",
    [FEATURES.AUTO_BETTOR]: "Integração com automação de apostas",
};
