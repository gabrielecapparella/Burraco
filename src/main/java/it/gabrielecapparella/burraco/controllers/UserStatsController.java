package it.gabrielecapparella.burraco.controllers;

import it.gabrielecapparella.burraco.exceptions.ForbiddenException;
import it.gabrielecapparella.burraco.exceptions.UserDoesNotExistsException;
import it.gabrielecapparella.burraco.users.*;
import it.gabrielecapparella.burraco.users.dto.UserStatsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user-stats")
public class UserStatsController {

	private final UserService userService;
	private final UserRepository userRepository;

	@Autowired
	public UserStatsController(UserService userService, UserRepository userRepository) {
		this.userService = userService;
		this.userRepository = userRepository;
	}

	@GetMapping("/{id}")
	public UserStatsDTO findById(@PathVariable("id") Long id) {
		User user = this.userRepository.getById(id);
		if (user == null) throw new UserDoesNotExistsException();
		return new UserStatsDTO(user);
	}

	@DeleteMapping("/{id}")
	public UserStatsDTO findById(@AuthenticationPrincipal User user, @PathVariable("id") Long id) {
		if (user.getId().equals(id)) {
			return this.userService.resetStats(user);
		} else if (user.getUserRole() == UserRole.ADMIN) {
			Optional<User> toReset = this.userRepository.findById(id);
			if (toReset.isPresent()) return this.userService.resetStats(toReset.get());
			else throw new UserDoesNotExistsException();
		} else {
			throw new ForbiddenException();
		}
	}
}
