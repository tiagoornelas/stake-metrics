import {Box, Flex, Table, TableContainer, Tbody, Text, Th, Thead, Tr} from "@chakra-ui/react";
import Modal from "components/Modal";
import StrategyCreateEditForm from "containers/strategy/components/StrategyCreateEditForm";
import StrategyTableItem from "containers/strategy/components/StrategyTableItem";
import React from 'react';
import {GoTelescope} from 'react-icons/all'
import {StrategyListItem} from "utils/interfaces";

const NewStrategyButton = () => {
    return (
        <Modal buttonText="Nova estratégia" title="Nova estratégia" actionText="Salvar"
               actionCallback={() => {
               }} icon={<GoTelescope/>} colorScheme="blue">
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

const strategiesMock: StrategyListItem[] = [
    {
        id: "1",
        name: "Under Apogeu",
        status: "ACTIVE",
        bets: Number((Math.random() * 1000).toFixed(0)),
        result: Number((Math.random() * 100).toFixed(0)),
        roi: Number((Math.random() * 10).toFixed(1)),
        activeResult: Number((Math.random() * 100).toFixed(0)),
        activeRoi: Number((Math.random() * 10).toFixed(1))
    },
    {
        id: "2",
        name: "Empate Bombito",
        status: "PAPER_BET",
        bets: Number((Math.random() * 1000).toFixed(0)),
        result: Number((Math.random() * 100).toFixed(1)),
        roi: Number((Math.random() * 10).toFixed(1)),
        activeResult: Number((Math.random() * 100).toFixed(0)),
        activeRoi: Number((Math.random() * 10).toFixed(1))
    },
    {
        id: "3",
        name: "Match Odds",
        status: "INACTIVE",
        bets: Number((Math.random() * 1000).toFixed(0)),
        result: Number((Math.random() * -100).toFixed(1)),
        roi: Number((Math.random() * -10).toFixed(1)),
        activeResult: Number((Math.random() * -100).toFixed(0)),
        activeRoi: Number((Math.random() * -10).toFixed(1))
    }
]


const Strategy = () => {
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
                        {strategiesMock.map((strategy: StrategyListItem) => <StrategyTableItem strategy={strategy}/>)}
                    </Tbody>
                </Table>
            </TableContainer>
        </Box>
    );
};

export default Strategy;
