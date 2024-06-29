// All colors inserted in themeColors will be dynamically inserted into Chakra's UI theming
// also will be added with color Mode sensitivity to useThemeColors hook.
// Define an index signature where values are arrays of two color strings, the first one for light
// and second for dark mode
interface CustomColorArrays {
    [colorName: string]: [string, string]
}

const themeColors: CustomColorArrays = {
    product: ["#68D89B", "#BFD7ED"],
    productContrast: ["#3B3C3D", "#3B3C3D"],
    textContrast: ["#3B3C3D", "#FFFFFF"],
    error: ["red", "red"]
}

export interface CustomThemeColorObj {
    [colorName: string]: {
        light: string,
        dark: string
    }
}

export const getCustomThemeColors = (): CustomThemeColorObj => Object.entries(themeColors).reduce((obj, [colorName, [light, dark]]) => ({
    ...obj,
    [colorName]: {
        light,
        dark
    }
}), {})

export const getPercentageColor = (value: number) => {
    if (value > 70) return "green";
    if (value > 30) return "yellow";
    return "red";
}