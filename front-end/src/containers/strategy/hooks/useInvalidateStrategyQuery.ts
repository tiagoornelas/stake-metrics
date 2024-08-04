import {useQueryClient} from 'react-query';

const useInvalidateStrategyQuery = () => {
    const queryClient = useQueryClient();

    return () => {
        queryClient.invalidateQueries('strategies');
    };
};

export default useInvalidateStrategyQuery;
