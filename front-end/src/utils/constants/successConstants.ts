import {SuccessDictionary} from "../interfaces";

export const SUCCESS_TYPES: Record<string, SuccessDictionary> = {
    DEFAULT: {
        title: "Ok!",
        description: "Operação realizada com sucesso."
    },
    USER_LOGGED_IN: {
        title: "Seja bem-vindo! 🎉",
        description: "Você foi logado com sucesso."
    },
    USER_LOGGED_OUT: {
        title: "Até mais! 👋",
        description: "Você foi deslogado com sucesso."
    },
    USER_CREATED: {
        title: "Tudo certo! 👍",
        description: "Seu usuário foi criado, agora é só fazer login."
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
        title: "Chat excluído!",
        description: "As mensagens enviadas a ele não serão mais editadas."
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
    STRATEGY_RESTARTED: {
        title: "Um novo começo! ✨",
        description: "Estratégia reiniciada com sucesso."
    },
    BET_DELETED: {
        title: "Ok!",
        description: "Entrada excluída com sucesso."
    },
    AUTO_BETTOR_INTEGRATED: {
        title: "Ok!",
        description: "Canal AutoBettor integrado com sucesso."
    },
    AUTO_BETTOR_DELETED: {
        title: "Ok!",
        description: "Canal AutoBettor excluído com sucesso."
    },
    AUTO_BETTOR_ACTIVATED: {
        title: "Ok!",
        description: "Canal AutoBettor ativado com sucesso."
    },
    AUTO_BETTOR_DEACTIVATED: {
        title: "Ok!",
        description: "Canal AutoBettor desativado com sucesso."
    }
}
