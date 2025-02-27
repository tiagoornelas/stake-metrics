import {
  Button,
  Flex,
  Heading,
  Text,
} from "@chakra-ui/react";
import { FaHandsHelping } from "react-icons/fa";

const SupportSection = () => {
  type LanguageLink = {
    link: string;
  };

  const HELP_CENTRAL_LINKS: { [key: string]: LanguageLink } = {
    pt: { link: "https://pyrite-seaplane-54b.notion.site/1a4532044a398000a301cdb8caffd12d?v=1a4532044a39801383de000c6957235b&pvs=4" },
    en: { link: "https://pyrite-seaplane-54b.notion.site/1a4532044a398071936df102a625253b?v=1a4532044a3981168062000ce6449b65&pvs=4" },
  };

  const handleClick = (language: keyof typeof HELP_CENTRAL_LINKS) => {
    window.open(HELP_CENTRAL_LINKS[language].link, "_blank");
  };

  return (
    <Flex direction="column" mb={4}>
      <Flex gap={4} alignItems="center">
        <Heading size="md" mb={2}>
          Central de ajuda
        </Heading>
      </Flex>
      <Text>{`Acesse nossa central de ajuda para aprender mais sobre apostas e como usar o Stake Metrics.`}</Text>
      <Flex direction="column" gap={4} alignItems="self-start" mt={4}>
          <Button
            aria-label="support-btn"
            rightIcon={<FaHandsHelping />}
            onClick={() => handleClick("pt")}
          >
            🇧🇷 Português
          </Button>
          <Button
            aria-label="support-btn"
            rightIcon={<FaHandsHelping />}
            onClick={() => handleClick("en")}
          >
            🇺🇸 English
          </Button>
      </Flex>
    </Flex>
  );
};

export default SupportSection;
