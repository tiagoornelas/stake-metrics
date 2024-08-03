import {Box, Flex, Table, TableContainer, Tbody, Text, Th, Thead, Tr} from "@chakra-ui/react";
import Modal from "components/Modal";
import StrategyCreateEditForm from "containers/strategy/components/StrategyCreateEditForm";
import StrategyTableItem from "containers/strategy/components/StrategyTableItem";
import React, {useEffect, useState} from 'react';
import {GoTelescope} from 'react-icons/all';
import {listStrategies} from "services/strategyService";
import {StrategyListItem} from "utils/interfaces";

const NewStrategyButton = () => {
    return (
        <Modal buttonText="Nova estratégia" title="Nova estratégia" noFooter icon={<GoTelescope/>} colorScheme="blue">
            <StrategyCreateEditForm/>
        </Modal>
    )
}

const Header = () => {
    return (<Flex align={"center"} justifyContent={"space-between"}>
        <Text>3 estratégias</Text>
        <NewStrategyButton/>
    </Flex>)
}

const Strategy = () => {
    const [strategies, setStrategies] = useState<StrategyListItem[]>([]);

    useEffect(() => {
        const fetchStrategies = async () => {
            const response = await listStrategies();
            setStrategies(response.strategies);
        };

        fetchStrategies();
    }, []);

    return (
        <Box p={8}>
            <Header/>
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
                    <Tbody>
                        {strategies.map((strategy: StrategyListItem) => <StrategyTableItem key={strategy.id}
                                                                                           strategy={strategy}/>)}
                    </Tbody>
                </Table>
            </TableContainer>
        </Box>
    );
};

export default Strategy;
