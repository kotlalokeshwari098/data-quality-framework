package eu.bbmri_eric.quality.agent.server;

import eu.bbmri_eric.quality.agent.events.FinishedReportEvent;
import eu.bbmri_eric.quality.agent.report.ReportDTO;
import eu.bbmri_eric.quality.agent.report.ReportService;
import eu.bbmri_eric.quality.agent.server.client.CentralServerClientFactory;
import eu.bbmri_eric.quality.agent.settings.SettingsService;
import jakarta.transaction.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ReportListener {
  private final ReportService reportService;
  private final CentralServerClientFactory clientFactory;
  private final ServerRepository serverRepository;
  private final SettingsService settingsService;

  public ReportListener(
      ReportService reportService,
      CentralServerClientFactory clientFactory,
      ServerRepository serverRepository,
      SettingsService settingsService) {
    this.reportService = reportService;
    this.clientFactory = clientFactory;
    this.serverRepository = serverRepository;
    this.settingsService = settingsService;
  }

  @EventListener
  @Transactional
  protected void onFinished(FinishedReportEvent event) {
    ReportDTO reportDTO = reportService.getById(event.getReportId());
    String agentId = settingsService.getSettings().getAgentId();
    serverRepository
        .findAllByStatusIs(ServerConnectionStatus.ACTIVE)
        .forEach(
            server -> {
              var client =
                  clientFactory.createClient(
                      agentId, server.getUrl(), server.getClientId(), server.getClientSecret());
              client.sendReport(reportDTO);
            });
  }
}
