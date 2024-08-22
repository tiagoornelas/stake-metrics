import {IconButton, Tag, Td, Text, Tr} from "@chakra-ui/react";
import {format} from "date-fns";
import React from 'react';
import {FaTrash} from "react-icons/all";
import {FifaBetStatus, statusColors, statusLabels} from "utils/constants/betConstants";
import {MARKET_CANDIDATES_DICT} from "utils/constants/marketCandidatesConstants";
import {formatProfit} from "utils/helpers/betHelper";
import {Bet} from "utils/interfaces";

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

    const candidate = MARKET_CANDIDATES_DICT[bet.candidate as keyof typeof MARKET_CANDIDATES_DICT];
    const {text: formattedProfit, color: profitColor} = formatProfit(bet.profit);

    return (
        <Tr key={bet.id}>
            <Td>{formatDate(bet.matchTime, "dd/MM/yy HH:mm")}</Td>
            <Td>{bet.strategyName}</Td>
            <Td>{bet.leagueName}</Td>
            <Td>{`${bet.homePlayerName} x ${bet.awayPlayerName}`}</Td>
            <Td>{bet.handicap ? `${candidate} ${bet.handicap}` : candidate}</Td>
            <Td>{bet.odds}</Td>
            <Td>{getStatusTag(bet.status)}</Td>
            <Td><Text color={profitColor}>{formattedProfit}</Text></Td>
            <Td>
                <IconButton
                    size="sm"
                    colorScheme="red"
                    icon={<FaTrash/>}
                    variant="outline"
                    aria-label="Delete bet"
                />
            </Td>
        </Tr>
    );
};

export default BetTableItem;
