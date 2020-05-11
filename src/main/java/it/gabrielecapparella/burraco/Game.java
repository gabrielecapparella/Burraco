package it.gabrielecapparella.burraco;

import com.google.gson.Gson;
import it.gabrielecapparella.burraco.cards.Card;
import it.gabrielecapparella.burraco.cards.CardSet;
import it.gabrielecapparella.burraco.cards.Suits;
import it.gabrielecapparella.burraco.websocket.Message;
import it.gabrielecapparella.burraco.websocket.MsgType;
import it.gabrielecapparella.burraco.websocket.PlayerWebSocket;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class Game { // TODO: self destruct on END_GAME
	private String id;
	private Instant creationTime;
	private int targetPoints;
	private int seatsToAssign;
	private Team team1;
	private Team team2;
	private List<Player> players;

	private List<Card> deck;
	private CardSet discardPile;
	public boolean isRunning;
	private Gson gson;

	public Game(String id, int targetPoints, int numPlayers) {
		this.id = id;
		this.creationTime = Instant.now();
		this.targetPoints = targetPoints;
		this.seatsToAssign = numPlayers;
		this.team1 = new Team();
		this.team2 = new Team();
		this.isRunning = false;
		this.gson = new Gson();

		this.players = new ArrayList<>(numPlayers);
		this.players.add(new Player(this, team1, 0));
		this.players.add(new Player(this, team2, 1));
		if (numPlayers==4) {
			this.players.add(new Player(this, team1, 2));
			this.players.add(new Player(this, team2, 3));
		}
	}

	private void setupTable() {
		this.isRunning = true;
		this.initDeck();

		this.team1.newRound(new CardSet(this.drawCards(11)));
		this.team2.newRound(new CardSet(this.drawCards(11)));

		for (Player p: this.players) {
			p.setHand(new CardSet(this.drawCards(11)));
		}

		this.discardPile = new CardSet(this.drawCards(1));
		this.broadcast(new Message(MsgType.START_ROUND, "Game", this.discardPile.toString()));

		int whoBegins = new Random().nextInt(this.players.size());
		this.players.get(whoBegins).setTurn(Turn.TAKE);
	}

	private void initDeck() {
		this.deck = new ArrayList<>(108);
		for (Suits s: Suits.values()) {
			if (s==Suits.J) {
				for(int i = 0; i<4; i++) this.deck.add(new Card(0, s));
			} else {
				for(int i = 1; i<=13; i++) {
					this.deck.add(new Card(i, s));
					this.deck.add(new Card(i, s));
				}
			}
		}
		Collections.shuffle(this.deck);
	}

	public Player join(PlayerWebSocket ps) {
		if (this.seatsToAssign==0) return null;
		this.seatsToAssign -= 1;
		Player justJoined = this.players.get(this.seatsToAssign);
		justJoined.setEndpoint(ps);
		this.broadcast(new Message(MsgType.JOIN, "Game", justJoined.id));
		if (this.seatsToAssign==0) this.setupTable();
		return justJoined;
	}

	private List<Card> drawCards(int howMany) {
		List<Card> cards;
		try {
			List<Card> sublist = this.deck.subList(0, howMany);
			cards = new ArrayList<>(sublist);
			sublist.clear(); // removes them from the deck
		} catch(IndexOutOfBoundsException e) {
			cards = null; // deck is empty
		}
		return cards;
	}

	public Card draw() {
		List<Card> c = this.drawCards(1);
		return c.get(0);
	}

	public void discard(Player p, Card c) {
		this.discardPile.add(c);
		if (this.deck.size()==2) {
			this.closeRound();
		} else {
			int next = (this.players.indexOf(p) + 1) % this.players.size();
			this.players.get(next).setTurn(Turn.TAKE);
		}
	}

	public List<Card> pickDiscardPile() {
		List<Card> picked = new ArrayList<>(this.discardPile);
		this.discardPile.clear();
		return picked;
	}

	public void closeGame() {
		this.closeRound(true);
		this.isRunning = false;
	}

	public void closeRound() {
		this.closeRound(false);
	}

	private void closeRound(boolean closeGame) {
		for (Player p: this.players) {
			p.payHandPoints();
		}

		TeamRoundReport report1 = this.team1.countRoundPoints();
		TeamRoundReport report2 = this.team2.countRoundPoints();
		String winner = "none";
		if (report1.total>=this.targetPoints && report1.total>report2.total) {
			winner = "team1";
		} else if(report2.total>=this.targetPoints && report2.total>report1.total) {
			winner = "team2";
		}

		RoundReport roundReport = new RoundReport(report1, report2, winner);

		MsgType msg_type = MsgType.END_GAME;
		if(winner.equals("none")) msg_type = MsgType.END_ROUND;

		String report_json = this.gson.toJson(roundReport);
		this.broadcast(new Message(msg_type, "Game", report_json));

		if(winner==null && !closeGame) this.setupTable();
	}

	public GameInfo getDescription() {
		GameInfo gameInfo = new GameInfo();
		gameInfo.id = this.id;
		gameInfo.numPlayers = this.players.size();
		gameInfo.targetPoints = this.targetPoints;
		gameInfo.seatsToAssign = this.seatsToAssign;
		gameInfo.creationTime = this.creationTime;
		// TODO: add players id/nicknames

		return gameInfo;
	}

	public void broadcast(Message msg) {
		for (Player p: this.players) {
			p.sendMessage(msg);
		}
	}
}
