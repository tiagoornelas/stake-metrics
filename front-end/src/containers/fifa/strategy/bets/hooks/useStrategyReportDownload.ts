import { useState } from 'react';
import { getStrategySimpleReport } from 'services/strategyService';
import useTranslation from 'hooks/useTranslation';
import { useErrorToast } from 'hooks/useErrorToast';
import { generateExcel } from 'modules/excel/excelUtils';

export const useStrategyReportDownload = () => {
    const [isDownloadingReport, setIsDownloadingReport] = useState(false);
    const { t } = useTranslation();

    const downloadSimpleReport = useErrorToast(
        async (strategyId: string) => {
            setIsDownloadingReport(true);
            try {
                const { bets } = await getStrategySimpleReport(strategyId);

                const excelColumns = [
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

                generateExcel(
                    bets,
                    excelColumns,
                    `stake_metrics_strategy_${strategyId}_simple`
                );

                return true;
            } finally {
                setIsDownloadingReport(false);
            }
        },
        {
            title: t('strategy.report.downloadSuccess.title'),
            description: t('strategy.report.downloadSuccess.description')
        }
    );

    const downloadDetailedReport = useErrorToast(
        async (strategyId: string) => {
            setIsDownloadingReport(true);
            try {
                // TODO: Implement detailed report download service method
                throw new Error('detailed_report_not_implemented');
            } finally {
                setIsDownloadingReport(false);
            }
        },
        null,
        () => {
            // Fallback for detailed report
            alert(t('strategy.report.detailed.comingSoon'));
        }
    );

    return { 
        downloadSimpleReport, 
        downloadDetailedReport, 
        isDownloadingReport 
    };
};
