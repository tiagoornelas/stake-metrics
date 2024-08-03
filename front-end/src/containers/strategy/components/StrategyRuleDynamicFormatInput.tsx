import {Input} from "@chakra-ui/react";
import React, {useState} from 'react';
import {Rule, RuleTypeDetail, RuleValueFormatTypes} from "utils/interfaces";

type Props = {
    onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
    rule: Rule;
    ruleTypeDetail?: RuleTypeDetail;
}

const StrategyRuleDynamicFormatInput = ({onChange, rule, ruleTypeDetail}: Props) => {
    const isPercentage = ruleTypeDetail?.format === RuleValueFormatTypes.PERCENTAGE;
    const defaultValue = isPercentage ? Number(rule.value * 100) : Number(rule.value);
    const [displayedValue, setDisplayedValue] = useState<number>(defaultValue);

    const minValue = isPercentage ? ruleTypeDetail?.minValue * 100 : ruleTypeDetail?.minValue;
    const maxValue = isPercentage ? ruleTypeDetail?.maxValue * 100 : ruleTypeDetail?.maxValue;

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newValue = Number(e.target.value);
        setDisplayedValue(newValue);
        const event = {
            ...e,
            target: {
                ...e.target,
                value: isPercentage ? (newValue / 100).toString() : newValue.toString()
            }
        };
        onChange(event as React.ChangeEvent<HTMLInputElement>);
    }

    return (
        <>
            <Input id="value" type="number"
                   onChange={handleChange}
                   value={displayedValue}
                   min={minValue}
                   max={maxValue}/>
        </>
    );
};

export default StrategyRuleDynamicFormatInput;
