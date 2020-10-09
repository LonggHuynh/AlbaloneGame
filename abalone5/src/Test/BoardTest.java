package Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import Machenism.Board;
import Machenism.Mark;

/**
 * Test for the methods in the Board class
 */
class BoardTest {
	private static Board board;
	private static Board board1;
	private static Board board2;

	// test whether the board fields are instantiated with empty fields
	@Test
	public void testSetEmpty() {
		board = new Board(2);
		board1 = new Board(3);
		board2 = new Board(4);
		boolean first = (board.getField(board.getPosition(3, 4)) == Mark.E);
		boolean second = (board.getField(board.getPosition(4, 5)) == Mark.E);
		boolean third = (board.getField(board.getPosition(0, 4)) == Mark.E);
		boolean fourth = (board.getField(board.getPosition(8, 6)) == Mark.E);
		assertTrue(first && second && third && fourth);

	}

	// test whether the setup for two players is correct
	@Test
	public void testSetupForTwo() {
		board = new Board(2);
		board1 = new Board(3);
		board2 = new Board(4);
		boolean first = (board.getField(board.getPosition(0, 0)) == Mark.Y);
		boolean sec = (board.getField(board.getPosition(5, 1)) == Mark.Y);
		boolean third = (board.getField(board.getPosition(2, 2)) == Mark.Y);
		boolean fourth = (board.getField(board.getPosition(3, 7)) == Mark.W);
		boolean fifth = (board.getField(board.getPosition(5, 6)) == Mark.W);
		boolean sixth = (board.getField(board.getPosition(8, 8)) == Mark.W);
		assertTrue(first && sec && third && fourth && fifth && sixth);
	}

	// test whether the setup for three players is correct
	@Test
	public void testSetupForThree() {
		board = new Board(2);
		board1 = new Board(3);
		board2 = new Board(4);

		boolean a = (board1.getField(board1.getPosition(0, 0)) == Mark.R);
		boolean b = (board1.getField(board1.getPosition(1, 1)) == Mark.R);
		boolean c = (board1.getField(board1.getPosition(0, 4)) == Mark.R);
		boolean d = (board1.getField(board1.getPosition(1, 5)) == Mark.R);
		boolean e = (board1.getField(board1.getPosition(3, 0)) == Mark.Y);
		boolean f = (board1.getField(board1.getPosition(4, 1)) == Mark.Y);
		boolean g = (board1.getField(board1.getPosition(7, 4)) == Mark.Y);
		boolean h = (board1.getField(board1.getPosition(8, 5)) == Mark.Y);
		boolean i = (board1.getField(board1.getPosition(3, 7)) == Mark.W);
		boolean j = (board1.getField(board1.getPosition(4, 8)) == Mark.W);
		boolean k = (board1.getField(board1.getPosition(8, 8)) == Mark.W);
		boolean l = (board1.getField(board1.getPosition(6, 7)) == Mark.W);
		assertTrue(a && b && c && d);
		assertTrue(e && f && g && h);
		assertTrue(i && j && k && l);
	}

	// test whether the setup for four players is correct
	@Test
	public void testSetupForFour() {
		board = new Board(2);
		board1 = new Board(3);
		board2 = new Board(4);

		boolean a = (board2.getField(board2.getPosition(0, 0)) == Mark.R);
		boolean b = (board2.getField(board2.getPosition(3, 0)) == Mark.R);
		boolean c = (board2.getField(board2.getPosition(2, 1)) == Mark.R);
		boolean d = (board2.getField(board2.getPosition(2, 2)) == Mark.R);
		boolean e = (board2.getField(board2.getPosition(0, 4)) == Mark.W);
		boolean f = (board2.getField(board2.getPosition(3, 7)) == Mark.W);
		boolean g = (board2.getField(board2.getPosition(2, 5)) == Mark.W);
		boolean h = (board2.getField(board2.getPosition(3, 5)) == Mark.W);
		boolean i = (board2.getField(board2.getPosition(5, 1)) == Mark.Y);
		boolean j = (board2.getField(board2.getPosition(8, 4)) == Mark.Y);
		boolean k = (board2.getField(board2.getPosition(6, 3)) == Mark.Y);
		boolean l = (board2.getField(board2.getPosition(6, 4)) == Mark.Y);
		boolean m = (board2.getField(board2.getPosition(5, 8)) == Mark.B);
		boolean n = (board2.getField(board2.getPosition(8, 8)) == Mark.B);
		boolean o = (board2.getField(board2.getPosition(6, 7)) == Mark.B);
		boolean p = (board2.getField(board2.getPosition(5, 6)) == Mark.B);
		assertTrue(a && b && c && d);
		assertTrue(e && f && g && h);
		assertTrue(i && j && k && l);
		assertTrue(m && n && o && p);
	}

