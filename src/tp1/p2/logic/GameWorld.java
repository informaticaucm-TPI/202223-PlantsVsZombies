package tp1.p2.logic;

import tp1.p2.logic.gameobjects.GameObject;

public interface GameWorld {

	public static final int NUM_ROWS = 4;

	public static final int NUM_COLS = 8;

	// TODO add your code here

	void addSun();

	boolean tryToCatchObject(int col, int row);

	boolean addItem(GameObject gameObject);

}
