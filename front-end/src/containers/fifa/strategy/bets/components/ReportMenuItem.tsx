import React from 'react';
import { MenuItem, Tag, useDisclosure } from "@chakra-ui/react";
import useTranslation from "hooks/useTranslation";
import { ReportDownloadModal } from './ReportDownloadModal';
import { getFeatureAmount } from 'utils/helpers/featureHelper';
import { FEATURES } from 'utils/constants/featureConstants';
import { useUserState } from 'context/UserContext';

export interface ReportMenuItemProps {
    reportType: 'simple' | 'detailed';
    strategyId: string;
    downloadReport: (strategyId: string) => Promise<void>;
    isDownloadingReport: boolean;
}

export const ReportMenuItem: React.FC<ReportMenuItemProps> = ({
    reportType,
    strategyId,
    downloadReport,
    isDownloadingReport
}) => {
    const { t } = useTranslation();
    const { isOpen, onOpen, onClose } = useDisclosure();
    const { user } = useUserState();

    const featureKey = reportType === 'simple'
        ? FEATURES.SIMPLE_REPORT
        : FEATURES.DETAILED_REPORT;

    const featureAmount = user ? getFeatureAmount(user, featureKey) : 0;
    const hasFeature = featureAmount > 0;

    const handleDownloadReport = async () => {
        await downloadReport(strategyId);
        onClose();
    };

    return (
        <>
            <MenuItem
                onClick={hasFeature ? onOpen : undefined}
                isDisabled={!hasFeature}
            >
                {t(`strategy.actions.downloadReport.${reportType}`)}
                {hasFeature ?
                    (<Tag ml={2} colorScheme="yellow">Beta</Tag>) : 
                    (<Tag ml={2} colorScheme="red">
                        {t('common.notHired')}
                    </Tag>)}
            </MenuItem>

            {hasFeature && (
                <ReportDownloadModal
                    isOpen={isOpen}
                    onClose={onClose}
                    onDownload={handleDownloadReport}
                    isDownloading={isDownloadingReport}
                    reportType={reportType}
                />
            )}
        </>
    );
};
