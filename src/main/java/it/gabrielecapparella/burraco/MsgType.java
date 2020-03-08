package it.gabrielecapparella.burraco;

public enum MsgType {
    JOIN,
    HAND,       // used both at the beginning and for pot
    TURN,
    DRAW,
    PICK,
    MELD,
    DISCARD,
    EXIT,
    END,        // end of battle
    CHAT        // used also for info messages
}
