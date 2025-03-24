import {
  Button,
  Center,
  Flex,
  Heading,
  Icon,
  Skeleton,
  Text,
} from "@chakra-ui/react";
import { useCallback, useEffect, useState } from "react";
import { FiLock } from "react-icons/fi";
import { createPortalSession } from "services/planService";

interface ModuleNotHiredEmptyStateProps {
  moduleName: string;
  description: string;
  helpDeskLink?: string;
}

const ModuleNotHiredEmptyState = ({
  moduleName,
  description,
  helpDeskLink,
}: ModuleNotHiredEmptyStateProps) => {
  const [managementLink, setManagementLink] = useState<string>("");
  const [isManagementLinkLoaded, setIsManagementLinkLoaded] = useState<boolean>(false);

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
  }, [getManagementLink]);

  return (
    <Center h="full" w="full" py={10}>
      <Flex 
        direction="column" 
        align="center" 
        maxW="md" 
        textAlign="center"
        bg="gray.50" 
        p={8} 
        borderRadius="md"
        boxShadow="sm"
      >
        <Icon as={FiLock} boxSize={12} color="gray.500" mb={4} />
        <Heading size="md" mb={2} color="gray.800">
          Acesso ao Módulo de {moduleName} não incluído no plano
        </Heading>
        <Text color="gray.600">
          {description}
        </Text>
        <Text mb={6} color="gray.600">
          Caso você já tenha um plano com acesso ao módulo e mesmo assim não consegue acessá-lo, entre em contato com o suporte usando o ícone de conversa no canto direito inferior da sua tela.
        </Text>
        <Flex direction="column" gap={4} align="center">
          <Skeleton isLoaded={isManagementLinkLoaded}>
            <Button colorScheme="blue" onClick={handleManagementSubscriptionClick}>
              Gerenciar assinatura
            </Button>
          </Skeleton>
          {helpDeskLink && (
            <Button as="a" href={helpDeskLink} target={"_blank"} variant="link" colorScheme="blue">
              Conheça o módulo
            </Button>
          )}
        </Flex>
      </Flex>
    </Center>
  );
};

export default ModuleNotHiredEmptyState;
