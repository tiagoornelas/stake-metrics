import React, { useState } from "react";
import { Stack, Text, Input } from "@chakra-ui/react";
import { FaRobot } from "react-icons/fa";
import Modal from "components/Modal";
import { saveAutoBettor } from "services/integrationService";
import { useErrorToast } from "hooks/useErrorToast";
import { SUCCESS_TYPES } from "utils/constants/successConstants";

interface TippyIntegrationModalProps {
    onCloseCallback?: () => void;
}

const TippyIntegrationModal: React.FC<TippyIntegrationModalProps> = ({ onCloseCallback }) => {
    const [integrationId, setIntegrationId] = useState<string>("");
    const [isIntegrating, setIsIntegrating] = useState<boolean>(false);

    const handleIntegrate = useErrorToast(async () => {
        setIsIntegrating(true);
        await saveAutoBettor(integrationId);
        setIsIntegrating(false);
    }, SUCCESS_TYPES.AUTO_BETTOR_INTEGRATED);

    return (
        <Modal
            buttonText="Automatizar apostas com Tippy"
            title="Automatizar apostas com Tippy"
            actionText="Integrar"
            actionCallback={handleIntegrate}
            onCloseCallback={onCloseCallback}
            actionIsLoading={isIntegrating}
            icon={<FaRobot />}
            colorScheme="blue"
        >
            <Stack spacing="5">
                <Text>
                    Tippy é uma plataforma de automação de apostas que sincroniza com suas estratégias do Stake Metrics
                    e realiza apostas automaticamente. Para conhecer mais,{" "}
                    <Text as="a" color="blue.500" href="https://tippy.bet" target="_blank" textDecoration="underline">
                        visite o site da plataforma parceira
                    </Text>
                    .
                </Text>
                <Text><b>Para integrar a sua conta:</b></Text>
                <Text><b>1. </b>Na sua conta Tippy, acesse o menu principal.</Text>
                <Text><b>2. </b>Selecione a aba apostas e crie um novo canal com o nome que desejar.</Text>
                <Text><b>2. </b>Após criado, selecione o canal e depois clique no nome do canal (Menu de opções) no canto superior esquerdo.</Text>
                <Text><b>2. </b>Clique em "Integrações".</Text>
                <Text><b>2. </b>Clique em "Gerar" para gerar o token.</Text>
                <Text><b>1. </b>No Stake Metrics, insira o token no campo abaixo e clique Integrar.</Text>
                <Input
                    placeholder="Token do canal"
                    value={integrationId}
                    onChange={(e) => setIntegrationId(e.target.value)}
                />
            </Stack>
        </Modal>
    );
};

export default TippyIntegrationModal;