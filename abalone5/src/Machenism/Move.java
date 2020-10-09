package Machenism;

/**
 * Class representing the abalone game a move of the abalone game
 * 
 */

public class Move {
	/**
	 * First position of the move
	 * 
	 * @invariant pos1>0
	 */
	private int pos1;
	/**
	 * Second position of the move
	 * 
	 * @invariant pos2>=0
	 * 
	 */
	private int pos2;
	/**
	 * First position of the move
	 * 
	 * @invariant pos3>=0. If pos2==0 -> pos3==0
	 * 
	 */
	private int pos3;

	/**
	 * Direction of the move
	 * 
	 * @invariant Board.DIRECTION_SET.contains(direction)
	 * 
	 */
	private String direction;

	/**
	 * Set the first position of the move
	 * 
	 * @param pos position of the marble to be move
	 * @requires pos != null && pos>0 && pos<62
	 * @ensures getPos1() == pos
	 */
	public void set1Pos(int pos) {
		this.pos1 = pos;
	}

	/**
	 * Set the first and second position of the move
	 * 
	 * @param pos1 position of marble 1
	 * @param pos2 position of marble 2
	 * @requires pos1 != null && pos1>0 && pos1<62 && pos2 != null && pos2>0 &&
	 *           pos2<62
	 * @ensures getPos1() == pos1 && getPos2() == pos2
	 */
	public void set2Pos(int pos1, int pos2) {
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.pos3 = 0;
	}

	/**
	 * Sets the first,second and third position of the move
	 * 
	 * @param pos1 position of marble 1
	 * @param pos2 position of marble 2
	 * @param pos3 position of marble 3
	 * @requires pos1>0 && pos1<62 && pos2>0 && pos2<62 && pos3>0 && pos3<62
	 * @ensures getPos1() == pos1 && getPos2() == pos2 && getPos3() == pos3
	 */
	public void set3Pos(int pos1, int pos2, int pos3) {
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.pos3 = pos3;
	}

	/**
	 * Set direction for the move
	 * 
	 * @param direction direction of the move
	 * @requires direction !=null
	 * @ensures getDirection() == direction
	 */
	public void setDirection(String direction) {
		this.direction = direction;
	}

	/**
	 * Get the position of the first marble
	 * 
	 * @ensures \result>0
	 *
	 */
	public int getPos1() {
		return pos1;
	}

	/**
	 * Get the position of the second marble
	 * 
	 * @ensures \result>=0
	 *
	 */
	public int getPos2() {
		return pos2;
	}

	/**
	 * Get the position of the second marble
	 * 
	 * @ensures \result>=0
	 *
	 */
	public int getPos3() {
		return pos3;
	}

	/**
	 * Get the position of the second marble
	 * 
	 * @ensures \result>=0
	 *
	 */
	public String getDirection() {
		return direction;
	}

}
