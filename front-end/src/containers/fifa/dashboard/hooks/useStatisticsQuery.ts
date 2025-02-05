import { useQuery, UseQueryResult } from "react-query";
import { getFifaBetStatistics } from "services/betService";
import { BetStatistics } from "utils/interfaces";

const useStatisticsQuery = (): UseQueryResult<BetStatistics> => {
  return useQuery(
    ['fifa-statistics'],
    () => getFifaBetStatistics(),
  );
};

export default useStatisticsQuery;