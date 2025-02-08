import { Flex, Menu, MenuButton, MenuList, IconButton, Skeleton, Text, Button, Tooltip, useBreakpointValue } from "@chakra-ui/react";
import MultiSelect from "components/MultiSelect";
import React, { useEffect, useState } from 'react';
import { getLeagues } from "services/leagueService";
import { BetQueryFilters, Option } from "utils/interfaces";
import { BsFileSpreadsheet } from "react-icons/bs";
import { useStrategyReportDownload } from '../hooks/useStrategyReportDownload';
import { ReportMenuItem } from './ReportMenuItem';
import useTranslation from 'hooks/useTranslation';

const ReportMenu = ({ strategyId }: { strategyId: string }) => {
    const { downloadSimpleReport, downloadDetailedReport } = useStrategyReportDownload();
    const { t } = useTranslation();
    const buttonText = t('reports.generateReports');
    const isSmallScreen = useBreakpointValue({ base: true, md: false });

    return (
        <Menu>
            {isSmallScreen ? (
                <MenuButton as={Button} leftIcon={<BsFileSpreadsheet />}>
                    <Text>{buttonText}</Text>
                </MenuButton>
            ) : (
                <Tooltip label={buttonText} aria-label="Generate reports tooltip" placement="top">
                    <MenuButton as={IconButton} icon={<BsFileSpreadsheet />} />
                </Tooltip>
            )}
            <MenuList>
                <ReportMenuItem
                    reportType="simple"
                    strategyId={strategyId}
                    downloadReport={downloadSimpleReport}
                />
                <ReportMenuItem
                    reportType="detailed"
                    strategyId={strategyId}
                    downloadReport={downloadDetailedReport}
                />
            </MenuList>
        </Menu>
    );
}

const BetsHeader = ({ showingBets, betsLength, isLoaded, filters, setFilters, strategyId }: {
    showingBets: number,
    betsLength: number,
    isLoaded: boolean,
    filters: BetQueryFilters,
    setFilters: (filters: BetQueryFilters) => void,
    strategyId: string
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
            direction={{ base: "column", md: "row" }}
            align={"center"}
            justifyContent={"space-between"}
            gap={2}
        >
            <Skeleton isLoaded={isLoaded} display={{ base: "none", md: "block" }}>
                <Text>{getLabel()}</Text>
            </Skeleton>
            <Flex
                direction={{ base: "column", md: "row" }}
                gap={2}
                w={{ base: "100%", md: "auto" }}
            >
                <ReportMenu strategyId={strategyId} />
                <MultiSelect
                    title="Situação"
                    options={[
                        { value: 'openBets', label: 'Abertas' },
                        { value: 'closedBets', label: 'Fechadas' }
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
                        { value: 'realBets', label: 'Reais' },
                        { value: 'paperBets', label: 'Paper Bets' }
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
                        onChange={(value) => setFilters({ ...filters, league: value })}
                        defaultSelected={isLoadingLeagues ? [] : leagues.map(league => league.value)}
                    />
                </Skeleton>
            </Flex>
        </Flex>
    );
};

export default BetsHeader;
