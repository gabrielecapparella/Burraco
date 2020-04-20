package it.gabrielecapparella.burraco;

import javax.websocket.*;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/game/{gameId}",
		decoders = MsgDecoder.class,
		encoders = MsgEncoder.class )
public class PlayerSession {
	private static GameRepo Games = GameRepo.getInstance();
	private Game game;
	private Player player;
	private Session session;

	@OnOpen
	public void onOpen(@PathParam("gameId") String gameId, Session session) throws IOException {
		System.out.println("onOpen:" + session.getId());

		this.game = Games.getGameById(gameId);
		if(this.game==null) {
			session.close(new CloseReason(CloseCodes.CANNOT_ACCEPT, "Game doesn't exist"));
		} else if (this.game.isRunning) {
			session.close(new CloseReason(CloseCodes.CANNOT_ACCEPT, "Game already running"));
		}
		this.session = session;
		this.player = this.game.join(this);
	}

	@OnMessage
	public void onMessage(Message msg, Session session) {
		System.out.println(msg.toString());
		switch (msg.type) {
			case DRAW:
				this.player.drawCard();
				break;
			case PICK:
				this.player.pickDiscard();
				break;
			case MELD:
				String[] args = msg.content.split(";");
				int runIndex = Integer.parseInt(args[0]);
				CardSet cards = new CardSet(args[1]);
				this.player.meld(cards, runIndex);
				break;
			case DISCARD:
				Card c = new Card(msg.content);
				this.player.discard(c);
				break;
			case CHAT:
				this.game.broadcast(msg);
				break;
			case EXIT:// TODO may use onClose instead
				break;
		}
	}

	@OnError
	public void onError(Throwable t) {
		System.out.println("onError::" + t.getMessage());
		t.printStackTrace();
		//The onerror event is fired when something wrong occurs between the communications.
		// The event onerror is followed by a connection termination, which is a close event.
	}

	@OnClose
	public void onClose(Session session, CloseReason cReason) { // TODO
		// this is called when the client closes the connection
		System.out.println("onClose::" +  session.getId());
		System.out.println("closeReason::" +cReason.toString());

	}

	public boolean send(Message msg) {
		try {
			this.session.getBasicRemote().sendObject(msg);
			return true;
		} catch (IOException | EncodeException e) {
			e.printStackTrace();
			return false;
		}
	}
}

// session.getUserPrincipal() returns the authenticated user or null
// session.getUserProperties() returns a map
// javax.websocket.ClientEndpoint is used to denote that a POJO is a web socket client
