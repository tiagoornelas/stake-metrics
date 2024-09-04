import {
    Box,
    Drawer,
    DrawerBody,
    DrawerCloseButton,
    DrawerContent,
    DrawerHeader,
    DrawerOverlay,
    SimpleGrid,
    Skeleton,
    Stat,
    StatArrow,
    StatHelpText,
    StatLabel,
    StatNumber,
    useBreakpointValue
} from "@chakra-ui/react";
import ProfitOverBetsLineChart from "components/charts/ProfitOverBetsLineChart";
import StrategyNameAndStatus from "containers/fifa/strategy/components/StrategyNameAndStatus";
import useStrategyCumulativeProfitsQuery from "containers/fifa/strategy/hooks/useStrategyCumulativeProfitsQuery";
import React from 'react';
import {StrategyListItem} from "utils/interfaces";

type Props = {
    isOpen: boolean
    onClose: () => void
    strategy: StrategyListItem
}

type DiffType = "increase" | "decrease" | undefined;

const StrategyBetsDrawer = ({isOpen, onClose, strategy}: Props) => {
    const {data = [], isLoading} = useStrategyCumulativeProfitsQuery(strategy.id);

    const formatPercentage = (value: number) => `${Math.round(value * 10000) / 100} %`;

    const getColorScheme = (value: number) => {
        if (value > 0) return 'green';
        if (value < 0) return 'red';
        return 'gray';
    };

    const calculateDiff = (value: number, comparedValue: number): { type: DiffType, text: string } | null => {
        const diff = ((value - comparedValue) / Math.abs(comparedValue)) * 100;
        if (diff === 0 || Math.abs(diff) === 100) {
            return null;
        }
        return {
            type: diff > 0 ? 'increase' : 'decrease',
            text: `${Math.abs(diff).toFixed(2)}%`
        };
    };

    const showTodaysStats = useBreakpointValue({base: false, md: true});

    return (
        <Drawer
            isOpen={isOpen}
            placement='right'
            onClose={onClose}
            size={"xl"}
        >
            <DrawerOverlay/>
            <DrawerContent>
                <DrawerCloseButton/>
                <DrawerHeader><StrategyNameAndStatus status={strategy.status} name={strategy.name}/></DrawerHeader>

                <DrawerBody>
                    <Skeleton isLoaded={!isLoading}>
                        <Box p={2} height={200}>
                            <ProfitOverBetsLineChart data={data}/>
                        </Box>
                    </Skeleton>
                    <SimpleGrid columns={{base: 2, md: 3, lg: 6}} spacing={4} mt={4} textAlign="center"
                                justifyContent="center">
                        {showTodaysStats && (
                            <>
                                <Stat colorScheme={getColorScheme(strategy.todaysResult)}>
                                    <StatLabel>Un Hoje</StatLabel>
                                    <StatNumber>{`${strategy.todaysResult.toFixed(2)} u`}</StatNumber>
                                </Stat>
                                <Stat colorScheme={getColorScheme(strategy.todaysRoi)}>
                                    <StatLabel>ROI Hoje</StatLabel>
                                    <StatNumber>{formatPercentage(strategy.todaysRoi)}</StatNumber>
                                    {strategy.roi !== 0 && calculateDiff(strategy.todaysRoi, strategy.roi) && (
                                        <StatHelpText>
                                            <StatArrow type={calculateDiff(strategy.todaysRoi, strategy.roi)!.type}/>
                                            {calculateDiff(strategy.todaysRoi, strategy.roi)!.text} (ROI Total)
                                        </StatHelpText>
                                    )}
                                </Stat>
                            </>
                        )}
                        <Stat colorScheme={getColorScheme(strategy.activeResult)}>
                            <StatLabel>Un Ativas</StatLabel>
                            <StatNumber>{`${strategy.activeResult.toFixed(2)} u`}</StatNumber>
                        </Stat>
                        <Stat colorScheme={getColorScheme(strategy.activeRoi)}>
                            <StatLabel>ROI Ativa</StatLabel>
                            <StatNumber>{formatPercentage(strategy.activeRoi)}</StatNumber>
                            {strategy.roi !== 0 && calculateDiff(strategy.activeRoi, strategy.roi) && (
                                <StatHelpText>
                                    <StatArrow type={calculateDiff(strategy.activeRoi, strategy.roi)!.type}/>
                                    {calculateDiff(strategy.activeRoi, strategy.roi)!.text} (ROI Total)
                                </StatHelpText>
                            )}
                        </Stat>
                        <Stat colorScheme={getColorScheme(strategy.result)}>
                            <StatLabel>Un Totais</StatLabel>
                            <StatNumber>{`${strategy.result.toFixed(2)} u`}</StatNumber>
                        </Stat>
                        <Stat colorScheme={getColorScheme(strategy.roi)}>
                            <StatLabel>ROI Total</StatLabel>
                            <StatNumber>{formatPercentage(strategy.roi)}</StatNumber>
                            {strategy.activeRoi !== 0 && calculateDiff(strategy.roi, strategy.activeRoi) && (
                                <StatHelpText>
                                    <StatArrow type={calculateDiff(strategy.roi, strategy.activeRoi)!.type}/>
                                    {calculateDiff(strategy.roi, strategy.activeRoi)!.text} (ROI Ativa)
                                </StatHelpText>
                            )}
                        </Stat>
                    </SimpleGrid>
                </DrawerBody>
            </DrawerContent>
        </Drawer>
    );
};

export default StrategyBetsDrawer;
