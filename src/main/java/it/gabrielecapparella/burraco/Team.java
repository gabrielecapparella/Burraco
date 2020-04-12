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

	public int meld(CardSet cards, int runIndex) {
		if (runIndex<0) {
			return this.addRun(cards);
		} else {
			return this.increaseRun(cards, runIndex);
		}
	}

	private int addRun(CardSet newRun) {
		this.runs.add(newRun);
		return this.runs.size()-1;
	}

	private int increaseRun(CardSet additionalCards, int runIndex) {
		try {
			CardSet newRun = this.runs.get(runIndex);
			newRun.addAll(additionalCards);
			if (newRun.isLegitRun()) {
				this.runs.set(runIndex, newRun);
				return runIndex;
			}
			return -1;
		} catch (IndexOutOfBoundsException e) {
			return -1;
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

	public CardSet getRun(int index) {
		if (index==-1) index = this.runs.size()-1;
		return this.runs.get(index);
	}

	public boolean willBurraco(int cards, int runIndex) {
		if (runIndex==-1 && cards<7) return false;
		if ((this.runs.get(runIndex).size()+cards)<7) return false;
		return true;
	}
}
