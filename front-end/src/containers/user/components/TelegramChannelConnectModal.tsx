import {Button, Input, Stack, Text} from "@chakra-ui/react";
import Modal from "components/Modal";
import {useUserState} from "context/UserContext";
import {useErrorToast} from "hooks/useErrorToast";
import React, {useEffect, useState} from 'react';
import {SiTelegram} from "react-icons/all";
import {integrateChannel} from "services/telegramService";
import {SUCCESS_TYPES} from "utils/constants/successConstants";
import {UserContext} from "utils/interfaces";

type Props = {
    onCloseCallback?: () => void;
};

const TelegramChannelConnectModal = ({onCloseCallback}: Props) => {
    const userContext: UserContext = useUserState();
    const [channelId, setChannelId] = useState<string>("");
    const [channelUrl, setChannelUrl] = useState<string>("");
    const [isValidUrl, setIsValidUrl] = useState<boolean>(false);

    const handleLinkClick = () => window.open("https://web.telegram.org/", "_blank");

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {value} = e.target;
        setChannelUrl(value);
    }

    const handleIntegrate = useErrorToast(async () => {
        if (!userContext.user.id) throw new Error("Erro ao integrar chat privado.");
        await integrateChannel(userContext.user.id, channelId);
    }, SUCCESS_TYPES.TELEGRAM_CHAT_CONNECTED);

    const validateUrl = (url: string) => {
        const regex = /^https:\/\/(?:web|desktop|macos|windows)\.telegram\.org\/[a-z]\/#@?([a-zA-Z0-9_-]+)$/;
        const match = url.match(regex);

        if (match) {
            let id = match[1];

            if (/^-\d+$/.test(id)) {
                if (!id.startsWith('-100')) {
                    const numericPart = id.startsWith('-') ? id.substring(1) : id;
                    id = `-100${numericPart}`;
                }
            }

            setChannelId(id);
            setIsValidUrl(true);
            return;
        }

        setChannelId("");
        setIsValidUrl(false);
    }

    useEffect(() => {
        validateUrl(channelUrl);
    }, [channelUrl]);

    return (
        <Modal buttonText="Conectar Canal" title="Conectar canal do Telgram" actionText="Conectar"
               actionCallback={handleIntegrate}
               onCloseCallback={onCloseCallback} icon={<SiTelegram/>} colorScheme="blue" disableAction={!isValidUrl}>
            <Stack spacing="5">
                <Text><b>Para conectar um canal do Telegram:</b></Text>
                <Text><b>1.</b> Abra o canal que deseja integrar, clique no nome do canal na parte superior.</Text>
                <Text><b>2.</b> Em "Administradores", vá em "Adicionar Admin".</Text>
                <Text><b>3.</b> Na lupa de pesquisa, pesquise StakeMetricsBot e adicione como administrador com todas as
                    permissões, exceto "Adicionar Novos Admins".</Text>
                <Text><b>4.</b> Acesse o telegram web através do botão abaixo.</Text>
                <Button onClick={handleLinkClick}>Acessar Telegram Web</Button>
                <Text><b>5.</b> Abra no Telegram Web, o canal que deseja integrar.</Text>
                <Text><b>6.</b> Copie a URL (endereço) do seu navegador depois de abrir o chat.</Text>
                <Text><b>7.</b> Cole o URL no campo abaixo e clique em conectar.</Text>
                <Input value={channelUrl} onChange={handleChange}
                       placeholder={"https://web.telegram.org/a/#-1001111111111"}/>
            </Stack>
        </Modal>
    );
}

export default TelegramChannelConnectModal;
