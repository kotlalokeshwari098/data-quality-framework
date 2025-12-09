package eu.bbmri_eric.quality.server.dataquality.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** Request DTO for creating a new report. */
@Schema(name = "Report Create Request", description = "Request object for creating a new report")
public record ReportCreateRequest(
    @Valid
        @NotEmpty(message = "Results cannot be empty")
        @Schema(
            description = "List of quality check results",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<QualityCheckResultDTO> results) {}
