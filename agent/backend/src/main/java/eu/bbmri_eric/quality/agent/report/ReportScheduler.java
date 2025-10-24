package eu.bbmri_eric.quality.agent.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReportScheduler {

  private static final Logger log = LoggerFactory.getLogger(ReportScheduler.class);

  private final IReportService reportService;

  public ReportScheduler(IReportService reportService) {
    this.reportService = reportService;
  }

  /** Scheduled task to generate reports daily at midnight. */
  @Scheduled(cron = "0 0 0 * * *")
  public void generateReport() {
    log.info("🕐 Scheduled report generation triggered");
    reportService.generateReportTransactional();
  }
}
