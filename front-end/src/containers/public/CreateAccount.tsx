import {
    Button,
    FormControl,
    FormLabel,
    HStack,
    Input,
    Stack,
} from '@chakra-ui/react'
import useThemeColors from "hooks/useThemeColors";
import {ChangeEvent, useState} from "react";
import * as React from "react";
import PasswordField from "components/PasswordField";
import {UserCreationBody} from "utils/interfaces";
import {useErrorToast} from "hooks/useErrorToast";
import {createUser} from "services/userService";
import InputMask from "react-input-mask";
import {sanitizePhoneMask} from "../../utils/helpers/sanitizationHelper";
import {SUCCESS_TYPES} from "../../utils/constants/successConstants";

const CreateAccount = () => {
    const colors = useThemeColors();
    const [form, setForm] = useState<UserCreationBody>({
        name: "",
        email: "",
        phone: "",
        password: "",
        passwordConfirmation: ""
    });

    const handleInput = (e: ChangeEvent<HTMLInputElement>) => {
        setForm({
            ...form,
            [e.target.id]: e.target.value
        });
    }

    const handleSubmit = useErrorToast(async () => {
        const user: UserCreationBody = {...form, phone: sanitizePhoneMask(form.phone)}
        await createUser(user)
        window.location.assign("/");
    }, SUCCESS_TYPES.USER_CREATED)

    return (
        <Stack spacing="6">
            <Stack spacing="5">
                <FormControl>
                    <FormLabel htmlFor="name">Nome</FormLabel>
                    <Input id="name" type="text" onChange={handleInput}/>
                </FormControl>
                <FormControl>
                    <FormLabel htmlFor="email">E-mail</FormLabel>
                    <Input id="email" type="email" onChange={handleInput}/>
                </FormControl>
                <FormControl>
                    <FormLabel htmlFor="phone">Telefone</FormLabel>
                    <Input id="phone" as={InputMask} mask="(**) *********" type="text" onChange={handleInput}/>
                </FormControl>
                <PasswordField onChange={handleInput}/>
                <PasswordField onChange={handleInput} passwordConfirmation/>
            </Stack>
            <Stack spacing="6">
                <Button bgColor={colors.product} color={colors.productContrast}
                        onClick={handleSubmit}>Criar conta</Button>
            </Stack>
            <HStack justify="space-between">
                <Button variant="text" size="sm">
                    <a href="/">Já tenho conta</a>
                </Button>
                <Button variant="text" size="sm">
                    <a href="/recover-account">Esqueci a senha</a>
                </Button>
            </HStack>
        </Stack>
    );
}

export default CreateAccount;
