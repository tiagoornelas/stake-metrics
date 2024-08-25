import {Box, Button, Flex, Heading, Highlight, Image, Tag, Text, useBreakpointValue} from "@chakra-ui/react";
import PublicBanner from "assets/PublicBanner.png";
import SettingsWireframe from "assets/SettingsWireframe.png";
import TelegramWireframe from "assets/TelegramWireframe.png";
import ProductFooter from "components/ProductFooter";
import PublicHeader from "containers/public/components/PublicHeader";
import {differenceInDays} from 'date-fns';
import React, {Fragment} from 'react';
import {GoTelescope} from "react-icons/all";
import {useNavigate} from "react-router-dom";

const daysValidatingDataBase = differenceInDays(new Date(), new Date(2023, 12, 5));

const firstSectionFeatures = [
    {
        text: "Estratégias para E-Soccer Bet365 e mais por vir!",
        emoji: "⚽"
    },
    {
        text: "Receba apostas com links e acompanhe os resultados, tudo pelo Telegram!",
        emoji: "✅"
    },
    {
        text: "Receba suas apostas em até 3 chats ou canais!",
        emoji: "🤝"
    },
    {
        text: "Configure um tempo pra você pegar sua aposta antes de compartilhar com outros chats!",
        emoji: "⏳"
    },
    {
        text: "Escolha compartilhar só algumas apostas com determinado chat!",
        emoji: "🧱"
    },
];

const secondSessionFeatures = [
    {
        text: "Envie relatórios automáticos para as apostas de cada chat!",
        emoji: "📊",
        tagColor: "blue",
        tagText: "Em breve"
    },
    {
        text: "Acompanhe visualmente as tendências das ligas de E-Soccer!",
        emoji: "📈",
        tagColor: "blue",
        tagText: "Em breve"
    },
    {
        text: "Monitore ligas e descubra parâmetros vencedores!",
        emoji: "🏆",
        tagColor: "blue",
        tagText: "Em breve"
    },
    {
        text: "Evite a pirataria: sistema próprio anti-repasse!",
        emoji: "🏴‍☠️",
        tagColor: "blue",
        tagText: "Em breve"
    },
    {
        text: "Suas estratégias são privadas e nunca serão vendidas!",
        emoji: "🔒"
    },
    {
        text: `Saia da manada com nossos dados próprios já validados por ${daysValidatingDataBase} dias!`,
        emoji: "🐘"
    },
];

const FeatureList = ({features}: {
    features: { text: string, emoji: string, tagColor?: string, tagText?: string }[]
}) => {
    const tagText = useBreakpointValue({base: "Breve", md: "Em breve"});

    return (
        <>
            {features.map((feature, index) => (
                <Fragment key={index}>
                    <Flex alignItems="center" justifyContent="center">
                        <Box display="inline-flex" alignItems="center">
                            {feature.tagColor && feature.tagText && (
                                <Tag colorScheme={feature.tagColor} mr={2} minWidth="fit-content" isTruncated>
                                    {tagText}
                                </Tag>
                            )}
                            <Box as="span">
                                {feature.text} {feature.emoji}
                            </Box>
                        </Box>
                    </Flex>
                    {index < features.length - 1 && <Box mt={2}/>}
                </Fragment>
            ))}
        </>
    );
};

const PublicPage = () => {
    const navigate = useNavigate();
    const goToAccountCreation = () => navigate("/create-account", {replace: true});

    return (
        <Fragment>
            <PublicHeader/>
            <Box p={12}>
                <Flex my={8} direction={"column"} alignItems={"center"} gap={6}>
                    <Heading lineHeight='tall' textAlign={"center"}>
                        <Highlight
                            query={["confiança", "dados", "resultado"]}
                            styles={{px: '2', py: '1', rounded: 'full', bg: 'yellow.100'}}
                        >
                            Aposte com confiança: crie estratégias baseadas em dados, ganhe tempo e tenha mais
                            resultado!
                        </Highlight>
                    </Heading>
                    <Text fontSize={"xl"} textAlign={"center"} width={"85%"}>
                        O Stake Metrics é uma ferramenta que busca oportunidades e as envia para você e seus canais.
                        🔔<br/>
                        Analise e defina suas estratégias com base em dados, acompanhe as tendências e tome decisões. 🎯
                    </Text>
                    <Button colorScheme={"yellow"} rightIcon={<GoTelescope/>} onClick={goToAccountCreation}>Comece
                        já!</Button>
                    <Box width={{base: "xs", md: "md", lg: "xl"}} mt={4}>
                        <Image src={PublicBanner} alt="App screen"/>
                    </Box>
                </Flex>

                <Flex my={8} alignItems={"center"} gap={6} maxWidth="1280px" mx="auto">
                    <Flex width={{base: "0%", md: "35%"}} justifyContent={"center"}
                          display={{base: "none", md: "flex"}}>
                        <Box width="220px" mt={4}>
                            <Image src={SettingsWireframe} alt="App screen"/>
                        </Box>
                    </Flex>
                    <Flex width={{base: "100%", md: "65%"}} justifyContent={"center"}>
                        <Text fontSize={"xl"} textAlign={"center"} width={"85%"}>
                            <FeatureList features={firstSectionFeatures}/>
                        </Text>
                    </Flex>
                </Flex>

                <Flex my={8} alignItems={"center"} gap={6} maxWidth="1280px" mx="auto">
                    <Flex width={{base: "100%", md: "65%"}} justifyContent={"center"}>
                        <Text fontSize={"xl"} textAlign={"center"} width={"85%"}>
                            <FeatureList features={secondSessionFeatures}/>
                        </Text>
                    </Flex>
                    <Flex width={{base: "0%", md: "35%"}} justifyContent={"center"}
                          display={{base: "none", md: "flex"}}>
                        <Box width="220px" mt={4}>
                            <Image src={TelegramWireframe} alt="App screen"/>
                        </Box>
                    </Flex>
                </Flex>
            </Box>
            <ProductFooter/>
        </Fragment>
    );
};

export default PublicPage;
