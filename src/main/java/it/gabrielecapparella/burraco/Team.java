package it.gabrielecapparella.burraco;

import it.gabrielecapparella.burraco.cards.CardSet;

import java.util.ArrayList;
import java.util.List;

public class Team {
	private CardSet pot;
	private List<CardSet> runs;
	private List<Points> pointsHistory = new ArrayList<>(); // not used as of now
	private Points currentPoints; // current round
	public int totalPoints = 0;
	public boolean potTaken;

	public void newRound(CardSet pot) {
		this.runs = new ArrayList<>();
		this.pot = pot;
		this.potTaken = false;
		this.currentPoints = new Points();
	}

	public boolean hasBurraco() {
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
		if (!newRun.isLegitRun()) return -1;
		this.runs.add(newRun);
		return this.runs.size()-1;
	}

	private int increaseRun(CardSet additionalCards, int runIndex) {
		try {
			CardSet newRun = new CardSet(this.runs.get(runIndex));
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

	public void close() {
		this.currentPoints.base += 100;
	}

	public void pay(int p) {
		this.currentPoints.points -= p;
	}

	public TeamRoundReport countRoundPoints() {
		Points runPoints;
		for (CardSet run: this.runs) {
			runPoints = run.countPoints();
			this.currentPoints.base += runPoints.base;
			this.currentPoints.points += runPoints.points;
		}
		if (!this.potTaken) this.currentPoints.points -= 100;
		this.pointsHistory.add(this.currentPoints);
		this.totalPoints += this.currentPoints.points + this.currentPoints.base;
		return new TeamRoundReport(this.currentPoints, this.totalPoints);
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
		if (runIndex==-1) return cards>=7;
		if ((this.runs.get(runIndex).size()+cards)<7) return false;
		return true;
	}
}
