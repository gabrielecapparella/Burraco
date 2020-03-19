package it.gabrielecapparella.burraco;

public enum MsgType {
	JOIN,
	HAND,       // used both at the beginning and for pot
	TURN,
	DRAW,
	PICK,
	MELD,
	DISCARD,
	POT,
	EXIT,
	END_ROUND,
	END_GAME,
	CHAT        // used also for info messages
}
