import { SimpleGrid, useBreakpointValue } from "@chakra-ui/react";
import SupportSection from "./sections/SupportSections";
import PlanSection from "./sections/PlanSection";
import TelegramSection from "./sections/TelegramSection";
import IntegrationSection from "./sections/IntegrationSection";
import UserSection from "./sections/UserSection";
import { getFeatureAmount } from "utils/helpers/featureHelper";
import { useUserState } from "context/UserContext";
import { FEATURES } from "utils/constants/featureConstants";

const UserManagement = () => {
  const columns: number = useBreakpointValue({ base: 1, md: 2, lg: 3 }) || 2;

  const userState = useUserState();
  const featureAmount = userState
    ? getFeatureAmount(userState.user, FEATURES.EARLY_ACCESS)
    : 0;
  const hasEarlyAccess = featureAmount > 0;

  return (
    <SimpleGrid columns={columns} spacing={10} p={4}>
      {hasEarlyAccess ? <IntegrationSection /> : null}
      <TelegramSection />
      <PlanSection />
      <SupportSection />
      <UserSection />
    </SimpleGrid>
  );
};

export default UserManagement;
