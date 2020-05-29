package it.gabrielecapparella.burraco.controllers;

import it.gabrielecapparella.burraco.Game;
import it.gabrielecapparella.burraco.GameInfo;
import it.gabrielecapparella.burraco.Games;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class GamesRest {
    private Games gameRepo;

    @Autowired
    public GamesRest(Games games) {
        this.gameRepo = games;

        // TODO: remove before flight
        GameInfo info = new GameInfo();
        info.setNumPlayers(2);
        info.setTargetPoints(2005);
        this.gameRepo.createGame(info, "carbonara");
        System.out.println("created game carbonara");
    }

    @GetMapping("/games")
    public List<GameInfo> getAll() {
        return this.gameRepo.getGames();
    }

    @GetMapping(path="/games/{gameId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public GameInfo getGameDescr(@PathVariable String gameId) {
        Game g = this.gameRepo.getGameById(gameId);
        return g.getDescription();
    }

    @PostMapping(path="/games")
    @ResponseStatus(HttpStatus.CREATED)
    public String create(@RequestBody GameInfo gInfo) {
        if (!gInfo.validateParams()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return "/game/"+this.gameRepo.createGame(gInfo);
    }

    @DeleteMapping(path="/games/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable String gameId) {
        //TODO: check things like authorization, game is running, ...
        this.gameRepo.deleteGame(gameId);
    }
}