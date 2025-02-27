import {FEATURE_LABELS, FEATURES} from "utils/constants/featureConstants";
import {UserInfo} from "utils/interfaces";

export const getFeatureAmount = (user: UserInfo, featureName: string): number => {
    const features = user.subscription?.features;

    if (!features) return 0;
    return features[featureName] || 0;
}

export const getFeaturesWithLabels = (user: UserInfo): Array<{ label: string, amount: number }> => {
    const result: Array<{ label: string, amount: number }> = [];

    if (!user.subscription?.features) {
        return result;
    }

    for (const featureKey in FEATURES) {
        if (FEATURES.hasOwnProperty(featureKey)) {
            const featureName = FEATURES[featureKey as keyof typeof FEATURES];
            
            if (featureName === FEATURES.EARLY_ACCESS && !user.subscription.features[FEATURES.EARLY_ACCESS]) {
                continue;
            }

            const featureLabel = FEATURE_LABELS[featureName];
            const featureAmount = getFeatureAmount(user, featureName);
            result.push({label: featureLabel, amount: featureAmount});
        }
    }

    return result;
}
