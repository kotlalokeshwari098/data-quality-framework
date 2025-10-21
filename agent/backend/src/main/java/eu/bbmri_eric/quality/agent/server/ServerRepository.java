package eu.bbmri_eric.quality.agent.server;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Server} entity persistence operations.
 *
 * <p>Provides CRUD operations for managing central server configurations in the database.
 */
@Repository
public interface ServerRepository extends CrudRepository<Server, String> {}
