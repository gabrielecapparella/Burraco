package it.gabrielecapparella.burraco;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ServerEndpoint(value = "/game/{gameId}",
		decoders = MsgDecoder.class,
		encoders = MsgEncoder.class )
public class PlayerSession {
	private static List<Session> sessions = new CopyOnWriteArrayList<>();
	private static GameRepo Games = GameRepo.getInstance();
	private Game game;
	private Player player;
	private String name;

	@OnOpen
	public void onOpen(@PathParam("gameId") String gameId, Session session) {
		System.out.println("onOpen:" + session.getId());

		this.game = Games.getGameById(gameId);
		if (this.game == null) {
			// TODO something bad
		}
		this.player = this.game.join();
		if (this.player == null) {
			// TODO something bad
		}
		sessions.add(session);
		session.getUserProperties().put("gameId", gameId); // used during broadcast
		this.name = session.getId();
		broadcast(gameId, new Message(MsgType.JOIN, this.name, null));
	}

	@OnClose
	public void onClose(@PathParam("gameId") Integer gameId, Session session) { // TODO
		System.out.println("onClose::" +  session.getId());

	}

	@OnMessage
	public void onMessage(@PathParam("gameId") String gameId, Message msg, Session session) {
		String pot;
		switch (msg.type) {
			case DRAW:
				if (!(this.player.turn==Turn.TAKE)) return;
				Card card = this.player.drawCard();
				if (card==null) return;
				String value = card.toString();

				this.sendMsg(session, new Message(MsgType.DRAW, "PlayerSession", value));
				broadcast(gameId, new Message(MsgType.DRAW, this.name, null));
				break;
			case PICK:
				if (!(this.player.turn==Turn.TAKE)) return;
				this.player.pickDiscard();
				broadcast(gameId, new Message(MsgType.PICK, this.name, null));
				break;
			case MELD:
				if (!(this.player.turn==Turn.DISCARD)) return;
				String[] args = msg.content.split(";");
				int runIndex = Integer.parseInt(args[0]);
				CardSet cards = new CardSet(args[1]);
				MeldRet mRet = this.player.meld(cards, runIndex);
				if (mRet==MeldRet.OK || mRet==MeldRet.POT) {
					broadcast(gameId, new Message(MsgType.MELD, this.name, msg.content));
				} else {
					this.sendMsg(session, new Message(MsgType.MELD, "PlayerSession", mRet.name()));
				}
				if (mRet==MeldRet.POT) {
					pot = this.player.getHand().toString();
					this.sendMsg(session, new Message(MsgType.HAND, "PlayerSession", pot));
					broadcast(gameId, new Message(MsgType.HAND, this.name, null));
				}
				break;
			case DISCARD:
				if (!(this.player.turn==Turn.DISCARD)) return;
				Card c = new Card(msg.content);
				DiscardRet dRet = this.player.discard(c);
				if (dRet==DiscardRet.OK) {
					broadcast(gameId, new Message(MsgType.DISCARD, this.name, msg.content));
				} else if(dRet==DiscardRet.POT) {
					pot = this.player.getHand().toString();
					this.sendMsg(session, new Message(MsgType.HAND, "PlayerSession", pot));
					broadcast(gameId, new Message(MsgType.HAND, this.name, null));
				} else if(dRet==DiscardRet.CLOSE) {
					this.game.closeRound();
				} else {
					this.sendMsg(session, new Message(MsgType.DISCARD, "PlayerSession", dRet.name()));
				}
				break;
			case EXIT:// TODO
				break;
		}


	}

	@OnError
	public void onError(Throwable t) {// TODO
		System.out.println("onError::" + t.getMessage());
	}

	public void sendMsg(Session session, Message msg) {
		try {
			session.getBasicRemote().sendObject(msg);
		} catch (IOException | EncodeException e) {
			e.printStackTrace();
		}
	}

	public static void broadcast(String gameId, Message msg) {
		for (Session s: sessions) {
			if (s.getUserProperties().get("gameId")==gameId) {
				try {
					s.getBasicRemote().sendObject(msg);
				} catch (IOException | EncodeException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

// session.getUserPrincipal() returns the authenticated user or null
// session.getUserProperties() returns a map
// javax.websocket.ClientEndpoint is used to denote that a POJO is a web socket client
