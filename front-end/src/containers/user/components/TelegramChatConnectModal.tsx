import {Button, Input, Skeleton, Stack, Text} from "@chakra-ui/react";
import Modal from "components/Modal";
import {useUserState} from "context/UserContext";
import {useErrorToast} from "hooks/useErrorToast";
import React, {useState} from 'react';
import {beginIntegration, integratePrivateChat} from "services/telegramService";
import {SUCCESS_TYPES} from "utils/constants/successConstants";
import {TelegramChat, UserContext} from "utils/interfaces";

type Props = {
    onCloseCallback?: () => void;
};

const TelegramChatConnectModal = ({onCloseCallback}: Props) => {
    const userContext: UserContext = useUserState();

    const [isLoaded, setIsLoaded] = useState<boolean>(true);
    const [telegramPassPhrase, setTelegramPassPhrase] = useState<string>("");

    const handleGenerate = useErrorToast(async () => {
        if (!userContext.user.id) throw new Error("Erro ao gerar código para conexão.");
        setIsLoaded(false);
        const {telegramChatId: id} = await beginIntegration(userContext.user.id);
        setTelegramPassPhrase(id);
        setIsLoaded(true);
    });

    const handleCopy = useErrorToast(
        () => navigator.clipboard.writeText(telegramPassPhrase),
        SUCCESS_TYPES.TELEGRAM_CODE_COPIED
    );

    const handleClick = telegramPassPhrase === "" ? handleGenerate : handleCopy;

    const handleBotLinkClick = () => window.open("https://t.me/StakeMetricsBot", "_blank");

    const handleIntegrate = useErrorToast(async () => {
        if (!userContext.user.id) throw new Error("Erro ao integrar chat privado.");
        await integratePrivateChat(userContext.user.id);
    }, SUCCESS_TYPES.TELEGRAM_CHAT_CONNECTED);

    return (
        <Modal buttonText="Conectar Telegram" title="Conectar" actionText="Conectar" actionCallback={handleIntegrate}
               onCloseCallback={onCloseCallback}>
            <Stack spacing="5">
                <Text><b>Para conectar um chat privado do Telegram:</b></Text>
                <Text><b>1.</b> Clique em gerar código e copie o código abaixo.</Text>
                <Skeleton isLoaded={isLoaded}>
                    <Input value={telegramPassPhrase} readOnly/>
                </Skeleton>
                <Button onClick={handleClick}>{telegramPassPhrase === "" ? "Gerar código" : "Copiar código"}</Button>
                <Text><b>2.</b> Inicie uma conversa com o bot Stake Metrics da conta que você deseja conectar.</Text>
                <Button onClick={handleBotLinkClick}>Iniciar conversa</Button>
                <Text><b>3.</b> Cole o código e envie para o bot exatamente como copiado acima, depois clique em
                    "Conectar".</Text>
            </Stack>
        </Modal>
    );
}

export default TelegramChatConnectModal;
