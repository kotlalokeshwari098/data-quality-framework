package eu.bbmri_eric.quality.agent.dataquality.check;

import eu.bbmri_eric.quality.agent.dataquality.fhir.FHIRStore;
import java.util.Map;

interface StratifiedDataQualityCheck extends DataQualityCheck {
  Map<String, Result> executeWithStratification(FHIRStore fhirStore);
}
