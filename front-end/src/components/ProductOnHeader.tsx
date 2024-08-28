import {Flex, Tag, Text, useBreakpointValue} from "@chakra-ui/react";
import ProductLogo from "components/ProductLogo";
import ProductName from "components/ProductName";
import React, {useEffect, useState} from 'react';
import {useNavigate} from "react-router-dom";

const ProductOnHeader = ({path = "/"}: { path?: string }) => {
    const BETA_STATE = "Closed Beta";
    const navigate = useNavigate();
    const tagText = useBreakpointValue({base: 'Beta', md: BETA_STATE});
    const handleClick = () => navigate(path, {replace: true});

    const [envTag, setEnvTag] = useState<string | null>(null);

    useEffect(() => {
        if (process.env.NODE_ENV === 'development') {
            setEnvTag('Dev');
        }
    }, []);

    return (
        <Flex direction="row" alignItems={'center'} gap={2} cursor="pointer" onClick={handleClick}>
            <ProductLogo/>
            <Text as={"b"} display={{base: "none", md: "flex"}}>
                <ProductName/>
            </Text>
            {!envTag && BETA_STATE && <Tag colorScheme={"yellow"}>{tagText}</Tag>}
            {envTag && <Tag colorScheme={"red"}>{envTag}</Tag>}
        </Flex>
    );
};

export default ProductOnHeader;
