package eu.bbmri_eric.quality.server.report;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** Request DTO for creating a new report. */
@Schema(name = "Report Create Request", description = "Request object for creating a new report")
public record ReportCreateRequest(
    @Valid
        @Schema(
            description = "List of quality check results",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "Results cannot be empty")
        List<QualityCheckResultDTO> results) {}
