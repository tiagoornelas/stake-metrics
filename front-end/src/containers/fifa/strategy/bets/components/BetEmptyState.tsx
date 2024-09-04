import {Box, Container, Heading, Stack, Text} from "@chakra-ui/react";
import ProductLogo from "components/ProductLogo";
import ProductName from "components/ProductName";
import useThemeColors from "hooks/useThemeColors";
import React from 'react';

const BetEmptyState = () => {
    const colors = useThemeColors();

    return (
        <Container maxW={'3xl'}>
            <Stack
                as={Box}
                textAlign={'center'}
                spacing={{base: 8, md: 14}}
                py={{base: 20, md: 36}}>
                <Heading
                    fontWeight={600}
                    fontSize={{base: '2xl', sm: '4xl', md: '5xl'}}
                    lineHeight={'110%'}>
                    Seja bem-vindo ao <br/>
                    <Text as={'span'} color={colors.product}>
                        <ProductName/> <ProductLogo/>
                    </Text>
                </Heading>
                <Text color={'gray.500'}>
                    Você ainda não possui nenhuma aposta. Para começar a vigiar os seus mercados
                    favoritos e realizar apostas, crie uma nova estratégia na aba de estratégias!
                </Text>
            </Stack>
        </Container>
    )
};

export default BetEmptyState;
