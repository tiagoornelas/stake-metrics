export const formatPercentage = (value: number) => `${(value * 100).toFixed(2)} %`;

export const formatProfit = (value: number) => 
  `${value >= 0 ? '+' : ''}${value.toFixed(2)} u`;

export const formatBetVolume = (value: number) => value > 0 ? `${Math.round(value)} / dia` : "0 / dia";
