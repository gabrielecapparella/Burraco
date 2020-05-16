package it.gabrielecapparella.burraco;

import it.gabrielecapparella.burraco.cards.Card;
import it.gabrielecapparella.burraco.cards.CardSet;
import it.gabrielecapparella.burraco.websocket.Message;
import it.gabrielecapparella.burraco.websocket.MsgType;
import it.gabrielecapparella.burraco.websocket.PlayerWebSocket;

import java.util.List;

public class Player {
	private CardSet hand;
	private Game game;
	private Team team;
	public String id;
	public Turn turn;
	private PlayerWebSocket endpoint;

	public Player(Game game, Team team, int id) {
		this.game = game;
		this.team = team;
		this.id = String.valueOf(id);
	}

	public void drawCard() {
		if (!(this.turn==Turn.TAKE)) {
			this.sendMessage(new Message(MsgType.CHAT, "Player", "You cannot draw now."));
			return;
		}
		Card drawn = this.game.draw();
		if (drawn==null) {
			this.sendMessage(new Message(MsgType.CHAT, "Player", "Deck is empty."));
			return;
		}
		this.hand.add(drawn);
		this.sendMessage(new Message(MsgType.DRAW, "Player", drawn.toString()));
		this.game.broadcast(new Message(MsgType.DRAW, this.id, null));
		this.setTurn(Turn.DISCARD);
	}

	public void pickDiscard() {
		if (!(this.turn==Turn.TAKE)) {
			this.sendMessage(new Message(MsgType.CHAT, "Player", "You cannot pick now."));
			return;
		}
		List<Card> picked = this.game.pickDiscardPile();
		this.hand.addAll(picked);
		this.game.broadcast(new Message(MsgType.PICK, this.id, null));
		this.setTurn(Turn.DISCARD);
	}

	public void meld(CardSet cs, int runIndex) {
		if (!(this.turn==Turn.DISCARD)) {
			this.sendMessage(new Message(MsgType.CHAT, "Player", "You cannot meld now."));
			return;
		}
		for (Card c: cs) {
			if (!this.hand.contains(c)) {
				this.sendMessage(new Message(MsgType.CHAT, "Player", "You don't have those cards."));
				return;
			}
		}
		boolean willEmpty = cs.size()==this.hand.size();
		boolean willClose = cs.size()==(this.hand.size()-1);
		boolean couldBurraco = this.team.willBurraco(cs.size(), runIndex);
		boolean hasBurraco = this.team.hasBurraco();
		if (willEmpty && this.team.potTaken) {
			this.sendMessage(new Message(MsgType.CHAT, "Player", "Cannot remain without cards."));
			return;
		}
		if (willClose && this.team.potTaken && this.hand.difference(cs).get(0).wildcard) {
			this.sendMessage(new Message(MsgType.CHAT, "Player", "Cannot close with a wildcard."));
			return;
		}
		if (willClose && this.team.potTaken && !couldBurraco && !hasBurraco) {
			this.sendMessage(new Message(MsgType.CHAT, "Player", "Cannot close yet."));
			return;
		}

		runIndex = this.team.meld(cs, runIndex);
		if (runIndex<0) {
			this.sendMessage(new Message(MsgType.CHAT, "Player", "Not a valid run."));
			return;
		}

		this.hand = this.hand.difference(cs);

		CardSet newRun = this.team.getRun(runIndex);
		this.game.broadcast(new Message(MsgType.MELD, this.id, runIndex+";"+newRun.toString()));

		if (willEmpty) {
			this.setHand(this.team.getPot()); // the setter notifies the client
			this.game.broadcast(new Message(MsgType.POT, this.id, null));
		}
	}

	public void discard(Card c) {
		if (!(this.turn==Turn.DISCARD)) {
			this.sendMessage(new Message(MsgType.CHAT, "Player", "You cannot discard now."));
			return;
		}
		if (!this.hand.contains(c)) {
			this.sendMessage(new Message(MsgType.CHAT, "Player", "You don't own that card."));
			return;
		}

		boolean willEmpty = this.hand.size()==1;
		this.hand.remove(c);
		this.game.broadcast(new Message(MsgType.DISCARD, this.id, c.toString()));

		if (willEmpty && !this.team.potTaken) {
			this.setHand(this.team.getPot()); // the setter notifies the client
			this.game.broadcast(new Message(MsgType.POT, this.id, null));
		} else if (willEmpty){
			this.team.close();
			this.game.closeRound();
		}
		this.game.discard(this, c);
		this.setTurn(Turn.NOPE);
	}

	public void payHandPoints() {
		this.team.pay(this.hand.countPoints().points);
	}

	public boolean sendMessage(Message msg) {
		if (this.endpoint==null) return false;
		return this.endpoint.send(msg);
	}

	public void setEndpoint(PlayerWebSocket ps) {
		this.endpoint = ps;
		this.sendMessage(new Message(MsgType.JOIN, "Player", this.id));
	}

	public void setHand(CardSet hand) {
		this.hand = hand;
		this.sendMessage(new Message(MsgType.HAND, "Player", hand.toString()));
	}

	public void setTurn(Turn t) {
		this.turn = t;
		this.game.broadcast(new Message(MsgType.TURN, this.id, t.name()));
	}
}
