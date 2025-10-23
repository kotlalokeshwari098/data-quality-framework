package eu.bbmri_eric.quality.agent.server;

import eu.bbmri_eric.quality.agent.auth.LoginRequest;
import eu.bbmri_eric.quality.agent.server.dto.AgentRegistrationRequest;
import eu.bbmri_eric.quality.agent.server.dto.AgentRegistrationResponse;
import eu.bbmri_eric.quality.agent.server.dto.AgentStatusResponse;
import eu.bbmri_eric.quality.agent.server.dto.LoginResponse;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of CentralServerClient for handling communication with central servers. Manages
 * agent registration, status checks, and health verification with remote servers.
 */
@Component
public class CentralServerClientImpl implements CentralServerClient {

  private static final Logger log = LoggerFactory.getLogger(CentralServerClientImpl.class);

  // API Endpoints
  private static final String AGENTS_ENDPOINT = "/api/v1/agents";
  private static final String LOGIN_ENDPOINT = "/api/auth/login";

  private final RestTemplate restTemplate;

  public CentralServerClientImpl(RestTemplate restTemplate) {
    this.restTemplate = Objects.requireNonNull(restTemplate, "RestTemplate cannot be null");
  }

  /**
   * Registers an agent with the central server.
   *
   * @param agentId the agent identifier
   * @param serverUrl the base URL of the central server
   * @return registration credentials containing clientId and clientSecret, or null if registration
   *     fails
   */
  @Override
  public RegistrationCredentials register(String agentId, String serverUrl) {
    validateInputs(agentId, serverUrl);
    try {
      AgentRegistrationRequest request = new AgentRegistrationRequest(agentId);
      HttpEntity<AgentRegistrationRequest> requestEntity = createJsonHttpEntity(request);

      String registrationUrl = buildApiUrl(serverUrl, AGENTS_ENDPOINT);
      ResponseEntity<AgentRegistrationResponse> response =
          restTemplate.exchange(
              registrationUrl, HttpMethod.POST, requestEntity, AgentRegistrationResponse.class);

      return handleRegistrationResponse(response, agentId, serverUrl);
    } catch (RestClientException e) {
      logRegistrationError(agentId, serverUrl, e);
      return null;
    }
  }

  /**
   * Checks the registration status of an agent with the central server.
   *
   * @param agentId the agent identifier
   * @param serverUrl the base URL of the central server
   * @param clientId the client ID (username) for authentication
   * @param clientSecret the client secret (password) for authentication
   * @return the current connection status
   */
  @Override
  public ServerConnectionStatus checkRegistrationStatus(
      String agentId, String serverUrl, String clientId, String clientSecret) {
    validateInputs(agentId, serverUrl);
    try {
      String token = authenticateWithServer(serverUrl, clientId, clientSecret);
      if (token == null) {
        return ServerConnectionStatus.ERROR;
      }
      return queryAgentStatus(agentId, serverUrl, token);
    } catch (RestClientException e) {
      log.error(
          "Error checking status for agent {} with server {} - {}",
          agentId,
          serverUrl,
          e.getMessage());
      return ServerConnectionStatus.ERROR;
    }
  }

