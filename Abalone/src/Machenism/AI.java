package Machenism;

import java.util.HashSet;
import java.util.Set;

public class AI {
	/**
	 * The move of of player
	 * 
	 * @invariant bestMove!=null;
	 */
	private Move bestMove = new Move();

	/**
	 * The steps ahead that AI think of, the higher, the more the performance cost
	 * 
	 * @invariant depth!=0
	 */
	private int depth;

	public AI(int depth) {
		this.depth = depth;
	}

	/**
	 * To give possible moves
	 * 
	 * @param board current board
	 * @param mark  the color/mark of player
	 * @return the set of possible moves
	 * @requires board!=null,mark!=null
	 * @ensures \result!=null
	 * 
	 *
	 */

	public Set<Move> possibleMove(Board board, Mark mark) {

		Set<String> movingDirectionset;
		// because the different rules for game of 2,3 and game of 4 players, using 3
		// direction set for game of 2 and 3 will reduce the selection, and thus the
		// performance
		if (board.getNrPLayer() == 4) {
			movingDirectionset = Board.DIRECTION_SET;
		} else {
			movingDirectionset = Board.HALF_DIRECTION_SET;
		}
		Set<Integer> pos = new HashSet<>();
		Set<Move> res = new HashSet<>();
		for (int i = 1; i < 62; i++) {
			if (board.getField(i) == mark) {
				pos.add(i);
			}
		}

		// choice of 1 ball
		for (int p : pos) {
			for (String dir : Board.DIRECTION_SET) {
				if (board.validMove(p, dir)) {
					Move move = new Move();
					move.set1Pos(p);
					move.setDirection(dir);
					res.add(move);
				}

			}
		}

		// choice of 2,3 balls
		for (int p : pos) {
			for (String lineDir : movingDirectionset) {
				int inLinePos = board.posOf(p, lineDir);
				int inLinePos3 = board.posOf(inLinePos, lineDir);
				if (board.validField(inLinePos)) {
					for (String movingDir : Board.DIRECTION_SET) {
						if (board.validMove(p, inLinePos, movingDir, mark)) {
							Move move = new Move();
							move.set1Pos(p);
							move.set2Pos(p, inLinePos);
							move.setDirection(movingDir);
							res.add(move);

						}

					}
					if (board.validField(inLinePos3)) {
						for (String movingDir : Board.DIRECTION_SET) {
							if (board.validMove(p, inLinePos, inLinePos3, movingDir, mark)) {
								Move move = new Move();
								move.set3Pos(p, inLinePos, inLinePos3);
								move.setDirection(movingDir);
								res.add(move);

							}
						}
					}

				}

			}
		}

		return res;
	}

	/**
	 * 
	 * Only apply after bestMove is applied
	 * 
	 * @return the best move of this instance
	 */
	public Move getBestMove() {
		return bestMove;
	}

	/**
	 * The random move of the board
	 * 
	 * @param board the current board
	 * @param mark  of player
	 * @ensures \result!=null
	 * @requires board!=null& mark!=null
	 * @return the random move
	 */
	public Move ramdomMove(Board board, Mark mark) {
		Move m = null;
		for (Move move : possibleMove(board, mark)) {
			return move;
		}

		return m;
	}

	/**
	 * To determine the best move without thinking about consequences
	 * 
	 * @requires board!=null & mark!=null
	 * @ensures result!null
	 * @param board current board
	 * @param mark  mark of player
	 * @return the best move without looking at the future
	 */
	public Move bestNaiveMove(Board board, Mark mark) {
		// priority 1 - get score with 3 marbles
		for (Move move : possibleMove(board, mark)) {
			if (board.validGetScore(move, mark) && move.getPos3() != 0) {
				return move;
			}

		}

		// priority 2 - get score with 2 marbles
		for (Move move : possibleMove(board, mark)) {
			if (board.validGetScore(move, mark)) {
				return move;
			}

		}

		// priority 3 - sumito moves without any point with 3 marbles
		for (Move move : possibleMove(board, mark)) {
			if (board.validSumitoNoScore(move, mark) && move.getPos3() != 0) {
				return move;
			}
		}

		// priority 4 - sumito moves without any point with 2 marbles
		for (Move move : possibleMove(board, mark)) {
			if (board.validSumitoNoScore(move, mark)) {
				return move;
			}
		}

		// priority 5 - in line moves with 3 marbles
		for (Move move : possibleMove(board, mark)) {
			if (board.pushing(move, mark) && move.getPos3() != 0) {
				return move;
			}
		}

		// priority 6 - in line moves with 2 marbles
		for (Move move : possibleMove(board, mark)) {
			if (board.pushing(move, mark)) {
				return move;
			}
		}

		// priority 7 - moves with 3 marbles
		for (Move move : possibleMove(board, mark)) {
			if (move.getPos3() != 0) {
				return move;
			}
		}

		// priority 8 - moves with 3 marbles
		for (Move move : possibleMove(board, mark)) {
			if (move.getPos2() != 0) {
				return move;
			}
		}

		return ramdomMove(board, mark);
	}

	/**
	 * To make the bestMove of this instance
	 * 
	 * @requires board!=null && mark!=null
	 * @param board the board used
	 * @param mark  mark of player
	 * 
	 */
	public void bestMove(Board board, Mark mark) {
		if (board.getNrPLayer() == 2) {
			bestMove(board, mark, this.depth, 0);
		} else {
			bestMove = bestNaiveMove(board, mark);
		}

	}

	/**
	 * To find the best move and assign to this instance bestMove
	 * 
	 * @requires board!=null & mark!=null
	 */
	private double bestMove(Board board, Mark mark, int depth, double score) {

		bestMove = new Move();
		if (depth == 0) {
			return score;
		} else {

			Move BestMove = null;
			double maxScore = Double.NEGATIVE_INFINITY;
			double minScore = Double.POSITIVE_INFINITY;
			for (Move m : possibleMove(board, mark)) {
				double s = score;
				Board futureBoard = board.deepCopy();

				if (futureBoard.validGetScore(m, mark)) {
					// 0.0001*depth is the priority of the move, e.g. if the AI calculates they will
					// get score in the second move and they will get the score in first move, it
					// will take the first move
					s += 1 + 0.0001 * depth;

				}
				futureBoard.applyMove(mark, m);
				// suppose that enemy gives their best move
				Move oppMove = bestNaiveMove(futureBoard, mark.opp());

				if (futureBoard.validGetScore(oppMove, mark.opp())) {
					s -= 1 + 0.0001 * depth;
				}
				futureBoard.applyMove(mark.opp(), oppMove);

				// check possible future
				double i = bestMove(futureBoard, mark, depth - 1, s);
				// get the max score of the future
				if (maxScore < i) {
					maxScore = i;
					BestMove = m;
				}

				// get the max score of the future
				if (minScore > i) {
					minScore = i;
				}

			}
			// if the score random move is not different from the best move(ie no get score)
			// then make best move the best naive move
			if (depth == this.depth && minScore != maxScore) {
				bestMove = BestMove;
			} else {
				bestMove = bestNaiveMove(board, mark);
			}
			return score;
		}
	}

}
