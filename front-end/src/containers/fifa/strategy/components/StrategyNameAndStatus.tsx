import {Badge, Flex, Text} from "@chakra-ui/react";
import React from 'react';
import {strategyStatusDict} from "utils/constants/strategyConstants";

type StrategyNameAndStatusProps = {
    status: "ACTIVE" | "INACTIVE" | "PAPER_BET";
    name: string;
}

const StrategyNameAndStatus = ({status, name}: StrategyNameAndStatusProps) => {
    return (
        <Flex direction={"row"} alignItems={"center"} gap={2}><Text>{name}</Text><Badge
            colorScheme={strategyStatusDict[status].color}>{strategyStatusDict[status].name}</Badge></Flex>
    )
}

export default StrategyNameAndStatus;
