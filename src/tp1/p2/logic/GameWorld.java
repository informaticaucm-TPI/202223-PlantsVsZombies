package tp1.p2.logic;

import tp1.p2.control.Level;
import tp1.p2.control.exceptions.GameException;
import tp1.p2.logic.gameobjects.GameObject;

public interface GameWorld {

	public static final int NUM_ROWS = 4;

	public static final int NUM_COLS = 8;

	// TODO add your code here

	void addSun();

	void tryToCatchObject(int col, int row) throws GameException;

	boolean addItem(GameObject gameObject);

	void update() throws GameException;

	void reset() throws GameException;

	void reset(Level level, long seed) throws GameException;

	void tryToBuy(int cost) throws GameException;

	void checkValidPlantPosition(int col, int row) throws GameException;

	void checkValidZombiePosition(int col, int row) throws GameException;



}
