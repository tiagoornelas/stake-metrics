import {Flex, Tag, Text, useBreakpointValue} from "@chakra-ui/react";
import ProductLogo from "components/ProductLogo";
import ProductName from "components/ProductName";
import React from 'react';
import {useNavigate} from "react-router-dom";

const ProductOnHeader = ({path = "/"}: { path?: string }) => {
    const BETA_STATE = "Closed Beta";
    const navigate = useNavigate();
    const tagText = useBreakpointValue({base: 'Beta', sm: BETA_STATE});
    const handleClick = () => navigate(path, {replace: true});

    return (
        <Flex direction="row" alignItems={'center'} gap={2} cursor="pointer" onClick={handleClick}>
            <ProductLogo/>
            <Text as={"b"} display={{base: "none", sm: "flex"}}>
                <ProductName/>
            </Text>
            {BETA_STATE && <Tag colorScheme={"yellow"}>{tagText}</Tag>}
        </Flex>
    );
};

export default ProductOnHeader;
