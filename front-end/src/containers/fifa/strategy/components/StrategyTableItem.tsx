import {Td, Tr, useColorModeValue, useDisclosure} from "@chakra-ui/react";
import StrategyBetsDrawer from "containers/fifa/strategy/components/StrategyBetsDrawer";
import StrategyNameAndStatus from "containers/fifa/strategy/components/StrategyNameAndStatus";
import StrategyTableActions from "containers/fifa/strategy/components/StrategyTableActions";
import useThemeColors from "hooks/useThemeColors";
import React from 'react';
import {StrategyListItem} from "utils/interfaces";
import { formatPercentage, formatProfit } from "utils/helpers/formatHelpers";

const ResultData = ({value}: { value: number }) => {
    const colors = useThemeColors();
    const color = value > 0 ? "green" : value < 0 ? "red" : colors.contrast;

    return (
        <Td textAlign={"center"} color={color}>
            {formatProfit(value)}
        </Td>
    );
};

const ROIData = ({value}: { value: number }) => {
    const colors = useThemeColors();
    const color = value > 0 ? "green" : value < 0 ? "red" : colors.contrast;

    return (
        <Td textAlign={"center"} color={color}>
            {formatPercentage(value)}
        </Td>
    );
};

const StrategyTableItem = ({strategy}: { strategy: StrategyListItem }) => {
    const {isOpen, onOpen, onClose} = useDisclosure();
    const hoverBgColor = useColorModeValue("gray.100", "gray.700");

    return (
        <>
            <Tr onClick={onOpen} key={strategy.id} style={{cursor: "pointer"}}
                sx={{_hover: {backgroundColor: hoverBgColor}}}>
                <Td><StrategyNameAndStatus status={strategy.status} name={strategy.name}/></Td>
                <Td textAlign={"center"}>{strategy.openBets}</Td>
                <Td textAlign={"center"}>{strategy.bets}</Td>
                <ResultData value={Number(strategy.todaysResult.toFixed(2))}/>
                <ResultData value={Number(strategy.result.toFixed(2))}/>
                <ROIData value={Number(strategy.roi)}/>
                <Td><StrategyTableActions strategy={strategy}/></Td>
            </Tr>

            <StrategyBetsDrawer isOpen={isOpen} onClose={onClose} strategy={strategy}/>
        </>
    );
};

export default StrategyTableItem;
