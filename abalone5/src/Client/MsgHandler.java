package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import Exception.ExitProgram;
import Machenism.AI;
import Machenism.Board;
import Machenism.Mark;
import Machenism.Move;
import Server.ProtocolMessages;

/**
 * 
 * This class handles all incoming messages during the communication
 * 
 *
 */
public class MsgHandler extends Thread {
	private BufferedReader in;
	private PrintWriter out;
	private Client client;

	private AI Mega = new AI(2);

	/**
	 * Construct the messages handler with given reader, writter and client
	 * 
	 * @param in  the destination of the incomming stream
	 * @param out the destination of the outgoing stream
	 * @client the client using the this handler
	 * 
	 */

	public MsgHandler(BufferedReader in, PrintWriter out, Client client) {
		this.client = client;
		this.in = in;
		this.out = out;

	}

	public void run() {
		while (true) {
			client.getView().showMessage("Enter Command");
			try {
				String inCommingMsg = in.readLine();
				handleMsg(inCommingMsg);

			} catch (IOException e) {
				client.getView().showMessage("Connection error...Closing program");
				try {
					client.handleDis();
				} catch (ExitProgram e1) {
					// TODO Auto-generated catch block
					client.getView().showMessage(e1.getMessage());
				}
				break;
			}
		}
	}

	/**
	 * Handle all messages from server
	 * 
	 * @param msg message recieved from server
	 */
	public void handleMsg(String msg) throws IOException {
		if (msg.contains(ProtocolMessages.TURN) && msg.contains(":" + client.Name() + ":")) {
			String[] words = msg.split(":");
			Mark color = client.convertToMark(words[words.length - 1]);
			client.setMark(color);
			while (client.getBoard() == null) {
				String k = in.readLine();
				handleMsg(k);

			}
			client.getView().showMessage(msg);
			handleAI();

		} else if (msg.contains(ProtocolMessages.GOVER)) {

			int nr = client.getBoard().getNrPLayer();
			client.newBoard(nr);
			client.getView().showMessage(msg);
			client.getView().showMessage(client.getView().coloredBoard(client.getBoard()));
		} else if (msg.contains(ProtocolMessages.JOINED)) {
			handleJoin();
		} else if (msg.contains(ProtocolMessages.UPDATE)) {
			handleMove(msg);
		} else if (msg.contains(ProtocolMessages.FAIL)) {
			handleError(msg);
		} else if (msg.contains(ProtocolMessages.ROOMS)) {
			printRoomList(msg);
		}

		else {
			client.getView().showMessage(msg);
		}

	}

	/**
	 * Activate AI
	 */
	public void handleAI() {
		if (client.getSupportLevel() == 0) {
			return;
		}
		String s = client.getBoard().toString();
		Board b = client.getBoard();
		Mega = client.getAI();
		Mega.bestMove(b, client.getMark());
		Move move = Mega.getBestMove();
		String msg = "move:" + move.getPos1() + ":" + move.getPos2() + ":" + move.getPos3() + ":" + move.getDirection();
		msg = msg.replaceAll(":0:", "::");
		msg = msg.replaceAll(":0:", "::");
		if (client.getSupportLevel() == 1) {
			client.getView().showMessage(msg);
		}
		if (client.getSupportLevel() == 2) {
			out.println(msg);
		}

	}

	/**
	 * Handle all moves messages from server
	 * 
	 * @requires move!= null
	 * @param move move from sent from server
	 */
	public void handleMove(String move) {
		client.getView().showMessage(move);
		client.makeMove(move);
		String s = client.getBoard().toString();
		client.getView().showMessage(client.printBoard());
	}

	/**
	 * Format room list in nicer presentation
	 * 
	 * @param rooms room list in form of the String sent from server
	 */
	public void printRoomList(String rooms) {
		String[] words = rooms.split(":");
		client.getView().showMessage("Room:");
		for (int i = 1; i < words.length; i++) {
			String line = words[i].replaceAll(",", " ");
			line = line.replace("true", "PasswordRequired");
			line = line.replace("false", "NoPassword");
			client.getView().showMessage(line);
		}
	}

	/**
	 * When recieving the messages "joined in" from server, this method will setup
	 * the game for client
	 * 
	 * 
	 */
	public void handleJoin() {
		try {
			client.sendMessege(ProtocolMessages.LIST);
			String m = in.readLine();
			while (!m.contains(ProtocolMessages.ROOMS)) {
				// To make sure the msg is player data other handle the message first
				handleMsg(m);
				m = in.readLine();
			}
			String[] words = m.split(":");
			int nr = 0;
			for (String word : words) {
				if (word.contains(client.getRoomName() + ",")) {
					String[] d = word.split(",");
					nr = Integer.parseInt(d[2]);
					client.newBoard(nr);
				}
			}
			client.newBoard(nr);
			client.getView().showMessage(client.printBoard());

		} catch (IOException e) {
			client.getView().showMessage(e.getMessage());
		}
	}

	/**
	 * replace error messages by more friendly messages
	 * 
	 * @param msg error messages from server
	 */

	public void handleError(String msg) {
		String e = msg.replace(ProtocolMessages.FAIL + ":", "");
		client.getView().showMessage(e + " !Try again");
	}

}
