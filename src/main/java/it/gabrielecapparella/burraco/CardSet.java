package it.gabrielecapparella.burraco;

import java.util.ArrayList;
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

    public boolean checkIfLegitRun() {// TODO
        return true;
    }

    public int countPoints() {
        int points = 0;
        for (Card c: this) {
            points += c.value;
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
