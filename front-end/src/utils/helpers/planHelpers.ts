import {Plan} from "../interfaces";

export const parsePlanByConstants = (plans: Plan[]): Plan[] => plans
    .sort((a: Plan, b: Plan) => Number(a.price) - Number(b.price))
    .map((plan, index) => {
        return {
            ...plan,
            price: plan.price === 0 ? "Gratuito" : "R$" + plan.price,
            popular: index === 2
        }
    });
;