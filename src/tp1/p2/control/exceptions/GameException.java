package tp1.p2.control.exceptions;

public class GameException extends Exception {

	private static final long serialVersionUID = 1L;

	public GameException(String message) {
		super(message);
	}

	public GameException(Throwable cause) {
		super(cause);
	}

	public GameException(String message, Throwable cause) {
		super(message, cause);
	}

}
