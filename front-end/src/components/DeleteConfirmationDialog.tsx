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
    useDisclosure
} from "@chakra-ui/react";
import React, {Fragment} from 'react';
import {MdDelete} from "react-icons/all";

type Props = {
    confirmCallback: () => void;
    entityName: string;
    text?: string | undefined;
    [key: string]: any;
}

const DeleteConfirmationDialog = ({confirmCallback, entityName, text, ...props}: Props) => {
    const {isOpen, onOpen, onClose} = useDisclosure()
    const cancelRef = React.useRef<HTMLButtonElement>(null)

    const handleConfirm = () => {
        confirmCallback();
        onClose();
    }

    return (
        <>
            <IconButton icon={<MdDelete/>} aria-label="Excluir" onClick={onOpen} {...props}/>

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
                            <Button ref={cancelRef} onClick={onClose}>
                                Cancelar
                            </Button>
                            <Button colorScheme='red' onClick={handleConfirm} ml={3}>
                                Excluir
                            </Button>
                        </AlertDialogFooter>
                    </AlertDialogContent>
                </AlertDialogOverlay>
            </AlertDialog>
        </>
    )
};

export default DeleteConfirmationDialog;
