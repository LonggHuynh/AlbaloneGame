package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import Machenism.Player;

public class PlayerHandler extends Thread {
	/**
	 * Destination of out out
	 */
	BufferedReader in;
	private PrintWriter out;
	private Socket sock;
	/**
	 *  
	 */
	private Server srv;
	/**
	 * the player that this handles
	 */

	private Player player;

	public PlayerHandler(Socket sock, Server srv) {
		try {
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()), true);
			this.sock = sock;
			this.srv = srv;

		} catch (IOException e) {
			shutdown();
		}

	}

	/**
	 * Get the destination of output
	 * 
	 * @return writter of this
	 * @ensures \result!=null
	 */
	public PrintWriter getWriter() {
		return out;
	}

	/**
	 * Set player for this handler and add to server's player list
	 * 
	 * @param player player connecting the server
	 * 
	 */
	public void setPlayer(Player player) {
		this.player = player;
		player.setPlayerHandler(this);
		srv.addPlayer(player);
	}

	/**
	 * shut down the handler
	 */

	private void shutdown() {
		srv.getView().showMessage(">Shutting down:" + "[" + player.getName() + "]");
		try {
			in.close();
			out.close();
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (player != null) {
			srv.reMovePlayer(player);
		}

	}

	public void run() {
		String msg;
		try {
			msg = in.readLine();
			while (msg != null) {
				if (player != null) {
					srv.getView().showMessage(">Incoming: " + "[" + player.getName() + "]" + msg);
				} else {
					srv.getView().showMessage(">Incoming: " + msg);
				}

				handleCmd(msg);

				msg = in.readLine();
			}
			shutdown();
		} catch (IOException e) {
			shutdown();
		}
	}

	/**
	 * handle messages sent from the clent
	 * 
	 * @param msg message sent from the client
	 */
	private void handleCmd(String msg) throws IOException {
		try {
			String[] words = msg.split(":");
			String command = words[0];
			if (player == null && !command.equals(ProtocolMessages.CONNECT)) {
				String s = ProtocolMessages.E_NOT_CONNECTED;
				out.println(s);
			}

			else if (command.equals(ProtocolMessages.CONNECT)) {
				if (player != null) {
					String s = ProtocolMessages.E_ALREADY_CONNECTED;
					out.println(s);
				} else {

					String s = srv.handleConnection(words[1]);
					if (s.equals(ProtocolMessages.OK)) {
						Player p = new Player(words[1]);
						setPlayer(p);
					}

					out.println(s);
				}

			} else if (command.equals(ProtocolMessages.MOVE)) {
				String s = srv.makeMove(player, msg);
				out.println(s);
				srv.getView().showMessage(">Outgoing: [" + player.getName() + "]" + s);

			} else if (command.equals(ProtocolMessages.READY)) {
				String s = srv.setReady(player);
				out.println(s);
				srv.start(player);
				srv.getView().showMessage(">Outgoing: [" + player.getName() + "]" + s);

			} else if (command.equals(ProtocolMessages.LIST)) {
				String s = srv.listRoom();
				out.println(s);
				srv.getView().showMessage(">Outgoing: [" + player.getName() + "]" + s);

			} else if (command.equals(ProtocolMessages.CREATE)) {
				int cap = Integer.parseInt(words[3]);
				String s = srv.createRoom(words[1], words[2], cap);
				out.println(s);
				srv.getView().showMessage(">Outgoing: [" + player.getName() + "]" + s);
			} else if (command.equals(ProtocolMessages.JOIN)) {
				String s = srv.join(player, words[1], words[2]);
				out.println(s);
				srv.getView().showMessage(">Outgoing: [" + player.getName() + "]" + s);

			} else if (command.equals(ProtocolMessages.GETPLAYERS)) {
				String s = srv.getPlayerData(player);
				out.println(s);
				srv.getView().showMessage(">Outgoing: [" + player.getName() + "]" + s);

			} else if (command.equals(ProtocolMessages.QUIT)) {
				String s = srv.quitRoom(player);
				out.println(s);
				srv.getView().showMessage(">Outgoing: [" + player.getName() + "]" + s);

			} else if (command.equals(ProtocolMessages.ASK_BOARD)) {
				String s = srv.giveBoard(player);
				out.println(s);
				srv.getView().showMessage(">Outgoing: [" + player.getName() + "]" + s);
			} else if (command.equals(ProtocolMessages.SAY)) {

				if (words.length > 2) {
					String s = ProtocolMessages.E_UNKNOWN_FORMAT;
					out.println(s);
					srv.getView().showMessage(s);
				} else {
					String s = srv.handleChat(words[1], player.getGame(), player);
					out.println(s);
					srv.getView().showMessage(">Outgoing: [" + player.getName() + "]" + s);

				}

			} else if (command.equals(ProtocolMessages.DISCONNECT)) {
				shutdown();

			} else {
				String s = ProtocolMessages.E_UNKNOWN_FORMAT;
				out.println(s);
				srv.getView().showMessage(">Outgoing: [" + player.getName() + "]" + s);

			}
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
			String s = ProtocolMessages.E_UNKNOWN_FORMAT;
			out.println(s);
			srv.getView().showMessage(">Outgoing: [" + player.getName() + "]" + s);
		}

	}
}
