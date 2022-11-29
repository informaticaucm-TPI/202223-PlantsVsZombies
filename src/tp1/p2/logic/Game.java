package tp1.p2.logic;

import static tp1.p2.view.Messages.error;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Random;

import tp1.p2.control.Command;
import tp1.p2.control.ExecutionResult;
import tp1.p2.control.Level;
import tp1.p2.logic.actions.GameAction;
import tp1.p2.logic.gameobjects.GameObject;
import tp1.p2.view.Messages;

public class Game implements GameStatus, GameWorld {

	private long seed;

	private Level level;
	
	private int cycle;

	private GameObjectContainer container;

	private Deque<GameAction> actions;

	// TODO add your attributes here

	public Game(long seed, Level level) {
		this.seed = seed;
		this.level = level;
		this.container = new GameObjectContainer();
		reset();
	}

	/**
	 * Resets the game.
	 */
	@Override
	public void reset() {
		reset(this.level, this.seed);
	}

	/**
	 * Resets the game with the provided level and seed.
	 * 
	 * @param level {@link Level} Used to initialize the game.
	 * @param seed Random seed Used to initialize the game.
	 */
	@Override
	public void reset(Level level, long seed) {
		// TODO add your code here
		this.cycle = 0;
		this.actions = new ArrayDeque<>();
	}


	/**
	 * Executes the game actions and update the game objects in the board.
	 * 
	 */
	@Override
	public void update() {

		// 1. Execute pending actions
		executePendingActions();

		// 2. Execute game Actions
		// TODO add your code here

		// 3. Game object updates
		// TODO add your code here

		// 4. & 5. Remove dead and execute pending actions
		boolean deadRemoved = true;
		while (deadRemoved || areTherePendingActions()) {
			// 4. Remove dead
			deadRemoved = this.container.removeDead();

			// 5. execute pending actions
			executePendingActions();
		}

		this.cycle++;

		// 6. Notify commands that a new cycle started
		Command.newCycle();

	}

	private void executePendingActions() {
		while (!this.actions.isEmpty()) {
			GameAction action = this.actions.removeLast();
			action.execute(this);
		}
	}

	private boolean areTherePendingActions() {
		return this.actions.size() > 0;
	}

	/**
	 * Checks if a cell is fully occupied, that is, the position can be shared between an NPC (Plant, Zombie) and Suns .
	 * 
	 * @param col Column of the cell
	 * @param row Row of the cell
	 * 
	 * @return <code>true</code> if the cell is fully occupied, <code>false</code>
	 *         otherwise.
	 */
	@Override
	public boolean isFullyOcuppied(int col, int row) {
		return this.container.isFullyOccupied(col, row);
	}

	// TODO add your code here

}
