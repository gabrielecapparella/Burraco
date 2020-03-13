package it.gabrielecapparella.burraco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardSet extends ArrayList<Card>{

	public CardSet() {
		super();
	}

	public CardSet(List<Card> cards) {
		super(cards);
	}

	public CardSet(String cards) {
		for (String v: cards.split(",")) {
			super.add(new Card(v));
		}
	}

	public boolean isLegitRun() {
		Collections.sort(this);
		if (super.size()<3) return false;
		if (get(0).num==0 && get(1).num==0) return false; // two jokers
		RunType type = this.inferType();

		if(type==RunType.GROUP) return this.isLegitGroup();
		else return this.isLegitSequence();
	}

	private RunType inferType() {
		if (get(0).num==get(1).num && get(1).num==get(2).num) return RunType.GROUP; //xxx
		if (get(0).wildcard && get(1).num==get(2).num) return RunType.GROUP; //jxx
		return RunType.SEQUENCE;
	}

	private boolean isLegitGroup() {
		int sameNumber = 1;
		for (int i=0; i<super.size()-1; i++) {
			if (get(i).num==get(i+1).num) sameNumber += 1;
		}

		if (sameNumber==super.size()) return true;
		if (sameNumber==(super.size()-1) && get(0).wildcard) return true;
		return false;
	}

	private boolean isLegitSequence() {
		Card[] run = new Card[15];
		Suits seqSuit = last().suit;
		int numCards = super.size();
		for (int i=0; i<numCards; i++) { // put cards in their slots and collect deuces
			Card curr = get(i);
			if (curr.wildcard) {
				if (curr.suit == seqSuit && run[2]==null) run[2] = curr; // two
				else if (run[0]==null) run[0] = curr; // non-two wildcard
				else return false;	// too many wildcards
				continue;
			}
			if (curr.suit!=seqSuit) return false; // not consistent suits
			if (curr.num==1 && run[1]!=null) run[14] = curr; // double ace over K
			if (run[curr.num]!=null) return false; // double card
			else run[curr.num] = curr;
		}

		//if (run[3]==null && run[2]!=null && run[0]!=null) return false;
		if (run[1]!=null && run[2]==null && run[3]==null) { // single ace over K
			run[14] = run[1]; // move ace on top of K
			run[1] = null;
		}
		boolean usedWild = false;
		for (int i = 13; i>0; i--) { // check for missing cards
			if (run[i]==null && run[i+1]!=null) {
				if (run[0]!=null) {
					run[i] = run[0];
					run[0] = null;
					usedWild = true;
				} else if (run[2]!=null) {
					if (usedWild) return false; // two free wildcards
					run[i] = run[2];
					run[2] = null;
				}
			}
		}
		int seqLen = 0;
		for (int i=0; i<15; i++) {
			if (run[i]!=null) seqLen +=1;
			if (seqLen>0 && run[i]==null) break;
		}
		if (seqLen!=numCards) return false;

		super.clear();
		for (Card c: run) {
			if (c!=null) this.add(c);
		}
		return true;
	}

	public Card last() {
		return super.get(super.size()-1);
	}

	public int countPoints() {
		int points = 0;
		for (Card c : this) {
			points += c.points;
		}
		if (this.size() >= 7) {
			switch (this.getBuracoType()) {
				case DIRTY:
					points += 100;
				case SEMICLEAN:
					points += 150;
				case CLEAN:
					points += 200;
			}
		}
		return points;
	}

	private BuracoType getBuracoType() { // TODO test
		int adjCards = 1;
		int numCards = super.size();
		for (int i=numCards-2; i>=0; i--) {
			if (!get(i).wildcard) {
				adjCards += 1;
			} else if (adjCards>=7 || i>=7) {
				return BuracoType.SEMICLEAN;
			} else {
				return BuracoType.DIRTY;
			}
		}
		return BuracoType.CLEAN;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (Card c: this) {
			result.append(c.toString());
			result.append(",");
		}
		return result.toString();
	}
}
