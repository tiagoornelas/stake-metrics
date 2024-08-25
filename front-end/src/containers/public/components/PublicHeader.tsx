import {Box, Button, Flex} from '@chakra-ui/react';
import ProductOnHeader from "components/ProductOnHeader";
import React, {Fragment} from "react";
import {useCookies} from "react-cookie";
import {useNavigate} from 'react-router-dom';

export const PublicHeader = () => {
    const [cookie] = useCookies(["userId", "token"]);
    const isUserLoggedIn = cookie.userId && cookie.token;
    const navigate = useNavigate();


    return (
        <>
            <Box bg={'gray.100'} px={4}>
                <Flex h={16} alignItems={'center'} justifyContent={'space-between'}>
                    <ProductOnHeader/>

                    <Flex alignItems={'center'} gap={2}>
                        {isUserLoggedIn ?
                            <Button colorScheme="yellow" onClick={() => navigate('/app', {replace: true})}>Acessar
                                plataforma</Button> :
                            <Fragment>
                                <Button variant="outline" colorScheme="yellow"
                                        onClick={() => navigate('/create-account', {replace: true})}>
                                    Criar conta
                                </Button>
                                <Button colorScheme="yellow" onClick={() => navigate('/login', {replace: true})}>
                                    Login
                                </Button>
                            </Fragment>}
                    </Flex>
                </Flex>
            </Box>
        </>
    )
}

export default PublicHeader;
