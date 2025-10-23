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
public class ServerRegistrationEventListener {

  private static final Logger log = LoggerFactory.getLogger(ServerRegistrationEventListener.class);

  private final CentralServerClient centralServerClient;
  private final ServerRepository serverRepository;

  public ServerRegistrationEventListener(
      CentralServerClient centralServerClient, ServerRepository serverRepository) {
    this.centralServerClient = centralServerClient;
    this.serverRepository = serverRepository;
  }

  @EventListener
  @Transactional
  public void handleServerRegistration(ServerRegistrationEvent event) {
    log.debug(
        "Processing server registration event for agent {} and server {}",
        event.getAgentId(),
        event.getServerUrl());
    try {
      RegistrationCredentials credentials =
          centralServerClient.register(event.getAgentId(), event.getServerUrl());

      if (credentials != null) {
        // Find the server and update it with the credentials
        serverRepository
            .findByUrl(event.getServerUrl())
            .ifPresentOrElse(
                server -> {
                  server.setClientId(credentials.getClientId());
                  server.setClientSecret(credentials.getClientSecret());
                  serverRepository.save(server);
                  log.info(
                      "Updated server {} with credentials for user {}",
                      event.getServerUrl(),
                      credentials.getClientId());
                },
                () ->
                    log.warn(
                        "Server not found for URL {} during registration", event.getServerUrl()));
      } else {
        log.warn(
            "Registration failed - no credentials returned for server {}", event.getServerUrl());
      }
    } catch (Exception e) {
      log.error(
          "Error registering agent {} with server {} - {}",
          event.getAgentId(),
          event.getServerUrl(),
          e.getMessage(),
          e);

      // Find the server and add error interaction
      serverRepository
          .findByUrl(event.getServerUrl())
          .ifPresent(
              server -> {
                String errorMessage =
                    e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
                server.addInteraction(
                    new ServerInteraction(InteractionType.REGISTRATION, errorMessage));
                serverRepository.save(server);
              });
    }
  }
}
