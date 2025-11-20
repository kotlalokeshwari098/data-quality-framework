package eu.bbmri_eric.quality.agent.dataquality.event;

import org.springframework.context.ApplicationEvent;

public class DQCheckResultsGathered extends ApplicationEvent {
  private Long reportId;

  public DQCheckResultsGathered(Object source) {
    super(source);
  }

  public DQCheckResultsGathered(Object source, Long reportId) {
    super(source);
    this.reportId = reportId;
  }

  public Long getReportId() {
    return reportId;
  }
}
