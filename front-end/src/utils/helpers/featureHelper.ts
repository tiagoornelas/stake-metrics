import {UserInfo} from "utils/interfaces";

export const getFeatureAmount = (user: UserInfo, featureName: string): number => {
    const features = user.subscription?.features;

    if(!features) return 0;
    return features[featureName] || 0;
}