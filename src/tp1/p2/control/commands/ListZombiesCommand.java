package tp1.p2.control.commands;

import tp1.p2.control.Command;
import tp1.p2.control.ExecutionResult;
import tp1.p2.logic.GameWorld;
import tp1.p2.logic.gameobjects.Zombie;
import tp1.p2.logic.gameobjects.ZombieFactory;
import tp1.p2.view.Messages;

public class ListZombiesCommand extends Command {

	@Override
	protected String getName() {
		return Messages.COMMAND_LIST_ZOMBIES_NAME;
	}

	@Override
	protected String getShortcut() {
		return Messages.COMMAND_LIST_ZOMBIES_SHORTCUT;
	}

	@Override
	public String getDetails() {
		return Messages.COMMAND_LIST_ZOMBIES_DETAILS;
	}

	@Override
	public String getHelp() {
		return Messages.COMMAND_LIST_ZOMBIES_HELP;
	}

	@Override
	public ExecutionResult execute(GameWorld game) {
		// TODO add your code here
	}

}
