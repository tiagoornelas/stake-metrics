import {SimpleGrid, useBreakpointValue} from "@chakra-ui/react";
import SupportSection from "./sections/SupportSections";
import PlanSection from "./sections/PlanSection";
import TelegramSection from "./sections/TelegramSection";
import IntegrationSection from "./sections/IntegrationSection";
import UserSection from "./sections/UserSection";

const UserManagement = () => {
  const columns: number = useBreakpointValue({base: 1, md: 2, lg: 3}) || 2;

  return (
    <SimpleGrid columns={columns} spacing={10} p={4}>
      <IntegrationSection />
      <TelegramSection />
      <PlanSection />
      <SupportSection />
      <UserSection />
    </SimpleGrid>
  );
};

export default UserManagement;
