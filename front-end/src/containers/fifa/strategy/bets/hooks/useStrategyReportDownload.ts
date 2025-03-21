import {getStrategyDetailedReport, getStrategySimpleReport} from 'services/strategyService';
import useTranslation from 'hooks/useTranslation';
import {useErrorToast} from 'hooks/useErrorToast';
import {generateExcel} from 'modules/excel/excelUtils';

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
  width?: number;
}

interface MetricItem {
  name: string;
  key: keyof TrendScopeAnalysis;
}

const baseColumns: ExcelColumn[] = [
  {header: 'Bet Time', key: 'betTime', width: 18},
  {header: 'Match Time', key: 'matchTime', width: 18},
  {header: 'League', key: 'leagueName', width: 20},
  {header: 'Home Team', key: 'homeName', width: 20},
  {header: 'Away Team', key: 'awayName', width: 20},
  {header: 'Home Score', key: 'homeScore', width: 12},
  {header: 'Away Score', key: 'awayScore', width: 12},
  {header: 'Line', key: 'line', width: 20},
  {header: 'Handicap', key: 'handicap', width: 12},
  {header: 'Odds', key: 'odds', width: 10},
  {header: 'Status', key: 'status', width: 12},
  {header: 'Profit', key: 'profit', width: 12}
];

const getLineMetrics = (line: string): MetricItem[] => {
  if (line === 'HOME') {
    return [
      {name: 'Probability', key: 'homePlayerProbability'},
      {name: 'Fair Line', key: 'homePlayerFairLine'},
      {name: 'Juice', key: 'homePlayerJuice'}
    ];
  }

  if (line === 'AWAY') {
    return [
      {name: 'Probability', key: 'awayPlayerProbability'},
      {name: 'Fair Line', key: 'awayPlayerFairLine'},
      {name: 'Juice', key: 'awayPlayerJuice'}
    ];
  }

  switch (line) {
    case 'ASIAN_OVER_GOALS':
      return [
        {name: 'Probability', key: 'overProbability'},
        {name: 'Fair Line', key: 'overFairLine'},
        {name: 'Juice', key: 'overJuice'}
      ];
    case 'ASIAN_UNDER_GOALS':
      return [
        {name: 'Probability', key: 'underProbability'},
        {name: 'Fair Line', key: 'underFairLine'},
        {name: 'Juice', key: 'underJuice'}
      ];
    case 'DRAW':
      return [
        {name: 'Probability', key: 'drawProbability'},
        {name: 'Fair Line', key: 'drawFairLine'},
        {name: 'Juice', key: 'drawJuice'}
      ];
    default:
      return [
        {name: 'Probability', key: 'overProbability'},
        {name: 'Fair Line', key: 'overFairLine'},
        {name: 'Juice', key: 'overJuice'}
      ];
  }
};

const createAnalysisFields = (analysis: TrendScopeAnalysis, index: number, line: string) => {
  const analysisNumber = `Analysis ${index + 1}`;
  const metrics = getLineMetrics(line);

  const result: Record<string, any> = {
    [`Matchup - ${analysisNumber}`]: analysis.matchup,
    [`Type - ${analysisNumber}`]: analysis.type,
    [`Total Matches - ${analysisNumber}`]: analysis.totalMatches,
  };

  metrics.forEach(metric => {
    result[`${metric.name} - ${analysisNumber}`] = analysis[metric.key];
  });

  return result;
};

const flattenBetWithAnalysis = (bet: Bet) => {
  const baseBet = {...bet, trendScopeAnalysis: undefined};

  const analysisFields = bet.trendScopeAnalysis?.reduce((acc, analysis, index) => ({
    ...acc,
    ...createAnalysisFields(analysis, index, bet.line)
  }), {});

  return {...baseBet, ...analysisFields};
};

const getColumnWidthByHeaderName = (header: string): number => {
  if (header.includes('Matchup')) return 25;
  if (header.includes('Type')) return 15;
  if (header.includes('Total Matches')) return 15;
  if (header.includes('Probability')) return 15;
  if (header.includes('Fair Line')) return 15;
  if (header.includes('Juice')) return 15;
  return Math.max(header.length + 2, 10);
};

const extractAnalysisColumns = (flattenedBets: Record<string, any>[]): ExcelColumn[] => {
  const uniqueKeys = Array.from(
    new Set(
      flattenedBets.flatMap(bet =>
        Object.keys(bet).filter(key => key.includes(' - Analysis '))
      )
    )
  );

  const metricOrder = [
    'Matchup',
    'Type',
    'Total Matches',
    'Probability',
    'Home Probability',
    'Away Probability',
    'Draw Probability',
    'Fair Line',
    'Home Fair Line',
    'Away Fair Line',
    'Draw Fair Line',
    'Juice',
    'Home Juice',
    'Away Juice',
    'Draw Juice'
  ];

  const analysisSets: Record<string, string[]> = {};

  uniqueKeys.forEach(key => {
    const match = key.match(/- Analysis (\d+)$/);
    if (match) {
      const analysisNumber = match[1];
      if (!analysisSets[analysisNumber]) {
        analysisSets[analysisNumber] = [];
      }
      analysisSets[analysisNumber].push(key);
    }
  });

  Object.keys(analysisSets).forEach(analysisNumber => {
    analysisSets[analysisNumber].sort((a, b) => {
      const metricA = a.split(' - Analysis')[0];
      const metricB = b.split(' - Analysis')[0];

      return metricOrder.indexOf(metricA) - metricOrder.indexOf(metricB);
    });
  });

  const orderedKeys: string[] = [];
  const analysisNumbers = Object.keys(analysisSets).sort((a, b) => Number(a) - Number(b));

  metricOrder.forEach(metric => {
    analysisNumbers.forEach(analysisNum => {
      const key = analysisSets[analysisNum].find(k => k.startsWith(metric));
      if (key) orderedKeys.push(key);
    });
  });

  return orderedKeys.map(key => ({
    header: key,
    key,
    width: getColumnWidthByHeaderName(key)
  }));
};

export const useStrategyReportDownload = () => {
  const {t} = useTranslation();

  const downloadSimpleReport = useErrorToast(
    async (strategyId: string, days: number) => {
      const {bets} = await getStrategySimpleReport(strategyId, days);
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
      const {bets} = await getStrategyDetailedReport(strategyId, days);
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