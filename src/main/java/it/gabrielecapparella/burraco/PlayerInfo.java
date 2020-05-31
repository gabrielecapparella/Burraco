package it.gabrielecapparella.burraco;

public class PlayerInfo {
	private long id;
	private String username;
	// TODO: add score/medals


	public PlayerInfo(long id, String username) {
		this.id = id;
		this.username = username;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
