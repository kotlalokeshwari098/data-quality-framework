package eu.bbmri_eric.quality.agent.dataquality.report;

import eu.bbmri_eric.quality.agent.dataquality.events.DQCheckResultsGathered;
import eu.bbmri_eric.quality.agent.dataquality.events.NewReportEvent;
import eu.bbmri_eric.quality.agent.dataquality.fhir.FHIRStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RepositoryEventHandler
class ReportEventHandler {

  private static final Logger log = LoggerFactory.getLogger(ReportEventHandler.class);
  private final ApplicationEventPublisher publisher;
  private final ReportRepository reportRepository;
  private final FHIRStore fhirStore;

  public ReportEventHandler(
      ApplicationEventPublisher publisher, ReportRepository reportRepository, FHIRStore fhirStore) {
    this.publisher = publisher;
    this.reportRepository = reportRepository;
    this.fhirStore = fhirStore;
  }

  @HandleAfterCreate
  @Transactional
  public void onAfterCreate(Report report) {
    publisher.publishEvent(new NewReportEvent(this, report.getId()));
  }

  @EventListener
  @Transactional
  void onResultsGathered(DQCheckResultsGathered event) {
    reportRepository
        .findById(event.getReportId())
        .ifPresent(
            report -> {
              int count = fhirStore.countResources("Patient");
              report.setNumberOfEntities(count);
              report.setStatus(Status.GENERATED);
              publisher.publishEvent(new ReportGeneratedEvent(this, report.getId()));
            });
  }
}
