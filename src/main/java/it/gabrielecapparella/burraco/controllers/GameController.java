package it.gabrielecapparella.burraco.controllers;

import it.gabrielecapparella.burraco.Game;
import it.gabrielecapparella.burraco.Games;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GameController {
	private Games gameRepo;

	@Autowired
	public GameController(Games games) {
		this.gameRepo = games;
	}

	@GetMapping(path="/game/{gameId}")
	public String getTable(@PathVariable String gameId, Model model) {
		Game g = this.gameRepo.getGameById(gameId);
		model.addAttribute("gameInfo", g.getDescription());
		return "game";
	}
}
