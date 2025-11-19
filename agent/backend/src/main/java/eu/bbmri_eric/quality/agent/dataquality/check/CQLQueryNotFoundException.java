package eu.bbmri_eric.quality.agent.dataquality.check;

/** Exception thrown when a CQL query is not found. */
public class CQLQueryNotFoundException extends RuntimeException {
  public CQLQueryNotFoundException(Long id) {
    super("CQL query not found with id: " + id);
  }
}
