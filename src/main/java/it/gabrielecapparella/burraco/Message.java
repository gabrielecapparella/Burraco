package it.gabrielecapparella.burraco;

import java.util.List;

public class Message {
    public MsgType type;
    public String sender; // now is sessionId, then will be nickname
    public String content;

    public Message(MsgType type, String sender, String content) {
        this.type = type;
        this.sender = sender;
        this.content = content;
    }

    @Override
    public String toString() {
        return this.type.name()+";"+this.sender+";"+this.content;
    }
}
