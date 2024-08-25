import {useQueryClient} from 'react-query';

const useInvalidateBetQuery = () => {
    const queryClient = useQueryClient();

    return () => {
        queryClient.invalidateQueries('bets');
    };
};

export default useInvalidateBetQuery;
