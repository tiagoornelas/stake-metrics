import React, {Fragment} from 'react';
import Header from './Header';

const RouterLayout = ({children}: { children: React.ReactNode }) => {
    return (
        <Fragment>
            <Header/>
            {children}
        </Fragment>
    );
};

export default RouterLayout;
