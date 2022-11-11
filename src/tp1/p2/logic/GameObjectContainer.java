package tp1.p2.logic;

import java.util.ArrayList;
import java.util.List;

import tp1.p2.logic.gameobjects.GameObject;
import tp1.p2.view.Messages;

public class GameObjectContainer {

	private List<GameObject> gameObjects;

	public GameObjectContainer() {
		gameObjects = new ArrayList<>();
	}

	public String positionToString(int col, int row) {
		StringBuilder buffer = new StringBuilder();
		boolean sunPainted = false;
		boolean sunAboutToPaint = false;

		for (GameObject g : gameObjects) {
			if(g.isAlive() && g.getCol() == col && g.getRow() == row) {
				String objectText = g.toString();
				sunAboutToPaint = objectText.indexOf(Messages.SUN_SYMBOL) >= 0;
				if (sunAboutToPaint) {
					if (!sunPainted) {
						buffer.append(objectText);
						sunPainted = true;
					}
				} else {
					buffer.append(objectText);
				}
			}
		}

		return buffer.toString();
	}

	public boolean removeDead() {
		// TODO add your code here
	}

	// TODO add your code here

}
