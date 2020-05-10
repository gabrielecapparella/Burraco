package it.gabrielecapparella.burraco;

import it.gabrielecapparella.burraco.cards.Card;
import it.gabrielecapparella.burraco.cards.CardSet;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardSetTest {

	@Test
	void listConstructor() {
		List<Card> clist = new ArrayList<>();
		clist.add(new Card("0J"));
		CardSet cs = new CardSet(clist);
		assertEquals(1, cs.size());
		cs.add(new Card("1H"));
		assertEquals(2, cs.size());
	}

	@Test
	void stringConstructor() {
		CardSet cs = new CardSet("0J,1D,2H");
		assertEquals(3, cs.size());
	}

	@Test
	void isLegitRun() {
		assertTrue((new CardSet("3D,4D,5D").isLegitRun()));
		assertFalse((new CardSet("3H,4D,5D").isLegitRun()));
		assertFalse((new CardSet("3D,4D,5H").isLegitRun()));
		assertFalse((new CardSet("3H,3D,5D").isLegitRun()));
		assertTrue((new CardSet("3H,3D,3S").isLegitRun()));
		assertTrue((new CardSet("3H,0J,3S").isLegitRun()));
		assertFalse((new CardSet("3H,0J,2S").isLegitRun()));
		assertTrue((new CardSet("3H,0J,2H").isLegitRun()));
		assertTrue((new CardSet("0J,2D,1D,4D").isLegitRun()));
		assertTrue((new CardSet("0J,2D,1D").isLegitRun()));
		assertFalse((new CardSet("0J,2D,1H").isLegitRun()));
		assertTrue((new CardSet("1D,12D,13D").isLegitRun()));
		assertTrue((new CardSet("0J,1D,13D").isLegitRun()));
		assertTrue((new CardSet("0J,1D,12D").isLegitRun()));
		assertTrue((new CardSet("0J,1D,13D").isLegitRun()));
		assertFalse((new CardSet("1D,1D,2D,3D").isLegitRun()));
		assertTrue((new CardSet("1D,2H,3D").isLegitRun()));
		assertTrue((new CardSet("4D,2H,6D").isLegitRun()));
		assertTrue((new CardSet("4D,0J,6D").isLegitRun()));
		assertFalse((new CardSet("2D,4D,0J,6D").isLegitRun()));
		assertFalse((new CardSet("4D,0J,7D").isLegitRun()));
		assertFalse((new CardSet("2D,4D,0J,6D,8D").isLegitRun()));
		assertFalse((new CardSet("0J,0J,0J").isLegitRun()));
		assertTrue((new CardSet("2D,2H,2S").isLegitRun()));
		assertTrue((new CardSet("2D,1H,1S").isLegitRun()));
		assertTrue((new CardSet("1D,2D,2S").isLegitRun()));
		assertTrue((new CardSet("1D,2D,3D,4D,5D,6D,7D,8D,9D,10D,11D,12D,13D,1D").isLegitRun()));
	}

	@Test
	void countPoints() {
		CardSet cs;
		cs = new CardSet("3D,4D,5D");
		assertTrue(cs.isLegitRun());
		assertEquals(15, cs.countPoints());

		cs = new CardSet("1D,12D,13D");
		assertTrue(cs.isLegitRun());
		assertEquals(35, cs.countPoints());

		cs = new CardSet("1D,2H,13D");
		assertTrue(cs.isLegitRun());
		assertEquals(45, cs.countPoints());

		cs = new CardSet("1D,2D,3D,4D,5D,6D,7D");
		assertTrue(cs.isLegitRun());
		assertEquals(260, cs.countPoints());

		cs = new CardSet("1D,2H,3D,4D,5D,6D,7D");
		assertTrue(cs.isLegitRun());
		assertEquals(160, cs.countPoints());

		cs = new CardSet("1D,2H,3D,4D,5D,6D,7D,8D,9D");
		assertTrue(cs.isLegitRun());
		assertEquals(230, cs.countPoints());

		cs = new CardSet("1D,2D,3D,4D,5D,6D,7D,8D,9D");
		assertTrue(cs.isLegitRun());
		assertEquals(280, cs.countPoints());

		cs = new CardSet("1D,2D,3D,4D,5D,0J,7D,8D,9D");
		assertTrue(cs.isLegitRun());
		assertEquals(205, cs.countPoints());

		cs = new CardSet("2D,3D,4D,5D,6D,7D,8D,9D");
		assertTrue(cs.isLegitRun());
		assertEquals(265, cs.countPoints());

		cs = new CardSet("2H,3D,4D,5D,6D,7D,8D,9D");
		assertTrue(cs.isLegitRun());
		assertEquals(215, cs.countPoints());

		cs = new CardSet("2D,8D,9D,10D,11D,12D,13D,1D");
		assertTrue(cs.isLegitRun());
		assertEquals(245, cs.countPoints());

		cs = new CardSet("2D,9D,10D,11D,12D,13D,1D");
		assertTrue(cs.isLegitRun());
		assertEquals(185, cs.countPoints());

		cs = new CardSet("8D,9D,10D,11D,2D,13D,1D");
		assertTrue(cs.isLegitRun());
		assertEquals(185, cs.countPoints());
	}

	@Test
	void difference() {
		CardSet cs_1 = new CardSet("9H,2S,10H,0J");
		CardSet cs_2 = new CardSet("9H,2S,10H");
		CardSet cs_actual = cs_1.difference(cs_2);
		CardSet cs_expected = new CardSet("0J");

		assertEquals(cs_expected, cs_actual);
		assertTrue(cs_actual.get(0).wildcard);
	}
}