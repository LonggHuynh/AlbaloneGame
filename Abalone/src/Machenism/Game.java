package Machenism;

import java.util.ArrayList;
import Server.ProtocolMessages;

/**
 * Class representing the abalone game
 */

public class Game {
	/**
	 * list of the players in the game
	 */
	private final ArrayList<Player> players;
	/**
	 * the board of the game
	 */
	private Board board;
	/**
	 * the current turn of the player
	 * 
	 * @invariant current >=0& current <capacity
	 * 
	 */
	private int current;
	/**
	 * password of the game
	 * 
	 * 
	 */
	private final String password;
	/**
	 * The number of remaining turns before the game ends
	 * 
	 * @invariant turnLeft<96 & turnLeft>0
	 */
	private int turnLeft = 96;

	/**
	 * The name of the game
	 * 
	 * @invariant name!=null
	 */
	private final String name;

	/**
	 * The start status of the game, true if the game is in progress
	 */
	private boolean start = false;

	/**
	 * Capacity of the game
	 * 
	 * @invariant capacity<5 &capacity >0
	 */
	private final int capacity;

	public Game(int capacity, String name, String password) {
		String hashedPassword = Password.getHashPassword(password);
		this.players = new ArrayList<>();
		this.capacity = capacity;
		this.name = name;
		this.password = hashedPassword;

	}

	/**
	 * gets the list of players in the game
	 * 
	 * @ensures result!=0
	 *
	 */
	public synchronized ArrayList<Player> getPlayers() {
		return players;
	}

	/**
	 * @return string representation of the game's information
	 */
	public String toString() {
		return "" + name + "," + players.size() + "," + capacity + "," + (!password.equals(""));
	}

	// returns game name
	public String getName() {
		return name;
	}

	/**
	 * To add a player into a game
	 * 
	 * @param p player
	 * @requires p!=null&
	 * @ensures player.contains(p)
	 */
	public synchronized void addPlayer(Player p) {
		p.setGame(this);
		players.add(p);

	}

	/**
	 * To reset the game after the game is over
	 * 
	 * @ensures start==false;
	 * @ensures turnLeft ==96;
	 * @ensures all players are unready
	 * @ensures for all players, player.getScore()==0
	 */
	public void reset() {
		start = false;
		turnLeft = 96;
		for (Player p : players) {
			p.resetScore();
			p.setUnready();
		}

	}

	/**
	 * @ensures start == true;
	 * @requires all players are ready
	 */
	public synchronized void setStart() {
		board = new Board(capacity);
		Mark[] marks;
		if (capacity == 2) {

			marks = new Mark[]{ Mark.Y, Mark.W };
		} else if (capacity == 3) {

			marks = new Mark[]{ Mark.W, Mark.Y, Mark.R };
		} else {

			marks = new Mark[]{ Mark.Y, Mark.B, Mark.W, Mark.R };
		}

		for (int i = 0; i < marks.length; i++) {
			players.get(i).setMark(marks[i]);
		}
		start = true;
		current = 0;

	}

	/**
	 * To get player who has the same mark
	 * 
	 * @requires p != null
	 * @param p player
	 * @return player with the same mark
	 */
	public Player ally(Player p) {
		for (Player pl : players) {
			if (capacity == 4) {
				if (p.getMark().alliedMark() == pl.getMark()) {
					return pl;
				}
			}

		}
		return null;
	}

	/**
	 * Check if there is turn left
	 * 
	 * @return true if remaining turn >0
	 */
	public synchronized boolean turnOver() {
		return turnLeft <= 0;

	}

	/**
	 * 
	 * @requires start == true;
	 * @ensures maxscore>0 && maxscore <=6
	 * @return current player/pair with the highest point in game
	 */
	public synchronized Player getHighestScorePlayer() {
		int maxScore = -1;
		Player hig = null;
		for (Player p : players) {
			Player ally = ally(p);
			if (ally(p) != null) {
				if (p.getScore() + ally.getScore() > maxScore) {
					maxScore = p.getScore() + ally.getScore();
					hig = p;
				}
			} else {
				if (p.getScore() > maxScore) {
					maxScore = p.getScore();
					hig = p;
				}
			}
		}
		return hig;
	}

	/**
	 * Check if the game is draw
	 * 
	 * @requires start == true;
	 * @return true if the game is draw, otherwise false
	 */

