import {
    Box,
    Heading,
    Input,
    Select,
    Slider,
    SliderFilledTrack,
    SliderThumb,
    SliderTrack,
    Stack
} from "@chakra-ui/react";
import DefaultSlider from "components/DefaultSlider";
import Modal from "components/Modal";
import {useUserState} from "context/UserContext";
import {useErrorToast} from "hooks/useErrorToast";
import React, {useState} from 'react';
import {SiTelegram} from "react-icons/all";
import {MdDelete} from "react-icons/md";
import {
    beginIntegration,
    deletePrivateChat,
    editIntegration,
    integratePrivateChat,
    testPrivateChat
} from "services/telegramService";
import {SUCCESS_TYPES} from "utils/constants/successConstants";
import {ExtraButton, TelegramChat, UserContext} from "utils/interfaces";

type Props = {
    chat: TelegramChat;
    onCloseCallback?: () => void;
};

const TelegramChatSettingsModal = ({chat, onCloseCallback}: Props) => {
    const [formState, setFormState] = useState({
        name: chat.name,
        status: chat.status,
        delay: chat.delay,
        deliveryProbability: chat.deliveryProbability,
        extraText: chat.extraText || ""
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const {name, value} = e.target;
        setFormState((prevState) => ({
            ...prevState,
            [name]: value,
        }));
    };

    const handleDelayChange = (value: number) => {
        setFormState((prevState) => ({
            ...prevState,
            delay: value,
        }));
    };

    const handleDeliveryProbabilityChange = (value: number) => {
        setFormState((prevState) => ({
            ...prevState,
            deliveryProbability: value,
        }));
    };

    const handleSave = useErrorToast(async () => editIntegration(chat.id, formState.name, formState.status, formState.delay, formState.deliveryProbability), SUCCESS_TYPES.TELEGRAM_CHAT_EDITED);

    const handleDelete = useErrorToast(async () => deletePrivateChat(chat.id), SUCCESS_TYPES.TELEGRAM_CODE_GENERATED);
    const handleTestMessage = useErrorToast(async () => testPrivateChat(chat.id), SUCCESS_TYPES.TELEGRAM_CHAT_TESTED);

    const extraButtons: ExtraButton[] = [
        {
            callback: handleDelete,
            colorScheme: "red",
            label: "Excluir",
            closeOnAction: true,
            rightIcon: <MdDelete/>
        },
        {
            callback: handleTestMessage,
            colorScheme: "blue",
            label: "Testar",
            rightIcon: <SiTelegram/>
        }
    ];

    return (
        <Modal buttonText={chat.name} title="Configuração de Telegram" actionText="Salvar"
               actionCallback={handleSave}
               colorScheme="blue"
               tooltip="Gerenciar Telegram"
               extraButtons={extraButtons}
               onCloseCallback={onCloseCallback}
               rightIcon={<SiTelegram/>}>
            <Stack as="form" spacing="5">
                <Box display="flex" flexDirection="column" gap={2}>
                    <Heading size="sm">Nome do chat</Heading>
                    <Input name="name" value={formState.name} onChange={handleChange}/>
                </Box>
                <Box display="flex" flexDirection="column" gap={2}>
                    <Heading size="sm">Status</Heading>
                    <Select name="status" value={formState.status} onChange={handleChange}>
                        <option value="ACTIVE">Ativo</option>
                        <option value="INACTIVE">Inativo</option>
                    </Select>
                </Box>
                <Box display="flex" flexDirection="column" gap={2}>
                    <Heading size="sm">Atraso (em segundos)</Heading>
                    <DefaultSlider handleChange={handleDelayChange} value={formState.delay} min={0} max={120} step={1}
                                   sufix=" s"/>
                </Box>
                <Box display="flex" flexDirection="column" gap={2}>
                    <Heading size="sm">Taxa de entrega (em %)</Heading>
                    <DefaultSlider handleChange={handleDeliveryProbabilityChange} value={formState.deliveryProbability}
                                   min={0} max={1} step={0.01} isPercentage sufix="%"/>
                </Box>
                <Box display="flex" flexDirection="column" gap={2}>
                    <Heading size="sm">Texto extra</Heading>
                    <Input name="extraText" value={formState.extraText} onChange={handleChange}/>
                </Box>
            </Stack>
        </Modal>
    );
};

export default TelegramChatSettingsModal;