import { Box, Grid, Skeleton, Text } from "@chakra-ui/react";
import { format } from "date-fns";
import useThemeColors from "hooks/useThemeColors";
import useTranslation from "hooks/useTranslation";
import ProfitBarChart from "components/charts/ProfitBarChart";
import { BetStatistics } from "utils/interfaces";
import ChartEmptyState from "./ChartEmptyState";

const ChartSkeleton = () => (
  <Box>
    <Skeleton height="24px" width="150px" mb={4} />
    <Skeleton height="300px" borderRadius="md" />
  </Box>
);

interface ChartsProps {
  statistics?: BetStatistics;
  isLoading?: boolean;
}

const Charts: React.FC<ChartsProps> = ({ statistics, isLoading }) => {
  const colors = useThemeColors();
  const { t } = useTranslation();

  const formatMonthLabel = (startDate: string) => {
    const date = new Date(startDate);
    return format(date, 'MM/yy');
  };

  const dailyChartData = statistics?.last12DaysProfit?.map(day => ({
    name: format(new Date(day.date), 'dd/MM'),
    profit: day.profit
  })) || [];

  const monthlyChartData = statistics?.last12MonthsProfit?.map(month => ({
    name: formatMonthLabel(month.startDate),
    profit: month.profit
  })) || [];

  if (isLoading) {
    return (
      <Grid templateColumns={{ base: "1fr", lg: "1fr 1fr" }} gap={8}>
        {[...Array(2)].map((_, i) => (
          <ChartSkeleton key={i} />
        ))}
      </Grid>
    );
  }

  const renderChartEmptyState = !statistics?.last12DaysProfit?.length && !statistics?.last12MonthsProfit?.length;

  if (renderChartEmptyState) {
    return <ChartEmptyState />;
  }

  return (
    <Grid templateColumns={{ base: "1fr", lg: "1fr 1fr" }} gap={8}>
      <Box>
        <Text mb={4} fontWeight="bold" color={colors.textContrast}>{t('dashboard.last12DaysChart')}</Text>
        <ProfitBarChart data={dailyChartData} />
      </Box>
      <Box>
        <Text mb={4} fontWeight="bold" color={colors.textContrast}>{t('dashboard.last12MonthsChart')}</Text>
        <ProfitBarChart data={monthlyChartData} />
      </Box>
    </Grid>
  );
};

export default Charts;
