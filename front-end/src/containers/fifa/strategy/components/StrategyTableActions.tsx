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
import React, {useState} from "react";
import {HiOutlineSparkles, IoMdPower, MdEdit} from "react-icons/all";
import {changeStrategyStatus, deleteStrategy, restartStrategy} from "services/strategyService";
import {strategyStatusDict} from "utils/constants/strategyConstants";
import {SUCCESS_TYPES} from "utils/constants/successConstants";
import {StrategyListItem, StrategyStatus} from "utils/interfaces";

const StatusMenu = ({strategy}: { strategy: StrategyListItem }) => {
    const invalidateStrategyQuery = useInvalidateStrategyQuery();

    const statuses: StrategyStatus[] = Object.keys(strategyStatusDict).filter(
        status => status !== strategy.status
    ) as StrategyStatus[];
    const handleStatusChange = useErrorToast(async (status: StrategyStatus, e: React.MouseEvent) => {
        e.stopPropagation();
        await changeStrategyStatus(strategy.id, status);
        invalidateStrategyQuery();
    }, SUCCESS_TYPES.STRATEGY_STATUS_CHANGED);

    return (
        <Menu>
            <Tooltip label={"Alterar status"} placement={"top"}>
                <MenuButton as={IconButton} icon={<IoMdPower/>} variant="outline"
                            onClick={(e: React.MouseEvent) => e.stopPropagation()}/>
            </Tooltip>
            <MenuList>
                {statuses.map((status: StrategyStatus) => (
                    <MenuItem key={status} onClick={(e: React.MouseEvent) => handleStatusChange(status, e)}>
                        {strategyStatusDict[status].actionText}
                    </MenuItem>
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
                <IconButton aria-label="Editar" onClick={(e: React.MouseEvent) => {
                    e.stopPropagation();
                    onOpen();
                }} icon={<MdEdit/>} variant="outline"/>
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
    );
};

const RestartButton = ({strategy}: { strategy: StrategyListItem }) => {
    const invalidateStrategyQuery = useInvalidateStrategyQuery();
    const {isOpen, onOpen, onClose} = useDisclosure();
    const cancelRef = React.useRef<HTMLButtonElement>(null);
    const [isLoading, setIsLoading] = useState(false);

    const handleConfirm = useErrorToast(async () => {
        setIsLoading(true);
        await restartStrategy(strategy.id);
        invalidateStrategyQuery();
        setIsLoading(false);
        onClose();
    }, SUCCESS_TYPES.STRATEGY_RESTARTED);

    return (
        <>
            <Tooltip label={"Reiniciar"} placement={"top"}>
                <IconButton icon={<HiOutlineSparkles/>} aria-label="Reiniciar" variant={"outline"}
                            onClick={(e: React.MouseEvent) => {
                                e.stopPropagation();
                                onOpen();
                            }}/>
            </Tooltip>

            <AlertDialog isOpen={isOpen} leastDestructiveRef={cancelRef} onClose={onClose}>
                <AlertDialogOverlay>
                    <AlertDialogContent>
                        <AlertDialogHeader fontSize="lg" fontWeight="bold">
                            {`Reiniciar estratégia`}
                        </AlertDialogHeader>

                        <AlertDialogBody>
                            <Text>Tem certeza? Essa ação não pode ser desfeita.</Text>
                            <Text>
                                Ao reiniciar a estratégia, você apagará todas as apostas e as mensagens enviadas
                                continuarão como estão.
                            </Text>
                            <Text>Esta ação pode levar algum tempo para ser realizada.</Text>
                        </AlertDialogBody>

                        <AlertDialogFooter>
                            <Button ref={cancelRef} onClick={onClose} isDisabled={isLoading}>
                                Cancelar
                            </Button>
                            <Button colorScheme="yellow" onClick={handleConfirm} ml={3} isLoading={isLoading}>
                                Reiniciar
                            </Button>
                        </AlertDialogFooter>
                    </AlertDialogContent>
                </AlertDialogOverlay>
            </AlertDialog>
        </>
    );
};

const DeleteButton = ({strategy}: { strategy: StrategyListItem }) => {
    const invalidateStrategyQuery = useInvalidateStrategyQuery();
    const [isLoading, setIsLoading] = useState(false);

    const handleDelete = useErrorToast(async () => {
        setIsLoading(true);
        await deleteStrategy(strategy.id);
        invalidateStrategyQuery();
        setIsLoading(false);
    }, SUCCESS_TYPES.STRATEGY_DELETED);

    return (
        <DeleteConfirmationDialog
            entityName={"estratégia"}
            tooltip
            confirmCallback={handleDelete}
            variant="outline"
            isLoading={isLoading}
            text={
                "Ao excluir a estratégia, todas as apostas serão excluídas e as mensagens permanecerão como estão, sem serem editadas com os resultados."
            }
            onClick={(e: React.MouseEvent) => e.stopPropagation()}
        />
    );
};

const StrategyTableActions = ({strategy}: { strategy: StrategyListItem }) => {
    return (
        <ButtonGroup isAttached>
            <StatusMenu strategy={strategy}/>
            <EditButton strategyId={strategy.id}/>
            <RestartButton strategy={strategy}/>
            <DeleteButton strategy={strategy}/>
        </ButtonGroup>
    );
};

export default StrategyTableActions;
