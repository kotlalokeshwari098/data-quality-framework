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
    CreateAgentDto createDto = new CreateAgentDto(agentId);
    mockMvc
        .perform(
            post("/api/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(agentId))
        .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/agents/" + agentId))
        .andExpect(jsonPath("$._links.agents.href").value("http://localhost/api/agents"));
    assert agentRepository.findById(agentId).isPresent();
  }

  @Test
  void create_shouldReturnBadRequestForInvalidUuid() throws Exception {
    CreateAgentDto createDto = new CreateAgentDto("invalid-uuid");
    mockMvc
        .perform(
            post("/api/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_shouldReturnBadRequestForBlankId() throws Exception {
    CreateAgentDto createDto = new CreateAgentDto("");
    mockMvc
        .perform(
            post("/api/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_shouldReturnBadRequestForNullId() throws Exception {
    CreateAgentDto createDto = new CreateAgentDto(null);

    mockMvc
        .perform(
            post("/api/agents")
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
        .perform(get("/api/agents/{id}", agentId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(agentId))
        .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/agents/" + agentId))
        .andExpect(jsonPath("$._links.agents.href").value("http://localhost/api/agents"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void findById_shouldReturnNotFoundWhenAgentDoesNotExist() throws Exception {
    String nonExistentAgentId = UUID.randomUUID().toString();
    mockMvc.perform(get("/api/agents/{id}", nonExistentAgentId)).andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void listAll_shouldReturnEmptyListWithHateoasLinksWhenNoAgents() throws Exception {
    mockMvc
        .perform(get("/api/agents"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded").doesNotExist())
        .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/agents"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void listAll_shouldReturnAllAgentsWithHateoasLinks() throws Exception {
    String agentId1 = UUID.randomUUID().toString();
    String agentId2 = UUID.randomUUID().toString();
    agentRepository.save(new Agent(agentId1));
    agentRepository.save(new Agent(agentId2));
    mockMvc
        .perform(get("/api/agents"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.agents").isArray())
        .andExpect(jsonPath("$._embedded.agents.length()").value(2))
        .andExpect(jsonPath("$._embedded.agents[?(@.id == '" + agentId1 + "')]").exists())
        .andExpect(jsonPath("$._embedded.agents[?(@.id == '" + agentId2 + "')]").exists())
        .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/agents"))
        .andExpect(jsonPath("$._embedded.agents[0]._links.self.href").exists())
        .andExpect(jsonPath("$._embedded.agents[1]._links.self.href").exists());
  }

  @Test
  void create_shouldHandleDuplicateAgentId() throws Exception {
    String agentId = UUID.randomUUID().toString();
    Agent existingAgent = new Agent(agentId);
    agentRepository.save(existingAgent);
    CreateAgentDto createDto = new CreateAgentDto(agentId);
    mockMvc
        .perform(
            post("/api/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isConflict());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void endToEndFlow_createAndRetrieveAgent() throws Exception {
    String agentId = UUID.randomUUID().toString();
    CreateAgentDto createDto = new CreateAgentDto(agentId);

    mockMvc
        .perform(
            post("/api/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isCreated());

    mockMvc
        .perform(get("/api/agents/{id}", agentId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(agentId));

    mockMvc
        .perform(get("/api/agents"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.agents[?(@.id == '" + agentId + "')]").exists());
  }
}
