package eu.bbmri_eric.quality.agent.server;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Base64;

/**
 * Validator implementation for {@link Base64Encoded} annotation.
 *
 * <p>Validates that a string contains valid Base64 encoded data.
 */
public class Base64Validator implements ConstraintValidator<Base64Encoded, String> {

  @Override
  public void initialize(Base64Encoded constraintAnnotation) {
    // No initialization needed
  }

  /**
   * Validates that the given value is Base64 encoded.
   *
   * @param value the value to validate
   * @param context the validation context
   * @return true if the value is valid Base64, false otherwise
   */
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isEmpty()) {
      return true; // Use @NotBlank for null/empty validation
    }

    try {
      Base64.getDecoder().decode(value);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
