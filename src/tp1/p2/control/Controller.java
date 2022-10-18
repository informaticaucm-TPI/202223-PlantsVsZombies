package tp1.p2.control;

import static tp1.p2.view.Messages.debug;
import static tp1.p2.view.Messages.error;

import java.util.Scanner;

import tp1.p2.logic.Game;
import tp1.p2.view.GamePrinter;
import tp1.p2.view.Messages;

/**
 * Accepts user input and coordinates the game execution logic.
 */
public class Controller {

	private Game game;

	private Scanner scanner;

	private GamePrinter gamePrinter;

	public Controller(Game game, Scanner scanner) {
		this.game = game;
		this.scanner = scanner;
		this.gamePrinter = new GamePrinter(game);
	}

	/**
	 * Draw / Paint the game.
	 */
	private void printGame() {
		System.out.println(gamePrinter);
	}

	/**
	 * Prints the final message once the match is finished.
	 */
	public void printEndMessage() {
		System.out.println(gamePrinter.endMessage());
	}

	/**
	 * Show prompt and request command.
	 *
	 * @return the player command as words
	 */
	private String[] prompt() {
		System.out.print(Messages.PROMPT);
		String line = scanner.nextLine();
		String[] words = line.toLowerCase().trim().split("\\s+");

		System.out.println(debug(line));

		return words;
	}

	/**
	 * Runs the game logic.
	 */
	public void run() {
		boolean refreshDisplay = true;

		while (!game.isFinished() && !game.isPlayerQuits()) {

			// 1. Draw
			if (refreshDisplay) {
				printGame();
			}

			// 2. User action
			String[] words = prompt();

			if (words.length == 0) {
				System.out.println(error(Messages.UNKNOWN_COMMAND));
			} else {
				Command command = Command.parse(words);
				if (command != null) {
					// 3-4. Game Action & Update
					refreshDisplay = game.execute(command);
				} else {
					refreshDisplay = false;
				}
			}
		}

		if (refreshDisplay) {
			printGame();
		}

		printEndMessage();
	}

}
