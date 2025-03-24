import {useQuery} from "react-query";
import {DateIntervalTypes} from "../../../../utils/constants/dateConstants";
import {getFifaGoalsTrend} from "../../../../services/trendService";

const useGoalsTrendQuery = (dateInterval: DateIntervalTypes) => {
  return useQuery(
    ['fifa-goals-trend', dateInterval],
    () => getFifaGoalsTrend(dateInterval),
  );
};

export default useGoalsTrendQuery;