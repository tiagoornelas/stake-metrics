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
import useTranslation from '../../hooks/useTranslation';

const daysValidatingDataBase = differenceInDays(new Date(), new Date(2023, 12, 5));

const FeatureList = ({features}: {
    features: { text: string, emoji: string, tagColor?: string, tagText?: string }[]
}) => {
    const { t } = useTranslation();
    const tagText = useBreakpointValue({base: t('common.soon.short'), md: t('common.soon.full')});

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
    const { t } = useTranslation();
    const navigate = useNavigate();
    const goToAccountCreation = () => navigate("/create-account", {replace: true});

    const firstSectionFeatures = [
        {
            text: t('publicPage.firstSectionFeatures.0'),
            emoji: "⚽"
        },
        {
            text: t('publicPage.firstSectionFeatures.1'),
            emoji: "✅"
        },
        {
            text: t('publicPage.firstSectionFeatures.2'),
            emoji: "🤝"
        },
        {
            text: t('publicPage.firstSectionFeatures.3'),
            emoji: "⏳"
        },
        {
            text: t('publicPage.firstSectionFeatures.4'),
            emoji: "🧱"
        }
    ];

    const secondSessionFeatures = [
        {
            text: t('publicPage.secondSectionFeatures.0'),
            emoji: "📊"
        },
        {
            text: t('publicPage.secondSectionFeatures.1'),
            emoji: "📈"
        },
        {
            text: t('publicPage.secondSectionFeatures.2'),
            emoji: "📎"
        },
        {
            text: t('publicPage.secondSectionFeatures.3', { days: daysValidatingDataBase }),
            emoji: "🐘"
        },
        {
            text: t('publicPage.secondSectionFeatures.4'),
            emoji: "🔒"
        }
    ];

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
                            {t('publicPage.heading')}
                        </Highlight>
                    </Heading>
                    <Text fontSize={"xl"} textAlign={"center"} width={"85%"}>
                        {t('publicPage.description')}
                    </Text>
                    <Button 
                        colorScheme={"yellow"} 
                        rightIcon={<GoTelescope/>} 
                        onClick={goToAccountCreation}
                    >
                        {t('publicPage.buttonText')}
                    </Button>
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
