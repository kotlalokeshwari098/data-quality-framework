package eu.bbmri_eric.quality.agent.server;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validation annotation to ensure a string is valid Base64 encoded data.
 *
 * <p>This annotation can be applied to fields that should contain Base64 encoded values, such as
 * client secrets or passwords.
 */
@Documented
@Constraint(validatedBy = Base64Validator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Base64Encoded {

  /**
   * Error message when validation fails.
   *
   * @return the error message
   */
  String message() default "Value must be Base64 encoded";

  /**
   * Validation groups.
   *
   * @return the validation groups
   */
  Class<?>[] groups() default {};

  /**
   * Payload for clients of the Bean Validation API.
   *
   * @return the payload
   */
  Class<? extends Payload>[] payload() default {};
}
