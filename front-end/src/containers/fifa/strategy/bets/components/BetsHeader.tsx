import {Flex, Menu, MenuButton, MenuList, MenuItem, IconButton, Skeleton, Text, Tag} from "@chakra-ui/react";
import MultiSelect from "components/MultiSelect";
import React, {useEffect, useState} from 'react';
import {getLeagues} from "services/leagueService";
import {BetQueryFilters, Option} from "utils/interfaces";
import {BsFileSpreadsheet} from "react-icons/all";
import { useTranslation } from "hooks/useTranslation";

const ReportMenu = () => {
    const { t } = useTranslation();

    return <Menu>
    <MenuButton as={IconButton} icon={<BsFileSpreadsheet />} />
    <MenuList>
        <MenuItem  disabled cursor={"not-allowed"}>
            {t("strategy.actions.downloadReport.simple")}
            <Tag ml={2} colorScheme={"blue"}>Em breve</Tag>
        </MenuItem>
        <MenuItem disabled cursor={"not-allowed"}>
            {t("strategy.actions.downloadReport.detailed")}
            <Tag ml={2} colorScheme={"blue"}>Em breve</Tag>
        </MenuItem>
    </MenuList>
    </Menu>
}

const BetsHeader = ({showingBets, betsLength, isLoaded, filters, setFilters}: {
    showingBets: number,
    betsLength: number,
    isLoaded: boolean,
    filters: BetQueryFilters,
    setFilters: (filters: BetQueryFilters) => void
}) => {
    const [leagues, setLeagues] = useState<Option[]>([]);
    const [isLoadingLeagues, setIsLoadingLeagues] = useState(true);

    useEffect(() => {
        const fetchLeagues = async () => {
            setIsLoadingLeagues(true);
            const leagues = await getLeagues();
            setLeagues(leagues);
            setIsLoadingLeagues(false);
        }

        fetchLeagues();
    }, []);

    const getLabel = () => {
        if (betsLength === 0) return "";
        if (betsLength === 1) return "1 aposta";
        if (showingBets >= betsLength) return `${betsLength} apostas`;
        return `${showingBets} de ${betsLength} apostas`;
    };

    return (
        <Flex
            direction={{base: "column", md: "row"}}
            align={"center"}
            justifyContent={"space-between"}
            gap={2}
        >
            <Skeleton isLoaded={isLoaded} display={{base: "none", md: "block"}}>
                <Text>{getLabel()}</Text>
            </Skeleton>
            <Flex
                direction={{base: "column", md: "row"}}
                gap={2}
                w={{base: "100%", md: "auto"}}
            >
                <ReportMenu />
                <MultiSelect
                    title="Situação"
                    options={[
                        {value: 'openBets', label: 'Abertas'},
                        {value: 'closedBets', label: 'Fechadas'}
                    ]}
                    onChange={(value) => setFilters({
                        ...filters,
                        openBets: value.includes('openBets'),
                        closedBets: value.includes('closedBets')
                    })}
                    defaultSelected={['openBets', 'closedBets'].filter(option => filters[option])}
                />
                <MultiSelect
                    title="Tipo"
                    options={[
                        {value: 'realBets', label: 'Reais'},
                        {value: 'paperBets', label: 'Paper Bets'}
                    ]}
                    onChange={(value) => setFilters({
                        ...filters,
                        realBets: value.includes('realBets'),
                        paperBets: value.includes('paperBets')
                    })}
                    defaultSelected={['realBets', 'paperBets'].filter(option => filters[option])}
                />
                <Skeleton isLoaded={!isLoadingLeagues}>
                    <MultiSelect
                        title="Ligas"
                        options={leagues}
                        onChange={(value) => setFilters({...filters, league: value})}
                        defaultSelected={isLoadingLeagues ? [] : leagues.map(league => league.value)}
                    />
                </Skeleton>
            </Flex>
        </Flex>
    );
};

export default BetsHeader;
