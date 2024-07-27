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
    },
    TELEGRAM_CODE_COPIED: {
        title: "Ok!",
        description: "Código para conexão com Telegram copiado para a área de transferência."
    },
    TELEGRAM_CHAT_CONNECTED: {
        title: "Ok!",
        description: "Chat do Telegram conectado com sucesso."
    },
    TELEGRAM_CHAT_DELETED: {
        title: "Ok!",
        description: "Chat do Telegram excluído com sucesso."
    },
    TELEGRAM_CHAT_EDITED: {
        title: "Ok!",
        description: "Chat do Telegram editado com sucesso."
    },
    TELEGRAM_CHAT_TESTED: {
        title: "Enviamos uma mensagem!",
        description: "Leve em consideração que o atraso e a taxa de entrega também são testados, então a mensagem pode chegar com atraso ou até não chegar."
    },
    STRATEGY_STATUS_CHANGED: {
        title: "Ok!",
        description: "Status da estratégia alterado com sucesso."
    },
    STRATEGY_SAVED: {
        title: "Ok!",
        description: "Estratégia salva com sucesso."
    },
    STRATEGY_DELETED: {
        title: "Ok!",
        description: "Estratégia excluída com sucesso."
    },
}
