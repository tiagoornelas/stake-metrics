import {Box, Flex, Skeleton, Spinner, Table, TableContainer, Tbody, Text, Th, Thead, Tr} from "@chakra-ui/react";
import Modal from "components/Modal";
import StrategyEmptyState from "containers/strategy/components/StrategyEmptyState";
import StrategyForm from "containers/strategy/components/StrategyForm";
import StrategyTableItem from "containers/strategy/components/StrategyTableItem";
import useStrategyQuery from "containers/strategy/hooks/useStrategyQuery";
import useThemeColors from "hooks/useThemeColors";
import React from 'react';
import {GoTelescope} from 'react-icons/all';
import {StrategyListItem} from "utils/interfaces";

const NewStrategyButton = () => {
    return (
        <Modal buttonText="Nova estratégia" title="Nova estratégia" noFooter icon={<GoTelescope/>} colorScheme="blue">
            <StrategyForm/>
        </Modal>
    )
}

const Header = ({strategiesLength, isLoaded}: { strategiesLength: number, isLoaded: boolean }) => {
    const getLabel = () => {
        switch (strategiesLength) {
            case 0:
                return "Nenhuma estratégia";
            case 1:
                return "1 estratégia";
            default:
                return `${strategiesLength} estratégias`;
        }
    }

    return (<Flex align={"center"} justifyContent={"space-between"}>
        <Skeleton isLoaded={isLoaded}>
            <Text>{getLabel()}</Text>
        </Skeleton>
        <NewStrategyButton/>
    </Flex>)
}

const Strategy = () => {
    const {data: strategies = [], isLoading} = useStrategyQuery();
    const colors = useThemeColors();
    const shouldRenderEmptyState = !isLoading && strategies.length === 0;

    return (
        <Box p={8}>
            <Header strategiesLength={strategies.length} isLoaded={!isLoading}/>
            <TableContainer mt={8}>
                <Table variant='simple'>
                    <Thead>
                        <Tr>
                            <Th>Estratégia</Th>
                            <Th w={"80px"} textAlign={"center"}>Entradas</Th>
                            <Th w={"80px"} textAlign={"center"}>Unidades</Th>
                            <Th w={"80px"} textAlign={"center"}>ROI</Th>
                            <Th w={"80px"} textAlign={"center"}>Unidades Ativas</Th>
                            <Th w={"80px"} textAlign={"center"}>ROI Ativo</Th>
                            <Th w={"210px"}/>
                        </Tr>
                    </Thead>
                    {!isLoading && <Tbody>
                        {strategies.map((strategy: StrategyListItem) => <StrategyTableItem
                            key={strategy.id}
                            strategy={strategy}/>)}
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
            {shouldRenderEmptyState && <StrategyEmptyState/>}
        </Box>
    )
};

export default Strategy;
