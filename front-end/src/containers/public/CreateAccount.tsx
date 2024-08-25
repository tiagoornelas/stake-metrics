import {Button, Flex, FormControl, FormLabel, Heading, HStack, Input, Stack, Text} from '@chakra-ui/react'
import PasswordField from "components/PasswordField";
import UseTerms from "components/UseTerms";
import {useErrorToast} from "hooks/useErrorToast";
import * as React from "react";
import {ChangeEvent, Fragment, useState} from "react";
import {useNavigate} from "react-router-dom";
import {createUser} from "services/userService";
import {SUCCESS_TYPES} from "utils/constants/successConstants";
import {UserCreationBody} from "utils/interfaces";

const WaitingListComponent = () => {
    return (<Flex direction="column" gap={4} mb={4}>
        <Heading>Calma aí! ✋</Heading>
        <Text fontSize="md">Não estamos aceitando novos clientes no momento! Por enquanto estamos trabalhando com uma
            seleção de clientes que estão testando o produto.</Text>
        <Text fontSize="md">Caso tenha interesse de participar dos testes, envie e-mail para
            <b> stakemetrics@gmail.com</b> que avaliaremos a sua solicitação.</Text>
    </Flex>)
}

const CreateAccount = () => {
    const navigate = useNavigate();
    const acceptingNewCustomers = process.env.REACT_APP_DISABLE_NEW_USERS !== "true";

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
        await createUser(form);
        navigate('/login', {replace: true});
    }, SUCCESS_TYPES.USER_CREATED)

    return (<Stack spacing="6">
            {acceptingNewCustomers ? <Fragment><Stack spacing="5">
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
                    <UseTerms buttonText={"Criar conta"} confirmAction={handleSubmit}/>
                </Stack></Fragment> : <WaitingListComponent/>}
            <HStack justify="space-between">
                <Button variant="text" size="sm">
                    <a href="/">Já tenho conta</a>
                </Button>
                <Button variant="text" size="sm">
                    <a href="/recover-account">Esqueci a senha</a>
                </Button>
            </HStack>
        </Stack>
    )
}

export default CreateAccount;
