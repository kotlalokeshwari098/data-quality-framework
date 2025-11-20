package eu.bbmri_eric.quality.agent.dataquality.event;

import org.springframework.context.ApplicationEvent;

public class ReportGeneratedEvent extends ApplicationEvent {
  private final Long reportId;

  public ReportGeneratedEvent(Object source, Long reportId) {
    super(source);
    this.reportId = reportId;
  }

  public Long getReportId() {
    return reportId;
  }
}