	// test if field is valid
	@Test
	public void testValidField() {
		board = new Board(2);
		board1 = new Board(3);
		board2 = new Board(4);

		boolean a = board.validField(65);
		boolean b = board.validField(1);
		boolean c = board.validField(25);
		boolean d = board.validField(61);
		boolean e = board.validField(0);
		assertTrue(b);
		assertTrue(c);
		assertTrue(d);
		assertFalse(a);
		assertFalse(e);
	}

	// test if move is pushing move
	@Test
	public void pushingMoveTest() {
		board = new Board(2);
		board1 = new Board(3);
		board2 = new Board(4);

		boolean k = board.pushing(8, 15, "DR", Mark.Y);
		boolean a = board.pushing(1, 6, "DL", Mark.Y);
		boolean b = board.pushing(46, 47, "R", Mark.W);
		boolean c = board.pushing(60, 54, 47, "UL", Mark.W);
		boolean d = board.pushing(51, 57, "UL", Mark.W);
		assertTrue(k);
		assertTrue(a);
		assertTrue(b);
		assertTrue(c);
		assertTrue(d);
	}

	// test if choice is valid
	@Test
	public void validChoiceTest() {
		board = new Board(2);
		board1 = new Board(3);
		board2 = new Board(4);

		boolean k = board.validChoice(16, 23, Mark.Y);
		boolean a = board.validChoice(1, Mark.Y);
		boolean b = board1.validChoice(13, Mark.R);
		boolean c = board1.validChoice(61, Mark.W);
		boolean d = board2.validChoice(11, 12, 13, Mark.R);
		boolean e = board2.validChoice(58, 53, 47, Mark.B);
		assertFalse(k);
		assertTrue(a);
		assertTrue(b);
		assertTrue(c);
		assertFalse(d);
		assertTrue(e);

	}

	// test the set field class
	@Test
	public void testSetField() {
		board.setField(2, Mark.Y);
		board.setField(57, Mark.W);
		assertEquals(Mark.Y, board.getField(2));
		assertEquals(Mark.W, board.getField(57));
		assertNotEquals(Mark.W, board.getField(2));
	}

	// test if move is a valid side move
	@Test
	public void testValidSide() {
		board = new Board(2);
		board1 = new Board(3);
		board2 = new Board(4);

		boolean a = (board.validSide(46, 47, "UL", Mark.W));
		boolean b = (board.validSide(10, 11, "DR", Mark.Y));
		boolean c = (board.validSide(12, 13, 14, "DL", Mark.R));
		boolean d = (board1.validSide(13, 20, 28, "DR", Mark.R));
		boolean e = (board2.validSide(24, 33, "DL", Mark.Y));
		boolean f = (board2.validSide(48, 55, 61, "UR", Mark.B));
		assertTrue(a);
		assertTrue(b);
		assertFalse(c);
		assertTrue(d);
		assertTrue(e);
		assertTrue(f);
	}

	// test the get middle function
	@Test
	public void testGetMiddle() {
		board = new Board(2);
		board1 = new Board(3);
		board2 = new Board(4);

		assertEquals(15, board.getMiddle(14, 15, 16));
		assertEquals(55, board.getMiddle(48, 55, 61));
		assertEquals(25, board1.getMiddle(17, 25, 34));
		assertEquals(13, board2.getMiddle(6, 13, 21));
	}

	// test the get row function
	@Test
	public void testGetRow() {
		board = new Board(2);
		board1 = new Board(3);
		board2 = new Board(4);

		assertEquals(4, board.getRow(30));
		assertEquals(0, board.getRow(4));
		assertEquals(8, board1.getRow(61));
		assertEquals(6, board2.getRow(47));
		assertEquals(-1, board1.getRow(63));
	}

