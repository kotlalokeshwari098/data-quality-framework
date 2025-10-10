package eu.bbmri_eric.quality.server.agent;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AgentControllerIntegrationTest {
  public static final String API_V_1_AGENTS = "/api/v1/agents";
  public static final String API_V_1_AGENTS_ID = "/api/v1/agents/{id}";
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Autowired private AgentRepository agentRepository;

  @BeforeEach
  void setUp() {
    agentRepository.deleteAll();
  }

  @Test
  void create_shouldCreateAgentAndReturnHateoasResponse() throws Exception {
    String agentId = UUID.randomUUID().toString();
    AgentRegistrationRequest createDto = new AgentRegistrationRequest(agentId);
    mockMvc
        .perform(
            post(API_V_1_AGENTS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.agent.id").value(agentId));
    assert agentRepository.findById(agentId).isPresent();
  }

  @Test
  void create_shouldReturnBadRequestForInvalidUuid() throws Exception {
    AgentRegistrationRequest createDto = new AgentRegistrationRequest("invalid-uuid");
    mockMvc
        .perform(
            post(API_V_1_AGENTS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_shouldReturnBadRequestForBlankId() throws Exception {
    AgentRegistrationRequest createDto = new AgentRegistrationRequest("");
    mockMvc
        .perform(
            post(API_V_1_AGENTS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_shouldReturnBadRequestForNullId() throws Exception {
    AgentRegistrationRequest createDto = new AgentRegistrationRequest(null);

    mockMvc
        .perform(
            post(API_V_1_AGENTS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void findById_shouldReturnAgentWithHateoasLinksWhenExists() throws Exception {
    String agentId = UUID.randomUUID().toString();
    Agent agent = new Agent(agentId);
    agentRepository.save(agent);

    mockMvc
        .perform(get(API_V_1_AGENTS_ID, agentId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(agentId))
        .andExpect(
            jsonPath("$._links.self.href").value("http://localhost/api/v1/agents/" + agentId))
        .andExpect(jsonPath("$._links.agents.href").value("http://localhost/api/v1/agents"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void findById_shouldReturnNotFoundWhenAgentDoesNotExist() throws Exception {
    String nonExistentAgentId = UUID.randomUUID().toString();
    mockMvc.perform(get(API_V_1_AGENTS_ID, nonExistentAgentId)).andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void listAll_shouldReturnEmptyListWithHateoasLinksWhenNoAgents() throws Exception {
    mockMvc
        .perform(get(API_V_1_AGENTS))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded").doesNotExist())
        .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/v1/agents"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void listAll_shouldReturnAllAgentsWithHateoasLinks() throws Exception {
    String agentId1 = UUID.randomUUID().toString();
    String agentId2 = UUID.randomUUID().toString();
    agentRepository.save(new Agent(agentId1));
    agentRepository.save(new Agent(agentId2));
    mockMvc
        .perform(get(API_V_1_AGENTS))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.agents").isArray())
        .andExpect(jsonPath("$._embedded.agents.length()").value(2))
        .andExpect(jsonPath("$._embedded.agents[?(@.id == '" + agentId1 + "')]").exists())
        .andExpect(jsonPath("$._embedded.agents[?(@.id == '" + agentId2 + "')]").exists())
        .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/v1/agents"))
        .andExpect(jsonPath("$._embedded.agents[0]._links.self.href").exists())
        .andExpect(jsonPath("$._embedded.agents[1]._links.self.href").exists());
  }

  @Test
  void create_shouldHandleDuplicateAgentId() throws Exception {
    String agentId = UUID.randomUUID().toString();
    Agent existingAgent = new Agent(agentId);
    agentRepository.save(existingAgent);
    AgentRegistrationRequest createDto = new AgentRegistrationRequest(agentId);
    mockMvc
        .perform(
            post(API_V_1_AGENTS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isConflict());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void endToEndFlow_createAndRetrieveAgent() throws Exception {
    String agentId = UUID.randomUUID().toString();
    AgentRegistrationRequest createDto = new AgentRegistrationRequest(agentId);

    mockMvc
        .perform(
            post(API_V_1_AGENTS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isCreated());

    mockMvc
        .perform(get(API_V_1_AGENTS_ID, agentId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(agentId));

    mockMvc
        .perform(get(API_V_1_AGENTS))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.agents[?(@.id == '" + agentId + "')]").exists());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void update_shouldUpdateAgentNameAndReturnHateoasResponse() throws Exception {
    String agentId = UUID.randomUUID().toString();
    Agent agent = new Agent(agentId);
    agentRepository.save(agent);

    AgentUpdateRequest updateRequest = new AgentUpdateRequest("Updated Agent Name", null);

    mockMvc
        .perform(
            patch(API_V_1_AGENTS_ID, agentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(agentId))
        .andExpect(jsonPath("$.name").value("Updated Agent Name"))
        .andExpect(
            jsonPath("$._links.self.href").value("http://localhost/api/v1/agents/" + agentId))
        .andExpect(jsonPath("$._links.agents.href").value("http://localhost/api/v1/agents"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void update_shouldUpdateAgentStatusAndReturnHateoasResponse() throws Exception {
    String agentId = UUID.randomUUID().toString();
    Agent agent = new Agent(agentId);
    agentRepository.save(agent);

    AgentUpdateRequest updateRequest = new AgentUpdateRequest(null, AgentStatus.ACTIVE);

    mockMvc
        .perform(
            patch(API_V_1_AGENTS_ID, agentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(agentId))
        .andExpect(jsonPath("$.status").value("ACTIVE"))
        .andExpect(
            jsonPath("$._links.self.href").value("http://localhost/api/v1/agents/" + agentId));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void update_shouldUpdateBothNameAndStatus() throws Exception {
    String agentId = UUID.randomUUID().toString();
    Agent agent = new Agent(agentId);
    agentRepository.save(agent);

    AgentUpdateRequest updateRequest = new AgentUpdateRequest("Test Agent", AgentStatus.INACTIVE);

    mockMvc
        .perform(
            patch(API_V_1_AGENTS_ID, agentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(agentId))
        .andExpect(jsonPath("$.name").value("Test Agent"))
        .andExpect(jsonPath("$.status").value("INACTIVE"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void update_shouldReturnNotFoundWhenAgentDoesNotExist() throws Exception {
    String nonExistentAgentId = UUID.randomUUID().toString();
    AgentUpdateRequest updateRequest = new AgentUpdateRequest("New Name", AgentStatus.ACTIVE);

    mockMvc
        .perform(
            patch(API_V_1_AGENTS_ID, nonExistentAgentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void update_shouldHandleEmptyUpdateRequest() throws Exception {
    String agentId = UUID.randomUUID().toString();
    Agent agent = new Agent(agentId);
    agentRepository.save(agent);

    AgentUpdateRequest updateRequest = new AgentUpdateRequest(null, null);

    mockMvc
        .perform(
            patch(API_V_1_AGENTS_ID, agentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(agentId));
  }

  @Test
  void update_shouldRequireAuthentication() throws Exception {
    String agentId = UUID.randomUUID().toString();
    AgentUpdateRequest updateRequest = new AgentUpdateRequest("New Name", AgentStatus.ACTIVE);
    mockMvc
        .perform(
            patch(API_V_1_AGENTS_ID, agentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(roles = "USER")
  void update_shouldRequireAdminRole() throws Exception {
    String agentId = UUID.randomUUID().toString();
    AgentUpdateRequest updateRequest = new AgentUpdateRequest("New Name", AgentStatus.ACTIVE);

    mockMvc
        .perform(
            patch(API_V_1_AGENTS_ID, agentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void endToEndFlow_createUpdateAndRetrieveAgent() throws Exception {
    String agentId = UUID.randomUUID().toString();
    AgentRegistrationRequest createDto = new AgentRegistrationRequest(agentId);

    mockMvc
        .perform(
            post(API_V_1_AGENTS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isCreated());

    AgentUpdateRequest updateRequest = new AgentUpdateRequest("Updated Agent", AgentStatus.ACTIVE);
    mockMvc
        .perform(
            patch(API_V_1_AGENTS_ID, agentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Agent"))
        .andExpect(jsonPath("$.status").value("ACTIVE"));

    mockMvc
        .perform(get(API_V_1_AGENTS_ID, agentId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(agentId))
        .andExpect(jsonPath("$.name").value("Updated Agent"))
        .andExpect(jsonPath("$.status").value("ACTIVE"));
  }
}
