import {Input} from "@chakra-ui/react";
import React, {useEffect, useState} from 'react';
import {Rule, RuleTypeDetail, RuleValueFormatTypes} from "utils/interfaces";

type Props = {
    onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
    rule: Rule;
    ruleTypeDetail?: RuleTypeDetail;
    isInvalid: boolean;
}

const StrategyRuleValueInput = ({onChange, rule, ruleTypeDetail, isInvalid}: Props) => {
    const isPercentage = ruleTypeDetail?.format === RuleValueFormatTypes.PERCENTAGE;
    const defaultValue = isPercentage ? Number(rule.value * 100) : Number(rule.value);
    const [displayedValue, setDisplayedValue] = useState<number>(defaultValue);

    const minValue = (isPercentage ? ruleTypeDetail?.minValue * 100 : ruleTypeDetail?.minValue) || 0;
    const maxValue = (isPercentage ? ruleTypeDetail?.maxValue * 100 : ruleTypeDetail?.maxValue) || 1000;

    useEffect(() => {
        if (isPercentage) {
            setDisplayedValue(Math.round(Number(rule.value * 100)));
        } else {
            setDisplayedValue(Math.round(Number(rule.value)));
        }
        //eslint-disable-next-line
    }, [rule.type, isPercentage]);

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
                   max={maxValue}
                   isInvalid={isInvalid}
            />
        </>
    );
};

export default StrategyRuleValueInput;
