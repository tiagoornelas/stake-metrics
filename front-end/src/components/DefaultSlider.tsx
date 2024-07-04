import {Slider, SliderFilledTrack, SliderThumb, SliderTrack, Tooltip} from "@chakra-ui/react";
import React from 'react';

interface DefaultSliderProps {
    handleChange: (value: number) => void;
    value: number;
    min: number;
    max: number;
    step: number;
    sufix: string;
    isPercentage?: boolean;
}

const DefaultSlider: React.FC<DefaultSliderProps> = ({handleChange, value, min, max, step, isPercentage = false, sufix = ""}) => {
    const [sliderValue, setSliderValue] = React.useState(isPercentage ? value * 100 : value); // Apply multiplier based on isPercentage
    const [showTooltip, setShowTooltip] = React.useState(false);

    const handleSliderChange = (val: number) => {
        const newValue = isPercentage ? val / 100 : val;
        setSliderValue(val);
        handleChange(newValue);
    };

    return (
        <Slider
            value={sliderValue}
            min={isPercentage ? min * 100 : min} // Adjust the range based on isPercentage
            max={isPercentage ? max * 100 : max}
            step={isPercentage ? step * 100 : step} // Adjust step if needed
            onChange={handleSliderChange}
            onMouseEnter={() => setShowTooltip(true)}
            onMouseLeave={() => setShowTooltip(false)}
        >
            <SliderTrack>
                <SliderFilledTrack/>
            </SliderTrack>
            <Tooltip
                bg='blue.500'
                color='white'
                placement='top'
                isOpen={showTooltip}
                label={`${sliderValue}${sufix}`}
            >
                <SliderThumb/>
            </Tooltip>
        </Slider>
    );
};

export default DefaultSlider;