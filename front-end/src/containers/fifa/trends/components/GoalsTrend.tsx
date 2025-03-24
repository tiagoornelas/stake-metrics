import { Flex, Spinner, Box } from "@chakra-ui/react";
import { useState } from "react";
import useGoalsTrendQuery from "../hooks/useGoalsTrendQuery";
import { DateIntervalTypes } from "../../../../utils/constants/dateConstants";
import LeagueTrendChart from "../../../../components/charts/LeagueTrendChart";
import GoalsTrendHeader from "./GoalsTrendHeader";
import useThemeColors from "../../../../hooks/useThemeColors";
import DateIntervalSelector from "./DateIntervalSelector";
import GoalsTrendStats from "./GoalsTrendStats";

const GoalsTrend = () => {
  const [dateInterval, setDateInterval] = useState<DateIntervalTypes>(DateIntervalTypes.TWENTY_FOUR_HOURS);
  const { isLoading, data } = useGoalsTrendQuery(dateInterval);
  const colors = useThemeColors();

  return (
    <>
      <GoalsTrendHeader />
      <GoalsTrendStats isLoaded={!isLoading} leagueTrends={data?.leagueTrends || []}/>
      <Box mb={4} mt={2}>
        <DateIntervalSelector 
          selectedInterval={dateInterval} 
          onIntervalChange={setDateInterval} 
        />
      </Box>
      {isLoading ? (
        <Flex justifyContent="center" w="100%" mt={8}>
          <Spinner
            thickness="4px"
            speed="0.65s"
            emptyColor="gray.200"
            color={colors.product}
            size="xl"
          />
        </Flex>
      ) : (
        <LeagueTrendChart leagueTrends={data?.leagueTrends || []} />
      )}
    </>
  );
};

export default GoalsTrend;
