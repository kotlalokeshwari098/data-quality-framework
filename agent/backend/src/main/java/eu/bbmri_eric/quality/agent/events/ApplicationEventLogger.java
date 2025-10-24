package eu.bbmri_eric.quality.agent.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Centralized logger for all application events.
 *
 * <p>This component asynchronously logs all application events to provide comprehensive audit
 * trails and debugging information. Events are logged with their simple class name.
 *
 * <p>Logging is done asynchronously to avoid blocking the main application flow.
 */
@Component
public class ApplicationEventLogger {

  private static final Logger log = LoggerFactory.getLogger(ApplicationEventLogger.class);

  /**
   * Logs any application event.
   *
   * @param event the ApplicationEvent
   */
  @EventListener
  @Async("asyncEventExecutor")
  public void logApplicationEvent(ApplicationEvent event) {
    log.info("EVENT: {}", event.getClass().getSimpleName());
  }
}
