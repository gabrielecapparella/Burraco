package it.gabrielecapparella.burraco;

import javax.xml.bind.annotation.XmlRootElement;
import java.time.Instant;
import java.util.Map;

@XmlRootElement
public class GameInfo {
	public String id;
	public int numPlayers;
	public int targetPoints;
	//public int turnTimeout; TODO
	public int seatsToAssign;
	public Instant creationTime;
	public Map<String,String> players;

	public GameInfo() {}

	public boolean validateParams() {
		if (numPlayers==0 || targetPoints==0) return false;
		if (numPlayers!=2 && numPlayers!=4) return false;
		return true;
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
}
