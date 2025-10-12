package eu.bbmri_eric.quality.server.common;

public class EntityNotFoundException extends RuntimeException {
  public EntityNotFoundException(String message) {
    super(message);
  }

  public EntityNotFoundException() {
    super("Entity not found");
  }
}
