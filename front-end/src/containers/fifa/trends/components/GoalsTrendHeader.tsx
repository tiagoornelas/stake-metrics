import { Box, Heading, Text } from "@chakra-ui/react";
import useThemeColors from "hooks/useThemeColors";

const GoalsTrendHeader = () => {
  const colors = useThemeColors();

  return (
    <Box mb={4}>
      <Heading size="sm" mb={1}>
        Tendência do mercado de gols
      </Heading>
      <Text fontSize={"xs"} color={colors.textContrast}>
        Identifique aqui a tendência de todos os jogos do mercado de gols para as ligas de
        E-Soccer.
      </Text>
      <Text fontSize={"xs"} color={colors.textContrast}>
        Acima da linha do meio do gráfico, é tendência de "Mais" gols, abaixo
        significa tendência de "Menos" gols.
      </Text>
    </Box>
  );
};

export default GoalsTrendHeader;
