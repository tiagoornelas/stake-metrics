export const formatProfit = (profit: number | null): { text: string, color: string } => {
    if (profit === null) return {text: "", color: ""};
    const sign = profit > 0 ? "+" : "";
    const color = profit > 0 ? "green" : profit < 0 ? "red" : "";
    return {text: `${sign}${profit} u`, color};
};
