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
		assertEquals(0, cs.size());
	}

	@Test
	void listConstructor() {
		List<Card> clist = new ArrayList<>();
		clist.add(new Card("0|J"));
		CardSet cs = new CardSet(clist);
		assertEquals(1, cs.size());
		cs.add(new Card("1|H"));
		assertEquals(2, cs.size());
	}

	@Test
	void stringConstructor() {
		CardSet cs = new CardSet("0|J,1|D,2|H");
		assertEquals(3, cs.size());
	}

	@Test
	void isLegitRun() {
		assertTrue((new CardSet("3|D,4|D,5|D").isLegitRun()));
		assertFalse((new CardSet("3|H,4|D,5|D").isLegitRun()));
		assertFalse((new CardSet("3|D,4|D,5|H").isLegitRun()));
		assertFalse((new CardSet("3|H,3|D,5|D").isLegitRun()));
		assertTrue((new CardSet("3|H,3|D,3|S").isLegitRun()));
		assertTrue((new CardSet("3|H,0|J,3|S").isLegitRun()));
		assertFalse((new CardSet("3|H,0|J,2|S").isLegitRun()));
		assertTrue((new CardSet("3|H,0|J,2|H").isLegitRun()));
		assertTrue((new CardSet("0|J,2|D,1|D,4|D").isLegitRun()));
		assertTrue((new CardSet("0|J,2|D,1|D").isLegitRun()));
		assertFalse((new CardSet("0|J,2|D,1|H").isLegitRun()));
		assertTrue((new CardSet("1|D,12|D,13|D").isLegitRun()));
		assertTrue((new CardSet("0|J,1|D,13|D").isLegitRun()));
		assertTrue((new CardSet("0|J,1|D,12|D").isLegitRun()));
		assertTrue((new CardSet("0|J,1|D,13|D").isLegitRun()));
		assertFalse((new CardSet("1|D,1|D,2|D,3|D").isLegitRun()));
		assertTrue((new CardSet("1|D,2|H,3|D").isLegitRun()));
		assertTrue((new CardSet("4|D,2|H,6|D").isLegitRun()));
		assertTrue((new CardSet("4|D,0|J,6|D").isLegitRun()));
		assertFalse((new CardSet("2|D,4|D,0|J,6|D").isLegitRun()));
		assertFalse((new CardSet("4|D,0|J,7|D").isLegitRun()));
		assertFalse((new CardSet("2|D,4|D,0|J,6|D,8|D").isLegitRun()));
		assertFalse((new CardSet("0|J,0|J,0|J").isLegitRun()));
		assertTrue((new CardSet("2|D,2|H,2|S").isLegitRun()));
		assertTrue((new CardSet("2|D,1|H,1|S").isLegitRun()));
		assertTrue((new CardSet("1|D,2|D,2|S").isLegitRun()));
	}

}