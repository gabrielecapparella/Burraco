package it.gabrielecapparella.burraco.controllers;

import it.gabrielecapparella.burraco.users.User;
import it.gabrielecapparella.burraco.users.UserRepository;
import it.gabrielecapparella.burraco.users.UserRole;
import it.gabrielecapparella.burraco.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Controller
public class UserController {

	private UserService userService;
	private UserRepository userRepository;

	@Autowired
	public UserController(UserService userService, UserRepository userRepository) {
		this.userService = userService;
		this.userRepository = userRepository;
		User u = new User();
		u.setEmail("polpetta@gne.it");
		u.setPassword("al sugo");
		u.setUsername("polpy");
		u.setUserRole(UserRole.USER);
		this.userRepository.saveAndFlush(u);
	}

	@GetMapping("/user/me")
	public Object user(Principal principal) {
		return principal;
	}

	@GetMapping(path="/user/{username}")
	public String getUser(@PathVariable String username) {
		return this.userService.loadUserByUsername(username).toString();
	}

	@GetMapping(path="/login/oauth2/success")
	public String changeUser() {
		UserDetails u = this.userService.loadUserByUsername("polpy");
		Authentication authentication = new UsernamePasswordAuthenticationToken(u, null, null);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return "index";
	}
}
