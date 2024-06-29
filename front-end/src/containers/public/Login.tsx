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
import {useCookies} from "react-cookie";
import {login} from "services/loginService";
import * as React from "react";
import PasswordField from "components/PasswordField";
import {LoginBody} from "utils/interfaces";
import {useErrorToast} from "hooks/useErrorToast";

const Login = () => {
    const colors = useThemeColors();
    const [, setCookies] = useCookies(["userId", "token"]);
    const [form, setForm] = useState<LoginBody>({
        email: "",
        password: ""
    });

    const handleInput = (e: ChangeEvent<HTMLInputElement>) => {
        setForm({
            ...form,
            [e.target.id]: e.target.value
        });
    }

    const handleSubmit = useErrorToast(async () => {
        const {token, userId} = await login(form);
        setCookies("token", token);
        setCookies("userId", userId);
        window.location.assign("/");
    })

    return (
        <Stack spacing="6">
            <Stack spacing="5">
                <FormControl>
                    <FormLabel htmlFor="email">E-mail</FormLabel>
                    <Input id="email" type="email" onChange={handleInput}/>
                </FormControl>
                <PasswordField onChange={handleInput}/>
            </Stack>
            <Stack spacing="6">
                <Button bgColor={colors.product} color={colors.productContrast}
                        onClick={handleSubmit}>Entrar</Button>
            </Stack>
            <HStack justify="space-between">
                <Button variant="text" size="sm">
                    <a href="/create-account">Criar conta</a>
                </Button>
                <Button variant="text" size="sm">
                    <a href="/recover-account">Esqueci a senha</a>
                </Button>
            </HStack>
        </Stack>
    );
}

export default Login;
