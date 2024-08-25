import {Box, Text} from "@chakra-ui/react";
import useThemeColors from "hooks/useThemeColors";
import React from 'react';

const ProductFooter = () => {
    const colors = useThemeColors();
    
    return (
        <Box bg={colors.product} w='100%' p={4} color="white">
            <Text textAlign={"center"}>
                <b>Stake Metrics</b> 🔭 Built with ❤️ and ☕ by Juquita<br/>
                🎰 Love Metrics, hate Denise 🎰
            </Text>
        </Box>)
};

export default ProductFooter;
