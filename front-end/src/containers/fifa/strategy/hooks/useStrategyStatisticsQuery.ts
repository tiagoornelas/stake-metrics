import {useQuery} from 'react-query';
import {listStrategiesStatistics} from 'services/strategyService';
import {StrategyListItem} from 'utils/interfaces';

const useStrategyStatisticsQuery = () => {
    return useQuery<StrategyListItem[]>('strategies', async () => {
        const response = await listStrategiesStatistics();
        return response.strategies;
    });
};

export default useStrategyStatisticsQuery;
