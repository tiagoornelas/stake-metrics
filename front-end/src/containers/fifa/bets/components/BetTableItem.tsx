import {IconButton, Tag, Td, Tr} from "@chakra-ui/react";
import {format} from "date-fns";
import React from 'react';
import {FaTrash} from "react-icons/all";
import {MARKET_CANDIDATES_DICT} from "utils/constants/marketCandidatesConstants";
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
        switch (status) {
            case "WON":
                return <Tag colorScheme="green">Vencida</Tag>;
            case "LOST":
                return <Tag colorScheme="red">Perdida</Tag>;
            case "PENDING":
                return <Tag colorScheme="yellow">Pendente</Tag>;
            default:
                return <Tag>{status}</Tag>;
        }
    };

    const candidate = MARKET_CANDIDATES_DICT[bet.candidate as keyof typeof MARKET_CANDIDATES_DICT];

    return (
        <Tr key={bet.id}>
            <Td>{formatDate(bet.matchTime, "dd/MM/yy HH:mm")}</Td>
            <Td>{bet.strategyName}</Td>
            <Td>{bet.leagueName}</Td>
            <Td>{`${bet.homePlayerName} x ${bet.awayPlayerName}`}</Td>
            <Td>{bet.handicap ? `${candidate} ${bet.handicap}` : candidate}</Td>
            <Td>{bet.odds}</Td>
            <Td>{getStatusTag(bet.status)}</Td>
            <Td>{bet.profit !== null ? bet.profit : ""}</Td>
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
