import {Box, Center, Text, useColorMode} from "@chakra-ui/react";
import useThemeColors from "hooks/useThemeColors";
import useTranslation from "hooks/useTranslation";
import React from "react";
import {Area, AreaChart, ResponsiveContainer, Tooltip, TooltipProps} from "recharts";
import {parseCumulativeProfitsToChart} from "utils/helpers/chartHelper";
import {calculateMovingAverage} from "utils/helpers/statisticHelper";

type Props = {
    data: number[]
}

interface ChartDataPoint {
    name: string;
    un: number;
    ma?: number | null;
}

const CustomTooltip = ({active, payload}: TooltipProps<number, string>) => {
    const {colorMode} = useColorMode();
    const isDarkMode = colorMode === "dark";

    if (active && payload && payload.length) {
        const value = payload[0].value;
        const color = value !== undefined
            ? (value >= 0
                ? (isDarkMode ? "lightgreen" : "green")
                : (isDarkMode ? "lightcoral" : "red"))
            : undefined;

        return (
            <div className="custom-tooltip">
                <p className="label" style={{color}}>{`${value}`}</p>
            </div>
        );
    }

    return null;
};

const ProfitOverBetsLineChart = ({data}: Props) => {
    const {colorMode} = useColorMode();
    const isDarkMode = colorMode === "dark";
    const colors = useThemeColors();
    const { t } = useTranslation();

    const parsedData: ChartDataPoint[] = parseCumulativeProfitsToChart(data);
    const lastValue = parsedData[parsedData.length - 1]?.un || 0;
    
    const movingAverages = calculateMovingAverage(data);
    
    parsedData.forEach((item, index) => {
        item.ma = movingAverages[index];
    });
    
    const fillColor = lastValue >= 0
        ? (isDarkMode ? "rgba(144, 238, 144, 0.3)" : "rgba(0, 128, 0, 0.3)")
        : (isDarkMode ? "rgba(240, 128, 128, 0.3)" : "rgba(255, 0, 0, 0.3)");
    const strokeColor = lastValue >= 0
        ? (isDarkMode ? "lightgreen" : "green")
        : (isDarkMode ? "lightcoral" : "red");
    const maStrokeColor = colors.textContrast;

    if (parsedData.length === 1 && parsedData[0].un === 0) {
        return (
            <Center width={"100%"} height={"100%"}>
                <Text>{t('charts.emptyState')}</Text>
            </Center>
        );
    }

    return (
        <Box width={"100%"} height={"100%"}>
            <ResponsiveContainer width={"100%"} maxHeight={400}>
                <AreaChart
                    data={parsedData}
                    margin={{top: 0, right: 0, left: 0, bottom: 0}}
                >
                    <Tooltip content={<CustomTooltip/>}/>
                    <Area type="monotone" dataKey="un" stroke={strokeColor} fill={fillColor}/>
                    <Area 
                        type="monotone" 
                        dataKey="ma" 
                        stroke={maStrokeColor} 
                        fill="none" 
                        dot={false} 
                        activeDot={false} 
                        strokeWidth={1}
                        strokeDasharray="3 3"
                    />
                </AreaChart>
            </ResponsiveContainer>
        </Box>
    )
}

export default ProfitOverBetsLineChart;
