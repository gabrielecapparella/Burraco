package it.gabrielecapparella.burraco;

import java.util.ArrayList;
import java.util.List;

public class Team {
	private CardSet pot;
	private List<CardSet> runs;
	public boolean potTaken = false;
	public int points = 0;

	public boolean canClose() {
		if (!potTaken) return false;
		for (CardSet r: this.runs) {
			if (r.size()>=7) return true;
		}
		return false;
	}

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
	}

	private boolean increaseRun(CardSet additionalCards, int runIndex) {
		try {
			CardSet newRun = this.runs.get(runIndex);
			newRun.addAll(additionalCards);
			if (newRun.isLegitRun()) {
				this.runs.set(runIndex, newRun);
				return true;
			}
			return false;
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}

	public int countRoundPoints() {
		for (CardSet run: this.runs) {
			this.points += run.countPoints();
		}
		if (!this.potTaken) this.points -= 100;
		return this.points;
	}

	public void newRound(CardSet pot) {
		this.runs = new ArrayList<>();
		this.pot = pot;
		this.potTaken = false;
	}

	public CardSet getPot() {
		this.potTaken = true;
		return this.pot;
	}
}
