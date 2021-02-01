package Server;

import Machenism.Game;
import Machenism.Player;

import java.util.ArrayList;
import java.util.List;

public class Server extends Thread implements Srv {

	/**
	 * hander list of the server
	 * 
	 * @requires handlerList!=null
	 */
	private final List<PlayerHandler> handlerList;

	/**
	 * list of players currently using the server
	 * 
	 * @requires players!=null
	 */
	private final List<Player> players;

	/**
	 * list of games created by players
	 * 
	 * @requires games!=null
	 */
	private final List<Game> games;

	/**
	 * Server view
	 */
	private ServerView view;

	/**
	 * @requires view!=null
	 * Construct the server
	 */

	public Server(ServerView view) {
		players = new ArrayList<>();
		handlerList = new ArrayList<>();
		this.view = view;
		games = new ArrayList<>();
	}

	/**
	 * Get the view of the server
	 * @ensures \result!=null
	 * @return the view of the server
	 */

	public ServerView getView() {
		return view;
	}

	/**
	 * add players when they are connected
	 * @ensures players.size()=\old.players.size()+1
	 * @param p player
	 * 
	 */
	public synchronized void addPlayer(Player p) {
		players.add(p);
	}

	/**
	 * @ensures \result!=null
	 * @return the list of handlers
	 */
	public List<PlayerHandler> handlerList() {
		return handlerList;
	}

	@Override
	public synchronized String handleConnection(String name) {
		if (name.contains(",")) {
			return ProtocolMessages.FAIL + ":" + ProtocolMessages.E_INVALID_USERNAME;
		}
		for (Player p : players) {
			if (p.getName().equals(name)) {
				return ProtocolMessages.FAIL + ":" + ProtocolMessages.E_DUPLICATE_USERNAME;
			}
		}

		return ProtocolMessages.OK;
	}

	@Override
	public synchronized String createRoom(String name, String password, int capacity) {
		for (Game g : games) {
			if (name.equals(g.getName())) {
				return ProtocolMessages.FAIL + ":" + ProtocolMessages.E_DUPLICATE_ROOM_NAME;
			}
		}
		if (capacity < 2 || capacity > 4) {
			return ProtocolMessages.FAIL + ":" + ProtocolMessages.E_INVALID_CAPACITY;
		}
		if (name.contains(",")) {
			return ProtocolMessages.FAIL + ":" + ProtocolMessages.E_INVALID_ROOM_NAME;
		}
		if (password.contains(",")) {
			return ProtocolMessages.FAIL + ":" + ProtocolMessages.E_INVALID_PASSWORD;
		}
		Game g = new Game(capacity, name, password);
		games.add(g);
		return ProtocolMessages.OK;
	}

	/**
	 * Set start for the game player is in if all players are ready
	 * 
	 * @param p player
	 */
	public void start(Player p) {
		Game g = p.getGame();
		if (g == null) {
			return;
		}
		for (Player pl : g.getPlayers()) {
			if (!pl.readyCheck() || !g.isFull()) {
				return;
			}
		}
		g.setStart();
		Player PlayerCurrent = p.getGame().currentPlayer();
		broadCast(ProtocolMessages.START, g);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		broadCast(ProtocolMessages.TURN + ":" + PlayerCurrent.getName() + ":" + PlayerCurrent.getMark(), g);

	}

	@Override
	public synchronized String listRoom() {
		StringBuilder res = new StringBuilder(ProtocolMessages.ROOMS);
		for (Game g : games) {
			res.append(":").append(g.toString());
		}
		return res.toString();
	}

	@Override
	public String setReady(Player p) {
		if (p.getGame() == null) {
			return ProtocolMessages.FAIL + ":" + ProtocolMessages.E_NOT_IN_ROOM;
		}

		return p.setReady();
	}

	@Override
	public synchronized String makeMove(Player player, String msg) {
		Game g = player.getGame();
		if (g == null || !g.isStart()) {
			return ProtocolMessages.FAIL + ":" + ProtocolMessages.E_NOT_IN_GAME;
		}

		String gameMove = player.getGame().handleMove(msg, player);
		if (g.hasWinner() || g.turnOver()) {
			handleGameOver(g);
			return ProtocolMessages.OK;
		}

		if (gameMove.contains("move") && !gameMove.contains("illegal")) {
			gameMove = gameMove.replaceAll("move", ProtocolMessages.UPDATE);

			Player PlayerCurrent = player.getGame().currentPlayer();

			broadCast(gameMove + ":" + player.getMark(), g);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (PlayerCurrent != null) {
				broadCast(ProtocolMessages.TURN + ":" + PlayerCurrent.getName() + ":" + PlayerCurrent.getMark(), g);
			}

			return ProtocolMessages.OK;
		}
		return gameMove;

	}

