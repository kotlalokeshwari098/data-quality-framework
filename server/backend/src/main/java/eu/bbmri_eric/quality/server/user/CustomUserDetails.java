package eu.bbmri_eric.quality.server.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final UserDTO user;
    private final String password;

    CustomUserDetails(User user) {
        this.user = new UserDTO(user.getUsername(), user.getId().toString());
        this.password = user.getPassword();

    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public UserDTO getUser() {
        return user;
    }
}
