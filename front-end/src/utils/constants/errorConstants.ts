import {ErrorDictionary} from "../interfaces";

export const TRANSLATED_ERRORS: Record<string, ErrorDictionary> = {
    "Bad credentials": {
        title: "Usuário ou senha inválida",
        description: "Verifique as credenciais e tente novamente."
    },
    "It was not possible to recover the account with given code.": {
        title: "Falha na recuperação de conta",
        description: "O código de recuperação está incorreto."
    },
    "The integration already exists.": {
        title: "A integração já existe",
        description: "Essa integração já foi criada para este usuário, tente outra integração."
    },
    "Name, marketSubTypes and leagues cannot be blank": {
        title: "Não salvamos a estratégia!",
        description: "Verifique se o nome, linhas do mercado ou ligas está vazio."
    },
    "Scopes cannot be empty": {
        title: "Não salvamos a estratégia!",
        description: "Não rola de salvar sem ter pelo menos uma regra na estratégia."
    },
    "Duplicate rule type found in scope": {
        title: "Não salvamos a estratégia!",
        description: "Tem regras duplicadas nas suas configurações, dê uma olhada nisso."
    },
    "Rule value out of range": {
        title: "Não salvamos a estratégia!",
        description: "Existem valores não permitidos para o tipo de regra, dê uma olhada nisso."
    },
    "Strategy does not belong to user": {
        title: "Não salvamos a estratégia!",
        description: "A estratégia não pertence a você, você não pode alterá-la."
    },
    "User has reached the maximum number of strategies": {
        title: "Limite de estratégias atingido!",
        description: "Você não pode ter mais do que 20 estratégias criadas."
    },
    "User has reached the maximum number of running strategies": {
        title: "Limite de estratégias atingido!",
        description: "Você chegou ao seu limite de estratégias ativas ou em Paper Bet, verifique seu plano."
    },
    "Telegram chat with passphrase not found": {
        title: "Não conseguimos integrar o Telegram!",
        description: "Tente novamente aguardando um pouco mais para conectar após enviar o código. Caso ainda assim não consiga, entre em contato com o suporte."
    },
    "Network Error": {
        title: "Ops! Tivemos uma queda por aqui.",
        description: "Tente novamente mais tarde, estamos trabalhando para resolver o problema."
    },
    "Password does not match for specific account.": {
        title: "Ops! Credenciais estão erradas.",
        description: "Verifique e-mail e login e tente novamente."
    },
    "Name must be at least 2 characters long": {
        title: "Ops! Nome muito curto.",
        description: "O nome precisa ter pelo menos 2 caracteres."
    },
    "Password must be at least 8 characters long": {
        title: "Ops! Senha muito curta.",
        description: "A senha precisa ter pelo menos 8 caracteres."
    },
    "Email is not valid": {
        title: "Ops! E-mail inválido.",
        description: "O e-mail precisa ser válido."
    },
    "Failed to retrieve authentication tokens. Please try again.": {
        title: "Ops! Falha na autenticação.",
        description: "Não conseguimos recuperar os tokens de autenticação. Tente novamente."
    },
}

export const ERROR_TYPES = {
    FORBIDDEN_ACCESS: {
        title: "Você ainda não possui permissão para acessar esta parte do sistema!",
        description: "Faça upgrade do seu plano para poder utilizar."
    }
}
