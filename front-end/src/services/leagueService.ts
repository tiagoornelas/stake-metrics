import {getFifaStrategyParams} from "services/strategyService";
import {FifaLeagueResponse} from "utils/interfaces";

export const getLeagues = () =>
    getFifaStrategyParams()
        .then(response => response.leagues)
        .then(leagues => leagues.map((league: FifaLeagueResponse) => ({
            value: league.id,
            label: league.name
        })));
