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
import FifaBets from "containers/fifa/strategy/bets/FifaBets";
import StrategyNameAndStatus from "containers/fifa/strategy/components/StrategyNameAndStatus";
import useStrategyCumulativeProfitsQuery from "containers/fifa/strategy/hooks/useStrategyCumulativeProfitsQuery";
import useThemeColors from "hooks/useThemeColors";
import React from 'react';
import {StrategyListItem} from "utils/interfaces";

type Props = {
    isOpen: boolean
    onClose: () => void
    strategy: StrategyListItem
}

type DiffType = "increase" | "decrease" | undefined;

const StrategyBetsDrawer = ({isOpen, onClose, strategy}: Props) => {
    const colors = useThemeColors();
    const {data = [], isLoading} = useStrategyCumulativeProfitsQuery(strategy.id);

    const formatPercentage = (value: number) => `${Math.round(value * 10000) / 100} %`;

    const getColorScheme = (value: number) => {
        if (value > 0) return 'green.500';
        if (value < 0) return 'red.500';
        return colors.contrast;
    };

    const calculateDiff = (value: number, comparedValue: number): { type: DiffType, text: string } | null => {
        const diff = Math.round(((value - comparedValue) / Math.abs(comparedValue)) * 100);
        if (diff === 0 || Math.abs(diff) === 100) {
            return null;
        }
        return {
            type: diff > 0 ? 'increase' : 'decrease',
            text: `${Math.abs(diff)}%`
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
                    <SimpleGrid columns={{base: 2, md: 3, lg: 6}} spacing={4} mt={4}
                                justifyContent="center">
                        {showTodaysStats && (
                            <>
                                <Stat>
                                    <StatLabel>Un Hoje</StatLabel>
                                    <StatNumber
                                        color={getColorScheme(strategy.todaysResult)}>{`${strategy.todaysResult.toFixed(2)} u`}</StatNumber>
                                </Stat>
                                <Stat>
                                    <StatLabel>ROI Hoje</StatLabel>
                                    <StatNumber
                                        color={getColorScheme(strategy.todaysRoi)}>{formatPercentage(strategy.todaysRoi)}</StatNumber>
                                    {strategy.roi !== 0 && calculateDiff(strategy.todaysRoi, strategy.roi) && (
                                        <StatHelpText>
                                            <StatArrow type={calculateDiff(strategy.todaysRoi, strategy.roi)!.type}/>
                                            {calculateDiff(strategy.todaysRoi, strategy.roi)!.text}
                                        </StatHelpText>
                                    )}
                                </Stat>
                            </>
                        )}
                        <Stat>
                            <StatLabel>Un Ativas</StatLabel>
                            <StatNumber
                                color={getColorScheme(strategy.activeResult)}>{`${strategy.activeResult.toFixed(2)} u`}</StatNumber>
                        </Stat>
                        <Stat>
                            <StatLabel>ROI Ativa</StatLabel>
                            <StatNumber
                                color={getColorScheme(strategy.activeRoi)}>{formatPercentage(strategy.activeRoi)}</StatNumber>
                            {strategy.roi !== 0 && calculateDiff(strategy.activeRoi, strategy.roi) && (
                                <StatHelpText>
                                    <StatArrow type={calculateDiff(strategy.activeRoi, strategy.roi)!.type}/>
                                    {calculateDiff(strategy.activeRoi, strategy.roi)!.text}
                                </StatHelpText>
                            )}
                        </Stat>
                        <Stat>
                            <StatLabel>Un Totais</StatLabel>
                            <StatNumber
                                color={getColorScheme(strategy.result)}>{`${strategy.result.toFixed(2)} u`}</StatNumber>
                        </Stat>
                        <Stat>
                            <StatLabel>ROI Total</StatLabel>
                            <StatNumber
                                color={getColorScheme(strategy.roi)}>{formatPercentage(strategy.roi)}</StatNumber>
                        </Stat>
                    </SimpleGrid>
                    <FifaBets strategyId={strategy.id}/>
                </DrawerBody>
            </DrawerContent>
        </Drawer>
    );
};

export default StrategyBetsDrawer;
