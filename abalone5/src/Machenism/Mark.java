package Machenism;

/**
 * Class representing the abalone game mark/color of the field or the player
 * 
 */

public enum Mark {
	B, R, O, G, E, I, Y, W;

	/**
	 * Get the mark of the enemy only used for 4-player mode
	 * 
	 * @ensures result!=null
	 * @return the teammate's mark
	 */
	public Mark alliedMark() {
		if (this == B)
			return R;
		if (this == R)
			return B;
		if (this == Y)
			return W;
		if (this == W)
			return Y;
		else
			return I;
	}

	/**
	 * Get the mark of the enemy only used for 1v1 mode (for AI)
	 * 
	 * @requires this==Y| this==W
	 * @ensures result!=null
	 * @return the enemy mark
	 */
	public Mark opp() {
		if (this == Y) {
			return W;
		} else {
			return Y;
		}
	}

}
