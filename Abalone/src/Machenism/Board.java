package Machenism;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Board {
    /**
     * the set of valid direction
     */

    public static final Set<String> DIRECTION_SET = new HashSet<>(Arrays.asList("UL", "UR", "L", "R", "DL", "DR"));

    /**
     * the set of valid direction used for AI, which reduces overlapping possible
     * moves and boost the performance
     */
    public static final Set<String> HALF_DIRECTION_SET = new HashSet<>(Arrays.asList("UL", "UR", "L"));

    /**
     * number of the player the board is set for
     *
     * @invariant nrPlayers>0 & nrPlayers<5
     */
    private int nrPlayers;

    private final Mark[][] fields = new Mark[9][9];

    /**
     * Construct the board with the given number of players
     *
     * @param nrPlayers number of the player the board is set for
     * @requires nrPlayers>0 & nrPlayers<5
     * @ensures this.nrPlayers==nrPlayers
     */
    public Board(int nrPlayers) {
        this.nrPlayers = nrPlayers;
        setInitialBoard(nrPlayers);
    }

    /**
     * To check if the position is a valid field
     *
     * @param pos of field
     * @return true if is valid and false otherwise
     */
    public boolean validField(int pos) {
        return (pos > 0) && (pos < 62);
    }

    /**
     * To set the field into the mark
     *
     * @param pos  in the board
     * @param mark mark of player
     * @requires mark != I & mark !=null
     * @ensures getField(pos) == mark
     */
    public void setField(int pos, Mark mark) {
        if (validField(pos)) {
            fields[getColumn(pos)][getRow(pos)] = mark;
        }

    }

    /**
     * Checks if move is valid for 2 marbles
     *
     * @param pos1      position of the first selected marble
     * @param pos2      position of the first selected marble
     * @param direction direction of the move
     * @return true if valid
     * @requires mark != null
     */
    public boolean validMove(int pos1, int pos2, String direction, Mark mark) {
        return validSumitoNoScore(pos1, pos2, direction, mark) || validGetScore(pos1, pos2, direction, mark)
                || validSide(pos1, pos2, direction, mark) || validSilentPush(pos1, pos2, direction, mark);
    }

    /**
     * Checks if move is valid for 3 marbles
     *
     * @param pos1      position of the marble 1
     * @param pos2      position of the marble 2
     * @param pos3      position of the marble 3
     * @param direction direction of the move
     * @param mark      player's mark
     * @return true if valid
     * @requires direction != null && mark != null
     */
    public boolean validMove(int pos1, int pos2, int pos3, String direction, Mark mark) {
        return validSumitoNoScore(pos1, pos2, pos3, direction, mark) || validGetScore(pos1, pos2, pos3, direction, mark)
                || validSide(pos1, pos2, pos3, direction, mark) || validSilentPush(pos1, pos2, pos3, direction, mark);
    }

    /**
     * Copy of the board
     */
    public Board deepCopy() {
        Board boardCopy = new Board(1);
        boardCopy.setEmpty();
        for (int i = 1; i < 62; i++) {
            boardCopy.setField(i, getField(i));
        }
        return boardCopy;
    }

    /**
     * @param nrPlayers number of players
     * @requires nrPlayers>1 && nrPlayers<5
     */
    private void setInitialBoard(int nrPlayers) {
        setEmpty();
        if (nrPlayers == 2) {
            setBoardForTwo();
        } else if (nrPlayers == 3) {
            setBoardForThree();
        } else {
            setBoardForFour();
        }
    }

    /**
     * Check if move is a valid sumito and pushes opponent out of board for 1
     * marbles
     *
     * @param mark player's mark
     * @return true if is valid and does not gives score
     * @requires m!= null && mark != null
     */
    public boolean validGetScore(Move m, Mark mark) {
        return validGetScore(m.getPos1(), m.getPos2(), m.getPos3(), m.getDirection(), mark)
                || validGetScore(m.getPos1(), m.getPos2(), m.getDirection(), mark);
    }

    /**
     * Check if move is valid sumito and does not push opponent out of board for 1
     * marble
     *
     * @param m    selected move
     * @param mark player's mark
     * @return true if valid sumito and does not push opponent out of board
     * @requires m!= null && mark != null
     */
    public boolean validSumitoNoScore(Move m, Mark mark) {
        return validSumitoNoScore(m.getPos1(), m.getPos2(), m.getPos3(), m.getDirection(), mark)
                || validSumitoNoScore(m.getPos1(), m.getPos2(), m.getDirection(), mark);
    }

    /**
     * Checks if move is valid pushing(inline)
     *
     * @param m    selected move
     * @param mark player's mark
     * @return true if is valid push
     * @requires m!= null && mark != null
     */
    public boolean pushing(Move m, Mark mark) {
        return pushing(m.getPos1(), m.getPos2(), m.getPos3(), m.getDirection(), mark)
                || pushing(m.getPos1(), m.getPos2(), m.getDirection(), mark);
    }

    /**
     * get field of position
     *
     * @param pos postion of the field
     * @return mark of field
     */
    public Mark getField(int pos) {
        if (validField(pos)) {
            int row = getRow(pos);
            int col = getColumn(pos);
            return fields[col][row];
        }
        return Mark.I;

    }

    /**
     * To check if move has been made with one marble selected
     *
     * @param pos       position of the marble
     * @param direction direction of intended move
     * @param mark      player's mark
     * @return true if move has been updated and false otherwise
     * @requires direction != null && mark!= null
     */
    public boolean makeMove(int pos, String direction, Mark mark) {
        if (validMove(pos, direction) && validChoice(pos, mark)) {
            int newPos1 = posOf(pos, direction);
            setField(pos, Mark.E);
            setField(newPos1, mark);
            return true;
        }
        return false;
    }

    /**
     * To check if the move has been made with three marbles selected, make the move
     * if result true, otherwise move is not made
     *
     * @param pos1      position of first marble
     * @param pos2      position of second marble
     * @param pos3      position of third marble
     * @param direction direction of intended move
     * @param mark      player's mark
     * @return true if move is valid
     * @requires direction!=null & mark!=null
     */
    public boolean makeMove(int pos1, int pos2, int pos3, String direction, Mark mark) {
        Mark colorPos1 = getField(pos1);
        Mark colorPos2 = getField(pos2);
        Mark colorPos3 = getField(pos3);
        int newPos1 = posOf(pos1, direction);
        int newPos2 = posOf(pos2, direction);
        int newPos3 = posOf(pos3, direction);
        int mid = getMiddle(pos1, pos2, pos3);
        int head = posOf(mid, direction);
        int enemy = posOf(head, direction);
        int sndEnemy = posOf(enemy, direction);
        int newSndEnemy = posOf(sndEnemy, direction);
        Mark enemyColour = getField(enemy);
        Mark sndEnemyColour = getField(sndEnemy);
        if (validSilentPush(pos1, pos2, pos3, direction, mark) || validSide(pos1, pos2, pos3, direction, mark)) {
            setField(pos1, Mark.E);
            setField(pos2, Mark.E);
            setField(pos3, Mark.E);
            setField(newPos1, colorPos1);
            setField(newPos2, colorPos2);
            setField(newPos3, colorPos3);
            return true;
        }

        if (validSumitoNoScore(pos1, pos2, pos3, direction, mark)) {
            // the second field ahead is empty
            if (validMove(enemy, direction)) {
                setField(pos1, Mark.E);
                setField(pos2, Mark.E);
                setField(pos3, Mark.E);
                setField(newPos1, colorPos1);
                setField(newPos2, colorPos2);
                setField(newPos3, colorPos3);
                setField(sndEnemy, enemyColour);
            } else {
                setField(pos1, Mark.E);
                setField(pos2, Mark.E);
                setField(pos3, Mark.E);
                setField(newPos1, colorPos1);
                setField(newPos2, colorPos2);
                setField(newPos3, colorPos3);
                setField(sndEnemy, enemyColour);
                setField(newSndEnemy, sndEnemyColour);
            }

            return true;

        }

        if (validGetScore(pos1, pos2, pos3, direction, mark)) {

            setField(pos1, Mark.E);
            setField(pos2, Mark.E);
            setField(pos3, Mark.E);
            setField(newPos1, colorPos1);
            setField(newPos2, colorPos2);
            setField(newPos3, colorPos3);
            setField(sndEnemy, enemyColour);

            return true;
        }

        return false;
    }

    /**
     * To check if the move has been made with 2 marbles selected, make the move if
     * result true, otherwise move is not made
     *
     * @param pos1      position of first marble
     * @param pos2      position of second marble
     * @param direction direction of intended move
     * @param mark      player's mark
     * @return true if move is valid
     * @requires direction!=null& mark!=null
     */
    public boolean makeMove(int pos1, int pos2, String direction, Mark mark) {
        int head;
        Mark colorPos1 = getField(pos1);
        Mark colorPos2 = getField(pos2);
        int newPos1 = posOf(pos1, direction);
        int newPos2 = posOf(pos2, direction);

        if (directionOf(pos1, pos2).equals(direction)) {
            head = pos2;
        } else {
            head = pos1;
        }
        int enemy = posOf(head, direction);
        int sndEnemy = posOf(enemy, direction);
        Mark enemyColour = getField(enemy);

        if (validSilentPush(pos1, pos2, direction, mark) || validSide(pos1, pos2, direction, mark)) {
            setField(pos1, Mark.E);
            setField(pos2, Mark.E);
            setField(newPos1, colorPos1);
            setField(newPos2, colorPos2);
            return true;
        }

        if (validSumitoNoScore(pos1, pos2, direction, mark)) {
            setField(pos1, Mark.E);
            setField(pos2, Mark.E);
            setField(newPos1, colorPos1);
            setField(newPos2, colorPos2);
            setField(sndEnemy, enemyColour);
            return true;

        }

        if (validGetScore(pos1, pos2, direction, mark)) {
            setField(pos1, Mark.E);
            setField(pos2, Mark.E);
            setField(newPos1, colorPos1);
            setField(newPos2, colorPos2);
            return true;
        }

        return false;
    }

    /**
     * Initializes the necessary board fields to be empty
     */
    public void setEmpty() {
        for (int i = 0; i < 9; i++) {
            if (i == 0 || i == 8) {
                if (i == 0) {
                    for (int j = 0; j < 5; j++) {
                        fields[i][j] = Mark.E;
                    }
                } else {
                    for (int j = 4; j < 9; j++) {
                        fields[i][j] = Mark.E;
                    }
                }
            } else if (i == 1 || i == 7) {
                if (i == 1) {
                    for (int j = 0; j < 6; j++) {
                        fields[i][j] = Mark.E;
                    }
                } else {
                    for (int j = 3; j < 9; j++) {
                        fields[i][j] = Mark.E;
                    }
                }
            } else if (i == 2 || i == 6) {
                if (i == 2) {
                    for (int j = 0; j < 7; j++) {
                        fields[i][j] = Mark.E;
                    }
                } else {
                    for (int j = 2; j < 9; j++) {
                        fields[i][j] = Mark.E;
                    }
                }
            } else if (i == 3 || i == 5) {
                if (i == 3) {
                    for (int j = 0; j < 8; j++) {
                        fields[i][j] = Mark.E;
                    }
                } else {
                    for (int j = 1; j < 9; j++) {
                        fields[i][j] = Mark.E;
                    }
                }
            } else {
                for (int j = 0; j < 9; j++) {
                    fields[i][j] = Mark.E;
                }
            }

        }
    }

    /**
     * Arranges the board for a 2 player game
     */
    private void setBoardForTwo() {
        for (int i = 0; i < 6; i++) {
            if (i == 0 || i == 1) {
                for (int j = 0; j < 2; j++) {
                    fields[i][j] = Mark.Y;
                }
            } else if (i == 2 || i == 3 || i == 4) {
                for (int j = 0; j < 3; j++) {
                    fields[i][j] = Mark.Y;
                }
            } else {
                for (int j = 1; j < 2; j++) {
                    fields[i][j] = Mark.Y;
                }
            }
        }
        for (int i = 3; i < 9; i++) {
            if (i == 3) {
                for (int j = 7; j < 8; j++) {
                    fields[i][j] = Mark.W;
                }
            } else if (i == 4 || i == 5 | i == 6) {
                for (int j = 6; j < 9; j++) {
                    fields[i][j] = Mark.W;
                }
            } else {
                for (int j = 7; j < 9; j++) {
                    fields[i][j] = Mark.W;
                }
            }
        }

    }

    /**
     * Arranges the board for a 3 player game
     */
    private void setBoardForThree() {
        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                for (int j = 0; j < 5; j++) {
                    fields[i][j] = Mark.R;
                }
            } else {
                for (int j = 0; j < 6; j++) {
                    fields[i][j] = Mark.R;
                }
            }
        }
        for (int i = 3; i < 9; i++) {
            if (i == 3) {
                for (int j = 0; j < 1; j++) {
                    fields[i][j] = Mark.Y;
                }
            } else if (i == 4) {
                for (int j = 0; j < 2; j++) {
                    fields[i][j] = Mark.Y;
                }
            } else if (i == 5) {
                for (int j = 1; j < 3; j++) {
                    fields[i][j] = Mark.Y;
                }
            } else if (i == 6) {
                for (int j = 2; j < 4; j++) {
                    fields[i][j] = Mark.Y;
                }
            } else if (i == 7) {
                for (int j = 3; j < 5; j++) {
                    fields[i][j] = Mark.Y;
                }
            } else {
                for (int j = 4; j < 6; j++) {
                    fields[i][j] = Mark.Y;
                }
            }
        }
        for (int i = 3; i < 9; i++) {
            if (i == 3) {
                for (int j = 7; j < 8; j++) {
                    fields[i][j] = Mark.W;
                }
            } else {
                for (int j = 7; j < 9; j++) {
                    fields[i][j] = Mark.W;
                }
            }
        }
    }

    /**
     * Arranges the board for a 4 player game
     */
    private void setBoardForFour() {
        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                for (int j = 0; j < 1; j++) {
                    fields[i][j] = Mark.R;
                }
            } else if (i == 1) {
                for (int j = 0; j < 2; j++) {
                    fields[i][j] = Mark.R;
                }
            } else {
                for (int j = 0; j < 3; j++) {
                    fields[i][j] = Mark.R;
                }
            }
        }
        for (int i = 5; i < 9; i++) {
            if (i == 5) {
                for (int j = 1; j < 4; j++) {
                    fields[i][j] = Mark.Y;
                }
            } else if (i == 6) {
                for (int j = 2; j < 5; j++) {
                    fields[i][j] = Mark.Y;
                }
            } else if (i == 7) {
                for (int j = 3; j < 5; j++) {
                    fields[i][j] = Mark.Y;
                }
            } else {
                for (int j = 4; j < 5; j++) {
                    fields[i][j] = Mark.Y;
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                for (int j = 4; j < 5; j++) {
                    fields[i][j] = Mark.W;
                }
            } else if (i == 1) {
                for (int j = 4; j < 6; j++) {
                    fields[i][j] = Mark.W;
                }
            } else if (i == 2) {
                for (int j = 4; j < 7; j++) {
                    fields[i][j] = Mark.W;
                }
            } else {
                for (int j = 5; j < 8; j++) {
                    fields[i][j] = Mark.W;
                }
            }
        }
        for (int i = 5; i < 9; i++) {
            if (i == 5) {
                for (int j = 6; j < 9; j++) {
                    fields[i][j] = Mark.B;
                }
            } else if (i == 6) {
                for (int j = 6; j < 9; j++) {
                    fields[i][j] = Mark.B;
                }
            } else if (i == 7) {
                for (int j = 7; j < 9; j++) {
                    fields[i][j] = Mark.B;
                }
            } else {
                for (int j = 8; j < 9; j++) {
                    fields[i][j] = Mark.B;
                }
            }
        }
    }

    /**
     * Check if move is a valid push(in-line move) when three marbles selected
     *
     * @param pos1      position of first marble
     * @param pos2      position of second marble
     * @param pos3      position of third marble
     * @param direction direction of intended push
     * @param mark      player's mark
     * @return true if is a pushing move
     * @requires direction!=null & mark!=null
     */
    public boolean pushing(int pos1, int pos2, int pos3, String direction, Mark mark) {
        int mid = getMiddle(pos1, pos2, pos3);
        int tail = posOf(mid, oppDirection(direction));
        boolean con1 = getField(tail) == mark;
        boolean con2 = validChoice(pos1, pos2, pos3, mark);
        boolean con3 = (directionOf(pos1, pos2).equals(direction) | directionOf(pos2, pos1).equals(direction)
                | directionOf(pos1, pos3).equals(direction) | directionOf(pos3, pos1).equals(direction));
        return con1 && con2 && con3;

    }

    /**
     * Check if move is a valid push(in-line move) when 2 marbles selected
     *
     * @param pos1      position of first marble
     * @param pos2      position of second marble
     * @param direction
     * @param mark
     * @return true if is a pushing move
     * @requires direction != null && mark != null
     */
    public boolean pushing(int pos1, int pos2, String direction, Mark mark) {
        int tail;
        if (directionOf(pos1, pos2).equals(direction)) {
            tail = pos1;
        } else {
            tail = pos2;
        }
        boolean con1 = getField(tail) == mark;
        boolean con2 = validChoice(pos1, pos2, mark);
        boolean con3 = directionOf(pos1, pos2).equals(direction) | directionOf(pos2, pos1).equals(direction);
        return con1 && con2 && con3;

    }

    /**
     * Check if move is valid side move for 3 marbles are selected
     *
     * @param pos1      position of 1st marble
     * @param pos2      position of 2nd marble
     * @param pos3      position of 3rd marble
     * @param direction direction of intended push
     * @param mark      player's mark
     * @return true if it is valid side move
     * @requires direction != null && mark != null
     */
    public boolean validSide(int pos1, int pos2, int pos3, String direction, Mark mark) {
        if (validChoice(pos1, pos2, pos3, mark)) {
            boolean con1 = validMove(pos1, direction);
            boolean con2 = validMove(pos2, direction);
            boolean con3 = validMove(pos3, direction);
            return !pushing(pos1, pos2, pos3, direction, mark) && con1 && con2 && con3;
        }
        return false;
    }

    /**
     * Check if move is valid side move when 2 marbles are selected
     *
     * @param pos1      position of first marble
     * @param pos2      position of second marble
     * @param direction direction of intended move
     * @param mark      player's mark
     * @return true if is valid side move
     * @requires direction != null && mark != null
     */
    public boolean validSide(int pos1, int pos2, String direction, Mark mark) {
        if (validChoice(pos1, pos2, mark)) {
            boolean con1 = validMove(pos1, direction);
            boolean con2 = validMove(pos2, direction);
            return !pushing(pos1, pos2, direction, mark) && con1 && con2;
        }
        return false;
    }

    /**
     * Checks if move is a valid sumito but gives no score when moving 2 marbles
     *
     * @param pos1      position of first marble
     * @param pos2      position of second marble
     * @param direction direction of intended push
     * @param mark      player's mark
     * @return true if the selection is valid and is a sumito which gets no point
     * @requires direction != null && mark != null
     */

    public boolean validSumitoNoScore(int pos1, int pos2, int pos3, String direction, Mark mark) {
        if (pushing(pos1, pos2, pos3, direction, mark)) {
            Mark alliedMark = mark;

            if (nrPlayers == 4) {
                alliedMark = mark.alliedMark();
            }
            int mid = getMiddle(pos1, pos2, pos3);
            int head = posOf(mid, direction);
            int enemy = posOf(head, direction);
            int sndEnemy = posOf(enemy, direction);
            int rdEnemy = posOf(sndEnemy, direction);
            boolean cond1 = getField(enemy) != mark && getField(enemy) != alliedMark && getField(enemy) != Mark.I
                    && getField(enemy) != Mark.E;
            boolean cond2 = getField(sndEnemy) != mark && getField(enemy) != alliedMark && getField(sndEnemy) != Mark.I
                    && getField(sndEnemy) != Mark.E;
            boolean cond3 = getField(sndEnemy) == Mark.E;
            boolean cond4 = getField(rdEnemy) == Mark.E;
            return cond1 && (cond3 || (cond2 && cond4));

        }
        return false;
    }

    /**
     * Checks if move is a valid sumito but gives no score when moving 2 marbles
     *
     * @param pos1      position of first marble
     * @param pos2      position of second marble
     * @param direction direction of intended push
     * @param mark      player's mark
     * @return true if the selection is valid and is a sumito which gets no point
     * @requires direction != null && mark != null
     */
    public boolean validSumitoNoScore(int pos1, int pos2, String direction, Mark mark) {
        if (pushing(pos1, pos2, direction, mark)) {
            int head;
            Mark alliedMark = mark;

            if (nrPlayers == 4) {
                alliedMark = mark.alliedMark();
            }
            if (directionOf(pos1, pos2).equals(direction)) {
                head = pos2;
            } else {
                head = pos1;
            }
            if (nrPlayers == 4) {
                alliedMark = mark.alliedMark();
            }
            int enemy = posOf(head, direction);
            int sndEnemy = posOf(enemy, direction);
            boolean cond1 = getField(enemy) != mark && getField(enemy) != alliedMark && getField(enemy) != Mark.I
                    && getField(enemy) != Mark.E;
            boolean cond2 = getField(sndEnemy) == Mark.E;
            return cond1 && cond2;

        }
        return false;
    }

    /**
     * Check if move is a valid sumito and pushes opponent out of board when moving
     * 2 marbles
     *
     * @param pos1      position of first marble
     * @param pos2      position of second marble
     * @param direction direction of intended move
     * @param mark      player's mark
     * @return true if valid and pushes opponent out of board
     * @requires direction != null && mark != null
     */

    public boolean validGetScore(int pos1, int pos2, String direction, Mark mark) {
        if (pushing(pos1, pos2, direction, mark)) {
            Mark alliedMark = mark;

            if (nrPlayers == 4) {
                alliedMark = mark.alliedMark();
            }
            int head;
            if (directionOf(pos1, pos2).equals(direction)) {
                head = pos2;
            } else {
                head = pos1;
            }
            int enemy = posOf(head, direction);
            int sndEnemy = posOf(enemy, direction);
            boolean cond1 = getField(enemy) != mark && getField(enemy) != alliedMark && getField(enemy) != Mark.I
                    && getField(enemy) != Mark.E;
            boolean cond2 = getField(sndEnemy) == Mark.I;
            return cond1 && cond2;

        }
        return false;
    }

    /**
     * Check if move is a valid sumito and gives a point when moving 2 marbles
     *
     * @param pos1      position of first marble
     * @param pos2      position of second marble
     * @param pos3      position of third marble
     * @param direction direction of intended move
     * @param mark      player's mark
     * @return true if valid and pushes opponent out of board
     * @requires direction != null && mark != null
     */
    public boolean validGetScore(int pos1, int pos2, int pos3, String direction, Mark mark) {
        if (pushing(pos1, pos2, pos3, direction, mark)) {
            Mark alliedMark = mark;

            if (nrPlayers == 4) {
                alliedMark = mark.alliedMark();
            }
            int mid = getMiddle(pos1, pos2, pos3);
            int head = posOf(mid, direction);
            int enemy = posOf(head, direction);
            int sndEnemy = posOf(enemy, direction);
            int rdEnemy = posOf(sndEnemy, direction);
            boolean cond1 = getField(enemy) != mark && getField(enemy) != alliedMark && getField(enemy) != Mark.I
                    && getField(enemy) != Mark.E;
            boolean cond2 = getField(sndEnemy) != mark && getField(sndEnemy) != alliedMark
                    && getField(sndEnemy) != Mark.I && getField(sndEnemy) != Mark.E;
            boolean cond3 = getField(sndEnemy) == Mark.I;
            boolean cond4 = getField(rdEnemy) == Mark.I;
            return cond1 && (cond3 || (cond2 && cond4));

        }
        return false;
    }

    /**
     * Check if player's move is a valid silent push for 3 marbles
     *
     * @param pos1      position of first marble
     * @param pos2      position of second marble
     * @param pos3      position of third marble
     * @param direction direction intended to push
     * @param mark
     * @return true if it is valid
     * @requires direction != null && mark != null
     */
    public boolean validSilentPush(int pos1, int pos2, int pos3, String direction, Mark mark) {
        if (pushing(pos1, pos2, pos3, direction, mark)) {
            boolean con1 = validMove(pos1, direction);
            boolean con2 = validMove(pos2, direction);
            boolean con3 = validMove(pos3, direction);
            int mid = getMiddle(pos1, pos2, pos3);
            int head = posOf(mid, direction);
            int newHead = posOf(head, direction);
            return getField(newHead) == Mark.E && (con1 || con2 || con3);

        }
        return false;
    }

    /**
     * To check if player's move is valid silent(in-line move) push for 2 marbles
     * selected
     *
     * @param pos1      position of first marble
     * @param pos2      position of second marble
     * @param direction direction of intended silent push
     * @param mark      player's mark
     * @return true if is valid and false otherwise
     * @requires direction != null && mark != null
     */
    public boolean validSilentPush(int pos1, int pos2, String direction, Mark mark) {
        if (pushing(pos1, pos2, direction, mark)) {
            boolean con1 = validMove(pos1, direction);
            boolean con2 = validMove(pos2, direction);
            int head;
            if (directionOf(pos1, pos2).equals(direction)) {
                head = pos2;
            } else {
                head = pos1;
            }

            int newHead = posOf(head, direction);

            return getField(newHead) == Mark.E && (con1 || con2);

        }
        return false;
    }

    /**
     * To give the middle integer of 3 numbers
     *
     * @param nr1 first number
     * @param nr2 second number
     * @param nr3 thrid number
     * @return the second largest number
     */
    public int getMiddle(int nr1, int nr2, int nr3) {
        int mar2;
        int mar1 = Math.min(nr1, Math.min(nr2, nr3));
        int mar3 = Math.max(nr1, Math.max(nr2, nr3));

        if (nr1 != mar1 && nr1 != mar3) {
            mar2 = nr1;
        } else if (nr2 != mar1 && nr2 != mar3) {
            mar2 = nr2;
        } else {
            mar2 = nr3;
        }
        return mar2;

    }

    /**
     * To check if the move selection is valid in terms of 3 marbles being selected
     * (marbles belong to player)
     *
     * @param pos1 position of 1st marble
     * @param pos2 position of 2nd marble
     * @param pos3 position of 3rd marble
     * @param mark player's mark
     * @return true if they are in line.
     * @requires mark != null
     */
    public boolean validChoice(int pos1, int pos2, int pos3, Mark mark) {
        boolean diff = pos1 != pos2 && pos2 != pos3 && pos1 != pos3;
        Mark alliedMark = mark;

        if (nrPlayers == 4) {
            alliedMark = mark.alliedMark();
        }
        boolean validFields = (validChoice(pos1, mark) || validChoice(pos1, alliedMark))
                && (validChoice(pos2, mark) || validChoice(pos2, alliedMark))
                && (validChoice(pos3, mark) || validChoice(pos3, alliedMark));
        boolean containsMark = validChoice(pos1, mark) || validChoice(pos2, mark) || validChoice(pos3, mark);

        int mar2 = getMiddle(pos1, pos2, pos3);
        int mar1 = Math.min(pos1, Math.min(pos2, pos3));
        int mar3 = Math.max(pos1, Math.max(pos2, pos3));

        return directionOf(mar1, mar2).equals(directionOf(mar2, mar3)) && !directionOf(mar1, mar2).equals("")
                && validFields && containsMark && diff;

    }

    /**
     * To check the move selection is valid in case of 2 marbles are selected (
     * marbles belong to player)
     *
     * @param pos1 of the marbles to move
     * @return true if they next to others and position is valid.
     * @requires mark != null
     */
    public boolean validChoice(int pos1, int pos2, Mark mark) {
        Mark alliedMark = mark;
        if (nrPlayers == 4) {
            alliedMark = mark.alliedMark();
        }
        boolean containsMark = validChoice(pos1, mark) || validChoice(pos2, mark);
        return (validChoice(pos1, mark) || validChoice(pos1, alliedMark))
                && (validChoice(pos2, mark) || validChoice(pos2, alliedMark)) && !directionOf(pos2, pos1).equals("")
                && containsMark;
    }

    /**
     * get the new position from old position with given direction of the marble the
     * result may be out of the board index
     *
     * @param pos of selected marble, direction
     * @return position from the selected position and direction or -1 if
     * !DIRECTION_SET.contains(direction)
     * @requires direction != null
     */

    public int posOf(int pos, String direction) {
        int res = -1;
        switch (direction) {
            case "UL":
                res = getPosition(getColumn(pos) - 1, getRow(pos) - 1);
                break;
            case "UR":
                res = getPosition(getColumn(pos), getRow(pos) - 1);
                break;
            case "L":
                res = getPosition(getColumn(pos) - 1, getRow(pos));
                break;
            case "R":
                res = getPosition(getColumn(pos) + 1, getRow(pos));
                break;
            case "DL":
                res = getPosition(getColumn(pos), getRow(pos) + 1);
                break;
            case "DR":
                res = getPosition(getColumn(pos) + 1, getRow(pos) + 1);
                break;
        }
        if (validField(res)) {
            return res;
        }

        return -1;
    }

    /**
     * checks if valid move for 1 marble
     *
     * @return false if validMove
     * @requires direction != null
     */

    public boolean validMove(int pos, String direction) {
        int posi = posOf(pos, direction);
        return getField(posi) == Mark.E;
    }

    /**
     * To return of pos2 compared to pos1 if they next to each other
     *
     * @param pos1 of the marbles to be compared
     * @return direction of pos2 to pos1 (LL,UL,DL,RR,UR,DR) if they are neighbors,
     * other wise ""
     * @requires pos2 != null
     */
    private String directionOf(int pos1, int pos2) {
        String res = "";
        int row1 = getRow(pos1);
        int col1 = getColumn(pos1);
        int row2 = getRow(pos2);
        int col2 = getColumn(pos2);
        if (row2 == row1 - 1) {

            if (col2 == col1) {
                res += "UR";
            } else if (col2 == col1 - 1) {
                res += "UL";
            } else {
                res = "";
            }
        } else if (row2 == row1) {

            if (col2 == col1 + 1) {
                res += "R";
            } else if (col2 == col1 - 1) {
                res += "L";
            } else {
                res = "";
            }
        } else if (row2 == row1 + 1) {

            if (col2 == col1 + 1) {
                res += "DR";
            } else if (col2 == col1) {
                res += "DL";
            } else {
                res = "";
            }
        } else {
            res = "";
        }

        return res;
    }

    /**
     * To give the oppsite direction of the selected direction
     *
     * @return direction of pos2 to pos1
     * @requires DIRECTION_SET.contains(direction)
     * @ensures DIRECTION_SET.contains(\ result)
     * @ensures \result!=direction
     */
    public String oppDirection(String direction) {

        if (direction.equals("UL")) {
            return "DR";
        }
        if (direction.equals("UR")) {
            return "DL";
        }
        if (direction.equals("R")) {
            return "L";
        }
        if (direction.equals("DL")) {
            return "UR";
        }
        if (direction.equals("DR")) {
            return "UL";
        }
        if (direction.equals("L")) {
            return "R";
        }

        // never happens
        return "??";
    }

    /**
     * To get the row of the position in the field
     *
     * @param pos position of the marble
     * @return the row of the selected position
     */

    public int getRow(int pos) {
        int pos0 = pos - 1;
        int index = 0;
        int maxCurrentCol = 5;
        for (int k = 0; k < 10; k++) {
            if (pos0 < index) {
                return k - 1;
            }
            index += maxCurrentCol;
            if (k < 4) {
                maxCurrentCol++;
            } else {
                maxCurrentCol--;
            }
        }

        return -1;
    }

    /**
     * To get the column of the position in the field
     *
     * @param pos position of the marble
     * @return the column of the selected position
     * @requires pos>0 & pos <62
     */

    public int getColumn(int pos) {
        int pos0 = pos - 1;
        int index = 0;
        int maxCurrentCol = 5;
        int row = getRow(pos);
        for (int k = 0; k < row; k++) {

            index += maxCurrentCol;
            if (k < 4) {
                maxCurrentCol++;
            } else {
                maxCurrentCol--;
            }

        }
        if (row > 4) {
            return pos0 - index + Math.abs(4 - row);
        }
        return pos0 - index;
    }

    /**
     * To check if the move selection is valid in case of 1 marble selected (marbles
     * belong to player)
     *
     * @param pos  selected position of field
     * @param mark mark of player
     * @return true if position is valid.
     * @requires mark != null
     */
    public boolean validChoice(int pos, Mark mark) {
        return validField(pos) && mark == getField(pos);
    }

    /**
     * Gets the position of field based on row and column
     *
     * @param col column of field
     * @param row row of field
     * @return integer of the selected field or -1 if the col and row is invalid
     */
    public int getPosition(int col, int row) {
        if (row > col + 4 || row < col - 4 || row < 0 || col < 0 || row > 8 || col > 8) {
            return -1;
        }
        int index = 0;
        int maxCurrentCol = 5;
        if (row > 4) {
            index -= (row - 4);
        }
        for (int k = 0; k < row; k++) {
            index += maxCurrentCol;
            if (k < 4) {
                maxCurrentCol++;
            } else {
                maxCurrentCol--;
            }
        }

        return col + 1 + index;
    }

    /**
     * @return string representation of the current board in terms of marks
     */
    public String toString() {
        String s = "        ";

        for (int i = 1; i < 6; i++) {

            s += getField(i) + "   ";
        }
        s += "\n      ";
        for (int i = 6; i < 12; i++) {

            s += getField(i) + "   ";
        }
        s += "\n    ";
        for (int i = 12; i < 19; i++) {
            s += getField(i) + "   ";
        }
        s += "\n  ";
        for (int i = 19; i < 27; i++) {
            s += getField(i) + "   ";
        }
        s += "\n";
        for (int i = 27; i < 36; i++) {
            s += getField(i) + "   ";
        }
        s += "\n  ";
        for (int i = 36; i < 44; i++) {
            s += getField(i) + "   ";
        }
        s += "\n    ";
        for (int i = 44; i < 51; i++) {
            s += getField(i) + "   ";
        }
        s += "\n      ";
        for (int i = 51; i < 57; i++) {
            s += getField(i) + "   ";
        }
        s += "\n        ";
        for (int i = 57; i < 62; i++) {
            s += getField(i) + "   ";
        }

        return s;
    }

    /**
     * Give the abstract board in form of the array of the Mark(color) corresponding
     * to the index of the field Only use to comply with the protocol
     *
     * @return a string of mark in corresponding to the position in the board
     */

    public String boardProtocolFormat() {
        String s = "";
        for (int i = 1; i < 62; i++) {
            s += "," + getField(i);
        }
        return s.substring(1);

    }

    /**
     * To apply the move to the board
     *
     * @requires mark!=null; & move != null
     */

    public void applyMove(Mark mark, Move move) {
        int pos1 = move.getPos1();
        int pos2 = move.getPos2();
        int pos3 = move.getPos3();
        String dir = move.getDirection();
        if (pos2 == 0) {
            makeMove(pos1, dir, mark);
        } else if (pos3 == 0) {
            makeMove(pos1, pos2, dir, mark);
        } else {
            makeMove(pos1, pos2, pos3, dir, mark);
        }
    }

    /**
     * return the number of the board
     *
     * @ensures \result&gt;0
     */
    public int getNrPLayer() {
        return nrPlayers;
    }

}