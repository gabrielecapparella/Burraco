package it.gabrielecapparella.burraco;

public class Card {
    public int number;
    public Suits suit;
    public int value;

    public Card(int n, Suits s) { // TODO value checks
        this.number = n;
        this.suit = s;
    }

    public Card(String s) {
        String[] v = s.split("\\|");
        this.number = Integer.parseInt(v[0]);
        this.suit = Suits.valueOf(v[1]);
    }

    public int compareTo(Card that) {
        int r = this.number-that.number;
        if (r==0) {r = this.suit.compareTo(that.suit);}
        return r;
    }

    @Override
    public String toString() {
        return this.number+"|"+this.suit.name();
    }
}
