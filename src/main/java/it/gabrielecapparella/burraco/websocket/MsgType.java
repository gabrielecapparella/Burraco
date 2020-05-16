package it.gabrielecapparella.burraco.websocket;

public enum MsgType {
	JOIN,
	START_ROUND,
	HAND,       // used both at the beginning and for pot
	TURN,
	DRAW,
	PICK,
	MELD,
	DISCARD,
	POT,
	EXIT,		// used when a players exits in order to notify the others
	END_ROUND,
	END_GAME,
	CHAT        // used also for info messages
}
