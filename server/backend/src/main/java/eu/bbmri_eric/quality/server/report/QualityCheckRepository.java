package eu.bbmri_eric.quality.server.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository for managing QualityCheck entities. */
@Repository
public interface QualityCheckRepository extends JpaRepository<QualityCheck, String> {}
