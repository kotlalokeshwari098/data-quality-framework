package eu.bbmri_eric.quality.server.common;

/** DTO for system counts. */
public class CountsDTO {
  private long agents;
  private long reports;

  public CountsDTO() {}

  public CountsDTO(long agents, long reports) {
    this.agents = agents;
    this.reports = reports;
  }

  public long getAgents() {
    return agents;
  }

  public void setAgents(long agents) {
    this.agents = agents;
  }

  public long getReports() {
    return reports;
  }

  public void setReports(long reports) {
    this.reports = reports;
  }
}
