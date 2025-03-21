import React from "react";
import { MenuItem, MenuGroup, Tag } from "@chakra-ui/react";
import useTranslation from "hooks/useTranslation";
import { hasFeature } from "utils/helpers/featureHelper";
import { FEATURES } from "utils/constants/featureConstants";
import { useUserState } from "context/UserContext";

export interface ReportMenuItemProps {
  reportType: "simple" | "detailed";
  strategyId: string;
  downloadReport: (strategyId: string, days: number) => Promise<void>;
}

export const ReportMenuItem: React.FC<ReportMenuItemProps> = ({
  reportType,
  strategyId,
  downloadReport,
}) => {
  const { t } = useTranslation();
  const { user } = useUserState();

  const featureKey =
    reportType === "simple" ? FEATURES.SIMPLE_REPORT : FEATURES.DETAILED_REPORT;

  const canAccess = hasFeature(user, featureKey);

  const reportPeriods = [
    { days: 7, label: t("strategy.actions.downloadReport.period.week") },
    { days: 15, label: t("strategy.actions.downloadReport.period.biweek") },
    { days: 30, label: t("strategy.actions.downloadReport.period.month") },
  ];

  const handleDownloadReport = async (days: number) => {
    await downloadReport(strategyId, days);
  };

  return (
    <>
      <MenuGroup title={t(`strategy.actions.downloadReport.${reportType}`)}>
        {reportPeriods.map(({ days, label }) => (
          <MenuItem
            key={days}
            onClick={canAccess ? () => handleDownloadReport(days) : undefined}
            isDisabled={!canAccess}
          >
            {label}
            {!canAccess && (
              <Tag ml={2} colorScheme="red">
                {t("common.notHired")}
              </Tag>
            )}
          </MenuItem>
        ))}
      </MenuGroup>
    </>
  );
};
