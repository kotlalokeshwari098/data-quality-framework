package eu.bbmri_eric.quality.agent.report;

import eu.bbmri_eric.quality.agent.check.CQLQueryDTO;
import eu.bbmri_eric.quality.agent.check.CQLQueryService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportServiceImpl implements ReportService {

  private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);
  private final ReportRepository reportRepository;
  private final ReportEventHandler reportRestEventHandler;
  private final CQLQueryService cqlQueryService;

  ReportServiceImpl(
      ReportRepository reportRepository,
      ReportEventHandler reportRestEventHandler,
      CQLQueryService cqlQueryService) {
    this.reportRepository = reportRepository;
    this.reportRestEventHandler = reportRestEventHandler;
    this.cqlQueryService = cqlQueryService;
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
    List<CQLQueryDTO> cqlQueryDTOS = cqlQueryService.findAll();
    var results =
        report.getResults().stream()
            .map(
                result ->
                    new QualityCheckResultDTO(
                        getCheckId(result, cqlQueryDTOS)
                            + (result.getStratum() != null
                                ? " (%s)".formatted(result.getStratum())
                                : ""),
                        result.getObfuscatedValue() / report.getNumberOfEntities()))
            .collect(Collectors.toList());
    return new ReportDTO(results);
  }

  private static String getCheckId(Result result, List<CQLQueryDTO> cqlQueryDTOS) {
    String query =
        cqlQueryDTOS.stream()
            .filter(cqlQueryDTO -> cqlQueryDTO.getId().equals(result.getCheckId()))
            .findFirst()
            .map(CQLQueryDTO::getQuery)
            .orElse(result.getCheckId().toString());
    return hashQuery(query);
  }

  private static String hashQuery(String query) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(query.getBytes(StandardCharsets.UTF_8));
      StringBuilder hexString = new StringBuilder();
      for (byte b : hash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) hexString.append('0');
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 algorithm not found", e);
    }
  }
}
