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
  Tag,
  useColorModeValue,
} from "@chakra-ui/react";
import MultiSelect from "components/MultiSelect";
import StrategyRuleValueInput from "containers/fifa/strategy/components/StrategyRuleValueInput";
import useInvalidateStrategyQuery from "containers/fifa/strategy/hooks/useInvalidateStrategyQuery";
import { useErrorToast } from "hooks/useErrorToast";
import useThemeColors from "hooks/useThemeColors";
import React, { ChangeEvent, useEffect, useState } from "react";
import {
  FaRegHourglass,
  MdAdd,
  MdContentCopy,
  MdDelete,
} from "react-icons/all";
import {
  getFifaStrategyParams,
  getStrategy,
  saveStrategy,
} from "services/strategyService";
import {
  FifaMarketSubTypesDict,
  FifaMarketTypesDict,
  FifaMatchupTypesDict,
  FifaRuleTypesDict,
  FifaStrategyScopeTypesDict,
  FifleRuleTypesFormatDict,
} from "utils/constants/strategyConstants";
import { SUCCESS_TYPES } from "utils/constants/successConstants";
import { isFormValid, validateForm } from "utils/helpers/strategyHelper";
import {
  FifaLeagueResponse,
  FifaPlayerResponse,
  Option,
  Scope,
  StrategyCreationBody,
  StrategyParams,
} from "utils/interfaces";

