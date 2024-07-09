import {Button, FormControl, FormLabel, HStack, Input, Stack,} from '@chakra-ui/react'
import PasswordField from "components/PasswordField";
import {useErrorToast} from "hooks/useErrorToast";
import useThemeColors from "hooks/useThemeColors";
import * as React from "react";
import {ChangeEvent, useState} from "react";
import InputMask from "react-input-mask";
import {createUser} from "services/userService";
import {UserCreationBody} from "utils/interfaces";
import {SUCCESS_TYPES} from "../../utils/constants/successConstants";

const CreateAccount = () => {
    const colors = useThemeColors();
    const [form, setForm] = useState<UserCreationBody>({
        name: "",
        email: "",
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
        await createUser(form)
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
