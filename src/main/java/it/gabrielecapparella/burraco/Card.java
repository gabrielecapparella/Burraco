package it.gabrielecapparella.burraco;

public class Card {
    public int number;
    public Suits suit;

    public Card(int n, Suits s) {
        this.number = n;
        this.suit = s;
    }

    public Card(String value) {
        String[] v = value.split("|");
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
        return String.valueOf(this.number)+"|"+this.suit.name();
    }
}
