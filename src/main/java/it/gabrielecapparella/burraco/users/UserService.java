package it.gabrielecapparella.burraco.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

	private UserRepository userRepository;

	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
		userRepository.saveAndFlush(new User("Boscaiola", "boscaiola@gmail.com", "2468", UserRole.TEST));
		userRepository.saveAndFlush(new User("Amatriciana", "amatriciana@gmail.com", "1357", UserRole.TEST));
	}

	@Override
	public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
		return this.userRepository.findByUsername(s);
	}

	public User loadUserByGoogleId(String s)  {
		return this.userRepository.findByGoogleId(s);
	}

	public User loadTestUser(String username) {
		User testUser = this.userRepository.findByUsernameAndUserRole(username, UserRole.TEST);
		return testUser;
	}

	public User registerUser(String email, String googleId, UserRole role) {
		User newUser = new User();
		String username = email.split("@")[0];
		newUser.setUsername(username);
		newUser.setEmail(email);
		newUser.setGoogleId(googleId);
		newUser.setUserRole(role);
		this.userRepository.saveAndFlush(newUser);
		return newUser;
	}
}
