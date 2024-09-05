import {
    Box,
    Button,
    Flex,
    Menu,
    MenuButton,
    MenuItem,
    MenuList,
    Skeleton,
    Spinner,
    Table,
    TableContainer,
    Tbody,
    Td,
    Text,
    Th,
    Thead,
    Tr
} from "@chakra-ui/react";
import BetEmptyState from "containers/fifa/strategy/bets/components/BetEmptyState";
import BetTableItem from "containers/fifa/strategy/bets/components/BetTableItem";
import useBetQuery from "containers/fifa/strategy/bets/hooks/useBetQuery";
import useThemeColors from "hooks/useThemeColors";
import React, {useState} from 'react';
import {FiFilter} from "react-icons/all";
import {Bet, BetQueryFilters} from "utils/interfaces";

const Header = ({showingBets, betsLength, isLoaded, filters, setFilters}: {
    showingBets: number,
    betsLength: number,
    isLoaded: boolean,
    filters: BetQueryFilters,
    setFilters: (filters: BetQueryFilters) => void
}) => {
    const getLabel = () => {
        if (betsLength === 0) return "";
        if (betsLength === 1) return "1 aposta";
        if (showingBets >= betsLength) return `${betsLength} apostas`;
        return `${showingBets} de ${betsLength} apostas`;
    }

    return (
        <Flex align={"center"} justifyContent={"space-between"}>
            <Skeleton isLoaded={isLoaded}>
                <Text>{getLabel()}</Text>
            </Skeleton>
            <Menu>
                <MenuButton as={Button} colorScheme="blue" rightIcon={<FiFilter/>}>
                    <Flex alignItems="center">
                        {/*<Tag mr={2}>{showPaperBets ? "Todas" : "Reais"}</Tag>*/}
                        Filtrar
                    </Flex>
                </MenuButton>
                <MenuList>
                    <MenuItem>Mostrar todas</MenuItem>
                    <MenuItem>Ocultar Paper Bet</MenuItem>
                </MenuList>
            </Menu>
        </Flex>
    )
}

const DEFAULT_FILTERS: BetQueryFilters = {
    openBets: true,
    closedBets: true,
    isPaperBet: true,
    realBets: true,
    league: [],
}

type Props = {
    strategyId: string
}

const FifaBets = ({strategyId}: Props) => {
    const [filters, setFilters] = useState<BetQueryFilters>(DEFAULT_FILTERS);
    const {data, isLoading, fetchNextPage, isFetchingNextPage} = useBetQuery(strategyId, filters);
    const colors = useThemeColors();
    const totalBets = data?.pages[0]?.totalElements ?? 0;
    const maxFetchedLength = data?.pages.reduce((acc, page) => acc + page.content.length, 0) ?? 0;
    const totalPagesFetched = data?.pages.length ?? 0;
    const totalPages = data?.pages[0]?.totalPages ?? 0;
    const shouldRenderEmptyState = !isLoading && data?.pages[0]?.empty;

    return (
        <Box mt={16}>
            <Header showingBets={maxFetchedLength} betsLength={totalBets} isLoaded={!isLoading} filters={filters}
                    setFilters={setFilters}/>
            <TableContainer mt={8}>
                <Table variant='simple'>
                    <Thead>
                        <Tr>
                            <Th>Hora</Th>
                            <Th>Liga</Th>
                            <Th>Confronto</Th>
                            <Th textAlign="center">Linha</Th>
                            <Th textAlign="center">Resultado</Th>
                            <Th/>
                        </Tr>
                    </Thead>
                    {!isLoading && <Tbody>
                        {data?.pages.map((page) =>
                            page.content.map((bet: Bet) => (
                                <BetTableItem key={bet.id} bet={bet}/>
                            ))
                        )}
                        {totalPagesFetched < totalPages && (
                            <Tr>
                                <Td colSpan={10} textAlign="center">
                                    <Button
                                        onClick={() => fetchNextPage()}
                                        isLoading={isFetchingNextPage}
                                        loadingText="Carregando..."
                                        width="100%"
                                    >
                                        Carregar mais
                                    </Button>
                                </Td>
                            </Tr>
                        )}
                    </Tbody>}
                </Table>
            </TableContainer>
            {isLoading && <Flex justifyContent={"center"} w={"100%"} mt={8}>
                <Spinner
                    thickness='4px'
                    speed='0.65s'
                    emptyColor='gray.200'
                    color={colors.product}
                    size='xl'
                />
            </Flex>}
            {shouldRenderEmptyState && <BetEmptyState/>}
        </Box>
    );
};

export default FifaBets;
