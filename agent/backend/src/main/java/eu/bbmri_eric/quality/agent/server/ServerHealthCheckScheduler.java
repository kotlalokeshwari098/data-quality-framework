package eu.bbmri_eric.quality.agent.server;

import eu.bbmri_eric.quality.agent.settings.SettingsService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler for periodic server health checks and registration status updates. Manages the
 * scheduled tasks for monitoring server connections without coupling the service layer to
 * scheduling concerns.
 */
@Component
class ServerHealthCheckScheduler {

  private static final Logger log = LoggerFactory.getLogger(ServerHealthCheckScheduler.class);

  private final ServerRepository serverRepository;
  private final SettingsService settingsService;
  private final CentralServerClient centralServerClient;

  ServerHealthCheckScheduler(
      ServerRepository serverRepository,
      SettingsService settingsService,
      CentralServerClient centralServerClient) {
    this.serverRepository = serverRepository;
    this.settingsService = settingsService;
    this.centralServerClient = centralServerClient;
  }

  /**
   * Periodically checks the registration status of all servers. Updates server status based on the
   * health check results. Runs every 60 seconds.
   */
  @Scheduled(fixedRate = 60000)
  @Transactional
  public void checkAllServerStatuses() {
    String agentId = settingsService.getSettings().getAgentId();
    serverRepository.findAll().forEach(server -> checkServerStatus(server, agentId));
  }

  private void checkServerStatus(Server server, String agentId) {
    try {
      ServerConnectionStatus newStatus =
          centralServerClient.checkRegistrationStatus(
              agentId, server.getUrl(), server.getClientId(), server.getClientSecret());
      updateServerStatus(server, newStatus);
    } catch (Exception e) {
      handleStatusCheckError(server, e);
    }
    serverRepository.save(server);
  }

  private void updateServerStatus(Server server, ServerConnectionStatus newStatus) {
    if (newStatus != server.getStatus()) {
      server.setStatus(newStatus);
    }
  }

  private void handleStatusCheckError(Server server, Exception e) {
    server.setStatus(ServerConnectionStatus.ERROR);
    String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
    server.addInteraction(new ServerInteraction(InteractionType.STATUS_CHECK, errorMessage));
    log.error("Error checking status for server {}", server.getUrl(), e);
  }
}
