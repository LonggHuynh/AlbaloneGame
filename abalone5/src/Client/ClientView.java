package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;

import Exception.ExitProgram;
import Machenism.AI;
import Machenism.Board;
import Server.ProtocolMessages;

/**
 * Class used for asking input
 */
public class ClientView extends Thread {
	/**
	 * colors used for the board
	 */

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	/**
	 * the client using this view
	 */
	private Client client;

	/**
	 * Writer for the view
	 */
	private PrintWriter console = new PrintWriter(System.out, true);

	/**
	 * intialize the server
	 * 
	 * @throws IOException
	 * @throws ExitProgram thrown when user exits the program
	 */
	public void intialize() throws IOException, ExitProgram {
		String host;
		int port;
		Socket socket;
		while (true) {
			try {
				host = getString("Enter Host");
				port = getInt("Enter port");
				socket = new Socket(host, port);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
				int supportLevel = getInt(
						"Are you new to this game? Do you need some help? \n0.No help\n1.Show hints\n2.AI player");
				AI bot;
				if (supportLevel != 0) {
					int AI_Inteligence = getInt("Enter AI level 1 or 2");
					bot = new AI(AI_Inteligence);
					showMessage("ok");
				} else {
					bot = new AI(2);
					showMessage("ok");
				}

				Client client = new Client(this, in, out, bot);
				client.setSupportLevel(supportLevel);
				this.client = client;
				client.handleConnection();
				MsgHandler handler = new MsgHandler(in, out, client);
				handler.start();
				break;

			} catch (UnknownHostException e) {
				if (!getBoolean("Host not found. Try again ?")) {
					throw new ExitProgram("Closing client");
				}

			} catch (ConnectException e) {
				if (!getBoolean("Access denied. Try again ?")) {
					throw new ExitProgram("Closing client");
				}

			} catch (InputMismatchException e) {
				if (!getBoolean("Format Error. Try again ?")) {
					throw new ExitProgram("Closing client");
				}
			} catch (IllegalArgumentException e) {
				if (!getBoolean("Port number error. Try again ?")) {
					throw new ExitProgram("Closing client");
				}
			}
		}

	}

	public void run() {
		try {
			intialize();
			Scanner scanner = new Scanner(System.in);
			String outGoingMsg;
			printMenu();
			do {

				outGoingMsg = scanner.next();
				handleInput(outGoingMsg);

			} while (!outGoingMsg.equals("d"));

		} catch (ExitProgram e) {
			console.println(e.getMessage());
		} catch (SocketException e) {
			console.println("Connection error! Closing...");
		} catch (IOException e) {

		}

	}

	/**
	 * send messages to the console
	 * 
	 * @param msg message to be sent
	 */

	public void showMessage(String msg) {
		console.println(msg);
	}

	/**
	 * Present the board in color
	 * 
	 * @param b board to be represented
	 * @return board with color
	 */
	public String coloredBoard(Board b) {
		String board = b.toString();
		board = board.replace("B", ANSI_BLUE + "O" + ANSI_RESET);
		board = board.replace("R", ANSI_RED + "O" + ANSI_RESET);
		board = board.replace("Y", ANSI_YELLOW + "O" + ANSI_RESET);
		board = board.replace("W", ANSI_WHITE + "O" + ANSI_RESET);
		board = board.replace("E", "-");
		return board;
	}

	/**
	 * Print menu
	 */
	public void printMenu() {
		showMessage("Welcome to the Abalone game");
		showMessage("");
		showMessage("Commands :");
		showMessage("create......................................create a room");
		showMessage("list........................................list all rooms");
		showMessage("ready........................................ready");
		showMessage("join....................................... join the room ");
		showMessage("getplayers................................. get players info in the room");
		showMessage("quit....................................... quit the room");
		showMessage("say........................................ chat to players in the room");
		showMessage("d.......................................... disconnect");
		showMessage("help........................................print menu");

	}

	/**
	 * Handle input of the user
	 * 
	 * @param command the input of the user
	 * @throws ExitProgram
	 */
	public void handleInput(String command) throws ExitProgram {
		Scanner scan = new Scanner(System.in);
		if (command.equals(ProtocolMessages.MOVE)) {
			String mar1 = getString("Select marble 1");
			String mar2 = getString("Select marble 2");
			String mar3 = getString("Select marble 3");
			String dir = getString("Select direction");
			client.handleMove("move:" + mar1 + ":" + mar2 + ":" + mar3 + ":" + dir);
		} else if (command.equals(ProtocolMessages.READY)) {
			client.handleReady();
		} else if (command.equals(ProtocolMessages.LIST)) {
			client.handleList();
		} else if (command.equals(ProtocolMessages.CREATE)) {
			String roomname = getString("Room name?");
			String password = getString("Password?");
			String capacity = getString("Capacity?");
			client.handlCreate(roomname + ":" + password + ":" + capacity);
		} else if (command.equals(ProtocolMessages.JOIN)) {
			client.sendMessege(ProtocolMessages.LIST);
			try {
				Thread.sleep(200);
				// Make sure the room of list shown first
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String roomname = getString("Enter room name");
			client.setRoomName(roomname);
			String password = getString("Enter password");
			client.handleJoin(roomname + ":" + password);
		} else if (command.equals(ProtocolMessages.GETPLAYERS)) {
			client.handlePlayerdata();
		} else if (command.equals(ProtocolMessages.QUIT)) {
			client.handleQuit();
		} else if (command.equals(ProtocolMessages.SAY)) {
			String msg = getString("Enter Messages");
			client.handleSay(msg);
		} else if (command.equals(ProtocolMessages.DISCONNECT)) {
			if (!getBoolean("Do you want to continue?")) {
				client.handleDis();
			}
		} else if (command.equals("help")) {
			printMenu();
		} else {
			showMessage("Unknown command");
		}

	}

	/**
	 * Ask the user to input answer for the given question
	 * 
	 * @param question question asked user
	 * @return user input
	 */
	public String getString(String question) {
		showMessage(question);
		Scanner scan = new Scanner(System.in);
		String i = scan.nextLine();
		return i;

	}

	/**
	 * Ask the user to input answer for the given question
	 * 
	 * @param question question asked user
	 * @return true if the answer is "y", otherwise false
	 */

	public boolean getBoolean(String question) {
		showMessage(question + "y/n?");
		Scanner scan = new Scanner(System.in);
		String i = scan.next();
		if (i.equals("y")) {
			return true;
		}
		return false;
	}

	/**
	 * Ask the user to input answer for the given question
	 * 
	 * @requires input is numeric
	 * @param question question asked user
	 * @return user input
	 */
	public int getInt(String question) {
		showMessage(question);
		Scanner scan = new Scanner(System.in);
		int i = scan.nextInt();
		return i;
	}

	// -----------------------main------------------------//
	public static void main(String[] args) {
		ClientView t = new ClientView();
		t.start();
	}

}
