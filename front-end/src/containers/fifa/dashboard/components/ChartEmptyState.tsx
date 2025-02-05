import { Button, Center, Text, VStack } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import useThemeColors from "hooks/useThemeColors";
import useTranslation from "hooks/useTranslation";

const ChartEmptyState = () => {
  const colors = useThemeColors();
  const { t } = useTranslation();
  const navigate = useNavigate();

  return (
    <Center mt={24}>
      <VStack spacing={8}>
        <Text color={colors.textContrast} textAlign="center">
          {t("dashboard.emptyCharts")}
        </Text>
        <Button
          colorScheme="blue"
          onClick={() => navigate("/app/esoccer/strategies")}
        >
          {t("dashboard.createStrategy")}
        </Button>
      </VStack>
    </Center>
  );
};

export default ChartEmptyState;
