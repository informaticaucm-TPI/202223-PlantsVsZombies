package tp1.p2.logic.gameobjects;

import tp1.p2.logic.GameWorld;
import tp1.p2.view.Messages;

public class Sun extends GameObject {

	// Remember that a Sun is updated the very same cycle is added to the container
	public static final int SUN_COOLDOWN = 10+1;

	@Override
	public boolean catchObject() {
		// TODO add your code here
	}

	@Override
	public boolean fillPosition() {
		return false;
	}
}
