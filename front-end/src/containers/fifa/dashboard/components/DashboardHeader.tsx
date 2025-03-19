import { useUserState } from "context/UserContext";
import { Box, Heading, Text } from "@chakra-ui/react";
import useThemeColors from "hooks/useThemeColors";

const DashboardHeader = () => {
  const userState = useUserState();
  const hasUserName = Boolean(userState.user.name);
  const colors = useThemeColors();

  return (
    <Box mb={6}>
      <Heading size="sm" mb={1}>
        {hasUserName ? `Olá, ${userState.user.name}!` : "Olá!"}
      </Heading>
      <Text fontSize={"xs"} color={colors.textContrast}>
        O seu painel mostra o resultado de suas entradas ativas, o treinamento das estratégias é ignorado.
      </Text>
    </Box>
  );
};

export default DashboardHeader;
