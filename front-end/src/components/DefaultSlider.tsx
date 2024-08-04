import {Slider, SliderFilledTrack, SliderThumb, SliderTrack} from "@chakra-ui/react";
import React from 'react';

interface DefaultSliderProps {
    handleChange: (value: number) => void;
    value: number;
    min: number;
    max: number;
    step: number;
    isPercentage?: boolean;
}

const DefaultSlider: React.FC<DefaultSliderProps> = ({
                                                         handleChange,
                                                         value,
                                                         min,
                                                         max,
                                                         step,
                                                         isPercentage = false,
                                                     }) => {
    const [sliderValue, setSliderValue] = React.useState(isPercentage ? value * 100 : value); // Apply multiplier based on isPercentage

    const handleSliderChange = (val: number) => {
        const newValue = isPercentage ? val / 100 : val;
        setSliderValue(val);
        handleChange(newValue);
    };

    return (
        <Slider
            value={sliderValue}
            min={isPercentage ? min * 100 : min}
            max={isPercentage ? max * 100 : max}
            step={isPercentage ? step * 100 : step}
            onChange={handleSliderChange}
        >
            <SliderTrack>
                <SliderFilledTrack/>
            </SliderTrack>
            <SliderThumb/>
        </Slider>
    );
};

export default DefaultSlider;
