import {
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
    Tooltip,
    useDisclosure
} from "@chakra-ui/react";
import DeleteConfirmationDialog from "components/DeleteConfirmationDialog";
import StrategyForm from "containers/strategy/components/StrategyForm";
import {useErrorToast} from "hooks/useErrorToast";
import React from 'react';
import {BiSpreadsheet, IoMdPower, MdEdit} from "react-icons/all";
import {changeStrategyStatus, deleteStrategy} from "services/strategyService";
import {strategyStatusDict} from "utils/constants/strategyConstants";
import {SUCCESS_TYPES} from "utils/constants/successConstants";
import {StrategyListItem, StrategyStatus} from "utils/interfaces";

const StatusMenu = ({strategy}: { strategy: StrategyListItem }) => {
    const statuses: StrategyStatus[] = Object.keys(strategyStatusDict).filter(status => status !== strategy.status) as StrategyStatus[];
    const handleStatusChange = useErrorToast(async (status: StrategyStatus) => {
        await changeStrategyStatus(strategy.id, status);
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

const ReportButton = () => {
    return <IconButton aria-label="Baixar relatório" icon={<BiSpreadsheet/>} variant='outline'/>
}

const EditButton = ({strategyId}: { strategyId: string }) => {
    const {isOpen, onOpen, onClose} = useDisclosure();

    return (
        <>
            <IconButton aria-label="Editar" onClick={onOpen} icon={<MdEdit/>} variant='outline'/>

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

const DeleteButton = ({strategy}: { strategy: StrategyListItem }) => {
    const handleDelete = useErrorToast(async () => {
        await deleteStrategy(strategy.id);
    }, SUCCESS_TYPES.STRATEGY_DELETED);

    return <DeleteConfirmationDialog entityName={"estratégia"} confirmCallback={handleDelete} variant='outline'/>
}

const StrategyTableActions = ({strategy}: { strategy: StrategyListItem }) => {
    return (
        <ButtonGroup isAttached>
            <StatusMenu strategy={strategy}/>
            <Tooltip label={"Baixar relatório"} placement={"top"}>
                <ReportButton/>
            </Tooltip>
            <Tooltip label={"Editar"} placement={"top"}>
                <EditButton strategyId={strategy.id}/>
            </Tooltip>
            <Tooltip label={"Excluir"} placement={"top"}>
                <DeleteButton strategy={strategy}/>
            </Tooltip>
        </ButtonGroup>
    )
};

export default StrategyTableActions;
