package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import Exception.ExitProgram;
import Machenism.AI;
import Machenism.Board;
import Machenism.Mark;
import Server.ProtocolMessages;

public class Client implements ClientInterface {
	private String roomName;
	private Board board;
	private String name;
	private Mark playerMark;
	private BufferedReader in;
	private PrintWriter out;
	private ClientView view;
	private AI ai;

	/**
	 * The level specifies the support of the AI. 1 is no hint. 2 is hint. 3 is AI
	 * player
	 */

	private int supportLevel;

	public Client(ClientView view, BufferedReader in, PrintWriter out, AI bot) {
		this.view = view;
		this.in = in;
		this.out = out;
		ai = bot;

	}

	/**
	 * set support level for the client
	 * 
	 * @param level level of suport from the program
	 * @requires level > 0 & level < 3
	 * @ensures this.supportlevel == level
	 */
	public void setSupportLevel(int level) {
		supportLevel = level;
	}

	/**
	 * @return support level
	 */
	public int getSupportLevel() {
		return supportLevel;
	}

	/**
	 * Get the view that client are using
	 * 
	 * @return the view used for the client
	 */
	public ClientView getView() {
		return view;
	}

	/**
	 * Set the name of the player's room according to the name of the room player
	 * joined
	 */
	public void setRoomName(String name) {
		this.roomName = name;
	}

	/**
	 * @return the current/last room the player in
	 */
	public String getRoomName() {
		return roomName;
	}

	/**
	 * @return the AI used by the client
	 */

	public AI getAI() {
		return ai;
	}

	/**
	 * Send message to the server
	 * 
	 * @param msg the messages to be sent
	 */
	public void sendMsg(String msg) {
		out.println(msg);
	}

	@Override
	public void handleMove(String move) {
		try {
			if (playerMark == null) {
				view.showMessage(ProtocolMessages.E_NOT_IN_GAME);
				return;
			}
			String[] msgs = move.split(":");
			int nrMar = 0;
			int[] balls = new int[3];
			for (int i = 1; i < 4; i++) {
				if (!msgs[i].equals("")) {
					balls[nrMar] = Integer.parseInt(msgs[i]);
					nrMar++;
				}
			}
			Mark mark = playerMark;

			if (nrMar == 2) {
				if (board.validMove(balls[0], balls[1], msgs[4], mark)) {
					sendMsg(move);
					return;
				}
			}

			else if (nrMar == 3) {
				if (board.validMove(balls[0], balls[1], balls[2], msgs[4], mark)) {
					sendMsg(move);
					return;
				}

			} else {
				if (board.validMove(balls[0], msgs[4]) & board.validChoice(balls[0], mark)) {
					sendMsg(move);
					return;
				}
			}
			view.showMessage("illegal_move");
		} catch (NumberFormatException e) {
			view.showMessage("Format Error");
		}

	}

	@Override
	public void handleDis() throws ExitProgram {
		sendMsg(ProtocolMessages.DISCONNECT);
		throw new ExitProgram("Closing.....See you again");

	}

	@Override
	public void handleList() {
		sendMsg(ProtocolMessages.LIST);
	}

	@Override
	public void handleQuit() {
		sendMsg(ProtocolMessages.QUIT);

	}

	@Override
	public void handleSay(String msg) {
		sendMsg(ProtocolMessages.SAY + ":" + msg);
	}

	@Override
	public void handlCreate(String msg) {
		sendMsg(ProtocolMessages.CREATE + ":" + msg);
	}

	@Override
	public void handleReady() {
		sendMsg(ProtocolMessages.READY);
	}

	@Override
	public void handlePlayers() {
		sendMsg(ProtocolMessages.GETPLAYERS);
	}

	@Override
	public void handleJoin(String msg) {

		sendMsg(ProtocolMessages.JOIN + ":" + msg);

	}

	@Override
	public void handleConnection() throws IOException {

		Scanner scan = new Scanner(System.in);
		String rs;
		do {
			System.out.println("Enter Name");
			name = scan.nextLine();
			out.println("c:" + name);

			rs = in.readLine();

			System.out.println(rs);

		} while (!rs.equals(ProtocolMessages.OK));

	}

	@Override
	public void newBoard(int nrPlayer) {
		board = new Board(nrPlayer);
	}

	@Override
	public Board getBoard() {
		return board;
	}

	@Override
	public String printBoard() {
		return view.coloredBoard(board);
	}

	@Override
	public void sendMessege(String msg) {
		out.println(msg);
	}

	@Override
	public String Name() {
		return name;
	}

	/**
	 * Converts the given string to Mark
	 * 
	 * @requires m! = null
	 * @param m mark
	 * @return mark according to m
	 */

	public Mark convertToMark(String m) {
		Mark mark;
		switch (m) {
			case "Y":
				mark = Mark.Y;
				break;
			case "R":
				mark = Mark.R;
				break;
			case "B":
				mark = Mark.B;
				break;
			default:
				mark = Mark.W;
				break;
		}
		return mark;
	}

	@Override
	public void makeMove(String move) {
		String[] msgs = move.split(":");
		int nrMar = 0;
		int[] balls = new int[3];
		for (int i = 1; i < 4; i++) {
			if (!msgs[i].equals("")) {
				balls[nrMar] = Integer.parseInt(msgs[i]);
				nrMar++;
			}
		}
		Mark mark = convertToMark(msgs[5]);

		if (nrMar == 2) {
			board.makeMove(balls[0], balls[1], msgs[4], mark);
		}

		else if (nrMar == 3) {
			board.makeMove(balls[0], balls[1], balls[2], msgs[4], mark);

		} else {
			board.makeMove(balls[0], msgs[4], mark);
		}
	}

	/**
	 * Set mark of the player
	 * 
	 * @param m the mark to be set
	 * 
	 */
	public void setMark(Mark m) {
		playerMark = m;
	}

	/**
	 * get the current mark of player
	 * 
	 * @return the mark of player
	 */
	public Mark getMark() {
		return playerMark;
	}

}
