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
    Tag,
    Tbody,
    Td,
    Text,
    Th,
    Thead,
    Tr
} from "@chakra-ui/react";
import BetEmptyState from "containers/fifa/bets/components/BetEmptyState";
import BetTableItem from "containers/fifa/bets/components/BetTableItem";
import useBetQuery from "containers/fifa/bets/hooks/useBetQuery";
import useThemeColors from "hooks/useThemeColors";
import React, {useState} from 'react';
import {FiFilter} from "react-icons/all";
import {Bet} from "utils/interfaces";

const Header = ({showingBets, betsLength, isLoaded, showPaperBets, setShowPaperBets}: {
    showingBets: number,
    betsLength: number,
    isLoaded: boolean,
    showPaperBets: boolean,
    setShowPaperBets: (showPaperBets: boolean) => void
}) => {
    const getLabel = () => {
        if (betsLength === 0) return "";
        if (betsLength === 1) return "1 aposta" + (showPaperBets ? "" : " real");
        if (showingBets >= betsLength) return `${betsLength} apostas` + (showPaperBets ? "" : " reais");
        return `${showingBets} de ${betsLength} apostas` + (showPaperBets ? "" : " reais");
    }

    return (
        <Flex align={"center"} justifyContent={"space-between"}>
            <Skeleton isLoaded={isLoaded}>
                <Text>{getLabel()}</Text>
            </Skeleton>
            <Menu>
                <MenuButton as={Button} colorScheme="blue" rightIcon={<FiFilter/>}>
                    <Flex alignItems="center">
                        <Tag mr={2}>{showPaperBets ? "Todas" : "Reais"}</Tag>
                        Filtrar
                    </Flex>
                </MenuButton>
                <MenuList>
                    <MenuItem onClick={() => setShowPaperBets(true)}>Mostrar todas</MenuItem>
                    <MenuItem onClick={() => setShowPaperBets(false)}>Ocultar Paper Bet</MenuItem>
                </MenuList>
            </Menu>
        </Flex>
    )
}

const FifaBets = () => {
    const [showPaperBets, setShowPaperBets] = useState<boolean>(false);
    const {data, isLoading, fetchNextPage, isFetchingNextPage} = useBetQuery(showPaperBets);
    const colors = useThemeColors();
    const totalBets = data?.pages[0]?.totalElements ?? 0;
    const maxFetchedLength = data?.pages.reduce((acc, page) => acc + page.content.length, 0) ?? 0;
    const totalPagesFetched = data?.pages.length ?? 0;
    const totalPages = data?.pages[0]?.totalPages ?? 0;
    const shouldRenderEmptyState = !isLoading && data?.pages[0]?.empty;

    return (
        <Box mt={4}>
            <Header showingBets={maxFetchedLength} betsLength={totalBets} isLoaded={!isLoading}
                    showPaperBets={showPaperBets} setShowPaperBets={setShowPaperBets}/>
            <TableContainer mt={8}>
                <Table variant='simple'>
                    <Thead>
                        <Tr>
                            <Th>Hora do Jogo</Th>
                            <Th>Estratégia</Th>
                            <Th>Liga</Th>
                            <Th>Confronto</Th>
                            <Th>Placar</Th>
                            <Th>Aposta</Th>
                            <Th>Odd</Th>
                            <Th>Resultado</Th>
                            <Th>L/P</Th>
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
