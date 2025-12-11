package eu.bbmri_eric.quality.agent.dataquality.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DTO for CQL Query entity. */
@Schema(name = "CQL Query", description = "A CQL-based data quality check")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CQLQueryDTO {

  @Schema(description = "Unique identifier of the CQL query", example = "1")
  private Long id;

  @Schema(description = "Name of the CQL query", example = "Patient Age Validation")
  private String name;

  @Schema(
      description = "Description of what the check validates",
      example = "Validates patient ages are within acceptable range")
  private String description;

  @Schema(
      description = "CQL query string",
      example = "library PatientAgeValidation version '1.0.0'...")
  private String query;

  @Schema(description = "Warning threshold percentage", example = "10")
  private int warningThreshold;

  @Schema(description = "Error threshold percentage", example = "30")
  private int errorThreshold;

  @Schema(description = "Epsilon budget for differential privacy", example = "1.0")
  private float epsilonBudget;

}
