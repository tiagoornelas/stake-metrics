export const parseCumulativeProfitsToChart = (cumulativeProfits: number[]): { name: string, un: number }[] => {
    return [
        {name: 'Ponto inicial', un: 0},
        ...cumulativeProfits.map((profit, index) => {
            return {
                name: `Entrada ${index + 1}`,
                un: profit
            }
        })
    ];
}
