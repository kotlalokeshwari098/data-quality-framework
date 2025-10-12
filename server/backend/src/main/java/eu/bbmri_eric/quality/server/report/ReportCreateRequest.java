package eu.bbmri_eric.quality.server.report;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/** Request DTO for creating a new report. */
@Schema(name = "Report Create Request", description = "Request object for creating a new report")
public record ReportCreateRequest(
    @NotBlank(message = "Agent ID cannot be blank")
        @Schema(
            description = "ID of the agent generating this report",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String agentId) {}
