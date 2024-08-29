import {StrategyCreationBody, StrategyParams} from "utils/interfaces";

export const validateForm = (form: StrategyCreationBody, formParams: StrategyParams) => {
    const errors: { [key: string]: boolean } = {};

    errors.name = !form.name;
    errors.marketSubTypes = form.marketSubTypes.length === 0;
    errors.leagues = form.leagues.length === 0;

    if (form.scopes.length === 0) {
        errors.scopes = true;
    } else {
        form.scopes.forEach((scope, index) => {
            scope.rules.forEach((rule, ruleIndex) => {
                const ruleDetails = formParams.ruleTypes.find(r => r.type === rule.type);
                if (ruleDetails) {
                    errors[`scope-${index}-rule-${ruleIndex}-value`] = rule.value < ruleDetails.minValue || rule.value > ruleDetails.maxValue;
                } else {
                    errors[`scope-${index}-rule-${ruleIndex}-value`] = false;
                }
            });
        });
    }

    return errors;
};

export const isFormValid = (errors: { [key: string]: boolean }) => {
    return Object.keys(errors).every(key => !errors[key]);
}