  /**
   * Performs a health check on the central server.
   *
   * @param agentId the agent identifier (for logging purposes)
   * @param serverUrl the base URL of the central server
   */
  @Override
  public void healthCheck(String agentId, String serverUrl) {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  /**
   * Authenticates with the central server and retrieves a bearer token.
   *
   * @param serverUrl the base URL of the central server
   * @param clientId the client ID (username) for authentication
   * @param clientSecret the client secret (password) for authentication
   * @return the authentication token, or null if authentication fails
   */
  private String authenticateWithServer(String serverUrl, String clientId, String clientSecret) {
    String loginUrl = buildApiUrl(serverUrl, LOGIN_ENDPOINT);
    LoginRequest loginRequest = new LoginRequest(clientId, clientSecret);
    HttpEntity<LoginRequest> requestEntity = createJsonHttpEntity(loginRequest);

    ResponseEntity<LoginResponse> response =
        restTemplate.exchange(loginUrl, HttpMethod.POST, requestEntity, LoginResponse.class);

    LoginResponse loginBody = response.getBody();
    if (loginBody == null || loginBody.getToken() == null) {
      log.error("Authentication failed with server {} - no token received", serverUrl);
      throw new RestClientException("Authentication failed with server " + serverUrl);
    }
    return loginBody.getToken();
  }

  /**
   * Queries the status of an agent from the central server.
   *
   * @param agentId the agent identifier
   * @param serverUrl the base URL of the central server
   * @param token the authentication token
   * @return the agent's connection status
   */
  private ServerConnectionStatus queryAgentStatus(String agentId, String serverUrl, String token) {
    String checkUrl = buildApiUrl(serverUrl, AGENTS_ENDPOINT + "/" + agentId);
    HttpHeaders headers = createDefaultHeaders();
    headers.setBearerAuth(token);
    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

    ResponseEntity<AgentStatusResponse> response =
        restTemplate.exchange(checkUrl, HttpMethod.GET, requestEntity, AgentStatusResponse.class);

    return mapAgentStatusToConnectionStatus(response.getBody());
  }

  /**
   * Maps an agent status to a server connection status.
   *
   * @param agentStatus the agent status response
   * @return the corresponding connection status
   */
  private ServerConnectionStatus mapAgentStatusToConnectionStatus(AgentStatusResponse agentStatus) {
    if (agentStatus == null || agentStatus.getStatus() == null) {
      return ServerConnectionStatus.PENDING;
    }

    return switch (agentStatus.getStatus()) {
      case ACTIVE -> ServerConnectionStatus.ACTIVE;
      case INACTIVE -> ServerConnectionStatus.INACTIVE;
      default -> ServerConnectionStatus.PENDING;
    };
  }

  /**
   * Handles the response from an agent registration request.
   *
   * @param response the registration response
   * @param agentId the agent identifier
   * @param serverUrl the server URL
   * @return registration credentials, or null if response is incomplete
   */
  private RegistrationCredentials handleRegistrationResponse(
      ResponseEntity<AgentRegistrationResponse> response, String agentId, String serverUrl) {
    AgentRegistrationResponse registrationResponse = response.getBody();
    if (registrationResponse != null
        && registrationResponse.getUser() != null
        && registrationResponse.getUser().getUsername() != null
        && registrationResponse.getUser().getTemporaryPassword() != null) {
      String clientId = registrationResponse.getUser().getUsername();
      String clientSecret = registrationResponse.getUser().getTemporaryPassword();
      log.info("Successfully registered agent {} with server {}", agentId, serverUrl);
      return new RegistrationCredentials(clientId, clientSecret);
    } else {
      log.warn(
          "Agent registration response was incomplete for agent {} at server {}",
          agentId,
          serverUrl);
      return null;
    }
  }

  /**
   * Creates an HTTP entity with JSON content type for the given payload.
   *
   * @param payload the request payload
   * @param <T> the type of the payload
   * @return an HTTP entity with appropriate headers
   */
  private <T> HttpEntity<T> createJsonHttpEntity(T payload) {
    HttpHeaders headers = createDefaultHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<>(payload, headers);
  }

  /**
   * Creates default HTTP headers.
   *
   * @return HTTP headers with Accept: application/json
   */
  private HttpHeaders createDefaultHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  /**
   * Builds a complete API URL from server base URL and endpoint.
   *
   * @param baseUrl the base server URL
   * @param endpoint the API endpoint path
   * @return the complete API URL
   */
  private String buildApiUrl(String baseUrl, String endpoint) {
    return baseUrl + endpoint;
  }

  /**
   * Validates that required inputs are not null or empty.
   *
   * @param agentId the agent identifier
   * @param serverUrl the server URL
   * @throws IllegalArgumentException if inputs are invalid
   */
  private void validateInputs(String agentId, String serverUrl) {
    if (agentId == null || agentId.isBlank()) {
      throw new IllegalArgumentException("Agent ID cannot be null or empty");
    }
    if (serverUrl == null || serverUrl.isBlank()) {
      throw new IllegalArgumentException("Server URL cannot be null or empty");
    }
  }

  /**
   * Logs registration errors with appropriate context.
   *
   * @param agentId the agent identifier
   * @param serverUrl the server URL
   * @param e the exception that occurred
   */
  private void logRegistrationError(String agentId, String serverUrl, Exception e) {
    log.error(
        "Error registering agent {} with server {} - {}", agentId, serverUrl, e.getMessage(), e);
  }
}
