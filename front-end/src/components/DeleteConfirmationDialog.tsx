import {
    AlertDialog,
    AlertDialogBody,
    AlertDialogContent,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogOverlay,
    Button,
    IconButton,
    Text,
    Tooltip,
    useDisclosure
} from "@chakra-ui/react";
import React from 'react';
import {MdDelete} from "react-icons/all";

type Props = {
    tooltip?: boolean | undefined
    confirmCallback: () => void;
    entityName: string;
    text?: string | undefined;
    [key: string]: any;
    isLoading?: boolean;
}

const DeleteConfirmationDialog = ({tooltip = false, confirmCallback, entityName, text, isLoading, ...props}: Props) => {
        const {isOpen, onOpen, onClose} = useDisclosure()
        const cancelRef = React.useRef<HTMLButtonElement>(null)

        const handleOpen = (e: React.MouseEvent) => {
            e.stopPropagation();
            onOpen();
        }

        const handleConfirm = () => {
            confirmCallback();
            onClose();
        }

        return (
            <>
                {tooltip ? (<Tooltip label="Excluir" placement="top">
                    <IconButton icon={<MdDelete/>} aria-label="Excluir" onClick={handleOpen} {...props}/>
                </Tooltip>) : <IconButton icon={<MdDelete/>} aria-label="Excluir" onClick={handleOpen} {...props}/>}

                <AlertDialog isOpen={isOpen} leastDestructiveRef={cancelRef} onClose={onClose}>
                    <AlertDialogOverlay>
                        <AlertDialogContent>
                            <AlertDialogHeader fontSize='lg' fontWeight='bold'>
                                {`Apagar ${entityName}`}
                            </AlertDialogHeader>

                            <AlertDialogBody>
                                <Text>Tem certeza? Essa ação não pode ser desfeita.</Text>
                                {text && <Text>{text}</Text>}
                            </AlertDialogBody>

                            <AlertDialogFooter>
                                <Button ref={cancelRef} onClick={onClose} isDisabled={isLoading}>
                                    Cancelar
                                </Button>
                                <Button colorScheme='red' onClick={handleConfirm} ml={3} isLoading={isLoading}>
                                    Excluir
                                </Button>
                            </AlertDialogFooter>
                        </AlertDialogContent>
                    </AlertDialogOverlay>
                </AlertDialog>
            </>
        )
    }
;

export default DeleteConfirmationDialog;
