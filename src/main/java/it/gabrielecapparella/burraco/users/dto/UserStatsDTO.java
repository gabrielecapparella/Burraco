package it.gabrielecapparella.burraco.users.dto;

import it.gabrielecapparella.burraco.users.User;

public class UserStatsDTO {
	private Long id;
	private String username;
	private int matchesPlayed;
	private int matchesWon;
	private int matchesLost;
	private int matchesDrawn;
	private int matchesAbandoned;
	private int score;

	public UserStatsDTO(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.matchesPlayed = user.getMatchesPlayed();
		this.matchesWon = user.getMatchesWon();
		this.matchesLost = user.getMatchesLost();
		this.matchesDrawn = user.getMatchesDrawn();
		this.matchesAbandoned = user.getMatchesAbandoned();
		this.score = user.getScore();
	}

	public UserStatsDTO(Long id, String username, int matchesPlayed, int matchesWon, int matchesLost, int matchesDrawn, int matchesAbandoned, int score) {
		this.id = id;
		this.username = username;
		this.matchesPlayed = matchesPlayed;
		this.matchesWon = matchesWon;
		this.matchesLost = matchesLost;
		this.matchesDrawn = matchesDrawn;
		this.matchesAbandoned = matchesAbandoned;
		this.score = score;
	}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public int getMatchesPlayed() {
		return matchesPlayed;
	}

	public int getMatchesWon() {
		return matchesWon;
	}

	public int getMatchesLost() {
		return matchesLost;
	}

	public int getMatchesDrawn() {
		return matchesDrawn;
	}

	public int getMatchesAbandoned() {
		return matchesAbandoned;
	}

	public int getScore() {
		return score;
	}
}
