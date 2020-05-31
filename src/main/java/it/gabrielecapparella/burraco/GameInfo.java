package it.gabrielecapparella.burraco;

import java.time.Instant;
import java.util.Arrays;

public class GameInfo {
	public String id;
	public int numPlayers;
	public int targetPoints;
	//public int turnTimeout; TODO
	public int seatsToAssign;
	public Instant creationTime;
	public PlayerInfo[] players;

	public GameInfo() {}

	public boolean validateParams() {
		if (numPlayers==0 || targetPoints==0) return false;
		if (numPlayers!=2 && numPlayers!=4) return false;
		return true;
	}

	@Override
	public String toString() {
		return "GameInfo{" +
				"id='" + id + '\'' +
				", numPlayers=" + numPlayers +
				", targetPoints=" + targetPoints +
				", seatsToAssign=" + seatsToAssign +
				", creationTime=" + creationTime +
				", players=" + Arrays.toString(players) +
				'}';
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getNumPlayers() {
		return numPlayers;
	}

	public void setNumPlayers(int numPlayers) {
		this.numPlayers = numPlayers;
	}

	public int getTargetPoints() {
		return targetPoints;
	}

	public void setTargetPoints(int targetPoints) {
		this.targetPoints = targetPoints;
	}

	public int getSeatsToAssign() {
		return seatsToAssign;
	}

	public void setSeatsToAssign(int seatsToAssign) {
		this.seatsToAssign = seatsToAssign;
	}

	public Instant getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Instant creationTime) {
		this.creationTime = creationTime;
	}

	public PlayerInfo[] getPlayers() {
		return players;
	}

	public void setPlayers(PlayerInfo[] players) {
		this.players = players;
	}
}
