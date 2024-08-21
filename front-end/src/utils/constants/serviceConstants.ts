export const BASE_URL = process.env.REACT_APP_BASE_URL;

export const ENDPOINTS = {
    LOGIN: {
        BASE: "/login"
    },
    USER: {
        BASE: "/user",
        CHANGE_PASSWORD: "/password",
        RECOVER_RECEIVE: "/recover-receive",
        RECOVER_APPLY: "/recover-apply",
    },
    RECOVER: {
        BASE: "/recover"
    },
    SUBSCRIPTION: {
        BASE: "/subscription",
        PRICING_TABLE: "/plan/pricing-table",
        CREATE_PORTAL_SESSION: "/create-portal-session",
    },
    TELEGRAM: {
        BASE: "/messenger",
        BEGIN_PRIVATE_CHAT_INTEGRATION: "/private-chat/begin-integration",
        INTEGRATE_PRIVATE_CHAT: "/private-chat/integrate",
        INTEGRATE_CHANNEL: "/channel/integrate",
        TEST_MESSAGE: "/test-message"
    },
    FIFA: {
        BASE: "/fifa"
    },
    STRATEGY: {
        BASE: "/strategy",
        PARAMS: "/params",
        STATUS: "/status",
        STATISTICS: "/statistics"
    }
}
