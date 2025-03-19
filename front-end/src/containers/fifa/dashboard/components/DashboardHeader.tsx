import { useUserState } from "context/UserContext";
import { Box, Heading, Text } from "@chakra-ui/react";

const DashboardHeader = () => {
  const userState = useUserState();
  const hasUserName = Boolean(userState.user.name);

  return (
    <Box mb={6}>
      <Heading size="sm" mb={1}>
        {hasUserName ? `Olá, ${userState.user.name}!` : "Olá!"}
      </Heading>
      <Text fontSize={"xs"} color="gray.600">
        O seu painel mostra o resultado de suas entradas ativas, o treinamento das estratégias é ignorado.
      </Text>
    </Box>
  );
};

export default DashboardHeader;
