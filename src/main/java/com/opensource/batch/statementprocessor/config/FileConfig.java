package com.opensource.batch.statementprocessor.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@Getter
public class FileConfig {

    @Value("${csv.transaction.detail.multiple.files:csv/records*.csv}")
    private Resource[] csvFiles;

    @Value("${xml.transaction.detail.multiple.files:xml/records*.xml}")
    private Resource[] xmlFiles;

    @Value("${report.error.transaction.detail.file:reports/errorTransactionReport.csv}")
    private String errorTransactionReportFile;
    @Value("${report.valid.transaction.detail.file:reports/validTransactionReport.csv}")
    private String validTransactionReportFile;
    @Value("${report.valid.transaction.detail.file:reports/validTransactionReport.csv}")
    private String archiveReportFolder;


}
