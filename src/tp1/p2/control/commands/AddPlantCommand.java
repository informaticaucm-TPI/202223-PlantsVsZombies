package tp1.p2.control.commands;

import tp1.p2.control.Command;
import tp1.p2.control.exceptions.CommandExecuteException;
import tp1.p2.control.exceptions.CommandParseException;
import tp1.p2.control.exceptions.GameException;
import tp1.p2.logic.GameWorld;
import tp1.p2.view.Messages;

public class AddPlantCommand extends Command implements Cloneable {

	private int col;

	private int row;

	private String plantName;

	private boolean consumeCoins;

	public AddPlantCommand() {
		this(true);
	}

	public AddPlantCommand(boolean consumeCoins) {
		this.consumeCoins = consumeCoins;
	}

	@Override
	protected String getName() {
		return Messages.COMMAND_ADD_NAME;
	}

	@Override
	protected String getShortcut() {
		return Messages.COMMAND_ADD_SHORTCUT;
	}

	@Override
	public String getDetails() {
		return Messages.COMMAND_ADD_DETAILS;
	}

	@Override
	public String getHelp() {
		return Messages.COMMAND_ADD_HELP;
	}


	@Override
	public boolean execute(GameWorld game) throws GameException {
		// TODO add your code here
	}

	@Override
	public Command create(String[] parameters) throws GameException {
		// TODO add your code here
	}

}