	@Override
	public String getPlayerData(Player player) {
		Game g = player.getGame();
		if (g == null) {
			return ProtocolMessages.FAIL + ":" + ProtocolMessages.E_NOT_IN_ROOM;
		}
		String res = ProtocolMessages.PLAYERDATA;

		for (Player p : g.getPlayers()) {
			res += ":" + p.toString();
		}

		return res;
	}

	@Override
	public synchronized String quitRoom(Player p) {
		if (p.getGame() == null) {
			return ProtocolMessages.FAIL + ":" + ProtocolMessages.E_NOT_IN_ROOM;
		}
		p.getGame().reMovePlayer(p);
		return ProtocolMessages.OK;
	}

	/**
	 * send message to specific player
	 * 
	 * @param msg messages
	 * @param p   player to recieve
	 * @requires p!=null
	 */
	public void sendMsg(String msg, Player p) {
		p.getPlayerHandler().getWriter().println(msg);
	}

	public void broadCast(String msg, Game g) {

		for (Player p : g.getPlayers()) {
			sendMsg(msg, p);
		}

	}

	@Override
	public synchronized void reMovePlayer(Player p) {
		Game g = p.getGame();
		if (g != null) {
			if (g.isStart()) {
				broadCast(ProtocolMessages.GOVER + ":", g);
				g.reset();
			}
			g.reMovePlayer(p);

		}
		players.remove(p);
		handlerList.remove(p.getPlayerHandler());

	}

	@Override
	public synchronized String join(Player p, String roomName, String password) {
		for (Game g : games) {
			if (roomName.equals(g.getName())) {
				if (g.isFull()) {
					return ProtocolMessages.FAIL + ":" + ProtocolMessages.E_ROOM_FULL;
				} else if (!g.checkPass(password)) {
					return ProtocolMessages.FAIL + ":" + ProtocolMessages.E_INCORRECT_PASSWORD;
				} else if (g.isStart()) {
					return ProtocolMessages.FAIL + ":" + ProtocolMessages.E_GAME_IN_PROGRESS;
				} else {
					g.addPlayer(p);
					return ProtocolMessages.JOINED;
				}

			}

		}
		return ProtocolMessages.FAIL + ":" + ProtocolMessages.E_ROOM_NOT_EXISTING;
	}

	public String handleChat(String msg, Game g, Player p) {
		String name = p.getName();
		if (g == null) {
			return ProtocolMessages.FAIL + ":" + ProtocolMessages.E_NOT_IN_ROOM;
		}
		broadCast(ProtocolMessages.SAYALL + ":" + name + ":" + msg, g);
		return ProtocolMessages.OK;

	}

	/**
	 * send the board to player that requests
	 * 
	 * @param p player
	 * @requires p!=null
	 * @return String presentation of the current board
	 */
	public String giveBoard(Player p) {
		if (p.getGame() == null || !p.getGame().isStart()) {
			return ProtocolMessages.FAIL + ":" + ProtocolMessages.E_NOT_IN_ROOM;
		}
		return ProtocolMessages.GIVE_BOARD + ":" + p.getGame().getBoard().boardProtocolFormat();
	}

	/**
	 * check and handle the game is over
	 * 
	 * @param g game to be handled
	 * 
	 */
	public void handleGameOver(Game g) {
		for (Player p : g.getPlayers()) {
			p.setUnready();
		}
		if (g.hasWinner()) {
			Player winner = g.getWinner();
			Player coWinner = g.ally(winner);
			if (coWinner != null) {
				broadCast(
						ProtocolMessages.GOVER + ":" + g.getWinner().getName() + ":" + g.ally(g.getWinner()).getName(),
						g);
			} else {
				broadCast(ProtocolMessages.GOVER + ":" + g.getWinner().getName(), g);
			}
		}

		else if (g.turnOver()) {
			Player winner = g.getHighestScorePlayer();
			Player coWinner = g.ally(winner);
			if (g.draw()) {
				broadCast(ProtocolMessages.GOVER + ":", g);
			} else {
				if (coWinner != null) {
					broadCast(ProtocolMessages.GOVER + ":" + winner.getName() + ":" + g.ally(winner).getName(), g);
				} else {
					broadCast(ProtocolMessages.GOVER + ":" + winner.getName(), g);

				}
			}

		}
		g.reset();
	}

}
