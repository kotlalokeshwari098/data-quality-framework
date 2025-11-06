package eu.bbmri_eric.quality.server.setting;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/settings")
@Tag(name = "Settings Management", description = "APIs for managing application settings")
public class SettingController {

  private final SettingServiceImpl settingService;

  public SettingController(SettingServiceImpl settingService) {
    this.settingService = settingService;
  }

  @Operation(summary = "Get all settings", description = "Retrieves application settings.")
  @GetMapping
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<SettingDTO> getSettings() {
    SettingDTO settings = settingService.getSettings();
    return ResponseEntity.ok(settings);
  }

  @Operation(summary = "Update settings", description = "Updates application settings.")
  @PatchMapping
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<SettingDTO> updateSettings(SettingDTO settingDTO) {
    SettingDTO updatedSettings = settingService.updateSettings(settingDTO);
    return ResponseEntity.ok(updatedSettings);
  }
}
