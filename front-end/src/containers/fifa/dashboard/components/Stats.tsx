import { Box, Grid, Skeleton, Stat, StatLabel, StatNumber, StatHelpText } from "@chakra-ui/react";
import useThemeColors from "hooks/useThemeColors";
import useTranslation from "hooks/useTranslation";
import { formatPercentage, formatProfit } from "utils/helpers/formatHelpers";
import { BetStatistics } from "utils/interfaces";

interface StatCardProps {
  label: string;
  value: string;
  helper?: string;
  valueColor?: string;
}

const StatCard: React.FC<StatCardProps> = ({ label, value, helper, valueColor }) => {
  const colors = useThemeColors();
  return (
    <Stat p={4} bg={colors.contrast} borderRadius="md" boxShadow="sm">
      <StatLabel fontSize="sm" color={colors.textContrast}>{label}</StatLabel>
      <StatNumber fontSize="2xl" fontWeight="bold" color={valueColor || colors.textContrast}>{value}</StatNumber>
      {helper && <StatHelpText fontSize="xs" color={colors.textContrast}>{helper}</StatHelpText>}
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

interface StatsProps {
  statistics?: BetStatistics;
  isLoading?: boolean;
}

const Stats: React.FC<StatsProps> = ({ statistics, isLoading }) => {
  const { t } = useTranslation();

  const getColorScheme = (value: number) => {
    if (value > 0) return 'green.500';
    if (value < 0) return 'red.500';
    return undefined;
  };

  if (isLoading) {
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
      <StatCard 
        label={t('dashboard.openBets')} 
        value={statistics?.openBets?.toString() || '0'}
        helper={statistics?.possibleProfitOnOpenBets != null 
          ? (statistics.possibleProfitOnOpenBets === 0 
            ? t('dashboard.noPendingProfit') 
            : `${t('dashboard.possibleProfit')}: ${formatProfit(statistics.possibleProfitOnOpenBets)}`)
          : undefined}
      />

      <StatCard
        label={t('dashboard.dailyProfit')}
        value={formatProfit(statistics?.dayStatistics?.profit || 0)}
        valueColor={getColorScheme(statistics?.dayStatistics?.profit || 0)}
        helper={`ROI ${formatPercentage(statistics?.dayStatistics?.roi || 0)}`}
      />

      <StatCard
        label={t('dashboard.monthlyProfit')}
        value={formatProfit(statistics?.monthStatistics?.profit || 0)}
        valueColor={getColorScheme(statistics?.monthStatistics?.profit || 0)}
        helper={`ROI ${formatPercentage(statistics?.monthStatistics?.roi || 0)}`}
      />

      <StatCard
        label={t('dashboard.monthlyVolume')}
        value={statistics?.monthStatistics?.numberOfBets || '0'}
        helper={`${statistics?.dayStatistics?.numberOfBets || '0'} ${t('dashboard.today')}`}
      />
    </Grid>
  );
};

export default Stats;
