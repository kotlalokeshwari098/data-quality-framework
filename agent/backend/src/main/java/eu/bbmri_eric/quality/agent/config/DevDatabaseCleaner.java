package eu.bbmri_eric.quality.agent.config;

import jakarta.annotation.PreDestroy;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Component that automatically deletes the development database file on application shutdown.
 *
 * <p>This ensures a clean state for the next development session by removing the database file
 * when the application terminates, preventing issues with schema changes and stale data.
 *
 * <p>This component only runs when the "dev" Spring profile is active.
 */
@Component
@Profile("dev")
public class DevDatabaseCleaner {

  private static final Logger logger = LoggerFactory.getLogger(DevDatabaseCleaner.class);

  @Value("${spring.datasource.url}")
  private String datasourceUrl;

  /**
   * Deletes the development database file on shutdown if it exists.
   *
   * <p>Extracts the database file path from the JDBC URL and attempts to delete it. This runs
   * during application shutdown before the JVM terminates.
   */
  @PreDestroy
  public void cleanDevDatabase() {
    try {
      String dbPath = datasourceUrl.replace("jdbc:sqlite:", "");
      File dbFile = new File(dbPath);

      if (dbFile.exists()) {
        boolean deleted = dbFile.delete();
        if (deleted) {
          logger.info("Development database '{}' deleted successfully on shutdown", dbPath);
        }
      }
    } catch (Exception e) {
      logger.error("Error while attempting to clean development database", e);
    }
  }
}
