import {
    Button,
    ButtonProps,
    IconButton,
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
import React, {MouseEventHandler, ReactElement, ReactNode} from "react";
import {ExtraButton} from "utils/interfaces";

interface Props extends ButtonProps {
    children: ReactNode;
    buttonText?: string;
    title: string;
    actionText?: string;
    actionCallback?: () => void;
    actionIsLoading?: boolean;
    disableAction?: boolean
    noFooter?: boolean;
    size?: string;
    tooltip?: string;
    extraButtons?: ExtraButton[];
    onCloseCallback?: () => void;
    icon?: ReactElement;
}

interface TooltipButtonProps extends ButtonProps {
    tooltip?: string;
    icon?: ReactElement;
    buttonText: string | undefined;
    onClick: MouseEventHandler<HTMLButtonElement>;
}

const TooltipButton: React.FC<TooltipButtonProps> = ({tooltip, onClick, buttonText, icon, ...props}) => {
    return tooltip ? (
        <Tooltip label={tooltip} placement="top">
            {buttonText ? (
                <Button onClick={onClick} {...props} rightIcon={icon}>
                    {buttonText}
                </Button>
            ) : (
                <IconButton aria-label="icon button" size="sm" icon={icon} onClick={onClick} {...props} />
            )}
        </Tooltip>
    ) : buttonText ? (
        <Button onClick={onClick} {...props} rightIcon={icon}>
            {buttonText}
        </Button>
    ) : (
        <IconButton aria-label="icon button" size="sm" icon={icon} onClick={onClick} {...props} />
    );
};

const Modal: React.FC<Props> = ({
                                    children,
                                    buttonText,
                                    title,
                                    actionText,
                                    actionCallback,
                                    actionIsLoading = false,
                                    disableAction = false,
                                    noFooter = false,
                                    size = "md",
                                    tooltip,
                                    extraButtons = [],
                                    onCloseCallback,
                                    icon,
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

    interface ChildWithOnClose {
        onClose?: () => void;
    }

    const childrenWithOnClose = React.Children.map(children, child => {
        if (React.isValidElement<ChildWithOnClose>(child)) {
            return React.cloneElement(child, {onClose});
        }
        return child;
    });

    return (
        <>
            <TooltipButton tooltip={tooltip} onClick={onOpen} buttonText={buttonText} icon={icon} {...props} />

            <ChakraModal isOpen={isOpen} onClose={onClose} size={size} motionPreset={"none"}>
                <ModalOverlay/>
                <ModalContent>
                    <ModalHeader>{title}</ModalHeader>
                    <ModalCloseButton/>
                    <ModalBody>
                        {childrenWithOnClose}
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
                                    isDisabled={button.disabled}
                                    mr={2}
                                    {...(button.rightIcon && {rightIcon: button.rightIcon})}
                                >
                                    {button.label}
                                </Button>
                            ))}
                            <Button
                                bgColor={colors.product}
                                color={colors.productContrast}
                                onClick={handleSubmit}
                                isDisabled={disableAction || actionIsLoading}
                                isLoading={actionIsLoading}
                                loadingText={actionText}
                            >
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
