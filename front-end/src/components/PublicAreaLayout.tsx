import {Box, Container, Grid, Heading, Stack,} from '@chakra-ui/react'
import {ColorModeSwitcher} from "ColorModeSwitcher";
import ProductLogo from 'components/ProductLogo'
import ProductName from "components/ProductName";
import * as React from "react";

interface Props {
    children: React.ReactNode;
}

const PublicAreaLayout = ({children}: Props) => {
    return (
        <Box textAlign="center" fontSize="xl">
            <Grid minH="100vh" p={3}>
                <ColorModeSwitcher justifySelf="flex-end"/>
                <Container maxW="lg" py={{base: '6', md: '12'}} px={{base: '0', sm: '8'}}>
                    <Stack spacing="8">
                        <Stack spacing="6" alignItems="center">
                            <ProductLogo/>
                            <Stack spacing={{base: '2', md: '3'}} textAlign="center">
                                <Heading size={{base: 'xs', md: 'sm'}}>Bem-vindo ao <ProductName/></Heading>
                            </Stack>
                        </Stack>
                        <Box
                            py={{base: '0', sm: '8'}}
                            px={{base: '4', sm: '10'}}
                            bg={{base: 'transparent', sm: 'bg.surface'}}
                            boxShadow={{base: 'none', sm: 'md'}}
                            borderRadius={{base: 'none', sm: 'xl'}}
                        >
                            {children}
                        </Box>
                    </Stack>
                </Container>
            </Grid>
        </Box>
    );
}

export default PublicAreaLayout;
