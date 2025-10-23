package eu.bbmri_eric.quality.agent.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Listener for server registration events. Handles the communication with central servers when a
 * registration is initiated.
 */
@Component
class ServerRegistrationEventListener {

  private static final Logger log = LoggerFactory.getLogger(ServerRegistrationEventListener.class);

  private final CentralServerClient centralServerClient;
  private final ServerRepository serverRepository;

  ServerRegistrationEventListener(
      CentralServerClient centralServerClient, ServerRepository serverRepository) {
    this.centralServerClient = centralServerClient;
    this.serverRepository = serverRepository;
  }

  @EventListener
  @Transactional
  void handleServerRegistration(ServerRegistrationEvent event) {
    try {
      RegistrationCredentials credentials =
          centralServerClient.register(event.getAgentId(), event.getServerUrl());
      updateServerWithCredentials(event.getServerUrl(), credentials);
    } catch (Exception e) {
      log.error("Error registering with server {}", event.getServerUrl(), e);
      recordRegistrationFailure(event.getServerUrl(), extractErrorMessage(e));
    }
  }

  private void updateServerWithCredentials(String serverUrl, RegistrationCredentials credentials) {
    serverRepository
        .findByUrl(serverUrl)
        .ifPresentOrElse(
            server -> {
              server.setClientId(credentials.getClientId());
              server.setClientSecret(credentials.getClientSecret());
              serverRepository.save(server);
            },
            () -> recordRegistrationFailure(serverUrl, "Server not found during registration"));
  }

  private void recordRegistrationFailure(String serverUrl, String errorMessage) {
    serverRepository
        .findByUrl(serverUrl)
        .ifPresent(
            server -> {
              server.setStatus(ServerConnectionStatus.ERROR);
              server.addInteraction(
                  new ServerInteraction(InteractionType.REGISTRATION, errorMessage));
              serverRepository.save(server);
            });
  }

  private String extractErrorMessage(Exception e) {
    return e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
  }
}