	// test the get column function
	@Test
	public void testGetCol() {
		board = new Board(2);
		board1 = new Board(3);
		board2 = new Board(4);

		assertEquals(3, board.getColumn(38));
		assertEquals(0, board.getColumn(12));
		assertEquals(8, board1.getColumn(50));
		assertEquals(4, board2.getColumn(57));
		assertEquals(-1, board2.getColumn(0));
	}

	// test the pofOf function
	@Test
	public void testPosOf() {
		board = new Board(2);
		board1 = new Board(3);
		board2 = new Board(4);

		assertEquals(13, board.posOf(14, "L"));
		assertEquals(10, (board.posOf(4, "DR")));
		assertEquals(34, (board.posOf(26, "DL")));
		assertEquals(17, board.posOf(16, "R"));
		assertEquals(39, board.posOf(47, "UL"));
		assertEquals(52, board.posOf(57, "UR"));
	}

	// test the valid sumito no score function
	@Test
	public void testValidSumitoNoScore() {
		board = new Board(2);
		board1 = new Board(3);
		board2 = new Board(4);

		board.makeMove(8, 15, "DR", Mark.Y);
		board.makeMove(52, 46, "UR", Mark.W);
		board.makeMove(23, "DL", Mark.Y);
		boolean a = board.validSumitoNoScore(39, 46, "UR", Mark.W);
		board1.makeMove(36, "DR", Mark.R);
		board1.makeMove(52, 53, "UL", Mark.W);
		boolean b = board1.validSumitoNoScore(45, 46, "L", Mark.W);
		assertTrue(a);
		assertFalse(b);

	}

	// test if sumito gives score
	@Test
	public void testValidGetScore() {
		board = new Board(2);
		board1 = new Board(3);
		board2 = new Board(4);

		board1.makeMove(36, "DR", Mark.R);
		board1.makeMove(52, 53, "UL", Mark.W);
		boolean a = board1.validGetScore(45, 46, "L", Mark.W);
		assertTrue(a);

		board2.setField(50, Mark.R);
		board2.setField(49, Mark.R);
		board2.setField(48, Mark.Y);
		board2.setField(47, Mark.Y);
		board2.setField(46, Mark.Y);
		boolean b = board2.validGetScore(46, 47, 48, "R", Mark.Y);
		assertTrue(b);

		board.makeMove(8, 15, "DR", Mark.Y);
		board.makeMove(52, 46, "UR", Mark.W);
		board.makeMove(23, "DL", Mark.Y);
		boolean c = board.validGetScore(39, 46, "UR", Mark.W);
		assertFalse(c);

	}

	@Test
	public void testMakeMove() {
		boolean a = board.makeMove(5, 10, 16, "DL", Mark.Y);
		boolean b = board.makeMove(6, 12, "DL", Mark.Y);
		boolean c = board.makeMove(46, "UR", Mark.W);
		boolean d = board1.makeMove(2, "L", Mark.R);
		board.setField(19, Mark.Y);
		board.setField(20, Mark.W);
		board.setField(21, Mark.W);
		boolean e = board.makeMove(20, 21, "L", Mark.W);
		board.setField(21, Mark.Y);
		board.setField(22, Mark.Y);
		boolean k = board.makeMove(22, 21, 23, "L", Mark.Y);
		System.out.println(board.toString());
		board.setField(37, Mark.W);
		board.setField(29, Mark.W);

		boolean z = board.makeMove(8, 14, 21, "DL", Mark.Y);
		boolean y = board.makeMove(8, 14, 21, "DR", Mark.Y);

		assertTrue(a);
		assertFalse(b);
		assertTrue(c);
		assertFalse(d);
		assertTrue(e);
		board1.setField(2, Mark.W);
		boolean f = board1.makeMove(7, 13, "UR", Mark.R);
		assertTrue(f);
		board.setField(39, Mark.Y);
		boolean g = board.makeMove(47, 54, 60, "UL", Mark.W);
		assertTrue(g);
		board1.setField(4, Mark.W);
		boolean h = board1.makeMove(17, 25, 10, "UL", Mark.Y);
		assertTrue(h);
		board.setField(50, Mark.Y);
		boolean i = board.makeMove(56, 61, "UR", Mark.W);
		assertTrue(i);
		board.setField(22, Mark.Y);
		board.setField(49, Mark.W);
		boolean o = board.makeMove(48, 49, 47, "UR", Mark.W);
		assertTrue(o);

	}

}
