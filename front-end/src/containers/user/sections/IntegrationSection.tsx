import {
  Badge,
  Box,
  Flex,
  Heading,
  IconButton,
  Menu,
  MenuButton,
  MenuItem,
  MenuList,
  Skeleton,
  Text,
} from "@chakra-ui/react";
import AutoBettorIntegrationModal from "../components/AutoBettorIntegrationModal";
import { AutoBettor } from "utils/interfaces";
import { useCallback, useEffect, useState } from "react";
import { activateAutoBettor, deactivateAutoBettor, deleteAutoBettor, fetchAutoBettor } from "services/integrationService";
import { MdSettings } from "react-icons/md";
import { useErrorToast } from "hooks/useErrorToast";
import { SUCCESS_TYPES } from "utils/constants/successConstants";

const IntegrationSection = () => {
  const [autoBettor, setAutoBettor] = useState<AutoBettor>();
  const [isLoaded, setIsLoaded] = useState<boolean>(false);

  const fetch = useCallback(async () => {
    setIsLoaded(false);
    const response = await fetchAutoBettor();
    setAutoBettor(response.autoBettor);
    setIsLoaded(true);
  }, []);

  useEffect(() => {
    fetch();
  }, [fetch]);

  const handleDelete = useErrorToast(async () => {
    await deleteAutoBettor();
    await fetch();
  }, SUCCESS_TYPES.AUTO_BETTOR_DELETED);

  const handleActivate = useErrorToast(async () => {
    await activateAutoBettor();
    await fetch();
  }, SUCCESS_TYPES.AUTO_BETTOR_ACTIVATED);

  const handleDeactivate = useErrorToast(async () => {
    await deactivateAutoBettor();
    await fetch();
  }, SUCCESS_TYPES.AUTO_BETTOR_DEACTIVATED);

  const IntegratedBadge = () => {
    return autoBettor ? (
      <Badge colorScheme="green">Integrado</Badge>
    ) : (
      <Badge colorScheme="red">Não Integrado</Badge>
    );
  };

  const IntegratedChannel = () => {
    return (
      <Flex alignItems={"center"} gap={4}>
        <Text>Automação com AutoBettor</Text>
        <Badge colorScheme={autoBettor?.status === "ACTIVE" ? "green" : "red"}>
          {autoBettor?.status === "ACTIVE" ? "Ativo" : "Inativo"}
        </Badge>
        <Menu>
          <MenuButton as={IconButton} icon={<MdSettings />} />
          <MenuList>
            {autoBettor?.status === "ACTIVE" ? (
              <MenuItem onClick={handleDeactivate}>Desativar</MenuItem>
            ) : (
              <MenuItem onClick={handleActivate}>Ativar</MenuItem>
            )}
            <MenuItem onClick={handleDelete}>Excluir</MenuItem>
          </MenuList>
        </Menu>
      </Flex>
    );
  };

  return (
    <Box>
      <Skeleton isLoaded={isLoaded}>
        <Flex direction="column" mb={4}>
          <Flex gap={4} alignItems="center">
            <Heading size="md" mb={2}>
              Integrações
            </Heading>
            <IntegratedBadge />
          </Flex>
          <Text>{`Seu plano dá direito a automação com AutoBettor.bet`}</Text>
          <Flex direction="column" gap={4} alignItems="self-start" mt={4}>
            <Skeleton isLoaded>
              {autoBettor ? (
                <IntegratedChannel />
              ) : (
                <AutoBettorIntegrationModal onCloseCallback={fetch} />
              )}
            </Skeleton>
          </Flex>
        </Flex>
      </Skeleton>
    </Box>
  );
};

export default IntegrationSection;
