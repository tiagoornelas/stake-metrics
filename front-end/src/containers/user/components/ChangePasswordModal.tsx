import {Stack} from "@chakra-ui/react";
import Modal from "components/Modal";
import PasswordField from "components/PasswordField";
import {useUserState} from "context/UserContext";
import {useErrorToast} from "hooks/useErrorToast";
import React, {ChangeEvent, useState} from 'react';
import {changePassword} from "services/userService";
import {SUCCESS_TYPES} from "utils/constants/successConstants";
import {PasswordChangeBody, UserContext} from "utils/interfaces";

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

export default ChangePasswordModal;
