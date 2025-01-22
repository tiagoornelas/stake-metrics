package net.stakemetrics.application.entities.enums

enum class EntitlementTypes(val identifier: String, val featureType: FeatureTypes, val amount: Int) {
    FIFA_STRATEGY_1("fifa-strategy-1", FeatureTypes.FIFA_STRATEGY, 1),
    FIFA_STRATEGY_3("fifa-strategy-3", FeatureTypes.FIFA_STRATEGY, 3),
    FIFA_STRATEGY_6("fifa-strategy-6", FeatureTypes.FIFA_STRATEGY, 6),
    FIFA_STRATEGY_9("fifa-strategy-9", FeatureTypes.FIFA_STRATEGY, 9),
    MESSENGER_CHAT_1("messenger-chat-1", FeatureTypes.MESSENGER_CHAT, 1),
    MESSENGER_CHAT_2("messenger-chat-2", FeatureTypes.MESSENGER_CHAT, 2),
    MESSENGER_CHAT_3("messenger-chat-3", FeatureTypes.MESSENGER_CHAT, 3),
    SIMPLE_REPORT_1("simple-report-1", FeatureTypes.SIMPLE_REPORT, 1),
    DETAILED_REPORT_1("detailed-report-1", FeatureTypes.DETAILED_REPORT, 1),
    NO_ADS_MESSAGES_1("no-ads-messages-1", FeatureTypes.NO_ADS_MESSAGES, 1),
    BACKTEST_MODULE_1("backtest-module-1", FeatureTypes.BACKTEST_MODULE, 1),
}