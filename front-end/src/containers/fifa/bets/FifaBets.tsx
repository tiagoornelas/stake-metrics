import {Box, Button, Flex, Skeleton, Text} from "@chakra-ui/react";
import React from 'react';
import {SiGooglesheets} from "react-icons/all";

const Header = ({betsLength, isLoaded}: { betsLength: number, isLoaded: boolean }) => {
    const getLabel = () => {
        switch (betsLength) {
            case 0:
                return "";
            case 1:
                return "1 aposta";
            default:
                return `${betsLength} apostas`;
        }
    }

    return (<Flex align={"center"} justifyContent={"space-between"}>
        <Skeleton isLoaded={isLoaded}>
            <Text>{getLabel()}</Text>
        </Skeleton>
        <Button colorScheme="blue" rightIcon={<SiGooglesheets/>}>Baixar relatório</Button>
    </Flex>)
}

const FifaBets = () => {
    return (
        <Box mt={4}>
            <Header betsLength={2} isLoaded={true}/>
            Apostas!
        </Box>
    );
};

export default FifaBets;
