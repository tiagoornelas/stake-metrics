import {Td, Tr, useBreakpointValue, useColorModeValue, useDisclosure} from "@chakra-ui/react";
import StrategyBetsDrawer from "containers/fifa/strategy/components/StrategyBetsDrawer";
import StrategyNameAndStatus from "containers/fifa/strategy/components/StrategyNameAndStatus";
import StrategyTableActions from "containers/fifa/strategy/components/StrategyTableActions";
import useThemeColors from "hooks/useThemeColors";
import React from 'react';
import {StrategyListItem} from "utils/interfaces";

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
    const {isOpen, onOpen, onClose} = useDisclosure();
    const isLargerThanLg = useBreakpointValue({base: false, lg: true});
    const hoverBgColor = useColorModeValue("gray.100", "gray.700");

    return (
        <>
            <Tr onClick={onOpen} key={strategy.id} style={{cursor: "pointer"}}
                sx={{_hover: {backgroundColor: hoverBgColor}}}>
                <Td><StrategyNameAndStatus status={strategy.status} name={strategy.name}/></Td>
                <Td textAlign={"center"}>{strategy.openBets}</Td>
                <Td textAlign={"center"}>{strategy.bets}</Td>
                <ResultData value={Number(strategy.result.toFixed(1))}/>
                <ROIData value={Number(strategy.roi)}/>
                {isLargerThanLg && (
                    <>
                        <ResultData value={Number(strategy.activeResult.toFixed(1))}/>
                        <ROIData value={Number(strategy.activeRoi)}/>
                    </>
                )}
                <Td><StrategyTableActions strategy={strategy}/></Td>
            </Tr>

            <StrategyBetsDrawer isOpen={isOpen} onClose={onClose} strategy={strategy}/>
        </>
    );
};

export default StrategyTableItem;
