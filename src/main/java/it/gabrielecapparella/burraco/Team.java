package it.gabrielecapparella.burraco;

import java.util.ArrayList;
import java.util.List;

public class Team {
	private CardSet pot;
	private List<CardSet> runs;
	public boolean potTaken = false;
	public boolean canClose = false; // TODO
	public int points = 0;

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
			if (newRun.checkIfLegitRun()) {
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
		this.canClose = false;
	}

	public CardSet getPot() {
		this.potTaken = true;
		return this.pot;
	}
}
