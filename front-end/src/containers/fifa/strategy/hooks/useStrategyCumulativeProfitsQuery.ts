import {useQuery} from 'react-query';
import {listStrategiesCumulativeProfit} from 'services/strategyService';

const useStrategyCumulativeProfitsQuery = (strategyId: string) => {
    return useQuery<number[]>(['strategies_profit', strategyId], async () => {
        const response = await listStrategiesCumulativeProfit(strategyId);
        return response.cumulativeProfit;
    }, {
        cacheTime: 0,
        staleTime: 0,
    });
};

export default useStrategyCumulativeProfitsQuery;
