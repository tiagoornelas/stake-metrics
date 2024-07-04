import {
    Button,
    ButtonProps,
    Modal as ChakraModal,
    ModalBody,
    ModalCloseButton,
    ModalContent,
    ModalFooter,
    ModalHeader,
    ModalOverlay,
    Tooltip,
    useDisclosure
} from "@chakra-ui/react";
import useThemeColors from "hooks/useThemeColors";
import React, {MouseEventHandler, ReactNode} from "react";
import {ExtraButton} from "utils/interfaces";

interface Props extends ButtonProps {
    children: ReactNode;
    buttonText: string;
    title: string;
    actionText?: string;
    actionCallback?: () => void;
    noFooter?: boolean;
    size?: string;
    tooltip?: string;
    extraButtons?: ExtraButton[];
    onCloseCallback?: () => void;
}

interface TooltipButtonProps extends ButtonProps {
    tooltip?: string;
    buttonText: string;
    onClick: MouseEventHandler<HTMLButtonElement>;
}

const TooltipButton: React.FC<TooltipButtonProps> = ({tooltip, onClick, buttonText, ...props}) => {
    return tooltip ? (
        <Tooltip label={tooltip} placement="top">
            <Button onClick={onClick} {...props}>
                {buttonText}
            </Button>
        </Tooltip>
    ) : (
        <Button onClick={onClick} {...props}>
            {buttonText}
        </Button>
    );
};

const Modal: React.FC<Props> = ({
                                    children,
                                    buttonText,
                                    title,
                                    actionText,
                                    actionCallback,
                                    noFooter = false,
                                    size = "md",
                                    tooltip,
                                    extraButtons = [],
                                    onCloseCallback,
                                    ...props
                                }) => {
    const {isOpen, onOpen, onClose} = useDisclosure();
    const colors = useThemeColors();

    const handleSubmit = async () => {
        if (actionCallback) await actionCallback();
        if (onCloseCallback) await onCloseCallback();
        onClose();
    };

    const handleExtraButtonClick = async (button: ExtraButton) => {
        await button.callback();
        if (button.closeOnAction) {
            if (onCloseCallback) await onCloseCallback();
            onClose();
        }
    };

    return (
        <>
            <TooltipButton tooltip={tooltip} onClick={onOpen} buttonText={buttonText} {...props} />

            <ChakraModal isOpen={isOpen} onClose={onClose} size={size}>
                <ModalOverlay/>
                <ModalContent>
                    <ModalHeader>{title}</ModalHeader>
                    <ModalCloseButton/>
                    <ModalBody>
                        {children}
                    </ModalBody>

                    {!noFooter && (
                        <ModalFooter>
                            <Button mr={3} onClick={onClose}>
                                Voltar
                            </Button>
                            {extraButtons.map((button, idx) => (
                                <Button
                                    key={idx}
                                    colorScheme={button.colorScheme}
                                    onClick={() => handleExtraButtonClick(button)}
                                    mr={2}
                                    {...(button.rightIcon && {rightIcon: button.rightIcon})}
                                >
                                    {button.label}
                                </Button>
                            ))}
                            <Button bgColor={colors.product} color={colors.productContrast} onClick={handleSubmit}>
                                {actionText}
                            </Button>
                        </ModalFooter>
                    )}
                </ModalContent>
            </ChakraModal>
        </>
    );
};

export default Modal;