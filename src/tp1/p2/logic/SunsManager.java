package tp1.p2.logic;

import java.util.Random;

import tp1.p2.logic.gameobjects.Sun;

public class SunsManager {

	private static final int COOLDOWN_RANDOM_SUN = 5;

	private GameWorld game;

	private Random rand;

	private int cooldown;

	public SunsManager(GameWorld game, Random rand) {
		this.game = game;
		this.rand = rand;
		this.cooldown = COOLDOWN_RANDOM_SUN;
		// TODO add your code here
	}

	public int getCatchedSuns() {
		// TODO add your code here
	}

	public int getGeneratedSuns() {
		// TODO add your code here
	}

	public void update() {
		if (cooldown == 0) {
			addSun();
			cooldown = COOLDOWN_RANDOM_SUN;
		} else {
			cooldown--;
		}
	}

	private int getRandomInt(int bound) {
		return this.rand.nextInt(bound);
	}

	public void addSun() {
		int col = getRandomInt(GameWorld.NUM_COLS);
		int row = getRandomInt(GameWorld.NUM_ROWS);
		game.addItem(new Sun(this.game, col, row));
	}
}
