import { Badge, Flex, Heading, Skeleton, Text } from "@chakra-ui/react";
import AutoBettorIntegrationModal from "../components/AutoBettorIntegrationModal";

const IntegrationSection = () => {
  return (
    <Flex direction="column" mb={4}>
      <Flex gap={4} alignItems="center">
        <Heading size="md" mb={2}>
          Integrações
        </Heading>
        <Badge colorScheme="red">Não Integrado</Badge>
      </Flex>
      <Text>{`Seu plano dá direito a automação com AutoBettor.bet`}</Text>
      <Flex direction="column" gap={4} alignItems="self-start" mt={4}>
        <Skeleton isLoaded>
          <AutoBettorIntegrationModal handleIntegrate={() => {}} />
        </Skeleton>
      </Flex>
    </Flex>
  );
};

export default IntegrationSection;
