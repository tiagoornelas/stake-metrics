import {Button, Flex, FormControl, FormLabel, Grid, Input, Select, Skeleton, Stack, Tag} from "@chakra-ui/react";
import MultiSelect from "components/MultiSelect";
import {useErrorToast} from "hooks/useErrorToast";
import useThemeColors from "hooks/useThemeColors";
import React, {ChangeEvent, useEffect, useState} from 'react';
import {FaMagic, MdDelete} from "react-icons/all";
import {getFifaStrategyParams, getStrategy, saveStrategy} from "services/strategyService";
import {
    FifaMarketSubTypesDict,
    FifaMarketTypesDict,
    FifaMatchupTypesDict,
    FifaRuleTypesDict,
    FifaStrategyScopeTypesDict
} from "utils/constants/strategyConstants";
import {SUCCESS_TYPES} from "utils/constants/successConstants";
import {
    FifaLeagueResponse,
    FifaPlayerResponse,
    Option,
    Rule,
    StrategyCreationBody,
    StrategyParams
} from "utils/interfaces";

const StrategyCreateEditForm = ({strategyId}: { strategyId?: string }) => {
    const [formParams, setFormParams] = useState<StrategyParams | null>(null);
    const [isLoaded, setIsLoaded] = useState<boolean>(false);
    const [form, setForm] = useState<StrategyCreationBody>({
        id: undefined,
        name: "",
        marketType: null,
        marketSubTypes: [],
        leagues: [],
        excludedPlayers: [],
        rules: []
    });

    const [dynamicSubmarkets, setDynamicSubmarkets] = useState<Option[]>([]);
    const [dynamicLeagues, setDynamicLeagues] = useState<Option[]>([]);
    const [dynamicPlayers, setDynamicPlayers] = useState<Option[]>([]);

    const colors = useThemeColors();

    useEffect(() => {
        const fetchStrategy = async () => {
            if (strategyId) {
                const strategy = await getStrategy(strategyId);
                setForm({
                    id: strategy.id,
                    name: strategy.name,
                    marketType: strategy.marketType,
                    marketSubTypes: strategy.marketSubTypes,
                    leagues: strategy.leagues.map((league: FifaLeagueResponse) => league.id),
                    excludedPlayers: strategy.excludedPlayers.map((player: FifaPlayerResponse) => player.id),
                    rules: strategy.rules
                });
            }
        };

        const getFormParams = async () => {
            const params = await getFifaStrategyParams();
            setFormParams(params);
        };

        Promise.all([fetchStrategy(), getFormParams()]).then(() => {
            setIsLoaded(true);
        });
    }, [strategyId]);

    useEffect(() => {
        const setDefaultFormBasedOnParams = () => {
            if (formParams && !strategyId) {
                setForm(prevState => ({
                    ...prevState,
                    marketType: formParams.marketTypes[0].marketType,
                }));
            }
        }

        setDefaultFormBasedOnParams();
    }, [formParams, strategyId]);

    useEffect(() => {
        const setSubmarketBasedOnMarketSelection = () => {
            if (form.marketType && formParams) {
                const market = formParams.marketTypes.find(marketType => marketType.marketType === form.marketType);
                if (market) {
                    const options: Option[] = market.marketSubTypes.map(subtype => ({
                        value: subtype,
                        label: FifaMarketSubTypesDict[subtype]
                    }))
                    setDynamicSubmarkets(options);
                }
            }
        }

        setSubmarketBasedOnMarketSelection();
    }, [form.marketType, formParams]);

    useEffect(() => {
        const setDynamicLeaguesBasedOnParams = () => {
            if (formParams) {
                setDynamicLeagues(formParams.leagues.map(league => ({value: league.id, label: league.name})));
            }
        }

        setDynamicLeaguesBasedOnParams();
    }, [formParams]);

    useEffect(() => {
        const setDynamicPlayersBasedOnLeagueSelection = () => {
            if (form.leagues.length > 0 && formParams) {
                const players = formParams.players.filter(player => player.leagueId && form.leagues.includes(player.leagueId));
                setDynamicPlayers(players.map(player => ({value: player.id, label: player.name})));
            }
        }

        setDynamicPlayersBasedOnLeagueSelection();
    }, [form.leagues, formParams]);

    const handleSubmarketChange = (selectedSubmarkets: string[]) => {
        setForm(prevState => ({
            ...prevState,
            marketSubTypes: selectedSubmarkets
        }));
    };

    const handleLeaguesChange = (selectedLeagues: string[]) => {
        debugger;
        setForm(prevState => ({
            ...prevState,
            leagues: selectedLeagues
        }));
    };

    const handlePlayersChange = (selectedPlayers: string[]) => {
        setForm(prevState => ({
            ...prevState,
            excludedPlayers: selectedPlayers
        }));
    };

    const setRules = (rules: Rule[]) => {
        setForm((prevState) => {
            return {
                ...prevState,
                rules
            }
        });
    }

    const handleInput = (e: ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const {id, value} = e.target;
        setForm({
            ...form,
            [id]: value
        });
    }

    const handleRuleInput = (index: number, e: ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const newRules = [...form.rules];
        newRules[index] = {
            ...newRules[index],
            [e.target.id]: e.target.value
        };
        setRules(newRules);
    }

    const addRule = () => {
        if (formParams) {
            const defaultRule = {
                type: formParams.ruleTypes[0] || "",
                matchup: formParams.matchupTypes[0] || "",
                scope: formParams.scopeTypes[0] || "",
                value: 0,
                scopeValue: 0
            };
            setRules([...form.rules, defaultRule]);
        }
    };

    const deleteRule = (index: number) => {
        const newRules = form.rules.filter((_, i) => i !== index);
        setRules(newRules);
    }

    const getRuleLabel = (count: number) => {
        return count === 1 ? "1 regra" : `${count} regras`;
    }

    const handleSave = useErrorToast(async () => {
        await saveStrategy(form);
    }, SUCCESS_TYPES.FIFA_STRATEGY_SAVED);

    return (
        <Stack spacing="5">
            <FormControl>
                <FormLabel htmlFor="name">Nome</FormLabel>
                <Input id="name" type="text" onChange={handleInput} value={form.name}/>
            </FormControl>

            <Skeleton isLoaded={isLoaded}>
                <FormControl>
                    <FormLabel htmlFor="market">Mercado</FormLabel>
                    <Select id="marketTypes" onChange={handleInput} value={form.marketType || ""}>
                        {formParams && formParams.marketTypes.map(marketType => (
                            <option key={marketType.marketType} value={marketType.marketType}>
                                {FifaMarketTypesDict[marketType.marketType]}
                            </option>
                        ))}
                    </Select>
                </FormControl>
            </Skeleton>

            <Skeleton isLoaded={isLoaded}>
                <FormControl>
                    <MultiSelect title={"Linhas do mercado"} entity={["linha", "linhas"]} options={dynamicSubmarkets}
                                 onChange={handleSubmarketChange}/>
                </FormControl>
            </Skeleton>

            <Skeleton isLoaded={isLoaded}>
                <FormControl>
                    <MultiSelect title={"Ligas observadas"} entity={["liga", "ligas"]}
                                 options={dynamicLeagues}
                                 defaultSelected={form.leagues}
                                 onChange={handleLeaguesChange}/>
                </FormControl>
            </Skeleton>

            {form.leagues.length > 0 && <Skeleton isLoaded={isLoaded}>
                <FormControl>
                    <MultiSelect title={"Jogadores ignorados"} entity={["jogador", "jogadores"]} tagColor={"red"}
                                 options={dynamicPlayers}
                                 defaultSelected={form.excludedPlayers}
                                 onChange={handlePlayersChange}/>
                </FormControl>
            </Skeleton>}

            <Skeleton isLoaded={isLoaded} w={"100%"}>
                <Button
                    onClick={addRule}
                    colorScheme={"blue"}
                    rightIcon={<FaMagic/>}
                    w={"100%"}
                >
                    {form.rules.length > 0 && <Tag m="2">{getRuleLabel(form.rules.length)}</Tag>}
                    Adicionar Regra
                </Button>
            </Skeleton>

            {isLoaded && formParams && form.rules.map((rule, index) => (
                <Stack key={index} spacing="5" border="1px solid #f0f0f0" padding="10px" borderRadius="5px">
                    <FormControl>
                        <FormLabel htmlFor={`matchupType-${index}`}>Tipo de Confronto</FormLabel>
                        <Select id="matchup" onChange={(e) => handleRuleInput(index, e)}
                                value={rule.matchup}>
                            {formParams.matchupTypes.map(matchupType => (
                                <option key={matchupType} value={matchupType}>
                                    {FifaMatchupTypesDict[matchupType]}
                                </option>
                            ))}
                        </Select>
                    </FormControl>

                    <Grid templateColumns="2fr 1fr" gap={4}>
                        <FormControl>
                            <FormLabel htmlFor={`ruleType-${index}`}>Tipo de Regra</FormLabel>
                            <Select id="type" onChange={(e) => handleRuleInput(index, e)}
                                    value={rule.type}>
                                {formParams.ruleTypes.map(ruleType => (
                                    <option key={ruleType} value={ruleType}>
                                        {FifaRuleTypesDict[ruleType]}
                                    </option>
                                ))}
                            </Select>
                        </FormControl>

                        <FormControl>
                            <FormLabel htmlFor={`value-${index}`}>Valor</FormLabel>
                            <Input id="value" type="number" onChange={(e) => handleRuleInput(index, e)}
                                   value={rule.value}/>
                        </FormControl>
                    </Grid>

                    <Grid templateColumns="2fr 1fr" gap={4}>
                        <FormControl>
                            <FormLabel htmlFor={`scopeType-${index}`}>Escopo</FormLabel>
                            <Select id="scope" onChange={(e) => handleRuleInput(index, e)}
                                    value={rule.scope}>
                                {formParams.scopeTypes.map(scopeType => (
                                    <option key={scopeType} value={scopeType}>
                                        {FifaStrategyScopeTypesDict[scopeType]}
                                    </option>
                                ))}
                            </Select>
                        </FormControl>

                        <FormControl>
                            <FormLabel htmlFor={`scopeValue-${index}`}>Valor</FormLabel>
                            <Input id="scopeValue" type="number" onChange={(e) => handleRuleInput(index, e)}
                                   value={rule.scopeValue}/>
                        </FormControl>
                    </Grid>

                    <Button colorScheme={"red"} onClick={() => deleteRule(index)} rightIcon={<MdDelete/>}>
                        Deletar Regra
                    </Button>
                </Stack>
            ))}
            <Flex justifyContent={"flex-end"} mb={2}>
                <Button mr={3} onClick={() => {
                }}>
                    Voltar
                </Button>
                <Button bgColor={colors.product} color={colors.productContrast} onClick={handleSave}>
                    Salvar
                </Button>
            </Flex>
        </Stack>
    );
};

export default StrategyCreateEditForm;
