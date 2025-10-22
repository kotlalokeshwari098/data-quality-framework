package eu.bbmri_eric.quality.agent.server;

import eu.bbmri_eric.quality.agent.auth.LoginRequest;
import eu.bbmri_eric.quality.agent.server.dto.AgentRegistrationRequest;
import eu.bbmri_eric.quality.agent.server.dto.AgentRegistrationResponse;
import eu.bbmri_eric.quality.agent.server.dto.AgentStatusResponse;
import eu.bbmri_eric.quality.agent.server.dto.LoginResponse;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Entity
public class Server {

  private static final Logger log = LoggerFactory.getLogger(Server.class);
  @Id private String id = UUID.randomUUID().toString();

  @NotBlank
  @Size(max = 500)
  @Column(name = "url", nullable = false, length = 500)
  private String url;

  @NotBlank
  @Size(max = 255)
  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Size(max = 255)
  @Column(name = "client_id", nullable = false, length = 255)
  private String clientId = "";

  @Size(max = 500)
  @Column(name = "client_secret", nullable = false, length = 500)
  private String clientSecret = "";

  @NotNull
  @Enumerated(EnumType.STRING)
  private ServerStatus status = ServerStatus.PENDING;

  @OneToMany(mappedBy = "serverId", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ServerInteraction> interactions = new ArrayList<>();

  protected Server() {}

  public Server(
      String url, String name, String clientId, String clientSecret, ServerStatus status) {
    this.url = url;
    this.name = name;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.status = status;
    addInteraction(new ServerInteraction(InteractionType.UPDATE, "Initial Registration"));
  }

  public Server(String url, String name) {
    this.url = url;
    this.name = name;
    addInteraction(new ServerInteraction(InteractionType.UPDATE, "Initial Registration"));
  }

  public String getId() {
    return id;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    String oldUrl = this.url;
    this.url = url;
    if (this.id != null && !Objects.equals(oldUrl, url)) {
      addInteraction(
          new ServerInteraction(
              InteractionType.UPDATE, String.format("URL updated from '%s' to '%s'", oldUrl, url)));
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    String oldName = this.name;
    this.name = name;
    if (this.id != null && !Objects.equals(oldName, name)) {
      addInteraction(
          new ServerInteraction(
              InteractionType.UPDATE,
              String.format("Name updated from '%s' to '%s'", oldName, name)));
    }
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    String oldClientId = this.clientId;
    this.clientId = clientId;
    if (this.id != null && !Objects.equals(oldClientId, clientId)) {
      addInteraction(
          new ServerInteraction(
              InteractionType.UPDATE,
              String.format("Client ID updated from '%s' to '%s'", oldClientId, clientId)));
    }
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    boolean hadSecret = this.clientSecret != null && !this.clientSecret.isEmpty();
    this.clientSecret = clientSecret;
    if (this.id != null && hadSecret) {
      addInteraction(new ServerInteraction(InteractionType.UPDATE, "Client secret updated"));
    }
  }

  public ServerStatus getStatus() {
    return status;
  }

  public void setStatus(ServerStatus status) {
    ServerStatus oldStatus = this.status;
    this.status = status;
    if (this.id != null && oldStatus != status) {
      addInteraction(
          new ServerInteraction(
              InteractionType.UPDATE,
              String.format("Status changed from %s to %s", oldStatus, status)));
    }
  }

  public List<ServerInteraction> getInteractions() {
    return Collections.unmodifiableList(interactions);
  }

  public void addInteraction(ServerInteraction interaction) {
    interaction.setServerId(this.id);
    interactions.add(interaction);
  }

  public void register(String agentId, RestTemplate restTemplate) {
    try {
      AgentRegistrationRequest registrationRequest = new AgentRegistrationRequest(agentId);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      HttpEntity<AgentRegistrationRequest> requestEntity =
          new HttpEntity<>(registrationRequest, headers);

      String registrationUrl = url + "/api/v1/agents";
      ResponseEntity<AgentRegistrationResponse> response =
          restTemplate.exchange(
              registrationUrl, HttpMethod.POST, requestEntity, AgentRegistrationResponse.class);
      AgentRegistrationResponse registrationResponse = response.getBody();
      if (registrationResponse.getUser() != null) {
        String username = registrationResponse.getUser().getUsername();
        String temporaryPassword = registrationResponse.getUser().getTemporaryPassword();
        if (username != null && temporaryPassword != null) {
          setClientId(username);
          setClientSecret(temporaryPassword);
        }
      }
      setStatus(ServerStatus.PENDING);
      addInteraction(
          new ServerInteraction(
              InteractionType.REGISTRATION,
              String.format("Successfully registered agent %s and received credentials", agentId)));
    } catch (Exception e) {
      log.error("Error registering agent {}", agentId, e);
      setStatus(ServerStatus.ERROR);
    }
  }

  public void checkStatus(String agentId, RestTemplate restTemplate) {
    try {
      // First, authenticate to get the JWT token
      String loginUrl = url + "/api/auth/login";
      LoginRequest loginRequest = new LoginRequest(clientId, clientSecret);

      HttpHeaders loginHeaders = new HttpHeaders();
      loginHeaders.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<LoginRequest> loginEntity = new HttpEntity<>(loginRequest, loginHeaders);

      ResponseEntity<LoginResponse> loginResponse =
          restTemplate.exchange(loginUrl, HttpMethod.POST, loginEntity, LoginResponse.class);

      LoginResponse loginBody = loginResponse.getBody();
      if (loginBody == null || loginBody.getToken() == null) {
        log.error("Failed to authenticate - no token received");
        setStatus(ServerStatus.ERROR);
        addInteraction(
            new ServerInteraction(
                InteractionType.STATUS_CHECK, "Failed to authenticate: No token received"));
        return;
      }

      String token = loginBody.getToken();

      // Now check the agent status using the token
      String checkUrl = url + "/api/v1/agents/" + agentId;

      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(token);
      HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

      ResponseEntity<AgentStatusResponse> response =
          restTemplate.exchange(checkUrl, HttpMethod.GET, requestEntity, AgentStatusResponse.class);

      AgentStatusResponse agentStatus = response.getBody();
      if (agentStatus != null && agentStatus.getStatus() != null) {
        AgentStatus newStatus = agentStatus.getStatus();
        if (status == ServerStatus.PENDING && newStatus == AgentStatus.ACTIVE) {
          setStatus(ServerStatus.ACTIVE);
          addInteraction(
              new ServerInteraction(
                  InteractionType.STATUS_CHECK,
                  String.format("Agent status changed to %s", newStatus)));
        } else if (newStatus == AgentStatus.INACTIVE && status == ServerStatus.ACTIVE) {
          setStatus(ServerStatus.INACTIVE);
          addInteraction(
              new ServerInteraction(
                  InteractionType.STATUS_CHECK,
                  String.format("Agent status changed to %s", newStatus)));
        }
      }
    } catch (Exception e) {
      log.error("Error checking agent status for agent {}", agentId, e);
      setStatus(ServerStatus.ERROR);
      addInteraction(
          new ServerInteraction(
              InteractionType.STATUS_CHECK,
              String.format("Failed to check agent status: %s", e.getMessage())));
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Server server = (Server) o;
    return Objects.equals(id, server.id)
        && Objects.equals(url, server.url)
        && Objects.equals(name, server.name)
        && Objects.equals(clientId, server.clientId)
        && Objects.equals(clientSecret, server.clientSecret)
        && status == server.status;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, url, name, clientId, clientSecret, status);
  }

  @Override
  public String toString() {
    return "Server{"
        + "id="
        + id
        + ", url='"
        + url
        + '\''
        + ", name='"
        + name
        + '\''
        + ", clientId='"
        + clientId
        + '\''
        + ", status="
        + status
        + '}';
  }
}
