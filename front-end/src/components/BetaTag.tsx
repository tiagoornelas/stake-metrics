import {Tag} from "@chakra-ui/react";
import React from 'react';

type Props = {
    ml?: number
}

const BetaTag = ({ml = 0}: Props) => {
    return (
        <Tag ml={ml} colorScheme="yellow">Beta</Tag>
    );
};

export default BetaTag;
