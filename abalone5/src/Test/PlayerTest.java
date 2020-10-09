package Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import Machenism.Game;
import Machenism.Mark;
import Machenism.Player;

/**
 * Test methods for class PlayerTest
 */
public class PlayerTest {
	private static Player player1;
	private static Player player2;
	private static Player player3;

	@BeforeAll
	public static void setup() {
		player1 = new Player("Ian");
		player2 = new Player("Bob");
		player3 = new Player("Luis");
	}

	@Test
	public void testGetName() {
		assertEquals("Ian", player1.getName());
		assertEquals("Bob", player2.getName());
		assertEquals("Luis", player3.getName());
	}

	@Test
	public void testIncrease() {
		player1.increase();
		player2.increase();
		assertEquals(1, player1.getScore());
		assertEquals(1, player2.getScore());
		assertNotEquals(0, player1.getScore());
	}

	@Test
	public void testSetMark() {
		player1.setMark(Mark.B);
		player2.setMark(Mark.G);
		assertEquals(Mark.B, player1.getMark());
		assertEquals(Mark.G, player2.getMark());
	}

	@Test
	public void testResetScore() {
		player3.increase();
		player3.resetScore();
		assertNotEquals(1, player3.getScore());
		assertEquals(0, player3.getScore());
	}

	@Test
	public void testGetGame() {
		Game g1 = new Game(3, "Poop", "ABCD");
		player3.setGame(g1);
		assertEquals(g1, player3.getGame());
		Game g2 = new Game(2, "123", "abcdef");
		player2.setGame(g2);
		assertEquals(g2, player2.getGame());
	}

	@Test
	public void testSetReady() {
		Player player4 = new Player("P4");
		assertEquals("ok", player4.setReady());
		player2.setReady();
		assertEquals("already_ready", player2.setReady());
	}

	@Test
	public void testReadyCheck() {
		player1.setReady();
		assertTrue(player1.readyCheck());
		assertFalse(player2.readyCheck());
	}

	@Test
	public void testToString() {
		assertTrue(player1.toString().contains("Ian"));
		assertTrue(player2.toString().contains("Bob"));
		assertTrue(player3.toString().contains("Luis"));
	}
}
