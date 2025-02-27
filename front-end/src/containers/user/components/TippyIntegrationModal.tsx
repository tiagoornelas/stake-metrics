import React from "react";
import { Stack, Text, Button, Input } from "@chakra-ui/react";
import { FaRobot } from "react-icons/fa";
import Modal from "components/Modal";
import { getFeatureAmount } from "utils/helpers/featureHelper";
import { useUserState } from "context/UserContext";
import { FEATURES } from "utils/constants/featureConstants";

interface TippyIntegrationModalProps {
    onCloseCallback?: () => void;
    handleIntegrate: () => void;
}

const TippyIntegrationModal: React.FC<TippyIntegrationModalProps> = ({ onCloseCallback, handleIntegrate }) => {
    const userState = useUserState();
    const featureAmount = userState ? getFeatureAmount(userState.user, FEATURES.EARLY_ACCESS) : 0;
    const hasFeature = featureAmount > 0;

    return hasFeature && (
        <Modal
            buttonText="Automatizar apostas com Tippy"
            title="Automatizar apostas com Tippy"
            actionText="Integrar"
            actionCallback={handleIntegrate}
            onCloseCallback={onCloseCallback}
            icon={<FaRobot />}
            colorScheme="blue"
        >
            <Stack spacing="5">
                <Text><b>Para integrar à sua conta Tippy.bet, siga os passos a seguir</b></Text>
                <Text><b>1. </b>Insira sua chave</Text>
                <Input placeholder="Chave Tippy.bet" />
                <Text><b>2. </b>Lorem ipsum...</Text>
            </Stack>
        </Modal>
    );
};

export default TippyIntegrationModal;