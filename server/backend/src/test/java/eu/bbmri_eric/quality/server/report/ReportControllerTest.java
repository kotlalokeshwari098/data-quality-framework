package eu.bbmri_eric.quality.server.report;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.bbmri_eric.quality.server.agent.Agent;
import eu.bbmri_eric.quality.server.agent.AgentRepository;
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
class ReportControllerTest {

  public static final String API_V1_AGENTS_REPORTS = "/api/v1/agents/{agentId}/reports";
  public static final String API_V1_REPORTS_ID = "/api/v1/reports/{id}";

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private ReportRepository reportRepository;
  @Autowired private AgentRepository agentRepository;

  private String testAgentId;

  @BeforeEach
  void setUp() {
    reportRepository.deleteAll();
    agentRepository.deleteAll();
    testAgentId = UUID.randomUUID().toString();
    Agent agent = new Agent(testAgentId);
    agentRepository.save(agent);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void create_shouldCreateReportAndReturnHateoasResponse() throws Exception {
    ReportCreateRequest createRequest = new ReportCreateRequest(testAgentId);

    mockMvc
        .perform(
            post(API_V1_AGENTS_REPORTS, testAgentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.agentId").value(testAgentId))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$._links.self.href").exists())
        .andExpect(jsonPath("$._links.reports.href").value("http://localhost/api/v1"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void create_shouldReturnBadRequestForBlankAgentId() throws Exception {
    ReportCreateRequest createRequest = new ReportCreateRequest("");

    mockMvc
        .perform(
            post(API_V1_AGENTS_REPORTS, testAgentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void create_shouldReturnBadRequestForNullAgentId() throws Exception {
    ReportCreateRequest createRequest = new ReportCreateRequest(null);

    mockMvc
        .perform(
            post(API_V1_AGENTS_REPORTS, testAgentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void findById_shouldReturnReportWithHateoasLinksWhenExists() throws Exception {
    Report report = new Report(testAgentId);
    reportRepository.save(report);

    mockMvc
        .perform(get(API_V1_REPORTS_ID, report.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(report.getId()))
        .andExpect(jsonPath("$.agentId").value(testAgentId))
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(
            jsonPath("$._links.self.href")
                .value("http://localhost/api/v1/reports/" + report.getId()))
        .andExpect(jsonPath("$._links.reports.href").value("http://localhost/api/v1"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void findById_shouldReturnNotFoundWhenReportDoesNotExist() throws Exception {
    String nonExistentReportId = UUID.randomUUID().toString();

    mockMvc.perform(get(API_V1_REPORTS_ID, nonExistentReportId)).andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void findByAgentId_shouldReturnEmptyListWithHateoasLinksWhenNoReports() throws Exception {
    mockMvc
        .perform(get(API_V1_AGENTS_REPORTS, testAgentId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded").doesNotExist())
        .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/v1"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void findByAgentId_shouldReturnAllReportsForAgentWithHateoasLinks() throws Exception {
    Report report1 = new Report(testAgentId);
    Report report2 = new Report(testAgentId);
    reportRepository.save(report1);
    reportRepository.save(report2);

    mockMvc
        .perform(get(API_V1_AGENTS_REPORTS, testAgentId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.reports").isArray())
        .andExpect(jsonPath("$._embedded.reports.length()").value(2))
        .andExpect(jsonPath("$._embedded.reports[?(@.id == '" + report1.getId() + "')]").exists())
        .andExpect(jsonPath("$._embedded.reports[?(@.id == '" + report2.getId() + "')]").exists())
        .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/v1"))
        .andExpect(jsonPath("$._embedded.reports[0]._links.self.href").exists())
        .andExpect(jsonPath("$._embedded.reports[1]._links.self.href").exists());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void findByAgentId_shouldNotReturnReportsFromOtherAgents() throws Exception {
    String otherAgentId = UUID.randomUUID().toString();
    Agent otherAgent = new Agent(otherAgentId);
    agentRepository.save(otherAgent);

    Report reportForTestAgent = new Report(testAgentId);
    Report reportForOtherAgent = new Report(otherAgentId);
    reportRepository.save(reportForTestAgent);
    reportRepository.save(reportForOtherAgent);

    mockMvc
        .perform(get(API_V1_AGENTS_REPORTS, testAgentId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.reports.length()").value(1))
        .andExpect(jsonPath("$._embedded.reports[0].id").value(reportForTestAgent.getId()))
        .andExpect(jsonPath("$._embedded.reports[0].agentId").value(testAgentId));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void endToEndFlow_createAndRetrieveReport() throws Exception {
    ReportCreateRequest createRequest = new ReportCreateRequest(testAgentId);

    String responseContent =
        mockMvc
            .perform(
                post(API_V1_AGENTS_REPORTS, testAgentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String reportId = objectMapper.readTree(responseContent).get("id").asText();

    mockMvc
        .perform(get(API_V1_REPORTS_ID, reportId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(reportId))
        .andExpect(jsonPath("$.agentId").value(testAgentId));

    mockMvc
        .perform(get(API_V1_AGENTS_REPORTS, testAgentId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.reports[?(@.id == '" + reportId + "')]").exists());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void create_shouldGenerateUniqueIdsForMultipleReports() throws Exception {
    ReportCreateRequest createRequest = new ReportCreateRequest(testAgentId);

    String response1 =
        mockMvc
            .perform(
                post(API_V1_AGENTS_REPORTS, testAgentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String response2 =
        mockMvc
            .perform(
                post(API_V1_AGENTS_REPORTS, testAgentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String reportId1 = objectMapper.readTree(response1).get("id").asText();
    String reportId2 = objectMapper.readTree(response2).get("id").asText();

    assert !reportId1.equals(reportId2);
  }

  @Test
  void create_shouldRequireAuthentication() throws Exception {
    ReportCreateRequest createRequest = new ReportCreateRequest(testAgentId);

    mockMvc
        .perform(
            post(API_V1_AGENTS_REPORTS, testAgentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void findById_shouldRequireAuthentication() throws Exception {
    Report report = new Report(testAgentId);
    reportRepository.save(report);

    mockMvc.perform(get(API_V1_REPORTS_ID, report.getId())).andExpect(status().isUnauthorized());
  }

  @Test
  void findByAgentId_shouldRequireAuthentication() throws Exception {
    mockMvc.perform(get(API_V1_AGENTS_REPORTS, testAgentId)).andExpect(status().isUnauthorized());
  }
}
