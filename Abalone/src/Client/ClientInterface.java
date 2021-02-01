package Client;

import java.io.IOException;

import Exception.ExitProgram;
import Machenism.Board;

public interface ClientInterface {
	/**
	 * creates connection with server
	 * 
	 * @throws IOException throw when user disconnect
	 */
	public void handleConnection() throws IOException;

	/**
	 * creates a new board
	 * 
	 * @requires nrPlayer &lt; 5 && nrPlayer &gt -1;
	 * @param nrPlayer number of players
	 */

	public void newBoard(int nrPlayer);

	/**
	 * @return the game board used by the client
	 * 
	 */

	public Board getBoard();

	/**
	 * String representation of the board
	 * 
	 * @return the board to send to the view
	 */

	public String printBoard();

	/**
	 * Send message to the server
	 * 
	 * @param msg the messages to be sent
	 */

	public void sendMessege(String msg);

	/**
	 * Give the name of the player
	 * 
	 * @return player's name
	 * @ensures result != null
	 */
	public String Name();

	/**
	 * Allows player to make move
	 * 
	 * @requires move != null
	 * @param move combination of the move including positions and direction
	 */
	public void makeMove(String move);

	/**
	 * handle disconnect request from players
	 * 
	 * @throws ExitProgram when user wants to disconnect
	 */

	public void handleDis() throws ExitProgram;

	/**
	 * handle room list request from players
	 */

	public void handleList();

	/**
	 * handle quit room request from players
	 */

	public void handleQuit();

	/**
	 * handle chat request from players
	 * 
	 * @param msg messages to sent by player
	 */
	public void handleSay(String msg);

	/**
	 * handle create room request from players
	 * 
	 * @param msg the combination of room, password, capacity to be sent
	 */
	public void handlCreate(String msg);

	/**
	 * handle ready request from players
	 */
	public void handleReady();

	/**
	 * handle get players info request
	 */
	public void handlePlayers();

	/**
	 * handle the join ready from players
	 * 
	 * @param msg the combination of room name and password
	 */

	public void handleJoin(String msg);

	/**
	 * handle the move request from players
	 * 
	 * @param move the combination of move by player
	 */

	public void handleMove(String move);

}
