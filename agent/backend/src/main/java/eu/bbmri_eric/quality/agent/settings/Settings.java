package eu.bbmri_eric.quality.agent.settings;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

@Entity
public class Settings {

  @Id
  @NotNull
  @Column(name = "setting_name")
  private String name;

  @NotNull
  @Column(name = "setting_value")
  private String value;

  protected Settings() {}

  public Settings(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public void setName(String name) {
    this.name = name;
  }
}
