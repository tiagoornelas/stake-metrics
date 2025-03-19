import { Box } from "@chakra-ui/react";
import useStatisticsQuery from "./hooks/useStatisticsQuery";
import Stats from "./components/Stats";
import Charts from "./components/Charts";
import DashboardHeader from "./components/DashboardHeader";

const FifaDashboard: React.FC = () => {
  const { data: statistics, isLoading } = useStatisticsQuery();

  return (
    <Box p={6}>
      <DashboardHeader />
      <Stats statistics={statistics} isLoading={isLoading} />
      <Charts statistics={statistics} isLoading={isLoading} />
    </Box>
  );
};

export default FifaDashboard;
