package eu.bbmri_eric.quality.agent.report;

public interface IReportService {

  /**
   * Generate a report transactionally. This method creates a new report and triggers the report
   * generation process.
   */
  void generateReportTransactional();
}
