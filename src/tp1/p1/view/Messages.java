package tp1.p1.view;

import tp1.p1.control.Level;
import tp1.p1.logic.gameobjects.Peashooter;
import tp1.p1.logic.gameobjects.Sunflower;

/**
 * String literals used in the game.
 * 
 */
public class Messages {

	public static final String VERSION = "1.0";

	public static final String GAME_NAME = "PlantsVsZombies";
	
	public static final String USAGE = "Usage: %s <level> [<seed>]".formatted(GAME_NAME);
	
	public static final String USAGE_LEVEL_PARAM = "\t<level>: %s".formatted(Level.all(", "));
	
	public static final String USAGE_SEED_PARAM = "\t<seed>: %s".formatted(Messages.SEED_NOT_A_NUMBER);

	public static final String WELCOME = String.format("%s %s%n", GAME_NAME, VERSION);

	public static final String ALLOWED_LEVELS = "Level must be one of: %s".formatted(Level.all(", "));

	public static final String SEED_NOT_A_NUMBER = "The seed must be a number";

	public static final String SEED_NOT_A_NUMBER_ERROR = String.format("%s: %%s", SEED_NOT_A_NUMBER);

	public static final String CONFIGURED_LEVEL = "Level: %s";

	public static final String CONFIGURED_SEED = "Random generator initialized with seed: %d";

	public static final String PROMPT = "Command > ";

	public static final String DEBUG = "%n[DEBUG] Executing: %s%n";

	public static final String ERROR = "[ERROR] Error: %s%n";
	
	public static final String[] HELP_LINES = new String[] {
			"Available commands:",
			"[a]dd <plant> <col> <row>: add a plant in position (col, row)",
			"[l]ist: print the list of available plants",
			"[r]eset: start a new game",
			"[h]elp: print this help message",
			"[e]xit: terminate the program",
			"[n]one | \"\": skips cycle",
	};
	
	public static final String HELP = String.join(System.lineSeparator(), HELP_LINES);

	public static final String UNKNOWN_COMMAND = "Unknown command";
	
	public static final String COMMAND_PARAMETERS_MISSING = "Missing parameters";

	public static final String INVALID_POSITION = "Invalid position";

	public static final String INVALID_GAME_OBJECT = String.format("Invalid object%n");
	
	public static final String LIST = "%s%n%s%n".formatted(Sunflower.getDescription(), Peashooter.getDescription()); 	

	public static final String INVALID_COMMAND = "Invalid command";
	
	public static final String PEASHOOTER_DESCRIPTION = "[P]eashooter: cost='%d' suncoins, damage='%d', endurance='%d'";

	public static final String PEASHOOTER_ICON = "P [%02d]";
	
	public static final String SUNFLOWER_DESCRIPTION = "[S]unflower: cost='%d' suncoins, damage='%d', endurance='%d'";

	public static final String SUNFLOWER_ICON = " S[%02d]";

	public static final String NOT_ENOUGH_COINS = "Not enough suncoins";

	public static final String NUMBER_OF_CYCLES = "Number of cycles:";
	
	public static final String NUMBER_OF_COINS = "Sun coins:";

	public static final String REMAINING_ZOMBIES = "Remaining zombies:";

	public static final String ZOMBIE_ICON = " Z[%02d]";
	
	public static final String GAME_OVER = "Game over";

	public static final String PLAYER_QUITS = "Player leaves the game";
	
	public static final String ZOMBIES_WIN = "Zombies win!";

	public static final String PLAYER_WINS = "Player wins!";


	/**
	 * Formats an debug message.
	 * 
	 * @param message debug message
	 * 
	 * @return the formated debug message;
	 */
	public static final String debug(String message) {
		return Messages.DEBUG.formatted(message);
	}

	/**
	 * Formats an error message.
	 * 
	 * @param message Error message
	 * 
	 * @return the formated error message;
	 */
	public static final String error(String message) {
		return Messages.ERROR.formatted(message);
	}
}
