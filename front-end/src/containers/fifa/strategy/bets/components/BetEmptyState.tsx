import {Box, Container, Heading, Stack, Text} from "@chakra-ui/react";
import React from 'react';

const BetEmptyState = () => {
    return (
        <Container maxW={'3xl'}>
            <Stack
                as={Box}
                textAlign={'center'}
                spacing={{base: 4, md: 6}}
                py={{base: 20, md: 36}}>
                <Heading
                    fontWeight={600}
                    fontSize={{base: 'lg', sm: 'xl', md: '2xl'}}
                    lineHeight={'110%'}>
                    Nada por aqui! 🪹
                </Heading>
                <Text color={'gray.500'}>
                    Você ainda não possui nenhuma aposta para os filtros selecionados.
                </Text>
            </Stack>
        </Container>
    )
};

export default BetEmptyState;
