package eu.bbmri_eric.quality.server.setting;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

@Entity
public class Setting {

  @Id
  @NotNull
  @Column(name = "setting_name")
  private String name;

  @NotNull
  @Column(name = "setting_value")
  private String value;

  protected Setting() {}

  public Setting(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Setting settings = (Setting) o;
    return Objects.equals(name, settings.name) && Objects.equals(value, settings.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, value);
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
