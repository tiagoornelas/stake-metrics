import {Box, Center, Text} from "@chakra-ui/react";
import React from "react";
import {Area, AreaChart, ResponsiveContainer, Tooltip} from "recharts";
import {parseCumulativeProfitsToChart} from "utils/helpers/chartHelper";

type Props = {
    data: number[]
}

const ProfitOverBetsLineChart = ({data}: Props) => {
    const parsedData = parseCumulativeProfitsToChart(data);
    const lastValue = parsedData[parsedData.length - 1]?.un || 0;
    const fillColor = lastValue >= 0 ? "rgba(0, 128, 0, 0.3)" : "rgba(255, 0, 0, 0.3)";
    const strokeColor = lastValue >= 0 ? "green" : "red";

    if (parsedData.length === 1 && parsedData[0].un === 0) {
        return (
            <Center width={"100%"} height={"100%"}>
                <Text>Ainda nenhuma aposta fechada para esta estratégia. 💭</Text>
            </Center>
        );
    }

    return (
        <Box width={"100%"} height={"100%"}>
            <ResponsiveContainer width={"100%"} maxHeight={400}>
                <AreaChart
                    data={parsedData}
                    margin={{
                        top: 10,
                        right: 30,
                        left: 0,
                        bottom: 0,
                    }}
                >
                    <Tooltip/>
                    <Area type="monotone" dataKey="un" stroke={strokeColor} fill={fillColor}/>
                </AreaChart>
            </ResponsiveContainer>
        </Box>
    )
}

export default ProfitOverBetsLineChart;
