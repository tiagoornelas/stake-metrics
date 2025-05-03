import { Box, Flex, Heading, Skeleton, Text } from "@chakra-ui/react";
import EditUserModal from "../components/EditUserModal";
import { UserContext } from "utils/interfaces";
import { useUserState } from "context/UserContext";
import ChangePasswordModal from "../components/ChangePasswordModal";

const UserSection = () => {
  const userContext: UserContext = useUserState();
  const isLoaded: boolean = !!userContext.user.id;

  return (
    <Box>
      <Skeleton isLoaded={isLoaded}>
        <Flex direction="column" mb={4}>
          <Heading size="md" mb={2}>
            Usuário
          </Heading>
          <Heading size="sm">Nome</Heading>
          <Text>{userContext.user.name}</Text>
          <Heading size="sm">E-mail</Heading>
          <Text>{userContext.user.email}</Text>
        </Flex>
        <Flex direction="column" gap={4} alignItems="self-start">
          <ChangePasswordModal />
          <EditUserModal />
        </Flex>
      </Skeleton>
    </Box>
  );
};

export default UserSection;
