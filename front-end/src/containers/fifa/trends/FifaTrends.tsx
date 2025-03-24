import { Box, Skeleton } from "@chakra-ui/react";
import { hasFeature } from "../../../utils/helpers/featureHelper";
import { useUserState } from "../../../context/UserContext";
import { FEATURES } from "../../../utils/constants/featureConstants";
import GoalsTrend from "./components/GoalsTrend";
import ModuleNotHiredEmptyState from "components/ModuleNotHiredEmptyState";

const FifaTrends = () => {
  const userState = useUserState();
  const canAccessModule = hasFeature(userState?.user, FEATURES.TREND_MODULE);
  const featuresWereLoaded = !!userState.user?.subscription?.features;
  const renderNotHiredEmptyState = !featuresWereLoaded || !canAccessModule;

  if (renderNotHiredEmptyState)
    return (
      <Skeleton isLoaded={featuresWereLoaded}>
        <ModuleNotHiredEmptyState
          moduleName="Tendências"
          description="O módulo de tendências auxilia a entender o comportamento do mercado tanto a curto quanto a médio prazo, possibilitando que o apostador veja com mais clareza a tendência e, com isso, ajuste suas estratégias de acordo com o que o mercado está apresentando."
          helpDeskLink="https://pyrite-seaplane-54b.notion.site/Como-fuciona-o-M-dulo-de-An-lise-de-Tend-ncias-1c0532044a3980bba340f910002713be?pvs=4"
        />
      </Skeleton>
    );

  return (
    <Box p={6}>
      <GoalsTrend />
    </Box>
  );
};

export default FifaTrends;
