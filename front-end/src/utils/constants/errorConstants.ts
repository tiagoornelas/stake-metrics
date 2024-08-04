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
}
