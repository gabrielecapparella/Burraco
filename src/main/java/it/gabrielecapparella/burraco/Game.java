package it.gabrielecapparella.burraco;

import com.google.gson.Gson;
import it.gabrielecapparella.burraco.cards.Card;
import it.gabrielecapparella.burraco.cards.CardSet;
import it.gabrielecapparella.burraco.cards.Suits;
import it.gabrielecapparella.burraco.users.User;
import it.gabrielecapparella.burraco.websocket.Message;
import it.gabrielecapparella.burraco.websocket.MsgType;
import it.gabrielecapparella.burraco.websocket.PlayerWebSocket;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class Game {
	private String id;
	private Instant creationTime;
	private int targetPoints;
	private int seatsToAssign;
	private Team[] teams;

	private Player[] players;
	private PlayerInfo[] playersInfo;

	private List<Card> deck;
	private CardSet discardPile;
	public boolean isRunning;
	private Gson gson;

	public Game(String id, int targetPoints, int numPlayers) {
		this.id = id;
		this.creationTime = Instant.now();
		this.targetPoints = targetPoints;
		this.isRunning = false;
		this.gson = new Gson();
		this.seatsToAssign = numPlayers;

		this.teams = new Team[2];
		this.teams[0] = new Team();
		this.teams[1] = new Team();

		this.playersInfo = new PlayerInfo[numPlayers];
		this.players = new Player[numPlayers];

		for (int i=0; i<numPlayers; i++) {
			this.players[i] = new Player(this, this.teams[i%2], i);
		}
	}

	private void setupTable() {
		this.isRunning = true;
		this.initDeck();

		this.teams[0].newRound(new CardSet(this.drawCards(11)));
		this.teams[1].newRound(new CardSet(this.drawCards(11)));

		for (Player p: this.players) {
			p.setHand(new CardSet(this.drawCards(11)));
		}

		this.discardPile = new CardSet(this.drawCards(1));
		this.broadcast(new Message(MsgType.START_ROUND, "Game", this.discardPile.toString()));

		int whoBegins = new Random().nextInt(this.players.length);
		this.players[whoBegins].setTurn(Turn.TAKE);
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

	public Player join(PlayerWebSocket ps, User user) {
		if (this.seatsToAssign==0) return null;
		this.seatsToAssign -= 1;

		Player justJoined = this.players[this.seatsToAssign];
		justJoined.setEndpoint(ps);

		PlayerInfo pInfo = new PlayerInfo(user.getId(), user.getUsername());
		this.playersInfo[this.seatsToAssign] = pInfo;
		this.broadcast(new Message(MsgType.JOIN, justJoined.id, this.gson.toJson(pInfo)));
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

	public void discard(int playerId, Card c) {
		this.discardPile.add(c);
		if (this.deck.size()==2) {
			this.closeRound();
		} else {
			int next = (playerId + 1) % this.players.length;
			this.players[next].setTurn(Turn.TAKE);
		}
	}

	public List<Card> pickDiscardPile() {
		List<Card> picked = new ArrayList<>(this.discardPile);
		this.discardPile.clear();
		return picked;
	}

	private void selfDestruct() {
		this.isRunning = false;
		// TODO: write stuff to db and remove itself from games.id2game
	}

	public void closeRound() {
		this.closeRound(false);
	}

	public void closeRound(boolean closeGame) {
		if (!this.isRunning) return;
		RoundReport rr = this.computeRoundReport();
		String report_json = this.gson.toJson(rr);

		if (closeGame || !rr.winner.equals("none")) {
			this.broadcast(new Message(MsgType.END_GAME, "Game", report_json));
			this.selfDestruct();
		} else {
			this.broadcast(new Message(MsgType.END_GAME, "Game", report_json));
			this.setupTable();
		}
	}

	private RoundReport computeRoundReport() {
		for (Player p: this.players) {
			p.payHandPoints();
		}

		TeamRoundReport report1 = this.teams[0].countRoundPoints();
		TeamRoundReport report2 = this.teams[1].countRoundPoints();
		String winner = "none";
		if (report1.total>=this.targetPoints && report1.total>report2.total) {
			winner = "team1";
		} else if(report2.total>=this.targetPoints && report2.total>report1.total) {
			winner = "team2";
		}

		return new RoundReport(report1, report2, winner);
	}

	public GameInfo getDescription() {
		GameInfo gameInfo = new GameInfo();

		gameInfo.setId(this.id);
		gameInfo.setNumPlayers(this.players.length);
		gameInfo.setTargetPoints(this.targetPoints);
		gameInfo.setSeatsToAssign(this.seatsToAssign);
		gameInfo.setCreationTime(this.creationTime);
		gameInfo.setPlayers(this.playersInfo);

		return gameInfo;
	}

	public void broadcast(Message msg) {
		for (Player p: this.players) {
			p.sendMessage(msg);
		}
	}
}
