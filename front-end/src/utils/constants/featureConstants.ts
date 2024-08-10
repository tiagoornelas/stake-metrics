export enum FEATURES {
    ACTIVE_STRATEGY = "active-strategy",
    TEST_STRATEGY = "test-strategy",
    MESSENGER_CHAT = "messenger-chat"
}

export const FEATURE_LABELS = {
    [FEATURES.ACTIVE_STRATEGY]: "Estratégia ativa",
    [FEATURES.TEST_STRATEGY]: "Estratégia em teste",
    [FEATURES.MESSENGER_CHAT]: "Chat do Telegram"
};
