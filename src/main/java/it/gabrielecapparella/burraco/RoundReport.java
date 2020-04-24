package it.gabrielecapparella.burraco;

public class RoundReport {
	TeamRoundReport team1;
	TeamRoundReport team2;
	String winner;

	public RoundReport(TeamRoundReport team1, TeamRoundReport team2, String winner) {
		this.team1 = team1;
		this.team2 = team2;
		this.winner = winner;
	}
}
