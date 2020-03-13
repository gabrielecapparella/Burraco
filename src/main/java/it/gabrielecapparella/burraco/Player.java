package it.gabrielecapparella.burraco;

import java.util.List;

public class Player {
	private CardSet hand;
	private Game game;
	private Team team;
	public Turn turn;

	public Player(Game game, Team team) {
		this.game = game;
		this.team = team;
	}

	public Card drawCard() {
		List<Card> drawn = this.game.drawCards(1);
		if (drawn==null) return null; // deck is empty
		Card drawnCard = drawn.get(0);
		this.hand.add(drawnCard);
		this.turn = Turn.DISCARD;
		return drawnCard;
	}

	public void pickDiscard() {
		List<Card> picked = this.game.pickDiscardPile();
		this.turn = Turn.DISCARD;
	}

	public MeldRet meld(CardSet cs, int runIndex) {
		for (Card c: cs) {
			if (!this.hand.contains(c)) return MeldRet.NOT_IN_HAND;
		}
		if (runIndex<0 && !cs.isLegitRun()) return MeldRet.NOT_LEGIT;
		boolean willPot = cs.size()==this.hand.size();
		if(willPot && this.team.potTaken) return MeldRet.CANNOT_POT;

		if (!this.team.meld(cs, runIndex)) return MeldRet.NOT_LEGIT;
		if (willPot) {
			this.hand = this.team.getPot();
			return MeldRet.POT;
		} else {
			this.hand.removeAll(cs);
			return MeldRet.OK;
		}
	}

	public DiscardRet discard(Card c) {
		if (!this.hand.contains(c)) return DiscardRet.NOT_IN_HAND;
		boolean willEmpty = this.hand.size()==1;
		if (willEmpty && (!this.team.canClose() || c.wildcard)) return DiscardRet.CANNOT_CLOSE;

		this.hand.remove(c);
		this.game.discard(this, c);
		this.turn = Turn.NOPE;

		if (willEmpty && !this.team.potTaken) {
			this.hand = this.team.getPot();
			return DiscardRet.POT;
		} else if (willEmpty && this.team.canClose()){
			this.team.points += 100;
			return DiscardRet.CLOSE;
		}
		return DiscardRet.OK;
	}

	public void payHandPoints() {
		this.team.points -= this.hand.countPoints();
	}

	public void setHand(CardSet hand) {
		this.hand = hand;
	}

	public CardSet getHand() {
		return hand;
	}
}
