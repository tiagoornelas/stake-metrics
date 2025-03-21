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

const createProfitByLineSheet = (bets: Bet[]) => {
  const profitByLine = bets.reduce((acc, bet) => {
    const line = bet.line;
    if (!acc[line]) {
      acc[line] = 0;
    }
    acc[line] += bet.profit || 0;
    return acc;
  }, {} as Record<string, number>);

  const profitByLeague = bets.reduce((acc, bet) => {
    const league = bet.leagueName;
    if (!acc[league]) {
      acc[league] = 0;
    }
    acc[league] += bet.profit || 0;
    return acc;
  }, {} as Record<string, number>);

  const profitByPlayer = bets.reduce((acc, bet) => {
    if (!acc[bet.homeName]) {
      acc[bet.homeName] = 0;
    }
    acc[bet.homeName] += bet.profit || 0;

    if (!acc[bet.awayName]) {
      acc[bet.awayName] = 0;
    }
    acc[bet.awayName] += bet.profit || 0;

    return acc;
  }, {} as Record<string, number>);

  const lineEntries = Object.entries(profitByLine)
    .sort((a, b) => b[1] - a[1])
    .map(([line, profit]) => ({Line: line, Profit: profit}));

  const leagueEntries = Object.entries(profitByLeague)
    .sort((a, b) => b[1] - a[1])
    .map(([league, profit]) => ({League: league, Profit: profit}));

  const playerEntries = Object.entries(profitByPlayer)
    .sort((a, b) => b[1] - a[1])
    .map(([player, profit]) => ({Player: player, Profit: profit}));

  const maxLength = Math.max(
    lineEntries.length,
    leagueEntries.length,
    playerEntries.length
  );

  const data = Array.from({length: maxLength}, (_, i) => ({
    Line: lineEntries[i]?.Line || '',
    'Profit By Line': lineEntries[i]?.Profit || '',
    League: leagueEntries[i]?.League || '',
    'Profit By League': leagueEntries[i]?.Profit || '',
    Player: playerEntries[i]?.Player || '',
    'Profit By Player': playerEntries[i]?.Profit || ''
  }));

  return {
    name: 'Profit Breakdown',
    data,
    columns: [
      {header: 'Line', key: 'Line', width: 20},
      {header: 'Profit By Line', key: 'Profit By Line', width: 15},
      {header: 'League', key: 'League', width: 20},
      {header: 'Profit By League', key: 'Profit By League', width: 15},
      {header: 'Player', key: 'Player', width: 20},
      {header: 'Profit By Player', key: 'Profit By Player', width: 15}
    ]
  };
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
      const profitByLineSheet = createProfitByLineSheet(bets);

      generateExcel(
        flattenedBets,
        [...baseColumns, ...analysisColumns],
        `stake_metrics_strategy_${strategyId}_detailed`,
        [profitByLineSheet]
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