package it.gabrielecapparella.burraco;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Team {
	private CardSet pot;
	private List<CardSet> runs;
	private List<Integer> pointsHistory = new ArrayList<>();
	public int points; // current round
	public int totalPoints;
	public boolean potTaken;

	public void newRound(CardSet pot) {
		this.runs = new ArrayList<>();
		this.pot = pot;
		this.potTaken = false;
		this.points = 0;
		this.totalPoints = 0;
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

	public int countRoundPoints() {
		for (CardSet run: this.runs) {
			this.points += run.countPoints();
		}
		if (!this.potTaken) this.points -= 100;
		this.pointsHistory.add(this.points);
		this.totalPoints += this.points;
		return this.totalPoints;
	}

	public String getReport() {
		JSONObject jo = new JSONObject();
		jo.put("history", this.pointsHistory);
		jo.put("total", this.totalPoints);
		return jo.toString();
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
