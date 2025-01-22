import { Box, Heading, Stack, Text, Alert, AlertIcon } from "@chakra-ui/react";
import { useMemo } from "react";
import Plans from "components/Plans";
import ProductLogo from "components/ProductLogo";
import ProductName from "components/ProductName";
import useThemeColors from "hooks/useThemeColors";
import { useUserState } from "context/UserContext";
import { UserContext } from "utils/interfaces";
import { useTranslation } from 'hooks/useTranslation';

const SubscriptionStatusAlert = ({
  hasPendingPayment,
}: {
  hasPendingPayment: boolean;
}) => {
    const { t } = useTranslation();
  if (hasPendingPayment) {
    return (
      <Alert status={"error"} maxWidth={"lg"} justifyContent={"center"}>
        <AlertIcon />
        {t('subscription.pendingPayment')}
      </Alert>
    );
  }

  return (
    <Alert status={"warning"} maxWidth={"md"} justifyContent={"center"}>
      <AlertIcon />
      {t('subscription.noSubscription')}
    </Alert>
  );
};

const Subscription = () => {
  const colors = useThemeColors();
  const userContext: UserContext = useUserState();
  const hasPendingPayment = useMemo(() => userContext.user.subscription?.hasPendingPayment, [userContext]) ?? false;

  return (
    <Box pt={16}>
      <Stack
        as={Box}
        alignItems={"center"}
        textAlign={"center"}
        spacing={4}
        mb={4}
      >
        <Heading
          fontWeight={600}
          fontSize={{ base: "xl", sm: "2xl", md: "4xl" }}
          lineHeight={"110%"}
        >
          <Text as={"span"} color={colors.product}>
            <ProductLogo />
            <ProductName />
          </Text>
        </Heading>
        <SubscriptionStatusAlert hasPendingPayment={hasPendingPayment} />
      </Stack>
      <Plans />
    </Box>
  );
};

export default Subscription;
