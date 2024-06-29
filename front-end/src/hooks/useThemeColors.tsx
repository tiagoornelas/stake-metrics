import {useColorMode} from "@chakra-ui/react";
import {CustomThemeColorObj, getCustomThemeColors} from "utils/helpers/themeColorHelper";
import {useMemo} from "react";

interface DynamicThemeColors {
    [colorName: string]: string
}

const useThemeColors = (): DynamicThemeColors => {
    const {colorMode} = useColorMode();

    const customColors: CustomThemeColorObj = getCustomThemeColors();
    return useMemo(() => Object.keys(customColors).reduce((obj, colorName) => ({
        ...obj,
        [colorName]: customColors[colorName][colorMode]
    }), {}), [colorMode, customColors]);
}

export default useThemeColors;
