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
		this.hand.cards.add(drawnCard);
		this.turn = Turn.DISCARD;
		return drawnCard;
	}

	public void pickDiscard() {
		List<Card> picked = this.game.pickDiscardPile();
		this.turn = Turn.DISCARD;
	}

	public MeldRet meld(CardSet cs, int runIndex) {
		for (Card c: cs.cards) {
			if (!this.hand.cards.contains(c)) return MeldRet.NOT_IN_HAND;
		}
		if (runIndex<0 && !cs.checkIfLegitRun()) return MeldRet.NOT_LEGIT;
		boolean willPot = cs.cards.size()==this.hand.cards.size();
		if(willPot && this.team.potTaken) return MeldRet.CANNOT_POT;

		if (!this.team.meld(cs, runIndex)) return MeldRet.NOT_LEGIT;
		if (willPot) {
			this.hand = this.team.getPot();
			return MeldRet.POT;
		} else {
			this.hand.cards.removeAll(cs.cards);
			return MeldRet.OK;
		}
	}

	public DiscardRet discard(Card c) {
		if (!this.hand.cards.contains(c)) return DiscardRet.NOT_IN_HAND;
		boolean willEmpty = this.hand.cards.size()==1;
		if (willEmpty && this.team.potTaken && !this.team.canClose) return DiscardRet.CANNOT_CLOSE;

		this.hand.cards.remove(c);
		this.game.discard(this, c);
		this.turn = Turn.NOPE;

		if (willEmpty && !this.team.potTaken) {
			this.hand = this.team.getPot();
			return DiscardRet.POT;
		} else if (willEmpty && this.team.canClose){
			return DiscardRet.CLOSE;
		}
		return DiscardRet.OK;
	}

	public void setHand(CardSet hand) {
		this.hand = hand;
	}

	public CardSet getHand() {
		return hand;
	}
}
