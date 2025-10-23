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
  @Scheduled(fixedRate = 60000) // Run every 60 seconds (1 minute)
  @Transactional
  public void checkAllServerStatuses() {
    log.debug("Starting periodic server status check");
    String agentId = settingsService.getSettings().getAgentId();

    serverRepository
        .findAll()
        .forEach(
            server -> {
              try {
                ServerConnectionStatus newStatus =
                    centralServerClient.checkRegistrationStatus(
                        agentId, server.getUrl(), server.getClientId(), server.getClientSecret());

                if (newStatus != server.getStatus()) {
                  log.debug(
                      "Server {} status changed from {} to {}",
                      server.getUrl(),
                      server.getStatus(),
                      newStatus);
                  server.setStatus(newStatus);
                } else {
                  log.debug("Server {} status remains {}", server.getUrl(), newStatus);
                }
              } catch (Exception e) {
                log.error("Error checking status for server {}", server.getUrl(), e);
                server.setStatus(ServerConnectionStatus.ERROR);
                String errorMessage =
                    e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
                server.addInteraction(
                    new ServerInteraction(InteractionType.STATUS_CHECK, errorMessage));
              }
              serverRepository.save(server);
            });
  }
}
