import {Tag, Td, Tr} from "@chakra-ui/react";
import DeleteConfirmationDialog from "components/DeleteConfirmationDialog";
import useInvalidateBetQuery from "containers/fifa/strategy/bets/hooks/useInvalidateBetQuery";
import useInvalidateStrategyQuery from "containers/fifa/strategy/hooks/useInvalidateStrategyQuery";
import {format} from "date-fns";
import {useErrorToast} from "hooks/useErrorToast";
import React from 'react';
import {deleteBet} from "services/betService";
import {FifaBetStatus, statusColors, statusLabels} from "utils/constants/betConstants";
import {MARKET_CANDIDATES_DICT} from "utils/constants/marketCandidatesConstants";
import {SUCCESS_TYPES} from "utils/constants/successConstants";
import {abbreviateLeagueName} from "utils/helpers/leagueHelper";
import {Bet} from "utils/interfaces";

const DeleteButton = ({bet}: { bet: Bet }) => {
    const invalidateBetQuery = useInvalidateBetQuery();
    const invalidateStrategyQuery = useInvalidateStrategyQuery();

    const handleDelete = useErrorToast(async () => {
        await deleteBet(bet.id);
        invalidateBetQuery();
        invalidateStrategyQuery();
    }, SUCCESS_TYPES.BET_DELETED);

    return <DeleteConfirmationDialog entityName={"entrada"} confirmCallback={handleDelete} variant="outline"
                                     text={"Ao excluir a aposta, se houver mensagens correspondentes, elas serão editadas e marcadas como aposta descartada."}/>
}

type Props = {
    bet: Bet
}

const BetTableItem = ({bet}: Props) => {
    const formatDate = (dateString: string, formatString: string) => {
        const date = new Date(dateString);
        return format(date, formatString);
    };

    const getStatusTag = (status: string) => {
        const fifaBetStatus = status as FifaBetStatus;
        return <Tag colorScheme={statusColors[fifaBetStatus]}>{statusLabels[fifaBetStatus]}</Tag>;
    };

    const getPaperBetTag = (isPaperBet: boolean) => {
        return isPaperBet 
            ? <Tag colorScheme="yellow">Paper Bet</Tag>
            : <Tag colorScheme="green">Ativa</Tag>;
    };

    const candidate = MARKET_CANDIDATES_DICT[bet.candidate as keyof typeof MARKET_CANDIDATES_DICT];

    return (
        <Tr key={bet.id}>
            <Td>{formatDate(bet.matchTime, "d/M HH:mm")}</Td>
            <Td>{abbreviateLeagueName(bet.leagueName)}</Td>
            <Td>
                {bet.score ? (
                    <>
                        {bet.homePlayerName} <strong>{bet.score}</strong> {bet.awayPlayerName}
                    </>
                ) : (
                    `${bet.homePlayerName} x ${bet.awayPlayerName}`
                )}
            </Td>
            <Td textAlign="center">{bet.handicap ? `${candidate} ${bet.handicap}` : candidate} @{bet.odds}</Td>
            <Td textAlign="center">{getStatusTag(bet.status)}</Td>
            <Td textAlign="center">{getPaperBetTag(bet.isPaperBet)}</Td>
            <Td><DeleteButton bet={bet}/></Td>
        </Tr>
    );
};

export default BetTableItem;
