import { Box, Grid, Skeleton, Stat, StatLabel, StatNumber, StatHelpText, Tag, Flex } from "@chakra-ui/react";
import useThemeColors from "hooks/useThemeColors";
import { LeagueTrend } from "utils/interfaces";
import { determineTrend, TREND_COLORS } from "../../../../utils/helpers/trendHelpers";

interface TrendStatCardProps {
  leagueName: string;
  historicalValues: number[];
  colorIndex: number;
}

const TrendStatCard: React.FC<TrendStatCardProps> = ({ 
  leagueName,
  historicalValues,
  colorIndex 
}) => {
  const colors = useThemeColors();

  const currentValue = historicalValues[historicalValues.length - 1];
  const formattedCurentValue = currentValue > 0 ? `+${currentValue}` : currentValue;
  const trend = determineTrend(historicalValues);
  const leagueColor = TREND_COLORS[colorIndex % TREND_COLORS.length];

  return (
    <Stat p={4} bg={colors.contrast} borderRadius="md" boxShadow="sm">
      <Flex alignItems="center" gap={2} mb={1}>
        <StatLabel fontSize="sm" color={colors.textContrast} mb={0} whiteSpace="nowrap" overflow="hidden" textOverflow="ellipsis">
          {leagueName}
        </StatLabel>
        <Box 
          display="inline-block"
          w="10px" 
          h="10px" 
          borderRadius="sm" 
          bg={leagueColor}
          flexShrink={0}
        />
      </Flex>
      <StatNumber fontSize="2xl" fontWeight="bold" color={colors.textContrast}>{formattedCurentValue}</StatNumber>
      <StatHelpText fontSize="xs" color={colors.textContrast}>
        <Tag size="sm" colorScheme={trend.colorScheme}>
          {trend.label}
        </Tag>
      </StatHelpText>
    </Stat>
  );
};

const StatCardSkeleton = () => {
  const colors = useThemeColors();
  return (
    <Box p={4} bg={colors.contrast} borderRadius="md" boxShadow="sm">
      <Skeleton height="16px" width="120px" mb={2} />
      <Skeleton height="30px" width="80px" mb={2} />
      <Skeleton height="14px" width="100px" />
    </Box>
  );
};

interface Props {
  isLoaded: boolean;
  leagueTrends: LeagueTrend[];
}

const GoalsTrendStats = ({ isLoaded, leagueTrends }: Props) => {
  if (!isLoaded) {
    return (
      <Grid templateColumns={{ base: "repeat(2, 1fr)", md: "repeat(4, 1fr)" }} gap={4} mb={8}>
        {[...Array(4)].map((_, i) => (
          <StatCardSkeleton key={i} />
        ))}
      </Grid>
    );
  }

  return (
    <Grid templateColumns={{ base: "repeat(2, 1fr)", md: "repeat(4, 1fr)" }} gap={4} mb={8}>
      {leagueTrends.map((trend, index) => (
        <TrendStatCard 
          key={trend.league.id}
          leagueName={trend.league.name}
          historicalValues={trend.trend}
          colorIndex={index}
        />
      ))}
    </Grid>
  );
};

export default GoalsTrendStats;
