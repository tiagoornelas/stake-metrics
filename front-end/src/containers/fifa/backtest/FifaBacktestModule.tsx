import {Box, CreateToastFnReturn, useToast} from "@chakra-ui/react";
import NewFeatureEmptyState from "components/NewFeatureEmptyState";
import {useUserState} from "context/UserContext";
import React, {useEffect} from 'react';
import {useNavigate} from "react-router-dom";
import {ERROR_TYPES} from "utils/constants/errorConstants";
import {FEATURES} from "utils/constants/featureConstants";
import {defaultToastProps} from "utils/constants/toastConstants";
import {getFeatureAmount} from "utils/helpers/featureHelper";

const FifaBacktestModule = () => {
    const toast: CreateToastFnReturn = useToast();
    const navigate = useNavigate();
    const {user} = useUserState();
    const canUserAccessTrendModule = getFeatureAmount(user, FEATURES.TREND_MODULE) >= 1 ?? false;

    useEffect(() => {
        if (canUserAccessTrendModule) {
            toast({
                status: "error",
                ...ERROR_TYPES.FORBIDDEN_ACCESS,
                ...defaultToastProps
            });

            navigate("/app/user-management", {replace: true});
        }
    }, [canUserAccessTrendModule, navigate, toast]);

    return (
        <Box mt={4}>
            <NewFeatureEmptyState
                text={"Aqui será possível testar suas estratégias com a base de dados de eventos passados e ver como ela teria se saído."}/>
        </Box>
    );
};

export default FifaBacktestModule;
