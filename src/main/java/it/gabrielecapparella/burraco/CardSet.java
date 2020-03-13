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
		int rightSuit = 1;
		Suits lastSuit = last().suit;

		for (int i=0; i<super.size()-1; i++) {
			if (get(i).suit==lastSuit) rightSuit += 1;
		}
		if (rightSuit<(super.size()-1)) return false;
		if (rightSuit==(super.size()-1) && !get(0).wildcard) return false;

		int diff;
		int deuces=0;
		Card curr, next;
		for (int i=0; i<super.size()-1; i++) {
			curr = get(i);
			next = get(i+1);
			if (curr.wildcard) {
				if (deuces==0) {
					deuces = 1;
					continue;
				} else if (next.num==1 || next.num==3){
					deuces = 2;
					continue;
				} else return false;
			}
			diff = next.num-curr.num;
			if (diff==2 && deuces>0) { // one missing card
				deuces -= 1;
				continue;
			}
			if (diff==3 && deuces==2 && next.num==4) { // two missing cards
				deuces = 0;
				continue;
			}
			if (diff>1 && curr.num==1) { // ace on top ok king
				if (last().num==13) continue;
				if (last().num==12 && deuces==1) {
					deuces = 0;
					continue;
				}
			}
			if(diff==0 && curr.num==1) {
				if (last().num==13) continue;
				if (last().num==12 && deuces>0) {
					deuces -= 1;
					continue;
				}
			}
			if (diff==1) continue;

			return false;
		}
		return true;
	}

	public Card last() {
		return super.get(super.size()-1);
	}

	public int countPoints() {
		int points = 0;
		int numCards = this.size();
		for (Card c : this) {
			points += c.points;
		}
		if (numCards >= 7) {
			Collections.sort(this);
			if (this.inferType() == RunType.GROUP) {
				if (get(0).num == last().num) return (points + 200);
				if (get(0).wildcard && numCards > 7) return (points + 150);
				return (points + 100);
			} else { // SEQUENCE
				if (!(get(0).wildcard)) return (points + 200);
				int adjCards = 1;
				for (int i = numCards - 1; i > 0; i--) {
					if (get(i).num - get(i-1).num == 1) { // consecutive
						adjCards += 1;
					} else { //TODO
						if (get(i).num == 3) { // 3,2,1

						} else if (get(i-1).num == 1) { // 1 over K

						} else { // wildcard

						}
					}
				}

			}
		}

		return points;
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
