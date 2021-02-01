package Machenism;

import Server.PlayerHandler;
import Server.ProtocolMessages;

/**
 * 
 * Player of the game
 * 
 * @author Long Quang Huynh
 */

public class Player {
	/**
	 * Mark that player plays for
	 */
	private Mark mark;
	/**
	 * Name of the player
	 * 
	 * @invariant name!=null
	 */
	private String name;
	/**
	 * Score of the player
	 * 
	 * @invariant score!=0
	 */
	private int score;
	/**
	 * ready status of the player
	 */
	private boolean ready;
	/**
	 * game the player are in
	 */
	private Game game;

	/**
	 * handler handling the player
	 */
	private PlayerHandler handler;

	/**
	 * Construct a player with the given name
	 */
	public Player(String name) {
		this.name = name;
	}

	/**
	 * set player handler
	 * 
	 * @requires handler != null
	 * @param handler
	 * @ensures this.handler == handler
	 */
	public void setPlayerHandler(PlayerHandler handler) {
		this.handler = handler;
	}

	/**
	 * Set player unready
	 * 
	 * @ensures this.ready == false
	 */
	public void setUnready() {
		ready = false;
	}

	/**
	 * get handler of this player
	 * 
	 * @return a handler of this player
	 * @ensures \result!=null
	 */
	public PlayerHandler getPlayerHandler() {
		return handler;
	}

	/**
	 * get the current room of the player
	 * 
	 * @return the room(game) player is in
	 */
	public Game getGame() {
		return this.game;
	}

	/**
	 * get the score of the player
	 * 
	 * @return the current score of player
	 * @ensures \result>=0
	 */
	public int getScore() {
		return score;
	}

	/**
	 * resets score of the player
	 * 
	 * @ensures getScore() == 0
	 */
	public void resetScore() {
		this.score = 0;
	}

	/**
	 * set player to ready
	 * 
	 * @return Ok if player was not ready and sets ready == true || already
	 *         connected if ready == true
	 * @ensures ready ==true
	 */
	public synchronized String setReady() {
		if (this.ready) {
			return ProtocolMessages.E_ALREADY_READY;
		}
		this.ready = true;
		return ProtocolMessages.OK;
	}

	/**
	 * To check if the player is ready
	 * 
	 * @return true if player is ready, otherwise fasle
	 */
	public boolean readyCheck() {
		return ready;
	}

	/**
	 * sets player's mark
	 * 
	 * @requires mark != null
	 * @param mark
	 * @ensures getMark() == mark
	 */
	public void setMark(Mark mark) {
		this.mark = mark;
	}

	/**
	 * Get mark of the player
	 * 
	 * @return the current mark of the player
	 * 
	 */
	public Mark getMark() {
		return this.mark;
	}

	/**
	 * Get name of the player
	 * 
	 * @return the name of the player
	 * @ensures \result!=null
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Set the game for the player
	 * 
	 * @requires game!=null
	 * @ensure this.game==game
	 */
	public void setGame(Game game) {
		this.game = game;
	}

	/**
	 * increments player score
	 * 
	 * @ensures this.score == old.score + 1
	 */
	public void increase() {
		score++;
	}

	/**
	 * String presentation of the player
	 */
	public String toString() {
		return name + "," + mark + "," + score;
	}

}
