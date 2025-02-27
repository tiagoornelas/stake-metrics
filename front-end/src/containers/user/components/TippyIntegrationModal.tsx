import React, { useState } from "react";
import { Stack, Text, Input } from "@chakra-ui/react";
import { FaRobot } from "react-icons/fa";
import Modal from "components/Modal";
import { saveAutoBettor } from "services/integrationService";
import { useErrorToast } from "hooks/useErrorToast";
import { SUCCESS_TYPES } from "utils/constants/successConstants";
import { set } from "date-fns";

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
                <Text><b>Para integrar à sua conta Tippy.bet:</b></Text>
                <Text><b>1. </b>Insira a chave do canal.</Text>
                <Input
                    placeholder="Chave do canal"
                    value={integrationId}
                    onChange={(e) => setIntegrationId(e.target.value)}
                />
            </Stack>
        </Modal>
    );
};

export default TippyIntegrationModal;