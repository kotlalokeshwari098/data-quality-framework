package eu.bbmri_eric.quality.agent.report;

import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportServiceImpl implements ReportService {

  private final ReportRepository reportRepository;
  private final ReportEventHandler reportRestEventHandler;
  private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

  ReportServiceImpl(ReportRepository reportRepository, ReportEventHandler reportRestEventHandler) {
    this.reportRepository = reportRepository;
    this.reportRestEventHandler = reportRestEventHandler;
  }

  @Transactional
  public void generateReport() {
    Report report = reportRepository.save(new Report());
    reportRestEventHandler.onAfterCreate(report);
    log.info("📊 Scheduled report created with ID: {}", report.getId());
  }

  @Transactional(readOnly = true)
  public ReportDTO getById(Long id) {
    Report report =
        reportRepository.findById(id).orElseThrow(() -> new ReportNotFoundException(id));

    var results =
        report.getResults().stream()
            .map(
                result ->
                    new QualityCheckResultDTO(
                        result.getCheckId()
                            + (result.getStratum() != null
                                ? " (%s)".formatted(result.getStratum())
                                : ""),
                        (double) result.getObfuscatedValue()))
            .collect(Collectors.toList());

    return new ReportDTO(results);
  }
}
