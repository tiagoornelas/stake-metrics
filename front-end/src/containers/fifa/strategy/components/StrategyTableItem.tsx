import {Badge, Flex, Td, Text, Tr} from "@chakra-ui/react";
import StrategyTableActions from "containers/fifa/strategy/components/StrategyTableActions";
import useThemeColors from "hooks/useThemeColors";
import React from 'react';
import {strategyStatusDict} from "utils/constants/strategyConstants";
import {StrategyListItem} from "utils/interfaces";

type StrategyNameAndStatusProps = {
    status: "ACTIVE" | "INACTIVE" | "PAPER_BET";
    name: string;
}

const StrategyNameAndStatus = ({status, name}: StrategyNameAndStatusProps) => {
    return (
        <Flex direction={"row"} gap={2}><Text>{name}</Text><Badge
            colorScheme={strategyStatusDict[status].color}>{strategyStatusDict[status].name}</Badge></Flex>
    )
}

const ResultData = ({value}: { value: number }) => {
    const colors = useThemeColors();
    const color = value > 0 ? "green" : value < 0 ? "red" : colors.contrast;
    const formattedValue = `${value > 0 ? "+" : ""}${value} u`;

    return (
        <Td textAlign={"center"} color={color}>
            {formattedValue}
        </Td>
    );
};

const ROIData = ({value}: { value: number }) => {
    const colors = useThemeColors();
    const color = value > 0 ? "green" : value < 0 ? "red" : colors.contrast;
    const percentageValue = value * 100;
    const roundedValue = Math.round(percentageValue * 100) / 100;
    const formattedValue = `${roundedValue > 0 ? "+" : ""}${roundedValue.toFixed(2)} %`;

    return (
        <Td textAlign={"center"} color={color}>
            {formattedValue}
        </Td>
    );
};
const StrategyTableItem = ({strategy}: { strategy: StrategyListItem }) => {
    return (
        <Tr key={strategy.id}>
            <Td><StrategyNameAndStatus status={strategy.status} name={strategy.name}/></Td>
            <Td textAlign={"center"}>{strategy.openBets}</Td>
            <Td textAlign={"center"}>{strategy.bets}</Td>
            <ResultData value={Number(strategy.result.toFixed(1))}/>
            <ROIData value={Number(strategy.roi)}/>
            <ResultData value={Number(strategy.activeResult.toFixed(1))}/>
            <ROIData value={Number(strategy.activeRoi)}/>
            <Td><StrategyTableActions strategy={strategy}/></Td>
        </Tr>
    );
};

export default StrategyTableItem;
