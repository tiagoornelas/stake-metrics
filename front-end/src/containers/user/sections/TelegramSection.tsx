import { Badge, Box, Flex, Heading, Skeleton, Text } from "@chakra-ui/react";
import TelegramChannelConnectModal from "containers/user/components/TelegramChannelConnectModal";
import TelegramChatConnectModal from "containers/user/components/TelegramChatConnectModal";
import TelegramSettingsModal from "containers/user/components/TelegramSettingsModal";
import { useUserState } from "context/UserContext";
import { useCallback, useEffect, useMemo, useState } from "react";
import { fetchTelegramChats } from "services/telegramService";
import { FEATURES } from "utils/constants/featureConstants";
import { getFeatureAmount } from "utils/helpers/featureHelper";
import { TelegramChat, UserContext } from "utils/interfaces";

const TelegramSection = () => {
  const [telegramChats, setTelegramChats] = useState<TelegramChat[]>([]);
  const [isTelegramLoaded, setIsTelegramLoaded] = useState<boolean>(false);
  const userContext: UserContext = useUserState();

  const connectedTelegramChats = useMemo(
    () => telegramChats.filter((chat) => !!chat.chatId),
    [telegramChats]
  );

  const getTelegramChats = useCallback(async () => {
    setIsTelegramLoaded(false);
    if (!!userContext.user.id) {
      const { messengerChats: chats } = await fetchTelegramChats(
        userContext.user.id
      );
      setTelegramChats(chats);
      setIsTelegramLoaded(true);
    }
  }, [userContext.user.id]);

  const telegramChatFeatures = getFeatureAmount(
    userContext.user,
    FEATURES.MESSENGER_CHAT
  );

  const availableTelegramChats = useMemo(
    () => telegramChatFeatures - connectedTelegramChats.length,
    [telegramChatFeatures, connectedTelegramChats.length]
  );

  useEffect(() => {
    getTelegramChats();
  }, [getTelegramChats, userContext.user]);

  return (
    <Box>
      <Skeleton isLoaded={isTelegramLoaded}>
        <Flex direction="column" mb={4}>
          <Flex gap={4} alignItems="center">
            <Heading size="md" mb={2}>
              Telegram
            </Heading>
            {connectedTelegramChats.length > 0 ? (
              <Badge colorScheme="green">Conectado</Badge>
            ) : (
              <Badge colorScheme="red">Desconectado</Badge>
            )}
          </Flex>
          <Text>{`Seu plano dá direito a ${telegramChatFeatures} chats do Telegram.`}</Text>
          {connectedTelegramChats.map((chat: TelegramChat, index: number) => (
            <Flex gap={4} mt={4} alignItems="center">
              <TelegramSettingsModal
                key={index}
                chat={chat}
                onCloseCallback={getTelegramChats}
              />
            </Flex>
          ))}
        </Flex>
        {availableTelegramChats > 0 && (
          <Flex direction="column" gap={4} alignItems="self-start">
            <Skeleton isLoaded={isTelegramLoaded}>
              <TelegramChatConnectModal onCloseCallback={getTelegramChats} />
            </Skeleton>
            <Skeleton isLoaded={isTelegramLoaded}>
              <TelegramChannelConnectModal onCloseCallback={getTelegramChats} />
            </Skeleton>
          </Flex>
        )}
      </Skeleton>
    </Box>
  );
};

export default TelegramSection;
