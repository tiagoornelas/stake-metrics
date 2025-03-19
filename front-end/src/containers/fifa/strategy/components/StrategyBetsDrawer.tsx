import {
  Box,
  Drawer,
  DrawerBody,
  DrawerCloseButton,
  DrawerContent,
  DrawerHeader,
  DrawerOverlay,
  Flex,
  Skeleton,
  Stat,
  StatLabel,
  StatNumber,
  Tag,
  useBreakpointValue,
} from "@chakra-ui/react";
import ProfitOverBetsLineChart from "components/charts/ProfitOverBetsLineChart";
import FifaBets from "containers/fifa/strategy/bets/FifaBets";
import StrategyNameAndStatus from "containers/fifa/strategy/components/StrategyNameAndStatus";
import useStrategyCumulativeProfitsQuery from "containers/fifa/strategy/hooks/useStrategyCumulativeProfitsQuery";
import useThemeColors from "hooks/useThemeColors";
import React from "react";
import { StrategyListItem } from "utils/interfaces";
import {
  formatPercentage,
  formatProfit,
  formatBetVolume,
} from "utils/helpers/formatHelpers";
import { compareToMovingAverage } from "utils/helpers/statisticHelper";

type Props = {
  isOpen: boolean;
  onClose: () => void;
  strategy: StrategyListItem;
};

const StrategyBetsDrawer = ({ isOpen, onClose, strategy }: Props) => {
  const colors = useThemeColors();
  const { data = [], isLoading } = useStrategyCumulativeProfitsQuery(
    strategy.id
  );

  const getColorScheme = (value: number) => {
    if (value > 0) return "green.500";
    if (value < 0) return "red.500";
    return colors.contrast;
  };

  const showExtraStats = useBreakpointValue({ base: false, md: true });
  const statFontSize = useBreakpointValue({ base: "md", sm: "xl" });

  const maComparison = compareToMovingAverage(data);

  const getMATagColor = () => {
    switch (maComparison.status) {
      case "above":
        return "green";
      case "below":
        return "red";
      case "equal":
        return "yellow";
      default:
        return "gray";
    }
  };

  const getMATagText = () => {
    switch (maComparison.status) {
      case "above":
        return "Acima da média";
      case "below":
        return "Abaixo da média";
      case "equal":
        return "Na média";
      default:
        return "Indisponível";
    }
  };

  const hasEnoughDataForTrend = data.length >= 10;

  return (
    <Drawer isOpen={isOpen} placement="right" onClose={onClose} size={"xl"}>
      <DrawerOverlay />
      <DrawerContent>
        <DrawerCloseButton />
        <DrawerHeader>
          <StrategyNameAndStatus
            status={strategy.status}
            name={strategy.name}
          />
        </DrawerHeader>

        <DrawerBody>
          <Skeleton isLoaded={!isLoading}>
            <Box p={2} height={200}>
              <ProfitOverBetsLineChart data={data} />
            </Box>
          </Skeleton>
          <Flex wrap="wrap" justifyContent="flex-end" mt={4}>
            {hasEnoughDataForTrend && (
              <Stat textAlign="center">
                <StatLabel>Fase atual</StatLabel>
                <StatNumber
                  fontSize={statFontSize}
                  display="flex"
                  justifyContent="center"
                >
                  <Tag colorScheme={getMATagColor()} size="md">
                    {getMATagText()}
                  </Tag>
                </StatNumber>
              </Stat>
            )}
            {showExtraStats && (
              <Stat textAlign="center">
                <StatLabel>Entradas</StatLabel>
                <StatNumber fontSize={statFontSize}>
                  {strategy.bets || 0}
                </StatNumber>
              </Stat>
            )}
            <Stat textAlign="center">
              <StatLabel>Volume</StatLabel>
              <StatNumber fontSize={statFontSize}>
                {formatBetVolume(strategy.averageDailyBets)}
              </StatNumber>
            </Stat>
            <Stat textAlign="center">
              <StatLabel>Hoje</StatLabel>
              <StatNumber
                fontSize={statFontSize}
                color={getColorScheme(strategy.todaysResult)}
              >
                {formatProfit(strategy.todaysResult)}
              </StatNumber>
            </Stat>
            <Stat textAlign="center">
              <StatLabel>Total</StatLabel>
              <StatNumber
                fontSize={statFontSize}
                color={getColorScheme(strategy.result)}
              >
                {formatProfit(strategy.result)}
              </StatNumber>
            </Stat>
            {showExtraStats && (
              <Stat textAlign="center">
                <StatLabel>ROI</StatLabel>
                <StatNumber
                  fontSize={statFontSize}
                  color={getColorScheme(strategy.roi)}
                >
                  {formatPercentage(strategy.roi)}
                </StatNumber>
              </Stat>
            )}
          </Flex>
          <FifaBets strategyId={strategy.id} />
        </DrawerBody>
      </DrawerContent>
    </Drawer>
  );
};

export default StrategyBetsDrawer;
