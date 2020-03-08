package it.gabrielecapparella.burraco;

import java.util.ArrayList;
import java.util.List;

public class CardSet { // TODO consider extending ArrayList
    public List<Card> cards;

    public CardSet(List<Card> cards) {
        this.cards = new ArrayList<>(cards);
    }

    public CardSet(String cards) {
        this.cards = new ArrayList<>();
        for (String v: cards.split(",")) {
            this.cards.add(new Card(v));
        }
    }

    public boolean checkIfLegitRun() {
        return true; // TODO
    }

    @Override
    public String toString() {
        String result = "";
        for (Card c: this.cards) {
            result += c.toString()+",";
        }
        return result;
    }

    // TODO
}
