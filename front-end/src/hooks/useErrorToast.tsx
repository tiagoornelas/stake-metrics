import {CreateToastFnReturn, useToast} from "@chakra-ui/react";
import {useCallback} from "react";
import {TRANSLATED_ERRORS} from "utils/constants/errorConstants";
import {defaultToastProps} from "utils/constants/toastConstants";
import {ErrorDictionary, SuccessDictionary} from "utils/interfaces";

export const useErrorToast = (fn: (...args: any[]) => Promise<any>, successDictionary: SuccessDictionary | null = null, successAction: Function | null = null) => {
    const toast: CreateToastFnReturn = useToast();

    const getErrorMessage = (err: any): ErrorDictionary => {
        const errorMessage: string = err.response?.data?.message || err.message;
        const hasTranslatedError: boolean = !!TRANSLATED_ERRORS[errorMessage];

        if (hasTranslatedError) return TRANSLATED_ERRORS[errorMessage];
        return {
            title: "Ops! Algo deu errado.",
            description: errorMessage
        };
    }

    return useCallback(async (...args: any[]) => {
        try {
            const result = await fn(...args);

            if (!!successDictionary) {
                const {title, description} = successDictionary

                toast({
                    title,
                    description,
                    status: "success",
                    ...defaultToastProps
                });
            }

            if (!!successAction) successAction();

            return result;
        } catch (err: any) {
            const {title, description} = getErrorMessage(err);

            toast({
                title,
                description,
                status: "error",
                ...defaultToastProps
            });
        }
    }, [fn, successDictionary, successAction, toast]);
};
