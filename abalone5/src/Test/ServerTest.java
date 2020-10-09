package Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import Machenism.Game;
import Machenism.Mark;
import Machenism.Player;
import Server.Server;
import Server.ServerView;

public class ServerTest {
	static Server ss;

	@BeforeAll
	public static void setup() {
		ServerView view = new ServerView();
		ss = new Server(view);

	}

	@Test
	public void testAddPlayer() {

		Player p = new Player("BOB");
		ss.addPlayer(p);
		assertTrue(ss.getPlayers().contains(p));

	}

	@Test
	public void testCreateGames() {
		assertEquals("ok", ss.createRoom("Pie", "1234", 2));
		assertEquals("fail:duplicate_room_name", ss.createRoom("Pie", "!23", 2));
		assertEquals("fail:invalid_capacity", ss.createRoom("iisi", "iuiwh", 5));
		assertEquals("fail:invalid_room_name", ss.createRoom("huied,", "123", 3));
		assertEquals("fail:invalid_password", ss.createRoom("123", "pw,12", 2));

	}

	@Test
	public void testHandleConnection() {
		assertEquals("ok", ss.handleConnection("john"));
		Player p = new Player("john");
		ss.addPlayer(p);
		assertEquals("fail:invalid_username", ss.handleConnection("iv,n"));
	}

	@Test
	public void testRemovePlayer() {
		Player p = new Player("bon");
		ss.addPlayer(p);
		Game g = new Game(2, "h", "1");
		ss.join(p, "h", "1");
		ss.reMovePlayer(p);
		assertTrue(!ss.getPlayers().contains(p));
	}

	@Test
	public void testHandleChat() {
		Game g = new Game(2, "hi", "123");
		Player p = new Player("p");
		assertEquals("fail:not_in_room", ss.handleChat("hey", null, p));
		assertEquals("ok", ss.handleChat("hey", g, p));
	}

	@Test
	public void testJoin() {

		Player p = new Player("Hey");
		Player p1 = new Player("Un");
		Player p2 = new Player("hb");
		ss.addPlayer(p);
		ss.addPlayer(p1);
		ss.addPlayer(p2);
		ss.createRoom("h", "123", 2);
		assertEquals("fail:not_existing", ss.join(p, "piiej", "hey"));
		assertEquals("joined in", ss.join(p, "h", "123"));
		assertEquals("fail:incorrect_password", ss.join(p1, "h", "1234"));
		ss.join(p1, "h", "123");
		assertEquals("fail:room_full", ss.join(p2, "h", "123"));

	}

	@Test
	public void testListRoom() {

		ss.createRoom("h", "123", 2);
		assertTrue(ss.listRoom().contains("h"));
		assertFalse(ss.listRoom().contains("bhebfeuhue"));
	}

	@Test
	public void testSetReady() {

		Player p = new Player("Em");
		ss.addPlayer(p);
		ss.createRoom("H", "13", 2);
		assertEquals("fail:not_in_room", ss.setReady(p));
		ss.join(p, "H", "13");
		assertEquals("ok", ss.setReady(p));
	}

	@Test
	public void testGetPlayerData() {

		Player p = new Player("Em");
		ss.addPlayer(p);
		ss.createRoom("H", "13", 2);
		assertEquals("fail:not_in_room", ss.getPlayerData(p));
		ss.join(p, "H", "13");
		assertTrue(ss.getPlayerData(p).contains("playerdata"));
	}

	@Test
	void testMakeMove() {
		Player p = new Player("Em");
		Player p1 = new Player("Fay");
		ss.addPlayer(p);
		ss.addPlayer(p1);
		assertEquals(ss.makeMove(p1, "move:12:13:14:L"), "fail:not_in_game");
		Game g = new Game(2, "", "");
		g.addPlayer(p1);
		g.addPlayer(p);
		p1.setReady();
		p.setReady();
		g.setStart();
		assertEquals(p.getMark(), Mark.W);
		assertEquals(ss.makeMove(p1, "move:12:13:14:L"), "illegal_move");
//		assertEquals(ss.makeMove(p1, "move:14:15:16:L"), "illegal_move");

	}

}
