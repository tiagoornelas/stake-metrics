import {
    Box,
    Flex,
    FormControl,
    FormLabel,
    Heading,
    Input,
    Stack,
    Switch, Tag,
    Text,
    useColorModeValue
} from "@chakra-ui/react";
import Modal from "components/Modal";
import {useUserState} from "context/UserContext";
import {useErrorToast} from "hooks/useErrorToast";
import React, {ChangeEvent, useState} from 'react';
import {editUser} from "services/userService";
import {SUCCESS_TYPES} from "utils/constants/successConstants";
import {UserContext, UserCreationBody} from "utils/interfaces";

const EditUserModal = () => {
    const userContext: UserContext = useUserState();
    const [form, setForm] = useState<UserCreationBody>({
        name: userContext.user.name || "",
        email: userContext.user.email || "",
        avoidRepeatedBets: userContext.user.avoidRepeatedBets || false
    });

    const explanatoryTextColor = useColorModeValue("gray.700", "gray.300");

    const handleInput = (e: ChangeEvent<HTMLInputElement>) => {
        setForm({
            ...form,
            [e.target.id]: e.target.value
        });
    }

    const handleSwitchChange = (e: ChangeEvent<HTMLInputElement>) => {
        setForm({
            ...form,
            [e.target.id]: e.target.checked
        });
    }

    const handleSubmit = useErrorToast(async () => {
        if (!!userContext.user.id) {
            await editUser(userContext.user.id, form);
            window.location.reload();
        }
    }, SUCCESS_TYPES.USER_EDITED)

    return <Modal buttonText="Configurações da conta" title="Configurações da conta" actionText="Salvar"
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
            <Box display="flex" flexDirection="column" gap={2}>
                <Heading size="sm" mb={2}>Configurações de apostas</Heading>
                <Box>
                    <Flex alignItems="center" gap={2}>
                        <Switch
                            id="avoidRepeatedBets"
                            isChecked={form.avoidRepeatedBets}
                            onChange={handleSwitchChange}
                        />
                        <Text>Evitar apostas repetidas</Text>s
                        <Tag colorScheme="yellow">Beta</Tag>
                    </Flex>
                    <Text fontSize="sm" color={explanatoryTextColor} mt={2}>
                        Esta funcionalidade evita apostas repetidas quando várias estratégias estão apostando na mesma partida e linha. Ao ativar, apenas uma estratégia (a primeira a apostar) fará a aposta ativa, as demais farão apostas em Paper Bet mesmo que estejam ativas.
                    </Text>
                </Box>
            </Box>
        </Stack>
    </Modal>
}

export default EditUserModal;
