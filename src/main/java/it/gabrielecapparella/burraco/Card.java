package it.gabrielecapparella.burraco;

public class Card implements Comparable<Card> {
    public int num;
    public Suits suit;
    public int value;
    public boolean wildcard;

    public Card(int n, Suits s) { // TODO value checks
        this.num = n;
        this.suit = s;
        if (this.num==0 || this.num==2) this.wildcard = true;
        else this.wildcard = false;
    }

    public Card(String s) {
        String[] v = s.split("\\|");
        this.num = Integer.parseInt(v[0]);
        this.suit = Suits.valueOf(v[1]);
        if (this.num==0 || this.num==2) this.wildcard = true;
        else this.wildcard = false;
    }

    @Override
    public int compareTo(Card that) {
        if (this.num==2 && that.num==1) return -1;
        if (this.num==1 && that.num==2) return 1;
        int r = this.num-that.num;
        if (r==0) {r = this.suit.compareTo(that.suit);}
        return r;
    }

    @Override
    public String toString() {
        return this.num +"|"+this.suit.name();
    }
}
