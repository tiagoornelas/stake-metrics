export type TrendType = 'over' | 'under' | 'lateral';

interface TrendResult {
  type: TrendType;
  colorScheme: string;
  label: string;
}

export const TREND_COLORS = [
    "#4299E1", 
    "#48BB78", 
    "#ED8936", 
    "#9F7AEA", 
    "#F56565", 
    "#38B2AC", 
    "#ECC94B", 
    "#805AD5", 
    "#DD6B20", 
    "#3182CE", 
  ];

/**
 * Determines the trend type based on the current value and historical data
 * @param historicalValues Array of historical values to analyze for determining lateral trends
 * @returns An object containing trend type, colorScheme and label
 */
export const determineTrend = (historicalValues: number[]): TrendResult => {
    const currentValue = historicalValues[historicalValues.length - 1];

  if (currentValue > 0) {
    const minValue = Math.min(...historicalValues);
    if (Math.abs(minValue) > currentValue) {
      return {
        type: 'lateral',
        colorScheme: 'yellow',
        label: 'Tendência lateral'
      };
    }

    return {
      type: 'over',
      colorScheme: 'green',
      label: 'Mais gols'
    };
  } else if (currentValue < 0) {
    const maxValue = Math.max(...historicalValues);
    if (maxValue > Math.abs(currentValue)) {
      return {
        type: 'lateral',
        colorScheme: 'yellow',
        label: 'Tendência lateral'
      };
    }

    return {
      type: 'under',
      colorScheme: 'red',
      label: 'Menos gols'
    };
  }


  return {
    type: 'lateral',
    colorScheme: 'yellow',
    label: 'Tendência lateral'
  };
};
