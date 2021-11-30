package it.gabrielecapparella.burraco.users.dto;

import it.gabrielecapparella.burraco.users.User;
import it.gabrielecapparella.burraco.users.UserRole;

public class UserRegistrationDTO {
	private String username;
	private String password;
	private String email;
	private UserRole role;

	public UserRegistrationDTO(User user) {
		this.username = user.getUsername();
		this.password = null;
		this.email = user.getEmail();
		this.role = user.getUserRole();
	}

	public UserRegistrationDTO(String username, String password, String email, UserRole role) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.role = role;
	}

	public String getUsername() { return username; }

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public UserRole getRole() {
		return role;
	}
}
