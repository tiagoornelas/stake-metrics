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
    }
}