package eu.bbmri_eric.quality.agent.server;

import org.springframework.data.repository.CrudRepository;

/**
 * Repository interface for {@link Server} entity persistence operations.
 *
 * <p>Provides CRUD operations for managing central server configurations in the database.
 */
public interface ServerRepository extends CrudRepository<Server, Long> {}
