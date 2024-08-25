import {Button, FormControl, FormLabel, HStack, Input, Stack,} from '@chakra-ui/react'
import PasswordField from "components/PasswordField";
import {useErrorToast} from "hooks/useErrorToast";
import useThemeColors from "hooks/useThemeColors";
import * as React from "react";
import {ChangeEvent, useState} from "react";
import {useCookies} from "react-cookie";
import {useNavigate} from "react-router-dom";
import {login} from "services/loginService";
import {SUCCESS_TYPES} from "utils/constants/successConstants";
import {LoginBody} from "utils/interfaces";

const Login = () => {
    const navigate = useNavigate();
    const colors = useThemeColors();
    const [, setCookies] = useCookies(["userId", "token"]);
    const [form, setForm] = useState<LoginBody>({
        email: "",
        password: ""
    });
    const [isLoading, setIsLoading] = useState(false);

    const handleInput = (e: ChangeEvent<HTMLInputElement>) => {
        setForm({
            ...form,
            [e.target.id]: e.target.value
        });
    }

    const handleSubmit = useErrorToast(async () => {
        setIsLoading(true);
        let token: string;
        let userId: string;
        try {
            const data = await login(form);
            token = data.token;
            userId = data.userId;
        } catch (e) {
            setIsLoading(false);
            throw e;
        }

        setCookies("token", token);
        setCookies("userId", userId);
        setTimeout(() => {
            navigate("/app", {replace: true});
            setIsLoading(false)
        }, 1500);
    }, SUCCESS_TYPES.USER_LOGGED_IN);

    const handleGoToCreateAccount = () => navigate('/create-account', {replace: true});
    const handleGoToRecoverPassword = () => navigate('/recover-account', {replace: true});

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
                        onClick={handleSubmit} isLoading={isLoading}>Entrar</Button>
            </Stack>
            <HStack justify="space-between">
                <Button variant="text" size="sm" onClick={handleGoToCreateAccount}>
                    Criar conta
                </Button>
                <Button variant="text" size="sm" onClick={handleGoToRecoverPassword}>
                    Esqueci a senha
                </Button>
            </HStack>
        </Stack>
    );
}

export default Login;