const StrategyForm = ({
  strategyId,
  onClose,
}: {
  strategyId?: string;
  onClose?: () => void;
}) => {
  const [formParams, setFormParams] = useState<StrategyParams | null>(null);
  const [isLoaded, setIsLoaded] = useState<boolean>(false);
  const [form, setForm] = useState<StrategyCreationBody>({
    id: undefined,
    name: "",
    marketType: null,
    marketSubTypes: [],
    leagues: [],
    excludedPlayers: [],
    scopes: [],
  });

  const [dynamicSubmarkets, setDynamicSubmarkets] = useState<Option[]>([]);
  const [dynamicLeagues, setDynamicLeagues] = useState<Option[]>([]);
  const [dynamicPlayers, setDynamicPlayers] = useState<Option[]>([]);
  const [validationErrors, setValidationErrors] = useState<{
    [key: string]: boolean;
  }>({});
  const [isSaving, setIsSaving] = useState(false);

  const colors = useThemeColors();
  const invalidateStrategyQuery = useInvalidateStrategyQuery();
  const scopeGridBorderColor = useColorModeValue("#f0f0f0", "#47536b");

  useEffect(() => {
    const getFormParams = async () => {
      const params = await getFifaStrategyParams();
      setFormParams(params);
    };

    getFormParams();
  }, []);

  useEffect(() => {
    setIsLoaded(false);

    const fetchStrategy = async () => {
      if (strategyId) {
        const strategy = await getStrategy(strategyId);
        setForm({
          id: strategy.id,
          name: strategy.name,
          marketType: strategy.marketType,
          marketSubTypes: strategy.marketSubTypes,
          leagues: strategy.leagues.map(
            (league: FifaLeagueResponse) => league.id
          ),
          excludedPlayers: strategy.excludedPlayers.map(
            (player: FifaPlayerResponse) => player.id
          ),
          scopes: strategy.scopes,
        });
      } else if (formParams) {
        setForm((prevState) => ({
          ...prevState,
          marketType: formParams.marketTypes[0].marketType,
        }));
      }
    };

    fetchStrategy().then(() => {
      setTimeout(() => setIsLoaded(true), 500);
    });
  }, [formParams, strategyId]);

  useEffect(() => {
    const setSubmarketBasedOnMarketSelection = () => {
      if (form.marketType && formParams) {
        const market = formParams.marketTypes.find(
          (marketType) => marketType.marketType === form.marketType
        );
        if (market) {
          const options: Option[] = market.marketSubTypes.map((subtype) => ({
            value: subtype,
            label: FifaMarketSubTypesDict[subtype],
          }));
          setDynamicSubmarkets(options);
        }
      }
    };

    setSubmarketBasedOnMarketSelection();
  }, [form.marketType, formParams]);

  useEffect(() => {
    const setDynamicLeaguesBasedOnParams = () => {
      if (formParams) {
        setDynamicLeagues(
          formParams.leagues.map((league) => ({
            value: league.id,
            label: league.name,
          }))
        );
      }
    };

    setDynamicLeaguesBasedOnParams();
  }, [formParams]);

  useEffect(() => {
    const setDynamicPlayersBasedOnLeagueSelection = () => {
      if (form.leagues.length > 0 && formParams) {
        const players = formParams.players.filter(
          (player) => player.leagueId && form.leagues.includes(player.leagueId)
        );
        setDynamicPlayers(
          players.map((player) => ({ value: player.id, label: player.name }))
        );
      }
    };

    setDynamicPlayersBasedOnLeagueSelection();
  }, [form.leagues, formParams]);

  const handleSubmarketChange = (selectedSubmarkets: string[]) => {
    setForm((prevState) => ({
      ...prevState,
      marketSubTypes: selectedSubmarkets,
    }));
  };

  const handleLeaguesChange = (selectedLeagues: string[]) => {
    setForm((prevState) => ({
      ...prevState,
      leagues: selectedLeagues,
    }));
  };

  const handlePlayersChange = (selectedPlayers: string[]) => {
    setForm((prevState) => ({
      ...prevState,
      excludedPlayers: selectedPlayers,
    }));
  };

  const setScopes = (scopes: Scope[]) => {
    setForm((prevState) => ({
      ...prevState,
      scopes,
    }));
  };

  const handleInput = (
    e: ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { id, value } = e.target;
    setForm({
      ...form,
      [id]: value,
    });
  };

  const handleScopeInput = (
    index: number,
    e: ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const newScopes = [...form.scopes];
    newScopes[index] = {
      ...newScopes[index],
      [e.target.id]: e.target.value,
    };
    setScopes(newScopes);
  };

  const handleRuleTypeInput = (
    scopeIndex: number,
    ruleIndex: number,
    e: ChangeEvent<HTMLSelectElement>
  ) => {
    const newScopes = [...form.scopes];
    newScopes[scopeIndex].rules[ruleIndex] = {
      ...newScopes[scopeIndex].rules[ruleIndex],
      type: e.target.value,
    };
    setScopes(newScopes);
  };

  const handleRuleValueInput = (
    scopeIndex: number,
    ruleIndex: number,
    e: ChangeEvent<HTMLInputElement>
  ) => {
    const newScopes = [...form.scopes];
    newScopes[scopeIndex].rules[ruleIndex] = {
      ...newScopes[scopeIndex].rules[ruleIndex],
      value: +e.target.value,
    };
    setScopes(newScopes);
  };

  const addScope = () => {
    if (formParams) {
      const defaultScope: Scope = {
        type: formParams.scopeTypes[4] || "",
        matchup: formParams.matchupTypes[1] || "",
        rules: formParams.ruleTypes.map((ruleType) => ({
          type: ruleType.type,
          value: ruleType.defaultValue,
        })),
      };
      setScopes([...form.scopes, defaultScope]);
    }
  };

  const deleteScope = (index: number) => {
    const newScopes = form.scopes.filter((_, i) => i !== index);
    setScopes(newScopes);
  };

  const duplicateScope = (index: number) => {
    const scopeToDuplicate = form.scopes[index];
    const newScopes = [...form.scopes, { ...scopeToDuplicate }];
    setScopes(newScopes);
  };

  const addRule = (scopeIndex: number) => {
    const newScopes = [...form.scopes];
    const selectedRuleTypes = newScopes[scopeIndex].rules.map(
      (rule) => rule.type
    );
    const availableRuleTypes = formParams?.ruleTypes.filter(
      (ruleType) => !selectedRuleTypes.includes(ruleType.type)
    );
    const defaultRuleType = availableRuleTypes
      ? availableRuleTypes[0].type
      : "";
    const defaultRuleValue = availableRuleTypes
      ? availableRuleTypes[0].defaultValue
      : 0;

    newScopes[scopeIndex].rules.push({
      type: defaultRuleType,
      value: defaultRuleValue,
    });
    setScopes(newScopes);
  };

  const deleteRule = (scopeIndex: number, ruleIndex: number) => {
    const newScopes = [...form.scopes];
    newScopes[scopeIndex].rules = newScopes[scopeIndex].rules.filter(
      (_, i) => i !== ruleIndex
    );
    setScopes(newScopes);
  };

  const getScopeLabel = (count: number) => {
    return count === 1 ? "1 janela" : `${count} janelas`;
  };

  const handleSave = useErrorToast(async () => {
    if (!formParams)
      throw new Error(
        "Formulário sem parâmetros de validação. Entre em contato com o suporte se o erro persistir."
      );

    const errors = validateForm(form, formParams);
    setValidationErrors(errors);

    if (isFormValid(errors)) {
      setIsSaving(true);
      try {
        await saveStrategy(form);
        if (onClose) onClose();
        invalidateStrategyQuery();
      } finally {
        setIsSaving(false);
      }
    } else {
      throw new Error("Revise os campos em vermelho e tente novamente.");
    }
  }, SUCCESS_TYPES.STRATEGY_SAVED);

  return (
    <Stack spacing="5">
      <FormControl>
        <FormLabel htmlFor="name">Nome</FormLabel>
        <Input
          id="name"
          type="text"
          onChange={handleInput}
          value={form.name}
          isInvalid={validationErrors.name}
        />
      </FormControl>

      <Skeleton isLoaded={isLoaded}>
        <FormControl>
          <FormLabel htmlFor="market">Mercado</FormLabel>
          <Select
            id="marketType"
            onChange={handleInput}
            value={form.marketType || ""}
            isInvalid={validationErrors.marketType}
          >
            {formParams &&
              formParams.marketTypes.map((marketType) => (
                <option
                  key={marketType.marketType}
                  value={marketType.marketType}
                >
                  {FifaMarketTypesDict[marketType.marketType]}
                </option>
              ))}
          </Select>
        </FormControl>
      </Skeleton>

      <Skeleton isLoaded={isLoaded}>
        <FormControl>
          <MultiSelect
            title={"Linhas do mercado"}
            entity={["linha", "linhas"]}
            options={dynamicSubmarkets}
            defaultSelected={form.marketSubTypes}
            onChange={handleSubmarketChange}
            isInvalid={validationErrors.marketSubTypes}
          />
        </FormControl>
      </Skeleton>

      <Skeleton isLoaded={isLoaded}>
        <FormControl>
          <MultiSelect
            title={"Ligas observadas"}
            entity={["liga", "ligas"]}
            options={dynamicLeagues}
            defaultSelected={form.leagues}
            onChange={handleLeaguesChange}
            isInvalid={validationErrors.leagues}
          />
        </FormControl>
      </Skeleton>

      {form.leagues.length > 0 && (
        <Skeleton isLoaded={isLoaded}>
          <FormControl>
            <MultiSelect
              title={"Jogadores ignorados"}
              entity={["jogador", "jogadores"]}
              tagColor={"red"}
              sortSelected
              options={dynamicPlayers}
              defaultSelected={form.excludedPlayers}
              onChange={handlePlayersChange}
            />
          </FormControl>
        </Skeleton>
      )}

      <Skeleton isLoaded={isLoaded} w={"100%"}>
        <Button
          onClick={addScope}
          colorScheme={"blue"}
          rightIcon={<FaRegHourglass />}
          w={"100%"}
        >
          {validationErrors.scopes &&
            (form.scopes.length > 0 ? (
              <Tag m="2">{getScopeLabel(form.scopes.length)}</Tag>
            ) : (
              <Tag colorScheme="red" m="2">
                Sem janelas
              </Tag>
            ))}
          Adicionar janela de análise
        </Button>
      </Skeleton>

      {isLoaded &&
        formParams &&
        form.scopes.map((scope, index) => {
          return (
            <Stack
              key={index}
              spacing="5"
              border={`1px solid ${scopeGridBorderColor}`}
              padding="10px"
              borderRadius="5px"
            >
              <Grid templateColumns="repeat(12, 1fr)" gap={4}>
                <GridItem colSpan={6}>
                  <FormControl>
                    <FormLabel htmlFor={`matchup-${index}`}>
                      Confronto
                    </FormLabel>
                    <Select
                      id="matchup"
                      onChange={(e) => handleScopeInput(index, e)}
                      value={scope.matchup}
                      isInvalid={validationErrors[`scope-${index}-matchup`]}
                    >
                      {formParams.matchupTypes.map((matchupType) => (
                        <option key={matchupType} value={matchupType}>
                          {FifaMatchupTypesDict[matchupType]}
                        </option>
                      ))}
                    </Select>
                  </FormControl>
                </GridItem>

                <GridItem colSpan={6}>
                  <FormControl>
                    <FormLabel htmlFor={`type-${index}`}>Período</FormLabel>
                    <Select
                      id="type"
                      onChange={(e) => handleScopeInput(index, e)}
                      value={scope.type}
                      isInvalid={validationErrors[`scope-${index}-type`]}
                    >
                      {formParams.scopeTypes.map((scopeType) => (
                        <option key={scopeType} value={scopeType}>
                          {FifaStrategyScopeTypesDict[scopeType]}
                        </option>
                      ))}
                    </Select>
                  </FormControl>
                </GridItem>
              </Grid>

              {scope.rules.map((rule, ruleIndex) => {
                const selectedRuleTypes = scope.rules.map((rule) => rule.type);
                const availableRuleTypes = formParams.ruleTypes.filter(
                  (ruleType) =>
                    !selectedRuleTypes.includes(ruleType.type) ||
                    ruleType.type === rule.type
                );
                const currentRuleDetail = formParams.ruleTypes.find(
                  (ruleType) => ruleType.type === rule.type
                );

                return (
                  <Grid
                    key={ruleIndex}
                    templateColumns="repeat(12, 1fr)"
                    gap={4}
                    alignItems={"end"}
                    justifyContent={"center"}
                  >
                    <GridItem colSpan={6}>
                      <FormControl>
                        {ruleIndex === 0 && (
                          <FormLabel htmlFor={`ruleType-${index}-${ruleIndex}`}>
                            Regras
                          </FormLabel>
                        )}
                        <Select
                          id="type"
                          onChange={(e) =>
                            handleRuleTypeInput(index, ruleIndex, e)
                          }
                          value={rule.type}
                          isInvalid={
                            validationErrors[
                              `scope-${index}-rule-${ruleIndex}-type`
                            ]
                          }
                        >
                          {availableRuleTypes.map((ruleType) => (
                            <option key={ruleType.type} value={ruleType.type}>
                              {FifaRuleTypesDict[ruleType.type]}
                            </option>
                          ))}
                        </Select>
                      </FormControl>
                    </GridItem>

                    <GridItem colSpan={4}>
                      <FormControl>
                        {ruleIndex === 0 && (
                          <FormLabel
                            htmlFor={`ruleValue-${index}-${ruleIndex}`}
                          >
                            Valor
                          </FormLabel>
                        )}
                        <Grid
                          templateColumns="repeat(12, 1fr)"
                          gap={2}
                          alignItems={"center"}
                          justifyItems={"center"}
                        >
                          <GridItem
                            colSpan={8}
                            alignItems={"center"}
                            justifyItems={"center"}
                          >
                            <StrategyRuleValueInput
                              onChange={(e) =>
                                handleRuleValueInput(index, ruleIndex, e)
                              }
                              rule={rule}
                              ruleTypeDetail={currentRuleDetail}
                              isInvalid={
                                validationErrors[
                                  `scope-${index}-rule-${ruleIndex}-value`
                                ]
                              }
                            />
                          </GridItem>
                          <GridItem colSpan={4}>
                            {currentRuleDetail && (
                              <Badge>
                                {
                                  FifleRuleTypesFormatDict[
                                    currentRuleDetail?.format
                                  ]
                                }
                              </Badge>
                            )}
                          </GridItem>
                        </Grid>
                      </FormControl>
                    </GridItem>

                    <GridItem colSpan={2}>
                      {ruleIndex === 0 &&
                      scope.rules.length < formParams.ruleTypes.length ? (
                        <IconButton
                          aria-label="Add rule"
                          icon={<MdAdd />}
                          variant={"outline"}
                          colorScheme={"blue"}
                          onClick={() => addRule(index)}
                        />
                      ) : (
                        <IconButton
                          aria-label="Delete rule"
                          icon={<MdDelete />}
                          variant={"outline"}
                          colorScheme={"red"}
                          onClick={() => deleteRule(index, ruleIndex)}
                        />
                      )}
                    </GridItem>
                  </Grid>
                );
              })}

              <Flex justifyContent="flex-end" alignItems="center" w={"100%"}>
                <Button
                  colorScheme="blue"
                  onClick={() => duplicateScope(index)}
                  leftIcon={<MdContentCopy />}
                  w={"100%"}
                >
                  Duplicar
                </Button>
                <Button
                  colorScheme="red"
                  onClick={() => deleteScope(index)}
                  leftIcon={<MdDelete />}
                  ml={2}
                  w={"100%"}
                >
                  Excluir
                </Button>
              </Flex>
            </Stack>
          );
        })}
      <Flex justifyContent={"flex-end"} mb={2}>
        <Button mr={3} onClick={onClose}>
          Voltar
        </Button>
        <Button
          bgColor={colors.product}
          color={colors.productContrast}
          onClick={handleSave}
          isLoading={isSaving}
          loadingText="Salvando"
        >
          Salvar
        </Button>
      </Flex>
    </Stack>
  );
};

export default StrategyForm;
