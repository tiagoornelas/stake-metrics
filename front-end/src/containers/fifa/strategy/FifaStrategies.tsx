import {
    Box,
    Flex,
    Skeleton,
    Spinner,
    Table,
    TableContainer,
    Tbody,
    Text,
    Th,
    Thead,
    Tr,
    useBreakpointValue
} from "@chakra-ui/react";
import Modal from "components/Modal";
import StrategyEmptyState from "containers/fifa/strategy/components/StrategyEmptyState";
import StrategyForm from "containers/fifa/strategy/components/StrategyForm";
import StrategyTableItem from "containers/fifa/strategy/components/StrategyTableItem";
import useStrategyStatisticsQuery from "containers/fifa/strategy/hooks/useStrategyStatisticsQuery";
import useThemeColors from "hooks/useThemeColors";
import {GoTelescope} from "react-icons/all";
import {StrategyListItem} from "utils/interfaces";

const NewStrategyButton = () => {
    return (
        <Modal buttonText="Nova estratégia" title="Nova estratégia" noFooter icon={<GoTelescope/>} colorScheme="blue">
            <StrategyForm/>
        </Modal>
    );
};

const Header = ({strategiesLength, isLoaded}: { strategiesLength: number; isLoaded: boolean }) => {
    const getLabel = () => {
        switch (strategiesLength) {
            case 0:
                return "";
            case 1:
                return "1 estratégia";
            default:
                return `${strategiesLength} estratégias`;
        }
    };

    return (
        <Flex align={"center"} justifyContent={"space-between"}>
            <Skeleton isLoaded={isLoaded}>
                <Text>{getLabel()}</Text>
            </Skeleton>
            <Flex gap={2}>
                <NewStrategyButton/>
            </Flex>
        </Flex>
    );
};

const FifaStrategies = () => {
    const {data: strategies = [], isLoading} = useStrategyStatisticsQuery();
    const colors = useThemeColors();
    const shouldRenderEmptyState = !isLoading && strategies.length === 0;
    const isLargerThanLg = useBreakpointValue({base: false, lg: true});

    return (
        <Box mt={4}>
            <Header strategiesLength={strategies.length} isLoaded={!isLoading}/>
            <TableContainer mt={8} sx={{
                '::-webkit-scrollbar': {
                    height: '2px',
                },
                '::-webkit-scrollbar-thumb': {
                    background: colors.product,
                    borderRadius: '2px',
                },
                '::-webkit-scrollbar-track': {
                    background: 'transparent',
                },
            }}>
                <Table variant="simple">
                    <Thead>
                        <Tr>
                            <Th>Estratégia</Th>
                            <Th w={"80px"} textAlign={"center"}>
                                Abertas
                            </Th>
                            <Th w={"80px"} textAlign={"center"}>
                                Entradas
                            </Th>
                            <Th w={"80px"} textAlign={"center"}>
                                Un
                            </Th>
                            <Th w={"80px"} textAlign={"center"}>
                                ROI
                            </Th>
                            {isLargerThanLg && (
                                <>
                                    <Th w={"80px"} textAlign={"center"}>
                                        Un Ativas
                                    </Th>
                                    <Th w={"80px"} textAlign={"center"}>
                                        ROI Ativo
                                    </Th>
                                </>
                            )}
                            <Th w={"210px"}/>
                        </Tr>
                    </Thead>
                    {!isLoading && (
                        <Tbody>
                            {strategies.map((strategy: StrategyListItem) => (
                                <StrategyTableItem key={strategy.id} strategy={strategy}/>
                            ))}
                        </Tbody>
                    )}
                </Table>
            </TableContainer>
            {isLoading && (
                <Flex justifyContent={"center"} w={"100%"} mt={8}>
                    <Spinner thickness="4px" speed="0.65s" emptyColor="gray.200" color={colors.product} size="xl"/>
                </Flex>
            )}
            {shouldRenderEmptyState && <StrategyEmptyState/>}
        </Box>
    );
};

export default FifaStrategies;
