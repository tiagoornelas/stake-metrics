import {Box, Button, Container, Heading, Stack, Text,} from '@chakra-ui/react'
import useThemeColors from "hooks/useThemeColors";
import ProductLogo from "components/ProductLogo";
import ProductName from 'components/ProductName';

export const Workspace = () => {
    const colors = useThemeColors();

    return (
        <>
            <Container maxW={'3xl'}>
                <Stack
                    as={Box}
                    textAlign={'center'}
                    spacing={{base: 8, md: 14}}
                    py={{base: 20, md: 36}}>
                    <Heading
                        fontWeight={600}
                        fontSize={{base: '2xl', sm: '4xl', md: '6xl'}}
                        lineHeight={'110%'}>
                        Seja bem-vindo ao <br/>
                        <Text as={'span'} color={colors.product}>
                            <ProductName/> <ProductLogo/>
                        </Text>
                    </Heading>
                    <Text color={'gray.500'}>
                        Mussum Ipsum, cacilds vidis litro abertis. Aenean aliquam molestie leo, vitae iaculis nisl. Quem
                        num gosta di mé, boa gentis num é. Tá deprimidis, eu conheço uma cachacis que pode alegrar sua
                        vidis. Mauris nec dolor in eros commodo tempor. Aenean aliquam molestie leo, vitae iaculis nisl.
                    </Text>
                    <Stack
                        direction={'column'}
                        spacing={3}
                        align={'center'}
                        alignSelf={'center'}
                        position={'relative'}>
                        <Button
                            bg={colors.product}
                            color={colors.productContrast}
                            rounded={'full'}
                            px={6}
                            onClick={() => window.location.assign("/")}
                        >
                            Começar
                        </Button>
                    </Stack>
                </Stack>
            </Container>
        </>
    )
}

export default Workspace;
