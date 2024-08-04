import {useQuery} from 'react-query';
import {listStrategies} from 'services/strategyService';
import {StrategyListItem} from 'utils/interfaces';

const useStrategyQuery = () => {
    return useQuery<StrategyListItem[]>('strategies', async () => {
        const response = await listStrategies();
        return response.strategies;
    });
};

export default useStrategyQuery;
