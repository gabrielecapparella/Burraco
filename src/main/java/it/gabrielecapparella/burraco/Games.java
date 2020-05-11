package it.gabrielecapparella.burraco;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Games {
	private Map<String, Game> id2game;

	public Games() {
		this.id2game = new HashMap<>();
	}

	public Game getGameById(String id) {
		return this.id2game.get(id);
	}

	public String createGame(GameInfo info) {
		String gameId = UUID.randomUUID().toString();
		this.createGame(info, gameId);
		return gameId;
	}

	public void createGame(GameInfo info, String gameId) {
		Game newGame = new Game(gameId, info.targetPoints, info.numPlayers);
		this.id2game.put(gameId, newGame);
	}

	public void deleteGame(String gameId) {
		this.id2game.remove(gameId);
	}

	public List<GameInfo> getGames() {
        List<GameInfo> out = new ArrayList<>();
        for (Game game : id2game.values()) {
            out.add(game.getDescription());
        }
        return out;
    }
}
