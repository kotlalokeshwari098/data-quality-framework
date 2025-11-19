package eu.bbmri_eric.quality.agent.dataquality.server.client;

import eu.bbmri_eric.quality.agent.dataquality.report.ReportDTO;
import eu.bbmri_eric.quality.agent.dataquality.server.ServerConnectionStatus;

public interface CentralServerClient {
  RegistrationCredentials register() throws Exception;

  ServerConnectionStatus checkRegistrationStatus() throws Exception;

  void healthCheck();

  void updateAgentVersion(String version) throws Exception;

  void sendReport(ReportDTO reportDTO) throws Exception;
}
