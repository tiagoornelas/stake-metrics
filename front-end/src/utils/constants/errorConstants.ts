import {ErrorDictionary} from "../interfaces";

export const TRANSLATED_ERRORS: Record<string, ErrorDictionary> = {
    "Bad credentials": {
        title: "Usuário ou senha inválida",
        description: "Verifique as credenciais e tente novamente."
    },
    "It was not possible to recover the account with given code.": {
        title: "Falha na recuperação de conta",
        description: "O código de recuperação está incorreto."
    }
}
