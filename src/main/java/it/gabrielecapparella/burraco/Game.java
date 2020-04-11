package it.gabrielecapparella.burraco;

import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class Game {
	private String id;
	private Instant startTime;
	private int targetPoints;
	private int seatsToAssign;
	public boolean isRunning;
	private Team team1;
	private Team team2;
	private List<Player> players;
	private List<Card> deck;
	private CardSet discardPile;

	public Game(String id, int targetPoints, int numPlayers) {
		this.id = id;
		this.startTime = Instant.now();
		this.targetPoints = targetPoints;
		this.seatsToAssign = numPlayers;
		this.isRunning = false;
		this.team1 = new Team();
		this.team2 = new Team();

		this.players = new ArrayList<>(numPlayers);
		this.players.add(new Player(this, team1, 0));
		this.players.add(new Player(this, team2, 1));
		if (numPlayers==4) {
			this.players.add(new Player(this, team1, 2));
			this.players.add(new Player(this, team2, 3));
		}
	}

	private void setupTable() {
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

		this.isRunning = true;
	}

	private void initDeck() {
		this.deck = new ArrayList<>(108);
		for (Suits s: Suits.values()) {
			if (s==Suits.J) {
				for(int i = 0; i<4; i++) this.deck.add(new Card(0, s));
			} else {
				for(int i = 1; i<=13; i++) this.deck.add(new Card(i, s));
			}
		}
		Collections.shuffle(this.deck);
	}

	public Player join(PlayerSession ps) {
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
		if (c==null) return null;
		return c.get(0);
	}

	public void discard(Player p, Card c) {
		this.discardPile.add(c);
		int next = (this.players.indexOf(p)+1) % this.players.size();
		this.players.get(next).setTurn(Turn.TAKE);
	}

	public List<Card> pickDiscardPile() {
		List<Card> picked = new ArrayList<>(this.discardPile);
		this.discardPile.clear();
		return picked;
	}

	public void closeRound() {
		for (Player p: this.players) {
			p.payHandPoints();
		}
		int p1 = this.team1.countRoundPoints();
		int p2 = this.team2.countRoundPoints();
		if ((p1>=this.targetPoints || p2>=this.targetPoints) && p1!=p2) { // someone won
			this.broadcast(new Message(MsgType.END_GAME, "Game", p1+","+p2));
		} else {
			this.broadcast(new Message(MsgType.END_ROUND, "Game", p1+","+p2));
			this.setupTable();
		}
	}

	public void closeGame() {
		this.isRunning = false;
		int p1 = this.team1.points;
		int p2 = this.team2.points;
		this.broadcast(new Message(MsgType.END_GAME, "Game", p1+","+p2));
	}

	public String getDescription() {
		JSONObject jo = new JSONObject();
		jo.put("id", this.id);
		jo.put("timestamp", this.startTime);
		jo.put("targetPoints", this.targetPoints);
		jo.put("numPlayers", this.players.size());
		jo.put("seatsToAssign", this.seatsToAssign);
		jo.put("isRunning", this.isRunning);
		// TODO: add seated players id/names
		return jo.toString();
	}

	public void broadcast(Message msg) {
		for (Player p: this.players) {
			p.sendMessage(msg);
		}
	}
}
