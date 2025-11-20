package eu.bbmri_eric.quality.agent.dataquality.exception;

public class ReportNotFoundException extends RuntimeException {
  public ReportNotFoundException(Long id) {
    super("Report not found with id: " + id);
  }
}
