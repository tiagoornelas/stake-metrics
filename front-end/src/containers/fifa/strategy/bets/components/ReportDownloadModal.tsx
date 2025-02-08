import React from 'react';
import {
    Modal, ModalOverlay, ModalContent, ModalHeader, ModalCloseButton, 
    ModalBody, ModalFooter, Button, Text
} from "@chakra-ui/react";
import useTranslation from "hooks/useTranslation";

export interface ReportDownloadModalProps {
    isOpen: boolean;
    onClose: () => void;
    onDownload: () => Promise<void>;
    isDownloading: boolean;
    reportType: 'simple' | 'detailed';
}

export const ReportDownloadModal: React.FC<ReportDownloadModalProps> = ({
    isOpen, 
    onClose, 
    onDownload, 
    isDownloading,
    reportType
}) => {
    const { t } = useTranslation();

    return (
        <Modal isOpen={isOpen} onClose={onClose} isCentered>
            <ModalOverlay />
            <ModalContent>
                <ModalHeader>
                    {t(`strategy.actions.downloadReport.${reportType}`)}
                </ModalHeader>
                <ModalCloseButton />
                <ModalBody>
                    <Text>
                        {t(`strategy.report.${reportType}DownloadConfirmation`)}
                    </Text>
                </ModalBody>
                <ModalFooter>
                    <Button 
                        colorScheme="blue" 
                        mr={3} 
                        onClick={onDownload}
                        isLoading={isDownloading}
                    >
                        {t("common.generate")}
                    </Button>
                    <Button variant="ghost" onClick={onClose}>
                        {t("common.cancel")}
                    </Button>
                </ModalFooter>
            </ModalContent>
        </Modal>
    );
};
