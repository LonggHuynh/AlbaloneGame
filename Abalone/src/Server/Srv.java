package Server;

import Machenism.Game;
import Machenism.Player;

public interface Srv {
	/**
	 * Creates connection with player
	 * 
	 * @requires player != null
	 * @param1 player's name
	 * @return OK if successful
	 */
	public String handleConnection(String player);

	/**
	 * Creates room
	 * 
	 * @requires name != null && password != null && capacity != null
	 * @param name     name of room game
	 * @param password password of the game
	 * @param capacity capacity of the game
	 * @return OK if successful
	 */
	public String createRoom(String name, String password, int capacity);

	/**
	 * List all the current rooms in the server
	 * 
	 * @ensures result != null
	 * @return list of current rooms in String format
	 */
	public String listRoom();

	/**
	 * Set player status ready
	 * 
	 * @param p player
	 * @requires p!= null
	 * @return OK is successful and error message if not
	 */
	public String setReady(Player p);

	/**
	 * Handles player's move
	 * 
	 * 
	 * @param player player
	 * @param msg    combination of the move including positions of marbles and
	 *               direction
	 * 
	 * @return OK if successful and error otherwise
	 * @requires player!=null, msg!=null
	 */
	public String makeMove(Player player, String msg);

	/**
	 * Gives player other player's data
	 * 
	 * @param player
	 * @requires player != null
	 * @return player data
	 */
	public String getPlayerData(Player player);

	/**
	 * Remove player from room
	 * 
	 * @param p player
	 * @requires p != null
	 * @return OK if successful and error if not
	 */
	public String quitRoom(Player p);

	/**
	 * Broadcast the message to all players in the rooms
	 * 
	 * @param msg message to be sent
	 * @param g   game which message is broadcasted
	 * @requires msg != null && g != null @
	 */
	public void broadCast(String msg, Game g);

	/**
	 * Removes player from server
	 * 
	 * @param p player
	 * @requires p != null
	 * @ensures ! player.contains(p)
	 */
	public void reMovePlayer(Player p);

	/**
	 * Handles client's attempt to join game
	 * 
	 * @param p        player
	 * @param roomName name of the room
	 * @param password password of the room
	 * @requires p!= null && roomName != null && password != null
	 * @ensures p.getGame().getName() == roomName
	 * @return ok is successfully joined and error otherwise
	 */
	public String join(Player p, String roomName, String password);

}
