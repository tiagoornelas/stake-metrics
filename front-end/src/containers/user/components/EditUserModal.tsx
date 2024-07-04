import {FormControl, FormLabel, Input, Stack} from "@chakra-ui/react";
import Modal from "components/Modal";
import {useUserState} from "context/UserContext";
import {useErrorToast} from "hooks/useErrorToast";
import React, {ChangeEvent, useState} from 'react';
import InputMask from "react-input-mask";
import {editUser} from "services/userService";
import {SUCCESS_TYPES} from "utils/constants/successConstants";
import {removeCountryPrefixFromPhone, sanitizePhoneMask} from "utils/helpers/sanitizationHelper";
import {UserContext, UserCreationBody} from "utils/interfaces";

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

export default EditUserModal;