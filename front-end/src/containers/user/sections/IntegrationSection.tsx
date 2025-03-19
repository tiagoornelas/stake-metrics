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
import TippyIntegrationModal from "../components/TippyIntegrationModal";
import { AutoBettor } from "utils/interfaces";
import { useCallback, useEffect, useState } from "react";
import {
  activateAutoBettor,
  deactivateAutoBettor,
  deleteAutoBettor,
  fetchAutoBettor,
} from "services/integrationService";
import { MdSettings } from "react-icons/md";
import { useErrorToast } from "hooks/useErrorToast";
import { SUCCESS_TYPES } from "utils/constants/successConstants";
import { useUserState } from "context/UserContext";
import { hasFeature } from "utils/helpers/featureHelper";
import { FEATURES } from "utils/constants/featureConstants";

const IntegrationSection = () => {
  const [autoBettor, setAutoBettor] = useState<AutoBettor>();
  const [isLoaded, setIsLoaded] = useState<boolean>(false);

  const userState = useUserState();
  const hasAutoBettorAccess = hasFeature(userState.user, FEATURES.AUTO_BETTOR);

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
    const channelName = `Tippy: ${autoBettor?.name}` || "Automação com Tippy";

    return (
      <Flex alignItems={"center"} gap={4}>
        <Text>{channelName}</Text>
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
          {hasAutoBettorAccess ? (
            <Text>{`Seu plano dá direito a integração com plataforma de automação de apostas.`}</Text>
          ) : (
            <Text>{`Seu plano não inclui integrações, faça o upgrade de plano para usufruir de benefícios como integração com plataforma de automação de apostas.`}</Text>
          )}
          {hasAutoBettorAccess && <Flex direction="column" gap={4} alignItems="self-start" mt={4}>
            <Skeleton isLoaded>
              {autoBettor ? (
                <IntegratedChannel />
              ) : (
                <TippyIntegrationModal onCloseCallback={fetch} />
              )}
            </Skeleton>
          </Flex>}
        </Flex>
      </Skeleton>
    </Box>
  );
};

export default IntegrationSection;
