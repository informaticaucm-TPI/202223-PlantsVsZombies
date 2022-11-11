package tp1.p2.logic;

/**
 * Represents a game item and its allowed game actions.
 *
 */
public interface GameItem {

	/**
	 * Receive a zombie attack.
	 * 
	 * @param damage Received damage.
	 * 
	 * @return <code>true</code> if a plant has been attacked, <code>false</code>
	 *         otherwise.
	 */
	boolean receiveZombieAttack(int damage);

	/**
	 * Receive a plant attack.
	 * 
	 * @param damage Received damage.
	 * 
	 * @return <code>true</code> if a plant has been attacked, <code>false</code>
	 *         otherwise.
	 */
	boolean receivePlantAttack(int damage);

	
	/**
	 * Try to catch a sun (if no other sun has been catched this cycle).
	 * 
	 * @return <code>true</code> if the sun has been catched, <code>false</code> otherwise.
	 */
	boolean catchObject();

}
