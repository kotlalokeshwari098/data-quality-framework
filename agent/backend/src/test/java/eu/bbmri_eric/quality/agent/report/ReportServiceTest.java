package eu.bbmri_eric.quality.agent.report;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ReportServiceTest {

  @Autowired private ReportRepository reportRepository;

  @Autowired private ReportService reportService;

  @BeforeEach
  void setUp() {
    reportRepository.deleteAll();
  }

  @Test
  void generateReportTransactional() {
    assertThat(reportRepository.findAll()).isEmpty();

    reportService.generateReportTransactional();

    assertThat(reportRepository.findAll()).hasSize(1);
    Report report = reportRepository.findAll().getFirst();

    assertThat(report.getId()).isNotNull();
  }
}
