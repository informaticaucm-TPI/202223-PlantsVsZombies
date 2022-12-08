package tp1.p2.control.commands;

import tp1.p2.control.Command;
import tp1.p2.control.exceptions.GameException;
import tp1.p2.logic.GameWorld;
import tp1.p2.view.Messages;

public class ExitCommand extends Command {

	@Override
	protected String getName() {
		return Messages.COMMAND_EXIT_NAME;
	}

	@Override
	protected String getShortcut() {
		return Messages.COMMAND_EXIT_SHORTCUT;
	}

	@Override
	public String getDetails() {
		return Messages.COMMAND_EXIT_DETAILS;
	}

	@Override
	public String getHelp() {
		return Messages.COMMAND_EXIT_HELP;
	}

	@Override
	public boolean execute(GameWorld game) throws GameException {
		// TODO add your code here
	}

}
