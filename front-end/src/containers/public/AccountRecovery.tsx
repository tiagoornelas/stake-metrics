import useThemeColors from "hooks/useThemeColors";
import {ChangeEvent, useState} from "react";
import {UserRecoveryBody} from "utils/interfaces";
import {useErrorToast} from "hooks/useErrorToast";
import {Button, FormControl, FormLabel, HStack, Input, Stack} from "@chakra-ui/react";
import PasswordField from "components/PasswordField";
import * as React from "react";
import {changePasswordWithRecoveryCode, sendRecoveryCode} from "services/recoveryService";
import {SUCCESS_TYPES} from "../../utils/constants/successConstants";

const AccountRecovery = () => {
    const colors = useThemeColors();
    const [recoveryCodeSent, setRecoveryCodeSent] = useState(false);
    const [isSendButtonDisabled, setIsSendButtonDisabled] = useState(false);
    const [countdown, setCountdown] = useState(30);
    const bgColor = isSendButtonDisabled ? "lightgrey" : colors.product;
    const cursor = isSendButtonDisabled ? "not-allowed" : "pointer";
    const sendCodeSubmitText = isSendButtonDisabled ? `Aguarde ${countdown} s para reenviar...` : "Receber código";
    const [form, setForm] = useState<UserRecoveryBody>({
        email: "",
        code: "",
        password: "",
        passwordConfirmation: ""
    });

    const handleInput = (e: ChangeEvent<HTMLInputElement>) => {
        setForm({
            ...form,
            [e.target.id]: e.target.value
        });
    }

    const handleCodeSendSubmit = useErrorToast(async () => {
        setIsSendButtonDisabled(true);
        setRecoveryCodeSent(true);
        await sendRecoveryCode(form.email);

        let countdownTimer = setInterval(() => {
            setCountdown(prevCountdown => {
                if (prevCountdown <= 1) {
                    clearInterval(countdownTimer);
                    return 30;
                }
                return prevCountdown - 1;
            });
        }, 1000);

        setTimeout(() => {
            setIsSendButtonDisabled(false);
            clearInterval(countdownTimer);
            setCountdown(30);
        }, 30000)

    }, SUCCESS_TYPES.RECOVERY_CODE_SENT);

    const handleCodeInputSubmit = useErrorToast(async () => {
        await changePasswordWithRecoveryCode(form)
    }, null, () => window.location.assign("/"));


    return (
        <Stack spacing="6">
            {recoveryCodeSent ? (<>
                <Stack spacing="5">
                    <FormControl>
                        <FormLabel htmlFor="code">Código</FormLabel>
                        <Input id="code" type="text" value={form.code} onChange={handleInput}/>
                    </FormControl>
                    <PasswordField onChange={handleInput}/>
                    <PasswordField onChange={handleInput} passwordConfirmation/>
                </Stack>
                <Stack spacing="6">
                    <Button bgColor={colors.product} color={colors.productContrast}
                            onClick={handleCodeInputSubmit}>Recuperar senha</Button>
                </Stack>
                <HStack justify="space-between">
                    <Button variant="text" size="sm" disabled onClick={() => setRecoveryCodeSent(false)}>
                        Reenviar código
                    </Button>
                    <Button variant="text" size="sm">
                        <a href="/">Lembrei minha senha</a>
                    </Button>
                </HStack>
            </>) : <>
                <Stack spacing="5">
                    <FormControl>
                        <FormLabel htmlFor="email">E-mail</FormLabel>
                        <Input id="email" type="email" value={form.email} onChange={handleInput}/>
                    </FormControl>
                </Stack>
                <Stack spacing="6">
                    <Button bgColor={bgColor}
                            cursor={cursor} color={colors.productContrast}
                            onClick={handleCodeSendSubmit}
                            disabled={isSendButtonDisabled}>{sendCodeSubmitText}</Button>
                </Stack>
                <HStack justify="space-between">
                    <Button variant="text" size="sm">
                        <a href="/create-account">Criar conta</a>
                    </Button>
                    <Button variant="text" size="sm">
                        <a href="/">Lembrei minha senha</a>
                    </Button>
                </HStack>
            </>}
        </Stack>
    );
};

export default AccountRecovery;
