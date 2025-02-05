import { Box, Center, Text, useColorMode, useToken } from "@chakra-ui/react";
import useTranslation from "hooks/useTranslation";
import useThemeColors from "hooks/useThemeColors";
import React from "react";
import { Bar, BarChart, Cell, LabelList, ResponsiveContainer, Tooltip, TooltipProps, XAxis } from "recharts";
import { formatProfit } from "utils/helpers/formatHelpers";

interface ChartData {
  name: string;
  profit: number;
}

type Props = {
  data: ChartData[];
  height?: number;
}

const CustomTooltip = ({ active, payload }: TooltipProps<number, string>) => {
  const { colorMode } = useColorMode();
  const isDarkMode = colorMode === "dark";

  if (active && payload && payload.length) {
    const value = payload[0].value;
    const color = value !== undefined
      ? (value >= 0
        ? (isDarkMode ? "lightgreen" : "green")
        : (isDarkMode ? "lightcoral" : "red"))
      : undefined;

    return (
      <Box p={2} bg={isDarkMode ? "gray.700" : "white"} borderRadius="md" boxShadow="sm">
        <Text color={color}>{formatProfit(value || 0)}</Text>
      </Box>
    );
  }

  return null;
};

const formatBarLabel = (value: number) => `${value >= 0 ? '+' : ''}${value.toFixed(2)}`;

const ProfitBarChart = ({ data, height: defaultHeight = 200 }: Props) => {
  const [isLargeDevice, setIsLargeDevice] = React.useState(window.innerWidth > 1024);

  React.useEffect(() => {
    const handleResize = () => setIsLargeDevice(window.innerWidth > 1024);
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  const height = isLargeDevice ? 300 : defaultHeight;
  const { colorMode } = useColorMode();
  const isDarkMode = colorMode === "dark";
  const colors = useThemeColors();
  const { t } = useTranslation();
  const [greenLight, greenDark, redLight, redDark] = useToken(
    'colors',
    ['green.500', 'green.300', 'red.500', 'red.300']
  );

  if (!data.length) {
    return (
      <Center width="100%" height={height}>
        <Text>{t('charts.emptyState')}</Text>
      </Center>
    );
  }

  return (
    <ResponsiveContainer width="100%" height={height}>
      <BarChart data={data} margin={{ top: 10, right: 0, left: 0, bottom: 0 }}>
        <XAxis
          dataKey="name"
          tick={{ fill: colors.textContrast, fontSize: 12 }}
          tickLine={false}
          axisLine={false}
        />
        <Tooltip
          content={<CustomTooltip />}
          cursor={{ fill: colors.productContrast, opacity: 0.1 }}
        />
        <Bar
          dataKey="profit"
          radius={[2, 2, 0, 0]}
        >
          {!isLargeDevice && (
            <LabelList
              dataKey="profit"
              position="top"
              formatter={formatBarLabel}
              style={{ fontSize: '11px', fill: colors.textContrast }}
            />
          )}
          {data.map((entry, index) => {
            const value = entry.profit;
            return (
              <Cell
                key={`cell-${index}`}
                fill={
                  value >= 0
                    ? isDarkMode ? greenDark : greenLight
                    : isDarkMode ? redLight : redDark
                }
                strokeWidth={2}
              />
            );
          })}
        </Bar>
      </BarChart>
    </ResponsiveContainer>
  );
};

export default ProfitBarChart;
