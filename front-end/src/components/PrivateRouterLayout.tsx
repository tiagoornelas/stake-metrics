import React, {Fragment} from 'react';
import Header from './Header';

const PrivateRouterLayout = ({children}: { children: React.ReactNode }) => {
    return (
        <Fragment>
            <Header/>
            {children}
        </Fragment>
    );
};

export default PrivateRouterLayout;
