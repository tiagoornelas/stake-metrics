import {
  Badge,
  Box,
  Button,
  Flex,
  Heading,
  Skeleton,
  Tag,
  Text,
} from "@chakra-ui/react";
import { useUserState } from "context/UserContext";
import { useCallback, useEffect, useState } from "react";
import { createPortalSession } from "services/planService";
import { getFeaturesWithLabels } from "utils/helpers/featureHelper";
import { applyDateMask } from "utils/helpers/sanitizationHelper";

const PlanSection = () => {
  const [managementLink, setManagementLink] = useState<string>("");
  const [isManagementLinkLoaded, setIsManagementLinkLoaded] =
    useState<boolean>(false);

  const userContext = useUserState();
  const features = getFeaturesWithLabels(userContext.user);

  const isLoaded: boolean = !!userContext.user.id;

  const getManagementLink = useCallback(async () => {
    const { url } = await createPortalSession();
    setManagementLink(url);
    setIsManagementLinkLoaded(true);
  }, []);

  const handleManagementSubscriptionClick = () => {
    if (managementLink !== "") window.location.assign(managementLink);
  };

  useEffect(() => {
    getManagementLink();
  }, [getManagementLink, userContext.user]);

  return (
    <Box>
      <Skeleton isLoaded={isLoaded}>
        <Flex direction="column" mb={4}>
          <Flex gap={4} alignItems="center">
            <Heading size="md" mb={2}>
              Plano
            </Heading>
            {userContext.user.isExpired ? (
              <Badge colorScheme="red">Expirado</Badge>
            ) : userContext.user.subscription?.status === "ACTIVE" ? (
              <Badge colorScheme="green">Ativo</Badge>
            ) : userContext.user.subscription?.status === "TRIALING" ? (
              <Badge colorScheme="yellow">Testando</Badge>
            ) : (
              <Badge colorScheme="red">Inativo</Badge>
            )}
          </Flex>
          {userContext.user.subscription?.expiresAt && (
            <Box>
              <Heading size="sm">
                {userContext.user.isExpired ? "Expirou em" : "Expira em"}
              </Heading>
              <Text>
                {applyDateMask(userContext.user.subscription?.expiresAt)}
              </Text>
            </Box>
          )}
          <Box mt={2}>
            <Heading size="sm">Seu plano inclui</Heading>
            {features.map((feature) => (
              <Flex alignItems="center" key={feature.label} mb={1}>
                <Text>{feature.label}</Text>
                <Tag
                  colorScheme={feature.amount === 0 ? "red" : "green"}
                  ml={2}
                >
                  {feature.amount === 0
                    ? "Não"
                    : feature.amount === 1
                    ? "Sim"
                    : feature.amount}
                </Tag>
              </Flex>
            ))}
          </Box>
        </Flex>
        <Flex direction="column" gap={4} alignItems="self-start">
          <Skeleton isLoaded={isManagementLinkLoaded}>
            <Button onClick={handleManagementSubscriptionClick}>
              Gerenciar assinatura
            </Button>
          </Skeleton>
        </Flex>
      </Skeleton>
    </Box>
  );
};

export default PlanSection;
