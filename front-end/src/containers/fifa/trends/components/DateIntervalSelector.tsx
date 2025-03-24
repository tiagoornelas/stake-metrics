import React from "react";
import { ButtonGroup, Button, Flex, useBreakpointValue, useColorModeValue } from "@chakra-ui/react";
import { DateIntervalTypes } from "../../../../utils/constants/dateConstants";
import useThemeColors from "../../../../hooks/useThemeColors";

interface DateIntervalSelectorProps {
  selectedInterval: DateIntervalTypes;
  onIntervalChange: (interval: DateIntervalTypes) => void;
}

const DateIntervalSelector: React.FC<DateIntervalSelectorProps> = ({
  selectedInterval,
  onIntervalChange,
}) => {
  const colors = useThemeColors();
  const size = useBreakpointValue({ base: "xs", md: "sm" });
  const unselectedText = useColorModeValue("gray.600", "gray.400");
  
  const intervals = [
    { value: DateIntervalTypes.TWENTY_FOUR_HOURS, label: "24 horas" },
    { value: DateIntervalTypes.THREE_DAYS, label: "3 dias" },
    { value: DateIntervalTypes.ONE_WEEK, label: "1 semana" },
    { value: DateIntervalTypes.TWO_WEEKS, label: "2 semanas" },
    { value: DateIntervalTypes.ONE_MONTH, label: "1 mês" },
  ];

  return (
    <Flex justify="center" width="100%">
      <ButtonGroup 
        variant="ghost" 
        spacing={1} 
        size={size}
      >
        {intervals.map((interval) => (
          <Button
            key={interval.value}
            onClick={() => onIntervalChange(interval.value)}
            color={selectedInterval === interval.value ? colors.productAlternative : unselectedText}
            fontWeight={selectedInterval === interval.value ? "bold" : "normal"}
            borderBottom={selectedInterval === interval.value ? `2px solid ${colors.productAlternative}` : "2px solid transparent"}
            borderRadius="0"
            _hover={{
              backgroundColor: "gray.50",
              color: selectedInterval === interval.value ? colors.productAlternative : "gray.800",
            }}
            px={2}
          >
            {interval.label}
          </Button>
        ))}
      </ButtonGroup>
    </Flex>
  );
};

export default DateIntervalSelector;
