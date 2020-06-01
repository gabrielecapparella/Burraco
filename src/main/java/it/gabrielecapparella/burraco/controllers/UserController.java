package it.gabrielecapparella.burraco.controllers;

import it.gabrielecapparella.burraco.users.BurracoAuthentication;
import it.gabrielecapparella.burraco.users.User;
import it.gabrielecapparella.burraco.users.UserRole;
import it.gabrielecapparella.burraco.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.view.RedirectView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Controller
public class UserController {

	private UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/user")
	public Object user(@AuthenticationPrincipal User user, Model model) {
		model.addAttribute("username", user.getUsername());
		model.addAttribute("email", user.getEmail());
		model.addAttribute("avatar", "/avatars/"+user.getId()+".jpg");
		return user;
	}

	@GetMapping(path="/user/{username}")
	public String getUser(@PathVariable String username) {
		return this.userService.loadUserByUsername(username).toString();
	}

	@GetMapping(path="/test/{username}")
	public RedirectView getTestUser(@PathVariable String username) {
		User testUser = this.userService.loadTestUser(username);
		Authentication authentication = new BurracoAuthentication(testUser);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return new RedirectView("/game/carbonara");
	}

	@GetMapping(path="/login/oauth2/success")
	public RedirectView oauthRedirect(@AuthenticationPrincipal OAuth2User principal) {
		RedirectView redirectView;
		String google_id = principal.getAttribute("sub");
		User currentUser = this.userService.loadUserByGoogleId(google_id);
		if(currentUser==null) { // new user
			String email = principal.getAttribute("email");
			String avatarUrl = principal.getAttribute("picture");
			currentUser = this.userService.registerUser(email, google_id, UserRole.USER);

			this.downloadAvatar(avatarUrl, currentUser.getId());

			redirectView = new RedirectView("/user");
		} else {
			redirectView = new RedirectView("/");
		}
		Authentication authentication = new BurracoAuthentication(currentUser);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return redirectView;
	}

	private void downloadAvatar(String url, long userId) {
		try {
			URL urlObj = new URL(url);
			BufferedImage img = ImageIO.read(urlObj);
			String filename = "src/main/resources/static/avatars/"+userId+".jpg";
			File file = new File(filename);
			ImageIO.write(img, "jpg", file);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
