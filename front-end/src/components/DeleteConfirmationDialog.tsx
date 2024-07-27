import {
    AlertDialog,
    AlertDialogBody,
    AlertDialogContent,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogOverlay,
    Button,
    IconButton,
    useDisclosure
} from "@chakra-ui/react";
import React from 'react';
import {MdDelete} from "react-icons/all";

type Props = {
    confirmCallback: () => void;
    entityName: string;
}

const DeleteConfirmationDialog = ({confirmCallback, entityName}: Props) => {
    const {isOpen, onOpen, onClose} = useDisclosure()
    const cancelRef = React.useRef<HTMLButtonElement>(null)

    const handleConfirm = () => {
        confirmCallback();
        onClose();
    }

    return (
        <>
            <IconButton icon={<MdDelete/>} aria-label="Excluir" onClick={onOpen}/>

            <AlertDialog isOpen={isOpen} leastDestructiveRef={cancelRef} onClose={onClose}>
                <AlertDialogOverlay>
                    <AlertDialogContent>
                        <AlertDialogHeader fontSize='lg' fontWeight='bold'>
                            {`Apagar ${entityName}`}
                        </AlertDialogHeader>

                        <AlertDialogBody>
                            Tem certeza? Essa ação não pode ser desfeita.
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
