package eu.bbmri_eric.quality.server.report;

import eu.bbmri_eric.quality.server.common.EntityNotFoundException;
import eu.bbmri_eric.quality.server.user.AuthenticationContextService;
import eu.bbmri_eric.quality.server.user.UserDTO;
import eu.bbmri_eric.quality.server.user.UserRole;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service implementation for managing reports. */
@Service
@Transactional
public class ReportServiceImpl implements ReportService {

  private final ReportRepository reportRepository;
  private final QualityCheckRepository qualityCheckRepository;
  private final AuthenticationContextService authenticationContextService;
  private final ApplicationEventPublisher eventPublisher;

  public ReportServiceImpl(
      ReportRepository reportRepository,
      QualityCheckRepository qualityCheckRepository,
      AuthenticationContextService authenticationContextService,
      ApplicationEventPublisher eventPublisher) {
    this.reportRepository = reportRepository;
    this.qualityCheckRepository = qualityCheckRepository;
    this.authenticationContextService = authenticationContextService;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public ReportDTO create(String agentId, ReportCreateRequest createRequest) {
    UserDTO currentUser = authenticationContextService.getCurrentUser();
    if (!isAuthorizedToCreateReport(currentUser, agentId)) {
      throw new AccessDeniedException(
          "User is not authorized to create reports for agent: " + agentId);
    }
    Report report = new Report(agentId);
    if (createRequest.results() != null && !createRequest.results().isEmpty()) {
      for (QualityCheckResultDTO resultDTO : createRequest.results()) {
        QualityCheck qualityCheck =
            qualityCheckRepository
                .findById(resultDTO.hash())
                .orElseGet(
                    () -> {
                      // Create a new quality check if it doesn't exist
                      QualityCheck newCheck = new QualityCheck(resultDTO.hash(), "", "");
                      return qualityCheckRepository.save(newCheck);
                    });

        report.addQualityCheckResult(qualityCheck, resultDTO.result());
      }
    }
    Report savedReport = reportRepository.save(report);
    eventPublisher.publishEvent(new ReportSubmittedEvent(this, agentId, report.getId()));
    return convertToDTO(savedReport);
  }

  private boolean isAuthorizedToCreateReport(UserDTO user, String agentId) {
    boolean isAdmin = user.getRoles() != null && user.getRoles().contains(UserRole.ADMIN);
    boolean isLinkedToAgent = agentId.equals(user.getAgentId());
    return isAdmin || isLinkedToAgent;
  }

  @Override
  @Transactional(readOnly = true)
  public ReportDTO findById(String id) {
    Report report =
        reportRepository
            .findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException("Report with ID %s not found".formatted(id)));
    return convertToDTO(report);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReportDTO> findByAgentId(String agentId) {
    return reportRepository.findByAgentId(agentId).stream().map(this::convertToDTO).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReportDTO> findAll() {
    return reportRepository.findAll().stream().map(this::convertToDTO).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public long countAll() {
    return reportRepository.count();
  }

  private ReportDTO convertToDTO(Report report) {
    ReportDTO dto = new ReportDTO();
    dto.setId(report.getId());
    dto.setTimestamp(report.getTimestamp());
    dto.setAgentId(report.getAgentId());

    // Map quality check results manually
    if (report.getQualityCheckResults() != null && !report.getQualityCheckResults().isEmpty()) {
      List<QualityCheckResultDTO> resultDTOs =
          report.getQualityCheckResults().stream()
              .map(
                  result ->
                      new QualityCheckResultDTO(
                          result.getQualityCheck().getHash(), result.getResult()))
              .toList();
      dto.setResults(resultDTOs);
    }

    return dto;
  }
}
