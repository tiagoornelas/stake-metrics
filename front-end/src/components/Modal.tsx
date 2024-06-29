import {
    Button,
    Modal as ChakraModal,
    ModalBody,
    ModalCloseButton,
    ModalContent, ModalFooter,
    ModalHeader,
    ModalOverlay,
    useDisclosure
} from "@chakra-ui/react";
import {ReactNode} from "react";
import useThemeColors from "hooks/useThemeColors";

interface Props {
    children: ReactNode;
    buttonText: string;
    title: string;
    actionText?: string;
    actionCallback?: () => void;
    noFooter?: boolean;
    size?: string;
}

const Modal = ({children, buttonText, title, actionText, actionCallback, noFooter = false, size = "md"}: Props) => {
    const {isOpen, onOpen, onClose} = useDisclosure();
    const colors = useThemeColors();

    const handleSubmit = () => {
        if (!!actionCallback) actionCallback();
        onClose();
    }

    return (
        <>
            <Button onClick={onOpen}>{buttonText}</Button>

            <ChakraModal isOpen={isOpen} onClose={onClose} size={size}>
                <ModalOverlay/>
                <ModalContent>
                    <ModalHeader>{title}</ModalHeader>
                    <ModalCloseButton/>
                    <ModalBody>
                        {children}
                    </ModalBody>

                    {!noFooter && <ModalFooter>
                        <Button mr={3} onClick={onClose}>
                            Voltar
                        </Button>
                        <Button bgColor={colors.product} color={colors.productContrast}
                                onClick={handleSubmit}>{actionText}</Button>
                    </ModalFooter>}
                </ModalContent>
            </ChakraModal>
        </>
    )
};

export default Modal;
