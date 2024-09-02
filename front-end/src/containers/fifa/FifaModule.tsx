import { Box, Tab, TabList, TabPanel, TabPanels, Tabs, Tag } from "@chakra-ui/react";
import NewFeatureEmptyState from "components/NewFeatureEmptyState";
import FifaBets from "containers/fifa/bets/FifaBets";
import FifaStrategies from "containers/fifa/strategy/FifaStrategies";
import { useUserState } from "context/UserContext";
import { FEATURES } from "utils/constants/featureConstants";
import { getFeatureAmount } from "utils/helpers/featureHelper";

const FifaModule = () => {
  const { user } = useUserState();
  const canUserAccessTrendModule = getFeatureAmount(user, FEATURES.TREND_MODULE) >= 1 ?? false;

  return (
    <Box p={4}>
      <Tabs variant="enclosed" isLazy>
        <Box
          overflow="auto"
          css={{
            "&::-webkit-scrollbar": {
              display: "none"
            },
            "-ms-overflow-style": "none",
            "scrollbar-width": "none"
          }}
        >
          <TabList w="max-content">
            <Tab>Estratégias</Tab>
            <Tab>Entradas</Tab>
            <Tab isDisabled={!canUserAccessTrendModule}>
              Tendências
              <Tag ml={2} colorScheme={"blue"}>
                Em breve
              </Tag>
            </Tab>
          </TabList>
        </Box>
        <TabPanels>
          <TabPanel>
            <FifaStrategies />
          </TabPanel>
          <TabPanel>
            <FifaBets />
          </TabPanel>
          <TabPanel>
            <NewFeatureEmptyState
              text={
                "Em breve será possível monitorar os jogos que ocorreram e as suas estatísticas, ver quais eram as previsões para aquela partida e qual foi, de fato, o resultado, permitindo que você use essas informações para encontrar as estratégias mais lucrativas."
              }
            />
          </TabPanel>
          <TabPanel>
            <NewFeatureEmptyState
              text={
                "Em breve será possível acompanhar as tendências dos mercados de E-Soccer com informações relevantes para entender como as ligas estão se comportando."
              }
            />
          </TabPanel>
        </TabPanels>
      </Tabs>
    </Box>
  );
};

export default FifaModule;
