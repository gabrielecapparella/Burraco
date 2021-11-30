package it.gabrielecapparella.burraco.users;

import it.gabrielecapparella.burraco.users.dto.UserRegistrationDTO;
import it.gabrielecapparella.burraco.users.dto.UserStatsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;

		this.registerUser(new UserRegistrationDTO("Amatriciana", "aaa", "amatriciana@gmail.com", UserRole.TEST));
		this.registerUser(new UserRegistrationDTO("Boscaiola", "bbb", "boscaiola@gmail.com", UserRole.TEST));
		this.registerUser(new UserRegistrationDTO("Admin", "admin", "admin@gmail.com", UserRole.ADMIN));
	}

	@Override
	public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
		User user = this.userRepository.findByUsername(s);
		if (user == null) throw new UsernameNotFoundException(s);
		return user;
	}

	public UserStatsDTO resetStats(User user) {
		user.setMatchesAbandoned(0);
		user.setMatchesDrawn(0);
		user.setMatchesPlayed(0);
		user.setMatchesLost(0);
		user.setScore(0);
		this.userRepository.save(user);
		return new UserStatsDTO(user);
	}

	public User editUser(User user, UserRegistrationDTO userDTO) {
		// TODO: manage avatar
		user.setUsername(userDTO.getUsername());
		user.setEmail(userDTO.getEmail());
		String pwd = userDTO.getPassword();
		if (pwd != null) user.setPassword(passwordEncoder.encode(pwd));
		this.userRepository.save(user);
		return user;
	}

	public User registerUser(UserRegistrationDTO userDTO) {
		// TODO: add avatar
		if (this.userExists(userDTO.getUsername(), userDTO.getEmail())) {
			return null;
		} else {
			User newUser = new User(
				userDTO.getUsername(),
				passwordEncoder.encode(userDTO.getPassword()),
				userDTO.getEmail(),
				userDTO.getRole()
			);
			this.userRepository.save(newUser);
			return newUser;
		}
	}

	private boolean userExists(String username, String email) {
		if (this.userRepository.findByUsername(username) != null) return true;
		if (this.userRepository.findByEmail(email) != null) return true;
		return false;
	}
}
