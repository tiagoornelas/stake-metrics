import {
    Badge,
    Box,
    Button,
    Flex,
    FormControl,
    FormLabel,
    Heading,
    Input,
    SimpleGrid,
    Skeleton,
    Stack,
    Text,
    useBreakpointValue
} from "@chakra-ui/react";
import Modal from "components/Modal";
import PasswordField from "components/PasswordField";
import {useUserState} from "context/UserContext";
import {useErrorToast} from "hooks/useErrorToast";
import * as React from "react";
import {ChangeEvent, useEffect, useState} from "react";
import InputMask from "react-input-mask";
import {createPortalSession} from "services/planService";
import {changePassword, editUser} from "services/userService";
import {SUCCESS_TYPES} from "utils/constants/successConstants";
import {
    applyDateMask,
    applyPhoneMask,
    removeCountryPrefixFromPhone,
    sanitizePhoneMask
} from "utils/helpers/sanitizationHelper";
import {PasswordChangeBody, UserContext, UserCreationBody} from "utils/interfaces";

const EditUserModal = () => {
    const userContext: UserContext = useUserState();
    const [form, setForm] = useState<UserCreationBody>({
        name: userContext.user.name || "",
        email: userContext.user.email || "",
        phone: removeCountryPrefixFromPhone(userContext.user.phone) || "",
    });

    const handleInput = (e: ChangeEvent<HTMLInputElement>) => {
        setForm({
            ...form,
            [e.target.id]: e.target.value
        });
    }

    const handleSubmit = useErrorToast(async () => {
        const user: UserCreationBody = {...form, phone: sanitizePhoneMask(form.phone)};
        if (!!userContext.user.id) {
            await editUser(userContext.user.id, user);
            window.location.reload();
        }
    }, SUCCESS_TYPES.USER_EDITED)

    return <Modal buttonText="Editar usuário" title="Editar usuário" actionText="Salvar"
                  actionCallback={handleSubmit}>
        <Stack spacing="5">
            <FormControl>
                <FormLabel htmlFor="name">Nome</FormLabel>
                <Input id="name" type="text" onChange={handleInput} value={form.name}/>
            </FormControl>
            <FormControl>
                <FormLabel htmlFor="email">E-mail</FormLabel>
                <Input id="email" type="email" onChange={handleInput} value={form.email}/>
            </FormControl>
            <FormControl>
                <FormLabel htmlFor="phone">Telefone</FormLabel>
                <Input id="phone" as={InputMask} mask="(**) *********" type="text" onChange={handleInput}
                       value={form.phone}/>
            </FormControl>
        </Stack>
    </Modal>
}

const ChangePasswordModal = () => {
    const userContext: UserContext = useUserState();
    const [form, setForm] = useState<PasswordChangeBody>({
        currentPassword: "",
        password: "",
        passwordConfirmation: "",
    });

    const handleInput = (e: ChangeEvent<HTMLInputElement>) => {
        setForm({
            ...form,
            [e.target.id]: e.target.value
        });
    }

    const handleSubmit = useErrorToast(async () => {
        if (userContext.user.id) await changePassword(userContext.user.id, form);
    }, SUCCESS_TYPES.PASSWORD_CHANGED);

    return <Modal buttonText="Alterar senha" title="Alterar senha" actionText="Salvar"
                  actionCallback={handleSubmit}><Stack spacing="5">
        <PasswordField onChange={handleInput} currentPassword/>
        <PasswordField onChange={handleInput}/>
        <PasswordField onChange={handleInput} passwordConfirmation/>
    </Stack></Modal>
}

const UserManagement = () => {
    const [managementLink, setManagementLink] = useState<string>("");
    const [isManagementLinkLoaded, setIsManagementLinkLoaded] = useState<boolean>(false);
    const columns: number = useBreakpointValue({base: 1, md: 2}) || 2;
    const userContext: UserContext = useUserState();
    const isLoaded: boolean = !!userContext.user.id;

    useEffect(() => {
        const getManagementLink = async () => {
            const {url} = await createPortalSession();
            setManagementLink(url);
            setIsManagementLinkLoaded(true);
        }

        getManagementLink();
    }, []);

    const handleManagementSubscriptionClick = () => {
        if (managementLink !== "") window.location.assign(managementLink);
    }

    return <SimpleGrid columns={columns} spacing={10} p={4}>
        <Box>
            <Skeleton isLoaded={isLoaded}>
                <Flex direction="column" mb={4}>
                    <Heading size="md" mb={2}>Usuário</Heading>
                    <Heading size="sm">Nome</Heading>
                    <Text>{userContext.user.name}</Text>
                    <Heading size="sm">E-mail</Heading>
                    <Text>{userContext.user.email}</Text>
                    <Heading size="sm">Telefone</Heading>
                    <Text>{applyPhoneMask(userContext.user.phone)}</Text>
                </Flex>
                <Flex direction="column" gap={4} alignItems="self-start">
                    <EditUserModal/>
                    <ChangePasswordModal/>
                </Flex>
            </Skeleton>
        </Box>
        <Box>
            <Skeleton isLoaded={isLoaded}>
                <Flex direction="column" mb={4}>
                    <Flex gap={4} alignItems="center"><Heading size="md" mb={2}>Plano</Heading>
                        {userContext.user.isExpired ? <Badge colorScheme='red'>Expirado</Badge> :
                            <Badge colorScheme='green'>Ativo</Badge>}
                    </Flex>
                    {userContext.user.subscription?.expiresAt && (
                        <>
                            <Heading size="sm">{userContext.user.isExpired ? "Expirou em" : "Expira em"}</Heading>
                            <Text>{applyDateMask(userContext.user.subscription?.expiresAt)}</Text>
                        </>
                    )}
                </Flex>
                <Flex direction="column" gap={4} alignItems="self-start">
                    <Skeleton isLoaded={isManagementLinkLoaded}>
                        <Button onClick={handleManagementSubscriptionClick}>Gerenciar assinatura</Button>
                    </Skeleton>
                </Flex>
            </Skeleton>
        </Box>
    </SimpleGrid>
}

export default UserManagement;
