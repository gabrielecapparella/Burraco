package it.gabrielecapparella.burraco;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@RequestMapping("/games")
@RestController
public class GamesRest { // TODO: port to spring
    private Games games;

    @Autowired
    public GamesRest(Games games) {
        this.games = games;
        // TODO: remove before flight
        GameInfo info = new GameInfo();
        info.setNumPlayers(2);
        info.setTargetPoints(2005);
        this.games.createGame(info, "carbonara");
        System.out.println("created game carbonara");
    }

    //@POST
    //@Consumes(MediaType.APPLICATION_JSON)
    //@Produces(MediaType.TEXT_PLAIN)
//    public String createGame(GameInfo info) {
//        // in: howManyPlayers, targetPoints, (turnTimeout)
//        if (!info.validateParams()) return "nope";
//        //String gameId = UUID.randomUUID().toString();
//        String gameId = "carbonara";
//        id2game.put(gameId, new Game(gameId, info.targetPoints, info.numPlayers));
//
//        return "/game/"+gameId;
//    }

//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public String getGames() {
//        JSONArray ja = new JSONArray();
//        for (Game game : id2game.values()) {
//            ja.put(game.getDescription());
//        }
//        return ja.toString();
//    }

    @GetMapping("{gameId}")
    public String getGameDescr(@PathVariable String gameId) {
        Game g = this.games.getGameById(gameId);
        return g.getDescription();
    }

//    @DELETE
//    @Path("{gameId}")
//    public Response deleteGameById(@PathParam("gameId") String gameId) {
//        Game game = id2game.get(gameId);
//        if (game==null) return Response.status(404).build();
//        game.closeGame();
//        return Response.status(200).build();
//    }
}