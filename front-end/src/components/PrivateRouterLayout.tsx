import {Box} from "@chakra-ui/react";
import React, {Fragment} from 'react';
import Header from './Header';

const PrivateRouterLayout = ({children}: { children: React.ReactNode }) => {
    return (
        <Fragment>
            <Header/>
            <Box p={4}>
                {children}
            </Box>
        </Fragment>
    );
};

export default PrivateRouterLayout;
