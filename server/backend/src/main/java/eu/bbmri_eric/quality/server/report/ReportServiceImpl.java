package eu.bbmri_eric.quality.server.report;

import eu.bbmri_eric.quality.server.common.EntityNotFoundException;
import eu.bbmri_eric.quality.server.user.AuthenticationContextService;
import eu.bbmri_eric.quality.server.user.UserDTO;
import eu.bbmri_eric.quality.server.user.UserRole;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service implementation for managing reports. */
@Service
@Transactional
public class ReportServiceImpl implements ReportService {

  private final ReportRepository reportRepository;
  private final ModelMapper modelMapper;
  private final AuthenticationContextService authenticationContextService;

  public ReportServiceImpl(
      ReportRepository reportRepository,
      ModelMapper modelMapper,
      AuthenticationContextService authenticationContextService) {
    this.reportRepository = reportRepository;
    this.modelMapper = modelMapper;
    this.authenticationContextService = authenticationContextService;
  }

  @Override
  public ReportDTO create(String agentId, ReportCreateRequest createRequest) {
    UserDTO currentUser = authenticationContextService.getCurrentUser();
    if (!isAuthorizedToCreateReport(currentUser, agentId)) {
      throw new AccessDeniedException(
          "User is not authorized to create reports for agent: " + agentId);
    }
    Report report = new Report(agentId);
    Report savedReport = reportRepository.save(report);
    return modelMapper.map(savedReport, ReportDTO.class);
  }

  private boolean isAuthorizedToCreateReport(UserDTO user, String agentId) {
    boolean isAdmin = user.getRoles() != null && user.getRoles().contains(UserRole.ADMIN);
    boolean isLinkedToAgent = agentId.equals(user.getAgentId());
    return isAdmin || isLinkedToAgent;
  }

  @Override
  @Transactional(readOnly = true)
  public ReportDTO findById(String id) {
    return modelMapper.map(
        reportRepository
            .findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException("Report with ID %s not found".formatted(id))),
        ReportDTO.class);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReportDTO> findByAgentId(String agentId) {
    return reportRepository.findByAgentId(agentId).stream()
        .map(report -> modelMapper.map(report, ReportDTO.class))
        .toList();
  }
}
