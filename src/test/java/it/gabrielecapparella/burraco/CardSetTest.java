package it.gabrielecapparella.burraco;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardSetTest {

	@Test
	void emptyConstructor() {
		CardSet cs = new CardSet();
		assertEquals(cs.toString(), "");
		assertTrue(cs.size()==0);
	}

	@Test
	void listConstructor() {
		List<Card> clist = new ArrayList<>();
		clist.add(new Card("0|J"));
		CardSet cs = new CardSet(clist);
		assertTrue(cs.size()==1);
		cs.add(new Card("1|H"));
		assertTrue(cs.size()==2);
	}

	@Test
	void stringConstructor() {
		CardSet cs = new CardSet("0|J,1|D,2|H");
		assertTrue(cs.size()==3);
	}

}