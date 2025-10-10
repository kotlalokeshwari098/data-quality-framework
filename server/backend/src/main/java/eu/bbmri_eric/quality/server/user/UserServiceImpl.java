package eu.bbmri_eric.quality.server.user;

import java.security.SecureRandom;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private static final String CHARACTERS =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
  private static final int DEFAULT_PASSWORD_LENGTH = 12;
  private final SecureRandom secureRandom = new SecureRandom();

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final ModelMapper modelMapper;

  UserServiceImpl(
      UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.modelMapper = modelMapper;
  }

  @Override
  public UserDTO createUser(UserCreateDTO userCreateDTO) {
    String rawPassword = generateRandomPassword();
    String encodedPassword = passwordEncoder.encode(rawPassword);
    User user = new User(userCreateDTO.getUsername(), encodedPassword, userCreateDTO.getAgentId());
    UserDTO savedUser = modelMapper.map(userRepository.save(user), UserDTO.class);
    savedUser.setTemporaryPassword(rawPassword);
    return savedUser;
  }

  private String generateRandomPassword() {
    return generateRandomPassword(DEFAULT_PASSWORD_LENGTH);
  }

  private String generateRandomPassword(int length) {
    if (length < 4) {
      throw new IllegalArgumentException("Password length must be at least 4 characters");
    }

    StringBuilder password = new StringBuilder(length);

    for (int i = 0; i < length; i++) {
      int randomIndex = secureRandom.nextInt(CHARACTERS.length());
      password.append(CHARACTERS.charAt(randomIndex));
    }

    return password.toString();
  }
}
