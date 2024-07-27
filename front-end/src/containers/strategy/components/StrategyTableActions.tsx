import {
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
    ModalFooter,
    ModalHeader,
    ModalOverlay,
    Tooltip,
    useDisclosure
} from "@chakra-ui/react";
import DeleteConfirmationDialog from "components/DeleteConfirmationDialog";
import StrategyCreateEditForm from "containers/strategy/components/StrategyCreateEditForm";
import {useErrorToast} from "hooks/useErrorToast";
import useThemeColors from "hooks/useThemeColors";
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
                <MenuButton as={IconButton} icon={<IoMdPower/>}/>
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

const DeleteButton = ({strategy}: { strategy: StrategyListItem }) => {
    const handleDelete = useErrorToast(async () => {
        await deleteStrategy(strategy.id);
    }, SUCCESS_TYPES.STRATEGY_DELETED);

    return <DeleteConfirmationDialog entityName={"estratégia"} confirmCallback={handleDelete}/>
}

const EditButton = () => {
    const {isOpen, onOpen, onClose} = useDisclosure();
    const colors = useThemeColors();

    return (
        <>
            <IconButton aria-label="Editar" onClick={onOpen} icon={<MdEdit/>}/>

            <ChakraModal isOpen={isOpen} onClose={onClose}>
                <ModalOverlay/>
                <ModalContent>
                    <ModalHeader>Editar estratégia</ModalHeader>
                    <ModalCloseButton/>
                    <ModalBody>
                        <StrategyCreateEditForm/>
                    </ModalBody>
                    <ModalFooter>
                        <Button mr={3} onClick={onClose}>
                            Voltar
                        </Button>
                        <Button bgColor={colors.product} color={colors.productContrast} onClick={() => {
                        }}>
                            Salvar
                        </Button>
                    </ModalFooter>
                </ModalContent>
            </ChakraModal>
        </>
    )

}

const StrategyTableActions = ({strategy}: { strategy: StrategyListItem }) => {
    return (
        <ButtonGroup isAttached variant='outline'>
            <StatusMenu strategy={strategy}/>
            <Tooltip label={"Baixar relatório"} placement={"top"}>
                <IconButton aria-label="Baixar relatório" icon={<BiSpreadsheet/>}/>
            </Tooltip>
            <Tooltip label={"Editar"} placement={"top"}>
                <EditButton/>
            </Tooltip>
            <Tooltip label={"Excluir"} placement={"top"}>
                <DeleteButton strategy={strategy}/>
            </Tooltip>
        </ButtonGroup>
    )
};

export default StrategyTableActions;
