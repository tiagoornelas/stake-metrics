import {
    Button,
    Divider,
    Flex,
    Icon,
    Input,
    Menu,
    MenuButton,
    MenuItemOption,
    MenuList,
    MenuOptionGroup,
    Tag,
    Text,
    useColorMode
} from "@chakra-ui/react";
import React, {useEffect, useState} from "react";
import {BiListCheck, BiSearch} from "react-icons/all";
import {Option} from "utils/interfaces";

type Props = {
    title: string;
    options: string[] | Option[];
    onChange: (value: string[]) => void;
    entity?: string[];
    tagColor?: "green" | "blue" | "yellow" | "red";
    defaultSelected?: string[];
    isInvalid?: boolean;
    sortSelected?: boolean;
    [x: string]: any;
};

const sortOptions = (options: Option[], selectedOptions: string[], sortSelected: boolean): Option[] => {
    if (!sortSelected) return options;
    return options.sort((a, b) => {
        const aSelected = selectedOptions.includes(a.value);
        const bSelected = selectedOptions.includes(b.value);
        if (aSelected && !bSelected) return -1;
        if (!aSelected && bSelected) return 1;
        return 0;
    });
};

const MultiSelect = ({
                         title,
                         options,
                         onChange,
                         entity = [],
                         tagColor = "blue",
                         defaultSelected = [],
                         isInvalid,
                         sortSelected = false,
                         ...rest
                     }: Props) => {
    const [selectedOptions, setSelectedOptions] = useState<string[]>(defaultSelected);
    const [searchInput, setSearchInput] = useState<string>("");

    const {colorMode} = useColorMode();
    const focusBg = colorMode === "dark" ? "gray.900" : "gray.100";
    const hoverBg = colorMode === "dark" ? "gray.900" : "gray.100";

    const normalizedOptions: Option[] = Array.isArray(options) && typeof options[0] === "string"
        ? (options as string[]).map(opt => ({value: opt, label: opt}))
        : (options as Option[]);

    useEffect(() => {
        const validSelectedOptions = selectedOptions.filter(option =>
            normalizedOptions.some(normalizedOption => normalizedOption.value === option)
        );
        if (validSelectedOptions.length !== selectedOptions.length) {
            setSelectedOptions(validSelectedOptions);
            onChange(validSelectedOptions);
        }
    }, [options, selectedOptions, normalizedOptions, onChange]);

    const handleOptionSelect = (value: string | string[]) => {
        const list = Array.isArray(value) ? value : [value];
        setSelectedOptions(list);
        onChange(list);
    };

    const handleClear = () => {
        setSelectedOptions([]);
        onChange([]);
    };

    const getBadgeLabel = () => {
        switch (entity.length) {
            case 0:
                return selectedOptions.length;
            case 1:
                return `${selectedOptions.length} ${entity[0]}`;
            default:
                return selectedOptions.length === 1 ? `1 ${entity[0]}` : `${selectedOptions.length} ${entity[1]}`;
        }
    };

    const handleSearchInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setSearchInput(event.target.value);
    };

    const filterOptions = (options: Option[], searchInput: string): Option[] => {
        return options.filter(option =>
            option.label.toLowerCase().includes(searchInput.toLowerCase())
        );
    };

    const filteredOptions = filterOptions(normalizedOptions, searchInput);
    const sortedOptions = sortOptions(filteredOptions, selectedOptions, sortSelected);

    return (
        <Flex direction={"column"} {...rest}>
            <Flex gap="4" align={"center"}>
                <Menu closeOnSelect={false}>
                    <MenuButton
                        as={Button}
                        colorScheme={isInvalid ? "red" : "gray"}
                        variant={"outline"}
                        rightIcon={<Icon as={BiListCheck}/>}
                        w={"100%"}
                    >
                        <Flex align="center" gap="2">
                            {selectedOptions.length > 0 && <Tag bg={`${tagColor}.600`} color="white" py={1} px={2}>
                                {getBadgeLabel()}
                            </Tag>}

                            <Text noOfLines={1}>{title}</Text>
                        </Flex>
                    </MenuButton>
                    <MenuList maxH="200px" overflowY="auto" minW="100%">
                        <MenuOptionGroup
                            value={selectedOptions}
                            onChange={handleOptionSelect}
                            type="checkbox"
                        >
                            <Flex direction={"column"} gap="2" px="4" py="2">

                                <Flex justify={"space-between"} align="center">
                                    <Flex gap="2" align={"center"}>
                                        <Icon as={BiSearch}/>
                                        <Input
                                            variant={"unstyled"}
                                            placeholder={"Pesquisar..."}
                                            value={searchInput}
                                            onChange={handleSearchInputChange}
                                        />
                                    </Flex>
                                    <Flex flexShrink={0} gap="2">
                                        <Button
                                            size="xs"
                                            variant="link"
                                            colorScheme="blue"
                                            onClick={() => handleOptionSelect(normalizedOptions.map(opt => opt.value))}
                                        >
                                            Todos
                                        </Button>

                                        <Button
                                            size="xs"
                                            variant="link"
                                            colorScheme="blue"
                                            onClick={handleClear}
                                        >
                                            Limpar
                                        </Button>
                                    </Flex>
                                </Flex>
                            </Flex>

                            {normalizedOptions.length > 5 && <Divider/>}

                            {sortedOptions.map((option) => (
                                <MenuItemOption
                                    key={option.value}
                                    value={option.value}
                                    _focus={{bg: focusBg}}
                                    _hover={{bg: hoverBg}}
                                >
                                    {option.label}
                                </MenuItemOption>
                            ))}
                        </MenuOptionGroup>
                    </MenuList>
                </Menu>
            </Flex>
        </Flex>
    );
};

export default MultiSelect;
