package it.gabrielecapparella.burraco;

import java.util.List;

public class Team {
	private List<Integer> pointsHistory;
	private CardSet pot;
	private List<CardSet> runs;
	private int points; // will be put inside pointsHistory
	public boolean potTaken = false;
	public boolean canClose = false; // TODO

	public boolean meld(CardSet cards, int runIndex) {
		if (runIndex<0) {
			this.addRun(cards);
			return true;
		} else {
			return this.increaseRun(cards, runIndex);
		}
	}

	private void addRun(CardSet newRun) {
		this.runs.add(newRun);
		this.points += this.countRunPoints(newRun);
	}

	private boolean increaseRun(CardSet additionalCards, int runIndex) {
		try {
			CardSet newRun = this.runs.get(runIndex);
			newRun.cards.addAll(additionalCards.cards);
			if (newRun.checkIfLegitRun()) {
				this.runs.set(runIndex, newRun);
				return true;
			}
			return false;
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}

	private int countRunPoints(CardSet run) {
		return 0; // TODO
	}

	public void setPot(CardSet pot) {
		this.pot = pot;
	}

	public CardSet getPot() {
		this.potTaken = true;
		return this.pot;
	}
}
