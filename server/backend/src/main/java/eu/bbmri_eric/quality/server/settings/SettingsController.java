package eu.bbmri_eric.quality.server.settings;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/settings")
@Tag(name = "Settings Management", description = "APIs for managing application settings")
public class SettingsController {

  private final SettingsService settingsService;

  public SettingsController(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @Operation(summary = "Get all settings", description = "Retrieves application settings.")
  @GetMapping
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<SettingsDTO> getSettings() {
    SettingsDTO settings = settingsService.getSettings();
    return ResponseEntity.ok(settings);
  }
}
