export enum FEATURES {
    ACTIVE_STRATEGY = "active-strategy",
    TEST_STRATEGY = "test-strategy",
    TELEGRAM_CHAT = "telegram-chat"
}

export const FEATURE_LABELS = {
    [FEATURES.ACTIVE_STRATEGY]: "Estratégia ativa",
    [FEATURES.TEST_STRATEGY]: "Estratégia em teste",
    [FEATURES.TELEGRAM_CHAT]: "Chat do Telegram"
};