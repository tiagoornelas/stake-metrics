import React, { useState } from "react";
import { Stack, Text, Input } from "@chakra-ui/react";
import { FaRobot } from "react-icons/fa";
import Modal from "components/Modal";
import { saveAutoBettor } from "services/integrationService";
import { useErrorToast } from "hooks/useErrorToast";
import { SUCCESS_TYPES } from "utils/constants/successConstants";

interface AutoBettorIntegrationModalProps {
    onCloseCallback?: () => void;
}

const AutoBettorIntegrationModal: React.FC<AutoBettorIntegrationModalProps> = ({ onCloseCallback }) => {
    const [integrationId, setIntegrationId] = useState<string>("");
    const [isIntegrating, setIsIntegrating] = useState<boolean>(false);

    const handleIntegrate = useErrorToast(async () => {
        setIsIntegrating(true);
        await saveAutoBettor(integrationId);
        setIsIntegrating(false);
    }, SUCCESS_TYPES.AUTO_BETTOR_INTEGRATED);

    return (
        <Modal
            buttonText="Conectar automação de apostas"
            title="Conectar automação de apostas"
            actionText="Integrar"
            actionCallback={handleIntegrate}
            onCloseCallback={onCloseCallback}
            actionIsLoading={isIntegrating}
            icon={<FaRobot />}
            colorScheme="blue"
        >
            <Stack spacing="5">
                <Text>
                    Conecte sua plataforma ou bot de automação via webhook para sincronizar com suas estratégias
                    do Stake Metrics e executar entradas automaticamente.
                </Text>
                <Text><b>Como integrar seu canal:</b></Text>
                <Text><b>1. </b>No seu serviço parceiro ou webhook receiver, gere um token ou identificador de canal.</Text>
                <Text><b>2. </b>Insira o token ou identificador no campo abaixo.</Text>
                <Text><b>3. </b>Clique em <b>Integrar</b> para validar a conexão com o Stake Metrics.</Text>
                <Input
                    placeholder="Token ou ID de Integração"
                    value={integrationId}
                    onChange={(e) => setIntegrationId(e.target.value)}
                />
            </Stack>
        </Modal>
    );
};

export default AutoBettorIntegrationModal;
