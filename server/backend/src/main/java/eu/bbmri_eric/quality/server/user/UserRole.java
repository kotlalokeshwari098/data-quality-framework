package eu.bbmri_eric.quality.server.user;

/** Enumeration representing the different roles a user can have in the system. */
public enum UserRole {
  /** Regular human user with standard permissions. */
  HUMAN_USER("ROLE_HUMAN_USER"),

  /** Administrator with elevated permissions. */
  ADMIN("ROLE_ADMIN");

  private final String authority;

  UserRole(String authority) {
    this.authority = authority;
  }

  /**
   * Gets the Spring Security authority string for this role.
   *
   * @return the authority string used by Spring Security
   */
  public String getAuthority() {
    return authority;
  }
}
