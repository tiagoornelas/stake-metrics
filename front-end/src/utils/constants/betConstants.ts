export enum FifaBetStatus {
    PENDING = "PENDING",
    WON = "WON",
    HALF_WON = "HALF_WON",
    VOID = "VOID",
    HALF_LOST = "HALF_LOST",
    LOST = "LOST"
}

export const statusLabels: Record<FifaBetStatus, string> = {
    [FifaBetStatus.PENDING]: "Pendente",
    [FifaBetStatus.WON]: "Vencida",
    [FifaBetStatus.HALF_WON]: "Meio Ganho",
    [FifaBetStatus.VOID]: "Devolvida",
    [FifaBetStatus.HALF_LOST]: "Meia Perca",
    [FifaBetStatus.LOST]: "Perdida"
};

export const statusColors: Record<FifaBetStatus, string> = {
    [FifaBetStatus.PENDING]: "blue",
    [FifaBetStatus.WON]: "green",
    [FifaBetStatus.HALF_WON]: "green",
    [FifaBetStatus.VOID]: "yellow",
    [FifaBetStatus.HALF_LOST]: "red",
    [FifaBetStatus.LOST]: "red"
};
