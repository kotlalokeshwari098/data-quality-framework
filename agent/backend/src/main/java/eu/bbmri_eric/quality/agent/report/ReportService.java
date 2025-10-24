package eu.bbmri_eric.quality.agent.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService implements IReportService {

  private final ReportRepository reportRepository;
  private final ReportRestEventHandler reportRestEventHandler;
  private static final Logger log = LoggerFactory.getLogger(ReportService.class);

  public ReportService(
      ReportRepository reportRepository, ReportRestEventHandler reportRestEventHandler) {
    this.reportRepository = reportRepository;
    this.reportRestEventHandler = reportRestEventHandler;
  }

  @Transactional
  public void generateReportTransactional() {
    Report report = reportRepository.save(new Report());

    reportRestEventHandler.onAfterCreate(report);
    log.info("📊 Scheduled report created with ID: {}", report.getId());
  }
}
