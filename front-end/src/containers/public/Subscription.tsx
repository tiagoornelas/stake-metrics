import {Box, Heading, Stack, Text} from "@chakra-ui/react";
import Plans from "components/Plans";
import ProductLogo from "components/ProductLogo";
import ProductName from "components/ProductName";
import useThemeColors from "hooks/useThemeColors";
import React from 'react';

const Subscription = () => {
    const colors = useThemeColors();

    return (
        <Box pt={16}>
            <Stack
                as={Box}
                textAlign={'center'}
                spacing={{base: 8, md: 14}}
            >
                <Heading
                    fontWeight={600}
                    fontSize={{base: 'xl', sm: '2xl', md: '4xl'}}
                    lineHeight={'110%'}>
                    <Text as={'span'} color={colors.product}>
                        <ProductLogo/>
                        <ProductName/>
                    </Text>
                </Heading>
                <Text color={'gray.500'}>
                    Você não possui uma assinatura ativa.
                </Text>
            </Stack>
            <Plans/>
        </Box>
    );
};

export default Subscription;
