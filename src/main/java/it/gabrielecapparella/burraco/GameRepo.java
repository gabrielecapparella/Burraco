package it.gabrielecapparella.burraco;

import com.sun.jersey.spi.resource.Singleton;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/games")
public class GameRepo {
    private static Map<Integer, Game> id2game = new HashMap<>();
    private static GameRepo instance;

    public GameRepo() {
        instance = this;
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String createGame(String params) {
        // TODO
        //if (!(numPlayers==2 || numPlayers==4)) throw new Exception("Only 2 or 4 players supported.");
        return "New game endpoint";
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getGames() {
        // TODO should output ids and description
        return "Games list";
    }

    @GET
    @Path("{gameId}")
    public Game getGameById(@PathParam("gameId") int gameId) {
        return id2game.get(gameId);
    }

    @DELETE
    @Path("{gameId}")
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteGameById(@PathParam("gameId") int gameId) {
        // TODO
        return "Delete Specific game "+gameId;
    }

    public static GameRepo getInstance() {
        if (instance == null) {
            instance = new GameRepo();
        }
        return instance;
    }
}