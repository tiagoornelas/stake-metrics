package com.saas.backend.plugins.subscription.enums

enum class EntitlementTypes(val identifier: String, val featureType: FeatureTypes, val amount: Int) {
    ACTIVE_STRATEGY_1("active-strategy-1", FeatureTypes.ACTIVE_STRATEGY, 1),
    ACTIVE_STRATEGY_3("active-strategy-3", FeatureTypes.ACTIVE_STRATEGY, 3),
    ACTIVE_STRATEGY_5("active-strategy-5", FeatureTypes.ACTIVE_STRATEGY, 5),
    TEST_STRATEGY_1("test-strategy-1", FeatureTypes.TEST_STRATEGY, 1),
    TEST_STRATEGY_3("test-strategy-3", FeatureTypes.TEST_STRATEGY, 3),
    TEST_STRATEGY_5("test-strategy-5", FeatureTypes.TEST_STRATEGY, 5),
}