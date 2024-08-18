import {
    Badge,
    Box,
    CreateToastFnReturn,
    Flex,
    Heading,
    Input,
    Select,
    Stack,
    Tag,
    Text,
    useToast
} from "@chakra-ui/react";
import DefaultSlider from "components/DefaultSlider";
import Modal from "components/Modal";
import {useErrorToast} from "hooks/useErrorToast";
import React, {useEffect, useState} from 'react';
import {IoMdSettings, SiTelegram} from "react-icons/all";
import {MdDelete} from "react-icons/md";
import {deletePrivateChat, editIntegration, testPrivateChat} from "services/telegramService";
import {SUCCESS_TYPES} from "utils/constants/successConstants";
import {defaultToastProps} from "utils/constants/toastConstants";
import {ExtraButton, TelegramChat} from "utils/interfaces";

type Props = {
    chat: TelegramChat;
    onCloseCallback?: () => void;
};

const TelegramSettingsModal = ({chat, onCloseCallback}: Props) => {
    const toast: CreateToastFnReturn = useToast();
    const [isFormDirty, setIsFormDirty] = useState(false);
    const [formState, setFormState] = useState({
        name: chat.name,
        status: chat.status,
        delay: chat.delay,
        deliveryProbability: chat.deliveryProbability,
        notDeliveredMessage: chat.notDeliveredMessage,
        extraText: chat.extraText
    });

    useEffect(() => {
        const originalChatExtraText = chat.extraText || "";
        const isDirty = formState.name !== chat.name ||
            formState.status !== chat.status ||
            formState.delay !== chat.delay ||
            formState.deliveryProbability !== chat.deliveryProbability ||
            formState.notDeliveredMessage !== chat.notDeliveredMessage ||
            formState.extraText !== originalChatExtraText;
        setIsFormDirty(isDirty);
    }, [formState, chat]);

    useEffect(() => {
        if (formState.deliveryProbability === 1) {
            setFormState((prevState) => ({
                ...prevState,
                notDeliveredMessage: ""
            }));
        }
    }, [formState.delay, formState.deliveryProbability]);

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

    const handleSave = useErrorToast(async () => editIntegration(
        chat.id,
        formState.name,
        formState.status,
        formState.delay,
        formState.deliveryProbability,
        formState.notDeliveredMessage,
        formState.extraText
    ), SUCCESS_TYPES.TELEGRAM_CHAT_EDITED);

    const handleDelete = useErrorToast(async () => deletePrivateChat(chat.id), SUCCESS_TYPES.TELEGRAM_CODE_GENERATED);
    const handleTestMessage = useErrorToast(async () => {
        toast({
            status: "info",
            ...SUCCESS_TYPES.TELEGRAM_CHAT_TESTED,
            ...defaultToastProps
        });

        testPrivateChat(chat.id);
    });

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
            disabled: isFormDirty,
            rightIcon: <SiTelegram/>
        }
    ];

    return (
        <>
            <Text>{chat.name}</Text>
            <Badge
                colorScheme={chat.status === "ACTIVE" ? "green" : "red"}>{chat.status === "ACTIVE" ? "Ativo" : "Inativo"}</Badge>
            <Modal title="Configuração de Telegram" actionText="Salvar"
                   actionCallback={handleSave}
                   extraButtons={extraButtons}
                   onCloseCallback={onCloseCallback}
                   icon={<IoMdSettings/>}>
                <Stack as="form" spacing="5">
                    <Box display="flex" flexDirection="column" gap={2}>
                        <Heading size="sm">Nome do chat</Heading>
                        <Input name="name" value={formState.name} onChange={handleChange} maxLength={60}/>
                    </Box>
                    <Box display="flex" flexDirection="column" gap={2}>
                        <Heading size="sm">Status</Heading>
                        <Select name="status" value={formState.status} onChange={handleChange}>
                            <option value="ACTIVE">Ativo</option>
                            <option value="INACTIVE">Inativo</option>
                        </Select>
                    </Box>
                    <Box display="flex" flexDirection="column" gap={2}>
                        <Flex gap={2}>
                            <Heading size="sm">Atraso</Heading>
                            <Tag>{`${formState.delay} s`}</Tag>
                        </Flex>
                        <DefaultSlider handleChange={handleDelayChange} value={formState.delay} min={0} max={120}
                                       step={1}/>
                    </Box>
                    <Box display="flex" flexDirection="column" gap={2}>
                        <Flex gap={2}>
                            <Heading size="sm">Taxa de entrega</Heading>
                            <Tag>{`${Math.floor(formState.deliveryProbability * 100)} %`}</Tag>
                        </Flex>
                        <DefaultSlider handleChange={handleDeliveryProbabilityChange}
                                       value={formState.deliveryProbability}
                                       min={0} max={1} step={0.01} isPercentage/>
                    </Box>
                    <Box display="flex" flexDirection="column" gap={2}>
                        <Heading size="sm">Aviso de
                            mensagem não entregue</Heading>
                        <Text fontSize="xs" color="gray">Preencha para enviar uma mensagem antes da mensagem principal
                            em caso de atraso</Text>
                        <Input disabled={formState.deliveryProbability === 1} name="notDeliveredMessage"
                               value={formState.notDeliveredMessage}
                               onChange={handleChange}/>
                    </Box>
                    <Box display="flex" flexDirection="column" gap={2}>
                        <Heading size="sm">Texto extra ao
                            final</Heading>
                        <Text fontSize="xs" color="gray">Preencha para enviar uma mensagem antes da mensagem principal
                            em caso de atraso</Text>
                        <Input name="extraText" value={formState.extraText} onChange={handleChange}/>
                    </Box>
                </Stack>
            </Modal>
        </>
    );
};

export default TelegramSettingsModal;
