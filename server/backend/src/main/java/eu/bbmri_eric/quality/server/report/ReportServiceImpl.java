package eu.bbmri_eric.quality.server.report;

import eu.bbmri_eric.quality.server.common.EntityNotFoundException;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service implementation for managing reports. */
@Service
@Transactional
public class ReportServiceImpl implements ReportService {

  private final ReportRepository reportRepository;
  private final ModelMapper modelMapper;

  public ReportServiceImpl(ReportRepository reportRepository, ModelMapper modelMapper) {
    this.reportRepository = reportRepository;
    this.modelMapper = modelMapper;
  }

  @Override
  public ReportDTO create(ReportCreateRequest createRequest) {
    Report report = new Report(createRequest.agentId());
    Report savedReport = reportRepository.save(report);
    return modelMapper.map(savedReport, ReportDTO.class);
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
