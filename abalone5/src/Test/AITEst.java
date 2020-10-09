package Test;

import Machenism.AI;
import Machenism.Board;
import Machenism.Mark;
import Machenism.Move;

/**
 * The test class is tested by main method
 *
 */
public class AITEst {
	public static void main(String[] args) {
		Board b = new Board(2);
		AI ad = new AI(2);
		// This case is to test the AI is aware of getting the point
		System.out.println("Case to be tested Yellow is in turn");
		b.setEmpty();
		b.setField(1, Mark.W);
		b.setField(13, Mark.Y);
		b.setField(6, Mark.Y);
		b.setField(21, Mark.W);
		b.setField(30, Mark.W);
		b.setField(31, Mark.W);
		b.setField(23, Mark.Y);
		b.setField(2, Mark.Y);
		b.setField(3, Mark.Y);

		System.out.println();
		System.out.println(b.toString());
		System.out.println();
		ad.bestMove(b, Mark.Y);
		System.out.println("After move is applied");
		Move move = ad.getBestMove();
		b.applyMove(Mark.Y, move);
		System.out.println(b.toString());
		System.out.println();

		// This case is to test the AI will not put itself in danger, i try to set up
		// the case that the AI has a chance to push enemy but it realises, doing so
		// will give enemy to push us
		System.out.println("Case to be tested Yellow is in turn, the");

		b.setEmpty();
		b.setField(13, Mark.Y);
		b.setField(6, Mark.Y);
		b.setField(21, Mark.W);
		b.setField(30, Mark.W);
		b.setField(31, Mark.W);
		b.setField(23, Mark.Y);
		b.setField(16, Mark.Y);

		System.out.println(b.toString());
		System.out.println();

		ad.bestMove(b, Mark.Y);
		Move move1 = ad.getBestMove();
		System.out.println("After move is applied, AI realize pushing the White mark, they will lose the score");
		b.applyMove(Mark.Y, move1);
		System.out.println();

		System.out.println(b.toString());

		// Avoid being pushed out
		System.out.println("Case to be tested White is in turn");
		b.setEmpty();
		b.setField(3, Mark.Y);
		b.setField(4, Mark.Y);
		b.setField(5, Mark.W);
		b.setField(13, Mark.Y);
		b.setField(6, Mark.Y);
		b.setField(21, Mark.W);
		b.setField(23, Mark.Y);
		b.setField(16, Mark.Y);

		System.out.println(b.toString());
		System.out.println();

		ad.bestMove(b, Mark.W);
		Move move2 = ad.getBestMove();
		System.out.println("After move is applied, the W in the right corner move away to avoid being pushed off");
		b.applyMove(Mark.W, move2);
		System.out.println();

		System.out.println(b.toString());

	}
}
