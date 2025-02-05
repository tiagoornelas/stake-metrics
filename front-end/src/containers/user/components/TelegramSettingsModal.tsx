import {
  Badge,
  Box,
  CreateToastFnReturn,
  Flex,
  Heading,
  Input,
  Select,
  Stack,
  Switch,
  Tag,
  Text,
  Tooltip,
  useToast
} from "@chakra-ui/react";
import DefaultSlider from "components/DefaultSlider";
import Modal from "components/Modal";
import { useErrorToast } from "hooks/useErrorToast";
import useTranslation from 'hooks/useTranslation';
import React, { useEffect, useState } from "react";
import { IoMdSettings, SiTelegram } from "react-icons/all";
import { MdDelete } from "react-icons/md";
import { deletePrivateChat, editIntegration, testPrivateChat } from "services/telegramService";
import { SUCCESS_TYPES } from "utils/constants/successConstants";
import { defaultToastProps } from "utils/constants/toastConstants";
import { ExtraButton, TelegramChat } from "utils/interfaces";
import { useUserState } from "context/UserContext";
import { getFeatureAmount } from 'utils/helpers/featureHelper';
import { FEATURES } from 'utils/constants/featureConstants';

type Props = {
  chat: TelegramChat;
  onCloseCallback?: () => void;
};

