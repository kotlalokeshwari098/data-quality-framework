package eu.bbmri_eric.quality.server.report;

import eu.bbmri_eric.quality.server.agent.AgentService;
import eu.bbmri_eric.quality.server.common.EntityNotFoundException;
import eu.bbmri_eric.quality.server.user.AuthenticationContextService;
import eu.bbmri_eric.quality.server.user.UserDTO;
import eu.bbmri_eric.quality.server.user.UserRole;
import java.util.List;
import org.modelmapper.ModelMapper;
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
  private final ModelMapper modelMapper;
  private final AgentService agentService;

  public ReportServiceImpl(
      ReportRepository reportRepository,
      QualityCheckRepository qualityCheckRepository,
      AuthenticationContextService authenticationContextService,
      ApplicationEventPublisher eventPublisher,
      ModelMapper modelMapper,
      AgentService agentService) {
    this.reportRepository = reportRepository;
    this.qualityCheckRepository = qualityCheckRepository;
    this.authenticationContextService = authenticationContextService;
    this.eventPublisher = eventPublisher;
    this.modelMapper = modelMapper;
    this.agentService = agentService;
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
                .findById(resultDTO.getHash())
                .orElseGet(
                    () -> {
                      // Create a new quality check if it doesn't exist
                      QualityCheck newCheck = new QualityCheck(resultDTO.getHash(), "", "");
                      return qualityCheckRepository.save(newCheck);
                    });

        report.addQualityCheckResult(qualityCheck, resultDTO.getResult());
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
    agentService.findById(agentId);
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
    return modelMapper.map(report, ReportDTO.class);
  }
}
