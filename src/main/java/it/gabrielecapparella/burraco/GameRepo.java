package it.gabrielecapparella.burraco;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

@Path("/games")
public class GameRepo {
    private static Map<String, Game> id2game = new ConcurrentHashMap<>();
    private static GameRepo instance;

    public GameRepo() {
        instance = this;
        GameInfo info = new GameInfo();
        info.setNumPlayers(2);
        info.setTargetPoints(2005);
        String id = createGame(info);
        System.out.println(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String createGame(GameInfo info) {
        // in: howManyPlayers, targetPoints, (turnTimeout)
        if (!info.validateParams()) return "nope";
        String gameId = UUID.randomUUID().toString();
        id2game.put(gameId, new Game(gameId, info.targetPoints, info.numPlayers));

        return "/game/"+gameId;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getGames() {
        JSONArray ja = new JSONArray();
        for (Game game : id2game.values()) {
            ja.put(game.getDescription());
        }
        return ja.toString();
    }

    //@GET
    //@Path("{gameId}")
    public Game getGameById(String gameId) {
        return id2game.get(gameId);
    }

    @DELETE
    @Path("{gameId}")
    public Response deleteGameById(@PathParam("gameId") String gameId) {
        Game game = id2game.get(gameId);
        if (game==null) return Response.status(404).build();
        game.closeGame();
        return Response.status(200).build();
    }

    public static GameRepo getInstance() {
        if (instance == null) {
            instance = new GameRepo();
        }
        return instance;
    }
}