	public synchronized boolean draw() {
		Player p = getHighestScorePlayer();
		if (ally(p) == null) {
			for (Player pl : players) {
				if (pl.getScore() == p.getScore() && pl != p) {
					return true;
				}
			}
			return false;
		} else {
			for (Player pl : players) {
				if (pl.getScore() + ally(pl).getScore() == p.getScore() + ally(p).getScore() && pl != p
						&& pl != ally(p)) {
					return true;
				}
			}

			return false;
		}

	}

	/**
	 * Check if the game has a winner
	 * 
	 * @requires start == true;
	 * @return true if the game has a player/pair reach the 6 points
	 */
	public synchronized boolean hasWinner() {
		return getWinner() != null;
	}

	/**
	 * 
	 * @requires getWinner() == true
	 * @return player who won the game
	 * @requires start == true
	 * @ensures result != null
	 */
	public Player getWinner() {
		for (Player p : players) {
			if (capacity < 4) {
				if (p.getScore() == 6) {
					return p;
				}
			}
			if (capacity == 4) {
				if (p.getScore() + ally(p).getScore() == 6) {
					return p;
				}
			}

		}
		return null;
	}

	/**
	 * @requires password != null
	 * @param password password to be checked
	 * @return true if password == this.password
	 */
	public boolean checkPass(String password) {
		String hashedPassword = Password.getHashPassword(password);
		return hashedPassword.equals(this.password);
	}

	/**
	 * check if the game is full
	 * 
	 * @return true if the room is full, otherwise false
	 */
	public synchronized boolean isFull() {
		return players.size() == capacity;
	}

	/**
	 * check if the game is start
	 */
	public boolean isStart() {
		return start;
	}

	/**
	 * get the player who is in turn
	 * 
	 * @requires isStart==true;
	 * @return the player who is in turn
	 */
	public Player currentPlayer() {
		if (current > players.size()) {
			return null;
		}
		return players.get(current);
	}

	/**
	 * handles player's move
	 * 
	 * @param move the string combination of the moves
	 * @param p    player who makes the move
	 * @requires move != null && p != null
	 * @ensures result != null
	 * @return move in string form
	 */
	public synchronized String handleMove(String move, Player p) {
		if (current > players.size()) {
			return "";
		}
		if (players.get(current) != p) {
			return ProtocolMessages.E_NOT_YOUR_TURN;
		} else {
			try {

				String[] msgs = move.split(":");
				int nrMar = 0;
				int[] balls = new int[3];
				for (int i = 1; i < 4; i++) {
					if (!msgs[i].equals("")) {
						balls[nrMar] = Integer.parseInt(msgs[i]);
						nrMar++;
					}
				}
				if (nrMar == 2) {
					if (board.validGetScore(balls[0], balls[1], msgs[4], players.get(current).getMark())) {

						players.get(current).increase();

					}
					if (board.makeMove(balls[0], balls[1], msgs[4], players.get(current).getMark())) {
						current = (current + 1) % capacity;
						turnLeft--;
						return move;

					} else {
						return ProtocolMessages.E_ILLEGAL_MOVE;
					}

				}
				if (nrMar == 3) {
					if (board.validGetScore(balls[0], balls[1], balls[2], msgs[4], players.get(current).getMark())) {
						players.get(current).increase();

					}
					if (board.makeMove(balls[0], balls[1], balls[2], msgs[4], players.get(current).getMark())) {
						current = (current + 1) % capacity;
						turnLeft--;
						return move;

					} else {
						return ProtocolMessages.E_ILLEGAL_MOVE;
					}

				} else {

					if (board.makeMove(balls[0], msgs[4], players.get(current).getMark())) {
						current = (current + 1) % capacity;

						turnLeft--;
						return move;

					} else {
						return ProtocolMessages.E_ILLEGAL_MOVE;
					}
				}

			} catch (Exception e) {
				return "unknown_format";
			}
		}

	}

	/**
	 * give the board of the game
	 * 
	 * @return the board of the game
	 */

	public Board getBoard() {
		return board;
	}

	/**
	 * Removes player from game
	 * 
	 * @param p the player to be removed
	 * @requires p!= null
	 * @ensures ! player.contains(p)
	 */
	public synchronized void reMovePlayer(Player p) {
		p.resetScore();
		p.setGame(null);
		p.setUnready();
		players.remove(p);
	}

}
