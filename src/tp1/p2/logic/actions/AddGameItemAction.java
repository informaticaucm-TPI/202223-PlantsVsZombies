package tp1.p2.logic.actions;

import tp1.p2.logic.GameWorld;
import tp1.p2.logic.gameobjects.GameObject;

public class AddGameItemAction implements GameAction {

	private GameObject gameObject;

	public AddGameItemAction(GameObject gameObject) {
		this.gameObject = gameObject;
	}

	@Override
	public void execute(GameWorld game) {
		game.addItem(gameObject);
	}
}
