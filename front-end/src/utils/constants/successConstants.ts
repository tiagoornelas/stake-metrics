import {SuccessDictionary} from "../interfaces";

export const SUCCESS_TYPES: Record<string, SuccessDictionary> = {
    DEFAULT: {
        title: "Ok!",
        description: "Operação realizada com sucesso."
    },
    USER_CREATED: {
        title: "Ok!",
        description: "Usuário criado com sucesso."
    },
    USER_EDITED: {
        title: "Ok!",
        description: "Usuário editado com sucesso."
    },
    PASSWORD_CHANGED: {
        title: "Ok!",
        description: "Senha alterada com sucesso."
    },
    RECOVERY_CODE_SENT: {
        title: "Ok!",
        description: "Código de recuperação enviado com sucesso."
    }
}

