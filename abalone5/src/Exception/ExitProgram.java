package Exception;

/**
 * Exception thrown when client request an exit
 * 
 */
public class ExitProgram extends Exception {

	private static final long serialVersionUID = 9073041248662660300L;

	public ExitProgram(String msg) {
		super(msg);
	}

}
