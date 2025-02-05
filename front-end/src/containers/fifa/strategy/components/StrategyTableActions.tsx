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
import useTranslation from "hooks/useTranslation";
import {useErrorToast} from "hooks/useErrorToast";
import React, {useState} from "react";
import ReactDOM from "react-dom";
import {HiOutlineSparkles, IoMdPower, MdEdit} from "react-icons/all";
import {changeStrategyStatus, deleteStrategy, restartStrategy} from "services/strategyService";
import {strategyStatusDict} from "utils/constants/strategyConstants";
import {SUCCESS_TYPES} from "utils/constants/successConstants";
import {StrategyListItem, StrategyStatus} from "utils/interfaces";

const StatusMenu = ({strategy}: { strategy: StrategyListItem }) => {
    const { t } = useTranslation();
    const invalidateStrategyQuery = useInvalidateStrategyQuery();

    const statuses: StrategyStatus[] = Object.keys(strategyStatusDict).filter(
        status => status !== strategy.status
    ) as StrategyStatus[];
    
    const handleStatusChange = useErrorToast(async (status: StrategyStatus, e: React.MouseEvent) => {
        e.stopPropagation();
        await changeStrategyStatus(strategy.id, status);
        invalidateStrategyQuery();
    }, SUCCESS_TYPES.STRATEGY_STATUS_CHANGED);

    const menuContent = (
        <MenuList 
            position="fixed" 
            zIndex={9999} 
            style={{ 
                transform: 'translateZ(0)', 
                willChange: 'transform',
                overflow: 'visible'
            }}
        >
            {statuses.map((status: StrategyStatus) => (
                <MenuItem key={status} onClick={(e: React.MouseEvent) => handleStatusChange(status, e)}>
                    {t(`strategy.statusMenu.statuses.${status}`)}
                </MenuItem>
            ))}
        </MenuList>
    );

    return (
        <Menu>
            <Tooltip label={t('strategy.statusMenu.tooltip')} placement={"top"}>
                <MenuButton as={IconButton} icon={<IoMdPower/>} variant="outline"
                            onClick={(e: React.MouseEvent) => e.stopPropagation()}/>
            </Tooltip>
            {ReactDOM.createPortal(
                menuContent, 
                document.body
            )}
        </Menu>
    );
};

const EditButton = ({strategyId}: { strategyId: string }) => {
    const {isOpen, onOpen, onClose} = useDisclosure();
    const { t } = useTranslation();

    return (
        <>
            <Tooltip label={t('strategy.actions.edit')} placement={"top"}>
                <IconButton aria-label={t('strategy.actions.edit')} onClick={(e: React.MouseEvent) => {
                    e.stopPropagation();
                    onOpen();
                }} icon={<MdEdit/>} variant="outline"/>
            </Tooltip>

            <ChakraModal isOpen={isOpen} onClose={onClose} motionPreset={"none"}>
                <ModalOverlay/>
                <ModalContent>
                    <ModalHeader>{t('strategy.actions.edit')} estratégia</ModalHeader>
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
    const { t } = useTranslation();

    const handleConfirm = useErrorToast(async () => {
        setIsLoading(true);
        await restartStrategy(strategy.id);
        invalidateStrategyQuery();
        setIsLoading(false);
        onClose();
    }, SUCCESS_TYPES.STRATEGY_RESTARTED);

    return (
        <>
            <Tooltip label={t('strategy.actions.restart')} placement={"top"}>
                <IconButton icon={<HiOutlineSparkles/>} aria-label={t('strategy.actions.restart')} variant={"outline"}
                            onClick={(e: React.MouseEvent) => {
                                e.stopPropagation();
                                onOpen();
                            }}/>
            </Tooltip>

            <AlertDialog isOpen={isOpen} leastDestructiveRef={cancelRef} onClose={onClose}>
                <AlertDialogOverlay>
                    <AlertDialogContent>
                        <AlertDialogHeader fontSize="lg" fontWeight="bold">
                            {t('strategy.restartDialog.title')}
                        </AlertDialogHeader>

                        <AlertDialogBody>
                            <Text>{t('strategy.restartDialog.confirmText')}</Text>
                            <Text>
                                {t('strategy.restartDialog.description1')}
                            </Text>
                            <Text>{t('strategy.restartDialog.description2')}</Text>
                        </AlertDialogBody>

                        <AlertDialogFooter>
                            <Button ref={cancelRef} onClick={onClose} isDisabled={isLoading}>
                                {t('strategy.restartDialog.cancel')}
                            </Button>
                            <Button colorScheme="yellow" onClick={handleConfirm} ml={3} isLoading={isLoading}>
                                {t('strategy.restartDialog.confirm')}
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
    const { t } = useTranslation();

    const handleDelete = useErrorToast(async () => {
        setIsLoading(true);
        await deleteStrategy(strategy.id);
        invalidateStrategyQuery();
        setIsLoading(false);
    }, SUCCESS_TYPES.STRATEGY_DELETED);

    return (
        <DeleteConfirmationDialog
            entityName={t('strategy.actions.delete')}
            tooltip
            confirmCallback={handleDelete}
            variant="outline"
            isLoading={isLoading}
            text={t('strategy.deleteDialog.text')}
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
