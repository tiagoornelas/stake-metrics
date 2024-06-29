export interface Plan {
    id: string;
    name: string;
    popular?: boolean;
    features?: string[];
    info?: string;
    monthlyPrice: number | string;
    quarterlyPrice: number | string;
    order?: number | undefined;
}

export type PlanDictionary = {
    [key: string]: {
        order: number;
        name: string;
        popular?: boolean;
        features: string[];
        info: string;
        monthlyPrice?: number;
        quarterlyPrice?: number;
    }
}