package Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import Machenism.Game;
import Machenism.Player;

public class GameTest {
	private static Game g1;
	private static Game g2;
	private static Game g3;

	@BeforeAll
	public static void setup() {
		g1 = new Game(2, "g1", "1234");
		g2 = new Game(3, "poop", "die123");
	}

	@Test
	public void testHasWinner() {
		g1.setStart();
		assertFalse(g1.hasWinner());
		Player p1 = new Player("Amy");
		Player p2 = new Player("BOB");
		g1.addPlayer(p1);
		g1.addPlayer(p2);
		g1.setStart();
		List<Player> p = g1.getPlayers();
		for (int i = 0; i < 6; i++) {
			p.get(0).increase();
		}
		assertTrue(g1.hasWinner());
	}

	@Test
	public void testGetName() {
		assertEquals("g1", g1.getName());
		assertEquals("poop", g2.getName());
	}

	@Test
	public void testAddPlayer() {
		Game g1 = new Game(2, "P", "asd");
		Player p = new Player("John");
		Player p1 = new Player("Amy");
		g1.addPlayer(p);
		g2.addPlayer(p1);
		assertTrue(g1.getPlayers().contains(p));
		assertFalse(g1.getPlayers().contains(p1));
		assertTrue(g2.getPlayers().contains(p1));
		assertFalse(g2.getPlayers().contains(p));
	}

	@Test
	public void testRemovePlayer() {
		Player p = new Player("John");
		Player p1 = new Player("Amy");
		g1.addPlayer(p);
		g1.reMovePlayer(p);
		assertFalse(g1.getPlayers().contains(p));
	}

	@Test
	public void testCheckPass() {
		assertTrue(g1.checkPass("1234"));
		assertFalse(g2.checkPass("travel"));
		assertTrue(g2.checkPass("die123"));
	}

	@Test
	public void testFull() {
		Player p = new Player("John");
		Player p1 = new Player("Amy");
		Player p2 = new Player("Abdul");
		g1.addPlayer(p1);
		g1.addPlayer(p);
		g2.addPlayer(p2);
		assertTrue(g1.isFull());
		assertFalse(g2.isFull());
	}

	@Test
	public void testToString() {
		assertTrue(g1.toString().contains("g1"));
		assertTrue(g1.toString().contains("2"));
		assertTrue(g1.toString().contains("true"));
		assertTrue(g2.toString().contains("poop"));
	}

	@Test
	public void testIsStart() {
		g1.setStart();
		assertTrue(g1.isStart());
		assertFalse(g2.isStart());

	}

	@Test
	public void testReset() {

		g1.reset();
		assertFalse(g1.isStart());
		assertFalse(g2.isStart());
	}

	@Test
	public void testTurnOver() {
		assertTrue(!g1.turnOver());
	}

	@Test
	public void testGetCurrent() {
		Game g3 = new Game(2, "woo", "1234");
		Player p1 = new Player("JOhn");
		Player p2 = new Player("BOB");
		g3.addPlayer(p1);
		g3.addPlayer(p2);
		assertEquals(p1, g3.currentPlayer());
	}

	@Test
	public void testAlly() {
		Player p1 = new Player("Amy");
		Player p2 = new Player("BOB");
		Player p = new Player("John");
		Player p3 = new Player("Judy");
		Game g = new Game(4, "test", "1234");
		Game g1 = new Game(2, "p", "ded");
		Player p4 = new Player("leg");
		g1.addPlayer(p4);
		g.addPlayer(p1);
		g.addPlayer(p2);
		g.addPlayer(p);
		g.addPlayer(p3);
		g.setStart();
		assertEquals(p2, g.ally(p3));
		assertNull(g1.ally(p4));
	}

	@Test
	public void testHandleMoves() {
		Player p1 = new Player("Amy");
		Player p2 = new Player("BOB");
		Player p = new Player("John");
		Player p3 = new Player("Judy");
		Player p4 = new Player("Indy");
		Game g = new Game(2, "test", "1234");
		g.addPlayer(p1);
		g.addPlayer(p2);
		g.setStart();
		assertEquals("not_your_turn", g.handleMove("move:1:2:3:DL", p2));
		assertEquals("unknown_format", g.handleMove("move:12;", p1));
		assertEquals("illegal_move", g.handleMove("move:1:3:4:L", p1));
		assertEquals("move:::15:DR", g.handleMove("move:::15:DR", p1));
		assertEquals("move::46:47:UL", g.handleMove("move::46:47:UL", p2));

		Game g2 = new Game(3, "t1", "asdf");
		g2.addPlayer(p3);
		g2.addPlayer(p);
		g2.addPlayer(p4);
		g2.setStart();
		assertEquals("illegal_move", g2.handleMove("move:13:20:28:DR", p3));

	}

	@Test
	public void testGetHighScore() {
		Game g = new Game(2, "test", "1234");
		Player p1 = new Player("Amy");
		p1.increase();
		p1.increase();
		Player p2 = new Player("BOB");
		p2.increase();
		g.addPlayer(p1);
		g.addPlayer(p2);
		assertEquals(p1, g.getHighestScorePlayer());
		assertFalse(g.draw());

		// 4 players test
		Game g4 = new Game(4, "123", "123");
		Player p3 = new Player("dad");
		Player p4 = new Player("KA");
		g4.addPlayer(p1);
		g4.addPlayer(p2);
		g4.addPlayer(p3);
		g4.addPlayer(p4);
		g4.setStart();
		assertEquals(p1, g4.getHighestScorePlayer());
		assertFalse(g4.draw());
		p4.increase();
		assertEquals(p1, g4.ally(p3));
		assertTrue(g4.draw());

	}

	@Test
	public void testGetWinner() {
		Game g = new Game(2, "test", "1234");
		Player p1 = new Player("Amy");
		Player p2 = new Player("BOB");
		g.addPlayer(p1);
		g.addPlayer(p2);
		g.setStart();
		List<Player> p = g.getPlayers();
		for (int i = 0; i < 6; i++) {
			p.get(0).increase();
		}
		assertEquals(p1, g.getWinner());

	}

}
