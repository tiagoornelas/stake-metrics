import {Badge, Box, Button, Flex, Heading, SimpleGrid, Skeleton, Tag, Text, useBreakpointValue} from "@chakra-ui/react";
import ChangePasswordModal from "containers/user/components/ChangePasswordModal";
import EditUserModal from "containers/user/components/EditUserModal";
import TelegramChannelConnectModal from "containers/user/components/TelegramChannelConnectModal";
import TelegramChatConnectModal from "containers/user/components/TelegramChatConnectModal";
import TelegramSettingsModal from "containers/user/components/TelegramSettingsModal";
import {useUserState} from "context/UserContext";
import * as React from "react";
import {useCallback, useEffect, useMemo, useState} from "react";
import {createPortalSession} from "services/planService";
import {fetchTelegramChats} from "services/telegramService";
import {FEATURES} from "utils/constants/featureConstants";
import {getFeatureAmount, getFeaturesWithLabels} from "utils/helpers/featureHelper";
import {applyDateMask} from "utils/helpers/sanitizationHelper";
import {TelegramChat, UserContext} from "utils/interfaces";

const UserManagement = () => {
    const [managementLink, setManagementLink] = useState<string>("");
    const [isManagementLinkLoaded, setIsManagementLinkLoaded] = useState<boolean>(false);
    const [telegramChats, setTelegramChats] = useState<TelegramChat[]>([]);
    const [isTelegramLoaded, setIsTelegramLoaded] = useState<boolean>(false);
    const columns: number = useBreakpointValue({base: 1, md: 2, lg: 3}) || 2;
    const userContext: UserContext = useUserState();
    const isLoaded: boolean = !!userContext.user.id;
    const connectedTelegramChats = useMemo(() => telegramChats.filter(chat => !!chat.chatId), [telegramChats]);

    const telegramChatFeatures = getFeatureAmount(userContext.user, FEATURES.MESSENGER_CHAT);
    const availableTelegramChats = useMemo(
        () => telegramChatFeatures - connectedTelegramChats.length,
        [telegramChatFeatures, connectedTelegramChats.length]
    );

    const getManagementLink = useCallback(async () => {
        const {url} = await createPortalSession();
        setManagementLink(url);
        setIsManagementLinkLoaded(true);
    }, []);

    const getTelegramChats = useCallback(async () => {
        setIsTelegramLoaded(false);
        if (!!userContext.user.id) {
            const {messengerChats: chats} = await fetchTelegramChats(userContext.user.id);
            setTelegramChats(chats);
            setIsTelegramLoaded(true);
        }
    }, [userContext.user.id]);

    useEffect(() => {
        getManagementLink();
        getTelegramChats();
    }, [getManagementLink, getTelegramChats, userContext.user]);

    const handleManagementSubscriptionClick = () => {
        if (managementLink !== "") window.location.assign(managementLink);
    }

    const UserSection = () => (
        <Box>
            <Skeleton isLoaded={isLoaded}>
                <Flex direction="column" mb={4}>
                    <Heading size="md" mb={2}>Usuário</Heading>
                    <Heading size="sm">Nome</Heading>
                    <Text>{userContext.user.name}</Text>
                    <Heading size="sm">E-mail</Heading>
                    <Text>{userContext.user.email}</Text>
                </Flex>
                <Flex direction="column" gap={4} alignItems="self-start">
                    <EditUserModal/>
                    <ChangePasswordModal/>
                </Flex>
            </Skeleton>
        </Box>
    );

    const TelegramSection = () => (
        <Box>
            <Skeleton isLoaded={isTelegramLoaded}>
                <Flex direction="column" mb={4}>
                    <Flex gap={4} alignItems="center"><Heading size="md" mb={2}>Telegram</Heading>
                        {connectedTelegramChats.length > 0 ? <Badge colorScheme='green'>Conectado</Badge> :
                            <Badge colorScheme='red'>Desconectado</Badge>}
                    </Flex>
                    <Text>{`Seu plano dá direito a ${telegramChatFeatures} chats do Telegram.`}</Text>
                    {connectedTelegramChats.map((chat: TelegramChat, index: number) => (
                        <Flex gap={4} mt={4} alignItems="center">
                            <TelegramSettingsModal key={index} chat={chat} onCloseCallback={getTelegramChats}/>
                        </Flex>))}
                </Flex>
                {availableTelegramChats > 0 && (
                    <Flex direction="column" gap={4} alignItems="self-start">
                        <Skeleton isLoaded={isTelegramLoaded}>
                            <TelegramChatConnectModal onCloseCallback={getTelegramChats}/>
                        </Skeleton>
                        <Skeleton isLoaded={isTelegramLoaded}>
                            <TelegramChannelConnectModal onCloseCallback={getTelegramChats}/>
                        </Skeleton>
                    </Flex>
                )}
            </Skeleton>
        </Box>
    );

    const PlanSection = () => {
        const {user} = useUserState();
        const features = getFeaturesWithLabels(user);

        return (
            <Box>
                <Skeleton isLoaded={isLoaded}>
                    <Flex direction="column" mb={4}>
                        <Flex gap={4} alignItems="center"><Heading size="md" mb={2}>Plano</Heading>
                            {userContext.user.isExpired ? <Badge colorScheme='red'>Expirado</Badge> :
                                <Badge colorScheme='green'>Ativo</Badge>}
                        </Flex>
                        {userContext.user.subscription?.expiresAt && (
                            <Box>
                                <Heading size="sm">{userContext.user.isExpired ? "Expirou em" : "Expira em"}</Heading>
                                <Text>{applyDateMask(userContext.user.subscription?.expiresAt)}</Text>
                            </Box>
                        )}
                        <Box mt={2}>
                            <Heading size="sm">Seu plano inclui</Heading>
                            {features.map((feature) => (
                                <Flex alignItems="center" key={feature.label} mb={1}>
                                    <Text>{feature.label}</Text>
                                    <Tag colorScheme={feature.amount === 0 ? 'red' : 'green'} ml={2}>
                                        {feature.amount === 0 ? 'Não' : feature.amount === 1 ? 'Sim' : feature.amount}
                                    </Tag>
                                </Flex>
                            ))}
                        </Box>
                    </Flex>
                    <Flex direction="column" gap={4} alignItems="self-start">
                        <Skeleton isLoaded={isManagementLinkLoaded}>
                            <Button onClick={handleManagementSubscriptionClick}>Gerenciar assinatura</Button>
                        </Skeleton>
                    </Flex>
                </Skeleton>
            </Box>
        );
    }

    const SupportSection = () => (
        <Box>
            <Skeleton isLoaded={isLoaded}>
                <Flex direction="column" mb={4}>
                    <Flex gap={4} alignItems="center"><Heading size="md" mb={2}>Suporte</Heading>
                    </Flex>
                    <Text>Precisa de ajuda? Entre em contato com o suporte abaixo.</Text>
                </Flex>
                <Flex direction="column" gap={4} alignItems="self-start">
                    <Button>Entrar em contato</Button>
                </Flex>
            </Skeleton>
        </Box>
    );

    return <SimpleGrid columns={columns} spacing={10} p={4}>
        <UserSection/>
        <TelegramSection/>
        <PlanSection/>
        <SupportSection/>
    </SimpleGrid>
}

export default UserManagement;
