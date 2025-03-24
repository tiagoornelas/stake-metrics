import { Box, Center, Text, useColorMode } from "@chakra-ui/react";
import useThemeColors from "hooks/useThemeColors";
import useTranslation from "hooks/useTranslation";
import React from "react";
import {
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  TooltipProps,
  XAxis,
  YAxis,
  ReferenceLine,
} from "recharts";
import { TREND_COLORS } from "utils/helpers/trendHelpers";
import { FifaLeagueResponse } from "utils/interfaces";

interface LeagueTrendData {
  league: FifaLeagueResponse;
  trend: number[];
}

interface ChartDataPoint {
  name: string;
  [key: string]: string | number | undefined;
}

interface Props {
  leagueTrends: LeagueTrendData[];
}

const CustomTooltip = ({ active, payload }: TooltipProps<number, string>) => {
  const { colorMode } = useColorMode();
  const isDarkMode = colorMode === "dark";
  const { t } = useTranslation();
  const colors = useThemeColors();

  if (active && payload && payload.length) {
    const sortedPayload = [...payload].sort((a, b) => {
      const valueA = a?.value as number || 0;
      const valueB = b?.value as number || 0;
      return valueB - valueA;
    });
    
    return (
      <div
        style={{
          backgroundColor: isDarkMode ? "#2D3748" : "white",
          padding: "10px",
          border: "1px solid #ccc",
          borderRadius: "4px"
        }}
      >
        {sortedPayload.map((entry, index) => {
          if (!entry) return null;

          const value = entry.value as number;
          const isOver = value >= 0;
          const tendencyColor = isOver
            ? (isDarkMode ? "lightgreen" : "green")
            : (isDarkMode ? "lightcoral" : "red");
          
          const displayValue = isOver ? value : Math.abs(value);

          return (
            <p key={`trend-${index}`} style={{ margin: "0", color: colors.text }}>
              <span style={{ display: "inline-block", marginRight: "8px", width: "10px", height: "10px", backgroundColor: entry.color }}></span>
              {entry.name}: {displayValue} <span style={{ color: tendencyColor }}>{isOver ? t('charts.over') : t('charts.under')}</span>
            </p>
          );
        }).filter(Boolean)}
      </div>
    );
  }
  return null;
};

const LeagueTrendChart = ({ leagueTrends }: Props) => {
  const { colorMode } = useColorMode();
  const isDarkMode = colorMode === "dark";
  const { t } = useTranslation();

  const processChartData = (): ChartDataPoint[] => {
    if (!leagueTrends || leagueTrends.length === 0) {
      return [];
    }

    const maxLength = Math.max(...leagueTrends.map(item => item.trend.length));

    const chartData: ChartDataPoint[] = [];

    for (let i = 0; i < maxLength; i++) {
      const dataPoint: ChartDataPoint = { name: (i + 1).toString() };

      leagueTrends.forEach((leagueData) => {
        if (i < leagueData.trend.length) {
          dataPoint[leagueData.league.name] = leagueData.trend[i];
        }
      });

      chartData.push(dataPoint);
    }

    return chartData;
  };

  const chartData = processChartData();

  if (!leagueTrends || leagueTrends.length === 0 || chartData.length === 0) {
    return (
      <Center width={"100%"} height={"100%"}>
        <Text>{t('charts.emptyState')}</Text>
      </Center>
    );
  }

  const getAllValues = (): number[] => {
    const values: number[] = [];
    leagueTrends.forEach(leagueData => {
      leagueData.trend.forEach(value => {
        values.push(value);
      });
    });
    return values;
  };

  const allValues = getAllValues();
  const minValue = Math.min(...allValues);
  const maxValue = Math.max(...allValues);
  const maxAbs = Math.max(Math.abs(minValue), Math.abs(maxValue));
  const yDomain = [-maxAbs - 1, maxAbs + 1];

  const chartHeight = 400;

  return (
    <Box width={"100%"} height={`${chartHeight}px`} data-testid="league-trend-chart">
      <ResponsiveContainer width={"100%"} height={chartHeight}>
        <LineChart
          data={chartData}
          margin={{ top: 10, right: 30, left: 30, bottom: 30 }}
        >
          <XAxis
            dataKey="name"
            hide={true}
          />
          <YAxis
            hide={true}
            domain={yDomain}
          />
          <Tooltip content={<CustomTooltip />} />

          <ReferenceLine
            y={0}
            stroke={isDarkMode ? "rgba(255,255,255,0.2)" : "rgba(0,0,0,0.1)"}
            strokeWidth={1}
          />

          {leagueTrends.map((leagueData, index) => (
            <Line
              key={leagueData.league.id}
              type="monotone"
              dataKey={leagueData.league.name}
              name={leagueData.league.name}
              stroke={TREND_COLORS[index % TREND_COLORS.length]}
              fill="none"
              dot={false}
              activeDot={{ r: 6, stroke: TREND_COLORS[index % TREND_COLORS.length], strokeWidth: 2, fill: 'white' }}
              strokeWidth={1}
              isAnimationActive={false}
            />
          ))}
        </LineChart>
      </ResponsiveContainer>
    </Box>
  );
};

export default LeagueTrendChart;