/**
 * Calculate the moving average for an array of numbers
 * @param data Array of numbers representing cumulative profits
 * @param minimumBets Minimum number of bets required to calculate MA
 * @returns Array of numbers or nulls (where MA is not yet calculated)
 */
export const calculateMovingAverage = (data: number[], minimumBets: number = 10): (number | null)[] => {
    const result: (number | null)[] = [];
    
    if (data.length < minimumBets) {
        return Array(data.length).fill(null);
    }
    
    const period = Math.min(Math.max(Math.floor(data.length / 3), minimumBets), 180);
    
    for (let i = 0; i < period - 1; i++) {
        result.push(null);
    }
    
    for (let i = period - 1; i < data.length; i++) {
        const sum = data.slice(i - period + 1, i + 1).reduce((acc, val) => acc + val, 0);
        result.push(Number((sum / period).toFixed(2)));
    }
    
    return result;
}

/**
 * Determine if the current result is above, below, or close to the moving average
 * @param data Array of numbers representing cumulative profits
 * @returns Object with status and movingAverage values
 */
export const compareToMovingAverage = (data: number[]): { 
    status: 'above' | 'below' | 'equal', 
    movingAverage: number | null 
} => {
    if (data.length === 0) {
        return { status: 'equal', movingAverage: null };
    }
    
    const movingAverages = calculateMovingAverage(data);
    const lastMA = movingAverages[movingAverages.length - 1];
    const currentValue = data[data.length - 1];
    
    if (lastMA === null) {
        return { status: 'equal', movingAverage: null };
    }
    
    // Define a threshold for "close to average" (5% of MA value)
    const threshold = Math.abs(lastMA) * 0.05;
    
    if (Math.abs(currentValue - lastMA) <= threshold) {
        return { status: 'equal', movingAverage: lastMA };
    } else if (currentValue > lastMA) {
        return { status: 'above', movingAverage: lastMA };
    } else {
        return { status: 'below', movingAverage: lastMA };
    }
}
