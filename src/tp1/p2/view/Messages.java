package tp1.p2.view;

import tp1.p2.control.Level;

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

	public static final String LINE_SEPARATOR = System.lineSeparator();

	public static final String HELP_AVAILABLE_COMMANDS = "Available commands:";

	public static final String HELP_DETAILS_COMMAND_HELP_SEPARATOR = ": ";

	public static final String UNKNOWN_COMMAND = "Unknown command";

	public static final String COMMAND_PARAMETERS_MISSING = "Missing parameters";

	public static final String COMMAND_INCORRECT_PARAMETER_NUMBER = "Incorrect parameter number";

	public static final String INVALID_POSITION = "Invalid position";

	public static final String INVALID_GAME_OBJECT = String.format("Invalid object%n");

	public static final String INVALID_COMMAND = "Invalid command";

	public static final String NOT_ENOUGH_COINS = "Not enough suncoins";

	public static final String NUMBER_OF_CYCLES = "Number of cycles:";

	public static final String NUMBER_OF_COINS = "Sun coins:";

	public static final String REMAINING_ZOMBIES = "Remaining zombies:";

	public static final String GAME_OVER = "Game over";

	public static final String PLAYER_QUITS = "Player leaves the game";

	public static final String ZOMBIES_WIN = "Zombies win!";

	public static final String PLAYER_WINS = "Player wins!";

	public static final String AVAILABLE_PLANTS = "Available plants:";

	public static final String UNEXPECTED_RUNTIME_ERROR = "Oops!, ಠ_ಠ";

	//
	// Game Objects
	//

	public static final String GAME_OBJECT_STATUS = "%2s[%02d]";

	public static final String PLANT_DESCRIPTION = "%s: cost='%d' suncoins, damage='%d', endurance='%d'";

	public static final String ZOMBIE_DESCRIPTION = "%s: speed='%d', damage='%d', endurance='%d'";

	public static final String ZOMBIE_SYMBOL = "Z";

	public static final String ZOMBIE_NAME = "Zombie";

	public static final String PEASHOOTER_NAME_SHORTCUT = "[P]eashooter";

	public static final String PEASHOOTER_SYMBOL = "P";

	public static final String PEASHOOTER_NAME = "Peashooter";

	public static final String SUNFLOWER_NAME_SHORTCUT = "[S]unflower";

	public static final String SUNFLOWER_SYMBOL = "S";

	public static final String SUNFLOWER_NAME = "Sunflower";

	//
	// User actions
	//

	public static final String COMMAND_ADD_NAME = "add";

	public static final String COMMAND_ADD_SHORTCUT = "a";

	public static final String COMMAND_ADD_DETAILS = "[a]dd <plant> <col> <row>";

	public static final String COMMAND_ADD_HELP = "add a plant in position (col, row)";

	public static final String COMMAND_LIST_NAME = "list";

	public static final String COMMAND_LIST_SHORTCUT = "l";

	public static final String COMMAND_LIST_DETAILS = "[l]ist";

	public static final String COMMAND_LIST_HELP = "print the list of available plants";

	public static final String COMMAND_RESET_NAME = "reset";

	public static final String COMMAND_RESET_SHORTCUT = "r";

	public static final String COMMAND_RESET_DETAILS = "[r]eset [<level> <seed>]";

	public static final String COMMAND_RESET_HELP = "start a new game (if level and seed are both provided, they are used to initialize the game)";

	public static final String COMMAND_HELP_NAME = "help";

	public static final String COMMAND_HELP_SHORTCUT = "h";

	public static final String COMMAND_HELP_DETAILS = "[h]elp";

	public static final String COMMAND_HELP_HELP = "print this help message";

	public static final String COMMAND_EXIT_NAME = "exit";

	public static final String COMMAND_EXIT_SHORTCUT = "e";

	public static final String COMMAND_EXIT_DETAILS = "[e]xit";

	public static final String COMMAND_EXIT_HELP = "terminate the program";

	public static final String COMMAND_NONE_NAME = "none";

	public static final String COMMAND_NONE_SHORTCUT = "n";

	public static final String COMMAND_NONE_DETAILS = "[n]one | \"\"";

	public static final String COMMAND_NONE_HELP = "skip user action for this cycle";

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

	public static final String status(String icon, int lives) {
		return Messages.GAME_OBJECT_STATUS.formatted(icon, lives);
	}

	public static final String plantDescription(String plantNameAndShortcut, int cost, int damage, int endurance) {
		return Messages.PLANT_DESCRIPTION.formatted(plantNameAndShortcut, cost, damage, endurance);
	}

	public static final String zombieDescription(String zombieNameAndShortcut, int speed, int damage, int endurance) {
		return Messages.ZOMBIE_DESCRIPTION.formatted(zombieNameAndShortcut, speed, damage, endurance);
	}

}
