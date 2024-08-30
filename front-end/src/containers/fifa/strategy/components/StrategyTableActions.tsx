import {
    AlertDialog,
    AlertDialogBody,
    AlertDialogContent,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogOverlay,
    Button,
    ButtonGroup,
    IconButton,
    Menu,
    MenuButton,
    MenuItem,
    MenuList,
    Modal as ChakraModal,
    ModalBody,
    ModalCloseButton,
    ModalContent,
    ModalHeader,
    ModalOverlay,
    Text,
    Tooltip,
    useDisclosure
} from "@chakra-ui/react";
import DeleteConfirmationDialog from "components/DeleteConfirmationDialog";
import StrategyForm from "containers/fifa/strategy/components/StrategyForm";
import useInvalidateStrategyQuery from "containers/fifa/strategy/hooks/useInvalidateStrategyQuery";
import {useErrorToast} from "hooks/useErrorToast";
import React from 'react';
import {HiOutlineSparkles, IoMdPower, MdEdit} from "react-icons/all";
import {changeStrategyStatus, deleteStrategy, restartStrategy} from "services/strategyService";
import {strategyStatusDict} from "utils/constants/strategyConstants";
import {SUCCESS_TYPES} from "utils/constants/successConstants";
import {StrategyListItem, StrategyStatus} from "utils/interfaces";

const StatusMenu = ({strategy}: { strategy: StrategyListItem }) => {
    const invalidateStrategyQuery = useInvalidateStrategyQuery();

    const statuses: StrategyStatus[] = Object.keys(strategyStatusDict).filter(status => status !== strategy.status) as StrategyStatus[];
    const handleStatusChange = useErrorToast(async (status: StrategyStatus) => {
        await changeStrategyStatus(strategy.id, status);
        invalidateStrategyQuery();
    }, SUCCESS_TYPES.STRATEGY_STATUS_CHANGED);

    return (
        <Menu>
            <Tooltip label={"Alterar status"} placement={"top"}>
                <MenuButton as={IconButton} icon={<IoMdPower/>} variant='outline'/>
            </Tooltip>
            <MenuList>
                {statuses.map((status: StrategyStatus) => (
                    <MenuItem key={status}
                              onClick={() => handleStatusChange(status)}>{strategyStatusDict[status].actionText}</MenuItem>
                ))}
            </MenuList>
        </Menu>
    );
};

const EditButton = ({strategyId}: { strategyId: string }) => {
    const {isOpen, onOpen, onClose} = useDisclosure();

    return (
        <>
            <Tooltip label={"Editar"} placement={"top"}>
                <IconButton aria-label="Editar" onClick={onOpen} icon={<MdEdit/>} variant='outline'/>
            </Tooltip>

            <ChakraModal isOpen={isOpen} onClose={onClose} motionPreset={"none"}>
                <ModalOverlay/>
                <ModalContent>
                    <ModalHeader>Editar estratégia</ModalHeader>
                    <ModalCloseButton/>
                    <ModalBody>
                        <StrategyForm strategyId={strategyId} onClose={onClose}/>
                    </ModalBody>
                </ModalContent>
            </ChakraModal>
        </>
    )
}

const RestartButton = ({strategy}: { strategy: StrategyListItem }) => {
    const invalidateStrategyQuery = useInvalidateStrategyQuery();
    const {isOpen, onOpen, onClose} = useDisclosure()
    const cancelRef = React.useRef<HTMLButtonElement>(null)

    const handleConfirm = useErrorToast(async () => {
        await restartStrategy(strategy.id);
        invalidateStrategyQuery();
        onClose();
    }, SUCCESS_TYPES.STRATEGY_RESTARTED);

    return (
        <>
            <Tooltip label={"Reiniciar"} placement={"top"}>
                <IconButton icon={<HiOutlineSparkles/>} aria-label="Reiniciar" variant={"outline"} onClick={onOpen}/>
            </Tooltip>

            <AlertDialog isOpen={isOpen} leastDestructiveRef={cancelRef} onClose={onClose}>
                <AlertDialogOverlay>
                    <AlertDialogContent>
                        <AlertDialogHeader fontSize='lg' fontWeight='bold'>
                            {`Reiniciar estratégia`}
                        </AlertDialogHeader>

                        <AlertDialogBody>
                            <Text>Tem certeza? Essa ação não pode ser desfeita.</Text>
                            <Text>Ao reiniciar a estratégia, você apagará todas as apostas e as mensagens enviadas
                                continuarão como estão.</Text>
                        </AlertDialogBody>

                        <AlertDialogFooter>
                            <Button ref={cancelRef} onClick={onClose}>
                                Cancelar
                            </Button>
                            <Button colorScheme="yellow" onClick={handleConfirm} ml={3}>
                                Reiniciar
                            </Button>
                        </AlertDialogFooter>
                    </AlertDialogContent>
                </AlertDialogOverlay>
            </AlertDialog>
        </>
    );
}

const DeleteButton = ({strategy}: { strategy: StrategyListItem }) => {
    const invalidateStrategyQuery = useInvalidateStrategyQuery();

    const handleDelete = useErrorToast(async () => {
        await deleteStrategy(strategy.id);
        invalidateStrategyQuery();
    }, SUCCESS_TYPES.STRATEGY_DELETED);


    return <DeleteConfirmationDialog
        entityName={"estratégia"}
        tooltip
        confirmCallback={handleDelete}
        variant="outline"
        text={"Ao excluir a estratégia, todas as apostas serão excluídas e as mensagens permanecerão como estão, sem serem editadas com os resultados."}
    />;
}

const StrategyTableActions = ({strategy}: { strategy: StrategyListItem }) => {
    return (
        <ButtonGroup isAttached>
            <StatusMenu strategy={strategy}/>
            <EditButton strategyId={strategy.id}/>
            <RestartButton strategy={strategy}/>
            <DeleteButton strategy={strategy}/>
        </ButtonGroup>
    )
};

export default StrategyTableActions;
