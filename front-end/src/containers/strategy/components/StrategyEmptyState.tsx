import {Box, Container, Heading, Stack, Text} from "@chakra-ui/react";
import ProductLogo from "components/ProductLogo";
import ProductName from "components/ProductName";
import useThemeColors from "hooks/useThemeColors";
import React from 'react';

const StrategyEmptyState = () => {
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
                    Você ainda não possui nenhuma estratégia cadastrada. Para começar a vigiar os seus mercados
                    favoritos e encontrar entradas de valor, crie uma nova estratégia clicando no botão no canto
                    superior direito e boa sorte!
                </Text>
            </Stack>
        </Container>
    )
};

export default StrategyEmptyState;
