import {
    Badge,
    Button,
    Flex,
    FormControl,
    FormLabel,
    Grid,
    GridItem,
    IconButton,
    Input,
    Select,
    Skeleton,
    Stack,
    Tag
} from "@chakra-ui/react";
import MultiSelect from "components/MultiSelect";
import StrategyRuleDynamicFormatInput from "containers/strategy/components/StrategyRuleDynamicFormatInput";
import {useErrorToast} from "hooks/useErrorToast";
import useThemeColors from "hooks/useThemeColors";
import React, {ChangeEvent, useEffect, useState} from 'react';
import {FaMagic, MdAdd, MdContentCopy, MdDelete} from "react-icons/all";
import {getFifaStrategyParams, getStrategy, saveStrategy} from "services/strategyService";
import {
    FifaMarketSubTypesDict,
    FifaMarketTypesDict,
    FifaMatchupTypesDict,
    FifaRuleTypesDict,
    FifaStrategyScopeTypesDict,
    FifleRuleTypesFormatDict
} from "utils/constants/strategyConstants";
import {SUCCESS_TYPES} from "utils/constants/successConstants";
import {
    FifaLeagueResponse,
    FifaPlayerResponse,
    Option,
    Scope,
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
        scopes: []
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
                    scopes: strategy.scopes
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

    const setScopes = (scopes: Scope[]) => {
        setForm((prevState) => ({
            ...prevState,
            scopes
        }));
    }

    const handleInput = (e: ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const {id, value} = e.target;
        setForm({
            ...form,
            [id]: value
        });
    }

    const handleScopeInput = (index: number, e: ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const newScopes = [...form.scopes];
        newScopes[index] = {
            ...newScopes[index],
            [e.target.id]: e.target.value
        };
        setScopes(newScopes);
    }

    const handleRuleTypeInput = (scopeIndex: number, ruleIndex: number, e: ChangeEvent<HTMLSelectElement>) => {
        const newScopes = [...form.scopes];
        newScopes[scopeIndex].rules[ruleIndex] = {
            ...newScopes[scopeIndex].rules[ruleIndex],
            type: e.target.value
        };
        setScopes(newScopes);
    }

    const handleRuleValueInput = (scopeIndex: number, ruleIndex: number, e: ChangeEvent<HTMLInputElement>) => {
        const newScopes = [...form.scopes];
        newScopes[scopeIndex].rules[ruleIndex] = {
            ...newScopes[scopeIndex].rules[ruleIndex],
            value: +e.target.value
        };
        setScopes(newScopes);
    }

    const addScope = () => {
        if (formParams) {
            const defaultScope: Scope = {
                type: formParams.scopeTypes[1] || "",
                matchup: formParams.matchupTypes[0] || "",
                value: 7,
                rules: [{
                    type: formParams.ruleTypes[0].type || "",
                    value: formParams.ruleTypes[0].defaultValue || 0
                }]
            };
            setScopes([...form.scopes, defaultScope]);
        }
    };

    const deleteScope = (index: number) => {
        const newScopes = form.scopes.filter((_, i) => i !== index);
        setScopes(newScopes);
    }

    const duplicateScope = (index: number) => {
        const scopeToDuplicate = form.scopes[index];
        const newScopes = [...form.scopes, {...scopeToDuplicate}];
        setScopes(newScopes);
    }

    const addRule = (scopeIndex: number) => {
        const newScopes = [...form.scopes];
        const selectedRuleTypes = newScopes[scopeIndex].rules.map(rule => rule.type);
        const availableRuleTypes = formParams?.ruleTypes.filter(ruleType => !selectedRuleTypes.includes(ruleType.type));
        const defaultRuleType = availableRuleTypes ? availableRuleTypes[0].type : "";
        const defaultRuleValue = availableRuleTypes ? availableRuleTypes[0].defaultValue : 0;

        newScopes[scopeIndex].rules.push({
            type: defaultRuleType,
            value: defaultRuleValue
        });
        setScopes(newScopes);
    };

    const deleteRule = (scopeIndex: number, ruleIndex: number) => {
        const newScopes = [...form.scopes];
        newScopes[scopeIndex].rules = newScopes[scopeIndex].rules.filter((_, i) => i !== ruleIndex);
        setScopes(newScopes);
    }

    const getScopeLabel = (count: number) => {
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
                    <Select id="marketType" onChange={handleInput} value={form.marketType || ""}>
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
                    onClick={addScope}
                    colorScheme={"blue"}
                    rightIcon={<FaMagic/>}
                    w={"100%"}
                >
                    {form.scopes.length > 0 && <Tag m="2">{getScopeLabel(form.scopes.length)}</Tag>}
                    Adicionar regra
                </Button>
            </Skeleton>

            {isLoaded && formParams && form.scopes.map((scope, index) => {
                return (
                    <Stack key={index} spacing="5" border="1px solid #f0f0f0" padding="10px" borderRadius="5px">
                        <Grid templateColumns="repeat(12, 1fr)" gap={4}>
                            <GridItem colSpan={6}>
                                <FormControl>
                                    <FormLabel htmlFor={`matchup-${index}`}>Confronto</FormLabel>
                                    <Select id="matchup" onChange={(e) => handleScopeInput(index, e)}
                                            value={scope.matchup}>
                                        {formParams.matchupTypes.map(matchupType => (
                                            <option key={matchupType} value={matchupType}>
                                                {FifaMatchupTypesDict[matchupType]}
                                            </option>
                                        ))}
                                    </Select>
                                </FormControl>
                            </GridItem>

                            <GridItem colSpan={6}>
                                <FormControl>
                                    <FormLabel htmlFor={`value-${index}`}>Período</FormLabel>
                                    <Grid templateColumns="repeat(6, 1fr)" gap={4}>
                                        <GridItem colSpan={2}>
                                            <Input id="value" type="number" onChange={(e) => handleScopeInput(index, e)}
                                                   value={scope.value}/>
                                        </GridItem>
                                        <GridItem colSpan={4}>
                                            <Select id="type" onChange={(e) => handleScopeInput(index, e)}
                                                    value={scope.type}>
                                                {formParams.scopeTypes.map(scopeType => (
                                                    <option key={scopeType} value={scopeType}>
                                                        {FifaStrategyScopeTypesDict[scopeType]}
                                                    </option>
                                                ))}
                                            </Select>
                                        </GridItem>
                                    </Grid>
                                </FormControl>
                            </GridItem>
                        </Grid>

                        {scope.rules.map((rule, ruleIndex) => {
                            const selectedRuleTypes = scope.rules.map(rule => rule.type);
                            const availableRuleTypes = formParams.ruleTypes.filter(ruleType => !selectedRuleTypes.includes(ruleType.type) || ruleType.type === rule.type);
                            const currentRuleDetail = formParams.ruleTypes.find(ruleType => ruleType.type === rule.type);

                            return (
                                <Grid key={ruleIndex} templateColumns="repeat(12, 1fr)" gap={4} alignItems={"end"}
                                      justifyContent={"center"}>
                                    <GridItem colSpan={6}>
                                        <FormControl>
                                            {ruleIndex === 0 && <FormLabel
                                                htmlFor={`ruleType-${index}-${ruleIndex}`}>Critério</FormLabel>}
                                            <Select id="type" onChange={(e) => handleRuleTypeInput(index, ruleIndex, e)}
                                                    value={rule.type}>
                                                {availableRuleTypes.map(ruleType => (
                                                    <option key={ruleType.type} value={ruleType.type}>
                                                        {FifaRuleTypesDict[ruleType.type]}
                                                    </option>
                                                ))}
                                            </Select>
                                        </FormControl>
                                    </GridItem>

                                    <GridItem colSpan={4}>
                                        <FormControl>
                                            {ruleIndex === 0 && <FormLabel
                                                htmlFor={`ruleValue-${index}-${ruleIndex}`}>Valor</FormLabel>}
                                            <Grid templateColumns="repeat(12, 1fr)" gap={2} alignItems={"center"}
                                                  justifyItems={"center"}>
                                                <GridItem colSpan={8} alignItems={"center"} justifyItems={"center"}>
                                                    <StrategyRuleDynamicFormatInput
                                                        onChange={(e) => handleRuleValueInput(index, ruleIndex, e)}
                                                        rule={rule}
                                                        ruleTypeDetail={currentRuleDetail}/>
                                                </GridItem>
                                                <GridItem colSpan={4}>
                                                    {currentRuleDetail &&
                                                        <Badge>{FifleRuleTypesFormatDict[currentRuleDetail?.format]}</Badge>}
                                                </GridItem>
                                            </Grid>
                                        </FormControl>
                                    </GridItem>

                                    <GridItem colSpan={2}>
                                        {ruleIndex === scope.rules.length - 1 && scope.rules.length < formParams.ruleTypes.length ? (
                                            <IconButton aria-label="Add rule" icon={<MdAdd/>} variant={"outline"}
                                                        colorScheme={"blue"} onClick={() => addRule(index)}/>
                                        ) : (
                                            <IconButton aria-label="Delete rule" icon={<MdDelete/>} variant={"outline"}
                                                        colorScheme={"red"}
                                                        onClick={() => deleteRule(index, ruleIndex)}/>
                                        )}
                                    </GridItem>
                                </Grid>
                            )
                        })}

                        <Flex justifyContent="flex-end" alignItems="center" w={"100%"}>
                            <Button colorScheme="blue" onClick={() => duplicateScope(index)} leftIcon={<MdContentCopy/>}
                                    w={"100%"}>
                                Duplicar
                            </Button>
                            <Button colorScheme="red" onClick={() => deleteScope(index)} leftIcon={<MdDelete/>} ml={2}
                                    w={"100%"}>
                                Excluir
                            </Button>
                        </Flex>
                    </Stack>
                )
            })}
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
