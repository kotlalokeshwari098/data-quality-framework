package eu.bbmri_eric.quality.agent.server;

import eu.bbmri_eric.quality.agent.auth.LoginRequest;
import eu.bbmri_eric.quality.agent.server.dto.AgentRegistrationRequest;
import eu.bbmri_eric.quality.agent.server.dto.AgentRegistrationResponse;
import eu.bbmri_eric.quality.agent.server.dto.AgentStatusResponse;
import eu.bbmri_eric.quality.agent.server.dto.LoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CentralServerClientImpl implements CentralServerClient {

  private static final Logger log = LoggerFactory.getLogger(CentralServerClientImpl.class);
  private final RestTemplate restTemplate;

  public CentralServerClientImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public void register(String agentId, String serverUrl) {
    try {
      AgentRegistrationRequest registrationRequest = new AgentRegistrationRequest(agentId);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      HttpEntity<AgentRegistrationRequest> requestEntity =
          new HttpEntity<>(registrationRequest, headers);

      String registrationUrl = serverUrl + "/api/v1/agents";
      ResponseEntity<AgentRegistrationResponse> response =
          restTemplate.exchange(
              registrationUrl, HttpMethod.POST, requestEntity, AgentRegistrationResponse.class);
      AgentRegistrationResponse registrationResponse = response.getBody();
      if (registrationResponse != null && registrationResponse.getUser() != null) {
        String username = registrationResponse.getUser().getUsername();
        String temporaryPassword = registrationResponse.getUser().getTemporaryPassword();
        if (username != null && temporaryPassword != null) {
          log.info("Successfully registered agent {} with server {}", agentId, serverUrl);
        }
      }
    } catch (Exception e) {
      log.error("Error registering agent {} with server {}", agentId, serverUrl, e);
    }
  }

  @Override
  public ServerConnectionStatus checkRegistrationStatus(String agentId, String serverUrl) {
    try {
      // First, authenticate to get the JWT token
      String loginUrl = serverUrl + "/api/auth/login";
      LoginRequest loginRequest = new LoginRequest("", "");

      HttpHeaders loginHeaders = new HttpHeaders();
      loginHeaders.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<LoginRequest> loginEntity = new HttpEntity<>(loginRequest, loginHeaders);

      ResponseEntity<LoginResponse> loginResponse =
          restTemplate.exchange(loginUrl, HttpMethod.POST, loginEntity, LoginResponse.class);

      LoginResponse loginBody = loginResponse.getBody();
      if (loginBody == null || loginBody.getToken() == null) {
        log.error("Failed to authenticate with server {} - no token received", serverUrl);
        return ServerConnectionStatus.ERROR;
      }

      String token = loginBody.getToken();

      // Now check the agent status using the token
      String checkUrl = serverUrl + "/api/v1/agents/" + agentId;

      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(token);
      HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

      ResponseEntity<AgentStatusResponse> response =
          restTemplate.exchange(checkUrl, HttpMethod.GET, requestEntity, AgentStatusResponse.class);

      AgentStatusResponse agentStatus = response.getBody();
      if (agentStatus != null && agentStatus.getStatus() != null) {
        AgentStatus newStatus = agentStatus.getStatus();
        if (newStatus == AgentStatus.ACTIVE) {
          return ServerConnectionStatus.ACTIVE;
        } else if (newStatus == AgentStatus.INACTIVE) {
          return ServerConnectionStatus.INACTIVE;
        }
      }
      return ServerConnectionStatus.PENDING;
    } catch (Exception e) {
      log.error("Error checking agent status for agent {} with server {}", agentId, serverUrl, e);
      return ServerConnectionStatus.ERROR;
    }
  }

  @Override
  public void healthCheck(String agentId, String serverUrl) {
    try {
      String healthUrl = serverUrl + "/api/health";
      HttpHeaders headers = new HttpHeaders();
      HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

      ResponseEntity<Void> response =
          restTemplate.exchange(healthUrl, HttpMethod.GET, requestEntity, Void.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        log.info("Health check successful for server {}", serverUrl);
      } else {
        log.warn("Health check failed for server {}", serverUrl);
      }
    } catch (Exception e) {
      log.error("Error performing health check for server {}", serverUrl, e);
    }
  }
}
