package Machenism;


/**
 * Class representing the abalone game mark/color of the field or the player
 */

public enum Mark {
    B, R, E, I, Y, W;

    /**
     * Get the mark of the enemy only used for 4-player mode
     *
     * @return the teammate's mark
     * @ensures result!=null
     */
    Mark alliedMark() {
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
     * @return the enemy mark
     * @requires this==Y| this==W
     * @ensures result!=null
     */
    Mark opp() {
        if (this == Y) {
            return W;
        } else {
            return Y;
        }
    }

}
