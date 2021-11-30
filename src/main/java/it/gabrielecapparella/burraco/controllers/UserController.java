package it.gabrielecapparella.burraco.controllers;

import it.gabrielecapparella.burraco.exceptions.ForbiddenException;
import it.gabrielecapparella.burraco.exceptions.UserAlreadyExistsException;
import it.gabrielecapparella.burraco.exceptions.UserDoesNotExistsException;
import it.gabrielecapparella.burraco.users.*;
import it.gabrielecapparella.burraco.users.dto.UserRegistrationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;
	private final UserRepository userRepository;

	@Autowired
	public UserController(UserService userService, UserRepository userRepository) {
		this.userService = userService;
		this.userRepository = userRepository;
	}

	@GetMapping
	@Secured("ADMIN")
	public List<User> findAll() {
		return this.userRepository.findAll();
	}

	@GetMapping("/{id}")
	public UserRegistrationDTO findById(@AuthenticationPrincipal User user, @PathVariable("id") Long id) {
		if (user.getId().equals(id)) {
			return new UserRegistrationDTO(user);
		} else if (user.getUserRole() == UserRole.ADMIN) {
			Optional<User> toGet = this.userRepository.findById(id);
			if (toGet.isPresent()) return new UserRegistrationDTO(toGet.get());
			else throw new UserDoesNotExistsException();
		} else {
			throw new ForbiddenException();
		}
	}

	@GetMapping("/me")
	public UserRegistrationDTO user(@AuthenticationPrincipal User user) {
		return this.findById(user, user.getId());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UserRegistrationDTO create(@RequestBody UserRegistrationDTO userDTO) {
		// TODO: check for validity
		User user = this.userService.registerUser(userDTO);
		if (user == null) throw new UserAlreadyExistsException();
		else return new UserRegistrationDTO(user);
	}

	@PutMapping("/{id}")
	public UserRegistrationDTO editUser(@AuthenticationPrincipal User user, @PathVariable("id") Long id, @RequestBody UserRegistrationDTO userDTO) {
		if (user.getId().equals(id)) {
			return new UserRegistrationDTO(this.userService.editUser(user, userDTO));
		} else if (user.getUserRole() == UserRole.ADMIN) {
			Optional<User> toEdit = this.userRepository.findById(id);
			if (toEdit.isPresent()) return new UserRegistrationDTO(this.userService.editUser(toEdit.get(), userDTO));
			else throw new UserDoesNotExistsException();
		} else {
			throw new ForbiddenException();
		}
	}

//	@GetMapping(path="/login/oauth2/success")
//	public RedirectView oauthRedirect(@AuthenticationPrincipal OAuth2User principal) {
//		RedirectView redirectView;
//		String google_id = principal.getAttribute("sub");
//		User currentUser = this.userService.loadUserByGoogleId(google_id);
//		if(currentUser==null) { // new user
//			String email = principal.getAttribute("email");
//			String avatarUrl = principal.getAttribute("picture");
//			currentUser = this.userService.registerUser(email, google_id, UserRole.USER);
//
//			this.downloadAvatar(avatarUrl, currentUser.getId());
//
//			redirectView = new RedirectView("/user");
//		} else {
//			redirectView = new RedirectView("/");
//		}
//		Authentication authentication = new BurracoAuthentication(currentUser);
//		SecurityContextHolder.getContext().setAuthentication(authentication);
//
//		return redirectView;
//	}
}

