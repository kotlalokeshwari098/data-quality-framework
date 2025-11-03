package eu.bbmri_eric.quality.agent.report;

public class ReportNotFoundException extends RuntimeException {
  public ReportNotFoundException(Long id) {
    super("Report not found with id: " + id);
  }
}
