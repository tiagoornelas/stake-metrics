import {defaultToastProps} from "../constants/toastConstants";
import {CreateToastFnReturn} from "@chakra-ui/react";

export const notImplementedToast = (toast: CreateToastFnReturn) => {
    toast({
        title: "Ainda não implementado. Aguarde as próximas atualizações",
        status: "warning",
        ...defaultToastProps
    });
}