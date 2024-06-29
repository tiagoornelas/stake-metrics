import {Box, Skeleton, Text} from "@chakra-ui/react";
import React, {useEffect} from 'react';
import {getPricingTableInfo} from "services/planService";

declare global {
    namespace JSX {
        interface IntrinsicElements {
            'stripe-pricing-table': React.DetailedHTMLProps<React.HTMLAttributes<HTMLElement>, HTMLElement>;
        }
    }
}

const Plans = () => {
    const [stripePricingTableId, setStripePricingTableId] = React.useState<string>("");
    const [stripePublicKey, setStripePublicKey] = React.useState<string>("");
    const [customerSessionClientSecret, setCustomerSessionClientSecret] = React.useState<string>("");
    const [isLoaded, setIsLoaded] = React.useState<boolean>(false);

    useEffect(() => {
        const fetchPricingTableParameters = async () => {
            setIsLoaded(false);

            const response = await getPricingTableInfo();

            setStripePricingTableId(response.pricingTableId);
            setStripePublicKey(response.publicKey);
            setCustomerSessionClientSecret(response.customerSessionClientSecret);
            setIsLoaded(true);
        }

        fetchPricingTableParameters();
    }, []);

    return (
        <Box display="flex" alignItems="center" flexDirection="column">
            <Text>💡 Encontre o plano ideal para os seus objetivos.</Text>
            <Box width="100%" pt={8} px={16}>
                <Skeleton isLoaded={isLoaded} width="100%">
                    <stripe-pricing-table
                        pricing-table-id={stripePricingTableId}
                        publishable-key={stripePublicKey}
                        customer-session-client-secret={customerSessionClientSecret}
                    >
                    </stripe-pricing-table>
                </Skeleton>
            </Box>
        </Box>
    );
};

export default Plans;