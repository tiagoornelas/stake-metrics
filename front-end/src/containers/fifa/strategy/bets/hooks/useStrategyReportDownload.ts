import { getStrategySimpleReport, getStrategyDetailedReport } from 'services/strategyService';
import useTranslation from 'hooks/useTranslation';
import { useErrorToast } from 'hooks/useErrorToast';
import { generateExcel } from 'modules/excel/excelUtils';

interface Bet {
    betTime: string;
    matchTime: string;
    leagueName: string;
    homeName: string;
    awayName: string;
    homeScore: string;
    awayScore: string;
    line: string;
    handicap: string;
    odds: string;
    status: string;
    profit: number;
    trendScopeAnalysis?: TrendScopeAnalysis[];
}

interface TrendScopeAnalysis {
    matchup: string;
    type: string;
    totalMatches: number;
    homePlayerProbability: number;
    homePlayerFairLine: number;
    homePlayerJuice: number;
    drawProbability: number;
    drawFairLine: number;
    drawJuice: number;
    awayPlayerProbability: number;
    awayPlayerFairLine: number;
    awayPlayerJuice: number;
    overProbability: number;
    overFairLine: number;
    overJuice: number;
    underProbability: number;
    underFairLine: number;
    underJuice: number;
}

interface ExcelColumn {
    header: string;
    key: string;
}

const baseColumns: ExcelColumn[] = [
    { header: 'Bet Time', key: 'betTime' },
    { header: 'Match Time', key: 'matchTime' },
    { header: 'League', key: 'leagueName' },
    { header: 'Home Team', key: 'homeName' },
    { header: 'Away Team', key: 'awayName' },
    { header: 'Home Score', key: 'homeScore' },
    { header: 'Away Score', key: 'awayScore' },
    { header: 'Line', key: 'line' },
    { header: 'Handicap', key: 'handicap' },
    { header: 'Odds', key: 'odds' },
    { header: 'Status', key: 'status' },
    { header: 'Profit', key: 'profit' }
];

const createAnalysisFields = (analysis: TrendScopeAnalysis, index: number) => {
    const prefix = `Analysis ${index + 1}`;
    return {
        [`${prefix} - Matchup`]: analysis.matchup,
        [`${prefix} - Type`]: analysis.type,
        [`${prefix} - Total Matches`]: analysis.totalMatches,
        [`${prefix} - Home Probability`]: analysis.homePlayerProbability,
        [`${prefix} - Home Fair Line`]: analysis.homePlayerFairLine,
        [`${prefix} - Home Juice`]: analysis.homePlayerJuice,
        [`${prefix} - Draw Probability`]: analysis.drawProbability,
        [`${prefix} - Draw Fair Line`]: analysis.drawFairLine,
        [`${prefix} - Draw Juice`]: analysis.drawJuice,
        [`${prefix} - Away Probability`]: analysis.awayPlayerProbability,
        [`${prefix} - Away Fair Line`]: analysis.awayPlayerFairLine,
        [`${prefix} - Away Juice`]: analysis.awayPlayerJuice,
        [`${prefix} - Over Probability`]: analysis.overProbability,
        [`${prefix} - Over Fair Line`]: analysis.overFairLine,
        [`${prefix} - Over Juice`]: analysis.overJuice,
        [`${prefix} - Under Probability`]: analysis.underProbability,
        [`${prefix} - Under Fair Line`]: analysis.underFairLine,
        [`${prefix} - Under Juice`]: analysis.underJuice
    };
};

const flattenBetWithAnalysis = (bet: Bet) => {
    const baseBet = { ...bet, trendScopeAnalysis: undefined };
    const analysisFields = bet.trendScopeAnalysis?.reduce((acc, analysis, index) => ({
        ...acc,
        ...createAnalysisFields(analysis, index)
    }), {});

    return { ...baseBet, ...analysisFields };
};

const extractAnalysisColumns = (flattenedBets: Record<string, any>[]): ExcelColumn[] => 
    Array.from(
        new Set(
            flattenedBets.flatMap(bet => 
                Object.keys(bet).filter(key => key.startsWith('Analysis'))
            )
        )
    ).map(key => ({ header: key, key }));

export const useStrategyReportDownload = () => {
    const { t } = useTranslation();

    const downloadSimpleReport = useErrorToast(
        async (strategyId: string, days: number) => {
            const { bets } = await getStrategySimpleReport(strategyId, days);
            generateExcel(bets, baseColumns, `stake_metrics_strategy_${strategyId}_simple`);
            return true;
        },
        {
            title: t('strategy.report.downloadSuccess.title'),
            description: t('strategy.report.downloadSuccess.description')
        },
        null,
        {
            title: t('strategy.report.downloadInProgress.title'),
            description: t('strategy.report.downloadInProgress.description')
        }
    );

    const downloadDetailedReport = useErrorToast(
        async (strategyId: string, days: number) => {
            const { bets } = await getStrategyDetailedReport(strategyId, days);
            const flattenedBets = bets.map(flattenBetWithAnalysis);
            const analysisColumns = extractAnalysisColumns(flattenedBets);
            
            generateExcel(
                flattenedBets,
                [...baseColumns, ...analysisColumns],
                `stake_metrics_strategy_${strategyId}_detailed`
            );
            return true;
        },
        {
            title: t('strategy.report.downloadSuccess.title'),
            description: t('strategy.report.downloadSuccess.description')
        },
        null,
        {
            title: t('strategy.report.downloadInProgress.title'),
            description: t('strategy.report.downloadInProgress.description')
        }
    );

    return { 
        downloadSimpleReport, 
        downloadDetailedReport
    };
};