const TelegramSettingsModal = ({ chat, onCloseCallback }: Props) => {
  const toast: CreateToastFnReturn = useToast();
  const { user } = useUserState();
  const { t } = useTranslation();
  const [isFormDirty, setIsFormDirty] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [formState, setFormState] = useState({
    name: chat.name,
    status: chat.status,
    delay: chat.delay,
    deliveryProbability: chat.deliveryProbability,
    notDeliveredMessage: chat.notDeliveredMessage,
    extraText: chat.extraText,
    receiveReports: chat.receiveReports ?? false,
    hideSoftwareLink: chat.hideSoftwareLink ?? false
  });

  const hasNoAdMessages = getFeatureAmount(user, FEATURES.NO_ADS_MESSAGES) > 0;

  useEffect(() => {
    const originalChatExtraText = chat.extraText || "";
    const isDirty =
      formState.name !== chat.name ||
      formState.status !== chat.status ||
      formState.delay !== chat.delay ||
      formState.deliveryProbability !== chat.deliveryProbability ||
      formState.notDeliveredMessage !== chat.notDeliveredMessage ||
      formState.extraText !== originalChatExtraText ||
      formState.receiveReports !== (chat.receiveReports ?? false) ||
      formState.hideSoftwareLink !== (chat.hideSoftwareLink ?? false);
    setIsFormDirty(isDirty);
  }, [formState, chat]);

  useEffect(() => {
    if (formState.deliveryProbability === 1) {
      setFormState(prevState => ({
        ...prevState,
        notDeliveredMessage: ""
      }));
    }
  }, [formState.delay, formState.deliveryProbability]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormState(prevState => ({
      ...prevState,
      [name]: value
    }));
  };

  const handleDelayChange = (value: number) => {
    setFormState(prevState => ({
      ...prevState,
      delay: value
    }));
  };

  const handleDeliveryProbabilityChange = (value: number) => {
    setFormState(prevState => ({
      ...prevState,
      deliveryProbability: value
    }));
  };

  const handleSave = useErrorToast(
    async () => {
      setIsSaving(true);
      try {
        await editIntegration(
          chat.id,
          formState.name,
          formState.status,
          formState.delay,
          formState.deliveryProbability,
          formState.notDeliveredMessage,
          formState.extraText,
          formState.receiveReports,
          formState.hideSoftwareLink
        );
      } finally {
        setIsSaving(false);
      }
    },
    SUCCESS_TYPES.TELEGRAM_CHAT_EDITED
  );

  const handleDelete = useErrorToast(async () => {
    setIsLoading(true);
    await deletePrivateChat(chat.id);
    setIsLoading(false);
  }, SUCCESS_TYPES.TELEGRAM_CHAT_DELETED);

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
      rightIcon: <MdDelete />,
      isLoading: isLoading,
      disabled: isLoading
    },
    {
      callback: handleTestMessage,
      colorScheme: "blue",
      label: "Testar",
      disabled: isFormDirty || isLoading,
      rightIcon: <SiTelegram />
    }
  ];

  return (
    <>
      <Text>{chat.name}</Text>
      <Badge colorScheme={chat.status === "ACTIVE" ? "green" : "red"}>
        {chat.status === "ACTIVE" ? "Ativo" : "Inativo"}
      </Badge>
      <Modal
        title="Configuração de Telegram"
        actionText="Salvar"
        actionCallback={handleSave}
        actionIsLoading={isSaving}
        extraButtons={extraButtons}
        onCloseCallback={onCloseCallback}
        icon={<IoMdSettings />}
      >
        <Stack as="form" spacing="5">
          <Box display="flex" flexDirection="column" gap={2}>
            <Heading size="sm">Nome do chat</Heading>
            <Input name="name" value={formState.name} onChange={handleChange} maxLength={60} disabled={isLoading} />
          </Box>
          <Box display="flex" flexDirection="column" gap={2}>
            <Heading size="sm">Status</Heading>
            <Select name="status" value={formState.status} onChange={handleChange} disabled={isLoading}>
              <option value="ACTIVE">Ativo</option>
              <option value="INACTIVE">Inativo</option>
            </Select>
          </Box>

          <Box display="flex" flexDirection="column" gap={2}>
            <Flex gap={2}>
              <Heading size="sm">Atraso</Heading>
              <Tag>{`${formState.delay} s`}</Tag>
            </Flex>
            <DefaultSlider handleChange={handleDelayChange} value={formState.delay} min={0} max={120} step={1} />
          </Box>
          <Box display="flex" flexDirection="column" gap={2}>
            <Flex gap={2}>
              <Heading size="sm">Taxa de entrega</Heading>
              <Tag>{`${Math.floor(formState.deliveryProbability * 100)} %`}</Tag>
            </Flex>
            <DefaultSlider
              handleChange={handleDeliveryProbabilityChange}
              value={formState.deliveryProbability}
              min={0}
              max={1}
              step={0.01}
              isPercentage
            />
          </Box>
          <Box display="flex" flexDirection="column" gap={2}>
            <Heading size="sm">Aviso de mensagem não entregue</Heading>
            <Text fontSize="xs" color="gray">
              Preencha para enviar uma mensagem antes da mensagem principal em caso de atraso
            </Text>
            <Input
              disabled={formState.deliveryProbability === 1 || isLoading}
              name="notDeliveredMessage"
              value={formState.notDeliveredMessage}
              onChange={handleChange}
            />
          </Box>
          <Box display="flex" flexDirection="column" gap={2}>
            <Heading size="sm">Texto extra ao final</Heading>
            <Text fontSize="xs" color="gray">
              Preencha para enviar uma mensagem ao final
            </Text>
            <Input name="extraText" value={formState.extraText} onChange={handleChange} disabled={isLoading} />
          </Box>
          <Box display="flex" flexDirection="column" gap={2}>
            <Heading size="sm">Configurações adicionais</Heading>
            <Stack>
              <Flex alignItems="center" gap={2}>
                <Switch
                  isChecked={formState.receiveReports}
                  onChange={(e) => {
                    setFormState((prevState) => ({
                      ...prevState,
                      receiveReports: e.target.checked,
                    }));
                  }}
                  disabled={isLoading}
                />
                <Text>Receber relatórios</Text>
              </Flex>
              <Tooltip
                label={t('features.upgrade_plan_required', { 
                  feature: t('features.no_ads_messages') 
                })}
                isDisabled={hasNoAdMessages}
              >
                <Flex alignItems="center" gap={2}>
                  <Switch
                    isChecked={formState.hideSoftwareLink}
                    onChange={(e) => {
                      setFormState((prevState) => ({
                        ...prevState,
                        hideSoftwareLink: e.target.checked,
                      }));
                    }}
                    disabled={isLoading || !hasNoAdMessages}
                  />
                  <Text>Remover link do software</Text>
                </Flex>
              </Tooltip>
            </Stack>
          </Box>
        </Stack>
      </Modal>
    </>
  );
};

export default TelegramSettingsModal;
