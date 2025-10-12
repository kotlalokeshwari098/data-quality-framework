package eu.bbmri_eric.quality.server.agent;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(name = "Agent Registration Request")
public record AgentRegistrationRequest(
    @NotBlank(message = "Agent ID cannot be blank")
        @Pattern(
            regexp =
                "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "Agent ID must be a valid UUID format")
        String id) {}
