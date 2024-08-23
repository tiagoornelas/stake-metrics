import {Box, Tab, TabIndicator, TabList, TabPanel, TabPanels, Tabs} from "@chakra-ui/react";
import FifaBets from "containers/fifa/bets/FifaBets";
import FifaStrategies from "containers/fifa/strategy/FifaStrategies";
import useThemeColors from "hooks/useThemeColors";
import React from 'react';

const FifaModule = () => {
    const colors = useThemeColors();

    return (
        <Box p={4}>
            <Tabs position="relative" variant="unstyled">
                <TabList>
                    <Tab>Estratégias</Tab>
                    <Tab>Entradas</Tab>
                    <Tab>Tendência</Tab>
                </TabList>
                <TabIndicator mt="-1.5px" height="2px" bg={colors.product} borderRadius="1px"/>
                <TabPanels>
                    <TabPanel>
                        <FifaStrategies/>
                    </TabPanel>
                    <TabPanel>
                        <FifaBets/>
                    </TabPanel>
                    <TabPanel>
                        Empty State
                    </TabPanel>
                </TabPanels>
            </Tabs>
        </Box>
    )
};

export default FifaModule;
