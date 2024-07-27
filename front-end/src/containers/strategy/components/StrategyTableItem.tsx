import {Badge, Flex, Td, Text, Tr} from "@chakra-ui/react";
import StrategyTableActions from "containers/strategy/components/StrategyTableActions";
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
    const color = value > 0 ? "green" : value < 0 ? "red" : "black";
    const formattedValue = `${value > 0 ? "+" : ""}${value} u`;

    return (
        <Td textAlign={"center"} color={color}>
            {formattedValue}
        </Td>
    );
};

const ROIData = ({value}: { value: number }) => {
    const color = value > 0 ? "green" : value < 0 ? "red" : "black";
    const formattedValue = `${value > 0 ? "+" : ""}${value} %`;

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
            <Td textAlign={"center"}>{strategy.bets}</Td>
            <ResultData value={Number(strategy.result)}/>
            <ROIData value={Number(strategy.roi)}/>
            <ResultData value={Number(strategy.activeResult)}/>
            <ROIData value={Number(strategy.activeRoi)}/>
            <Td><StrategyTableActions strategy={strategy}/></Td>
        </Tr>
    );
};

export default StrategyTableItem;
