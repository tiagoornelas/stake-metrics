import {Flex, Tag} from "@chakra-ui/react";

const ProductName = () => <Flex direction={"row"} gap={2} justifyContent={"center"} alignItems={"center"}>
    Stake Metrics
    <Tag colorScheme={"yellow"}>Beta</Tag>
</Flex>;

export default ProductName;
