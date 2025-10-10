package eu.bbmri_eric.quality.server.agent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateAgentDto(
    @NotBlank(message = "Agent ID cannot be blank")
        @Pattern(
            regexp =
                "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "Agent ID must be a valid UUID format")
        String id) {}
