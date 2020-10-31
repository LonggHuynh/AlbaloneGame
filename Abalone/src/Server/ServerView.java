package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ServerView extends Thread {
	/**
	 * server using this view
	 */
	private Server srv;

	/**
	 * server socker
	 */
	ServerSocket sock;

	/**
	 * destination of the output
	 */
	private PrintWriter console = new PrintWriter(System.out, true);

	/**
	 * setup for server
	 */
	public void intialize() {
		boolean again = true;
		while (again) {
			try {
				showMessage("IP address is: " + InetAddress.getLocalHost().getHostAddress());
				srv = new Server(this);
				int port = getInt("Enter Port");
				sock = new ServerSocket(port);
				showMessage("Listening.........");
				again = false;

			} catch (UnknownHostException e) {

				showMessage("Server exception " + e.getMessage());
				again = getBoolean("Do you want to try again ?");

			} catch (IOException e) {
				showMessage("Connection error");
				again = getBoolean("Do you want to try again ?");
			} catch (InputMismatchException e) {
				showMessage("Format error");
				again = getBoolean("Do you want to try again ?");
			} catch (IllegalArgumentException e) {
				showMessage("Out of limited port number");
				again = getBoolean("Do you want to try again ?");
			}
		}

	}

	public void run() {
		intialize();
		while (true) {
			Socket socket;
			try {
				socket = sock.accept();
				PlayerHandler p = new PlayerHandler(socket, srv);
				srv.handlerList().add(p);
				p.start();

			} catch (Exception e) {
				showMessage("Server exception " + e.getMessage());
				if (!getBoolean("Do you want to try again ?")) {
					break;
				}
				intialize();
			}

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

	public static void main(String[] args) {
		ServerView ser = new ServerView();
		ser.start();
	}
}
