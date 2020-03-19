package it.gabrielecapparella.burraco;

public class Card implements Comparable<Card> {
	public int num;
	public Suits suit;
	public int points;
	public boolean wildcard;

	public Card(int n, Suits s) {
		this.num = n;
		this.suit = s;
		if (!(n==0 && s==Suits.J) && (n<1 || n>13)) throw new IllegalArgumentException("Invalid number.");
		this.wildcard = (this.num==0 || this.num==2);
		this.points = this.getPoints();
	}

	public Card(String s) {
		String[] v = s.split("\\|");
		this.num = Integer.parseInt(v[0]);
		this.suit = Suits.valueOf(v[1]);
		if (!s.equals("0|J") && (num<0 || num>13)) throw new IllegalArgumentException("Invalid number.");
		this.wildcard = (this.num==0 || this.num==2);
		this.points = this.getPoints();
	}

	private int getPoints() {
		if (this.num==0) return 30;
		if (this.num==1) return 15;
		if (this.num==2) return 20;
		if (this.num<8) return 5;
		return 10;
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
