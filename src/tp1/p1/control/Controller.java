package tp1.p1.control;

import java.util.Scanner;

import static tp1.p1.view.Messages.debug;
import tp1.p1.logic.Game;
import tp1.p1.view.GamePrinter;
import tp1.p1.view.Messages;

/**
 * Accepts user input and coordinates the game execution logic.
 *
 */
public class Controller {//maneja el funcionamieto del juego

	private Game game;

	private Scanner scanner;

	private GamePrinter gamePrinter;

	public Controller(Game game, Scanner scanner) {//Se crea en PlantsVsZombies.java un controlador, un game y un scaner y se ejecuta run.
		this.game = game;
		this.scanner = scanner;
		this.gamePrinter = new GamePrinter(game);
	}

	/**
	 * Draw / Paint the game.
	 */
	private void printGame() {
		System.out.println(gamePrinter);
	}

	/**
	 * Prints the final message once the match is finished.
	 */
	public void printEndMessage() {
		System.out.println(gamePrinter.endMessage());
	}

	/**
	 * Show prompt and request command.
	 *
	 * @return the player command as words
	 */
	private String[] prompt() {
		System.out.print(Messages.PROMPT);
		String line = scanner.nextLine();
		String[] words = line.toLowerCase().trim().split("\\s+");

		System.out.println(debug(line)); //está en el messages.java?? 

		return words;
	}
	//...se muestra el prompt y le pide al usuario la siguiente acción
	//pide al jugador que hacer
	private boolean UserAction() {
		boolean continua = true;
		String[] command = prompt();
		if(command[0].equals("add")||command[0].equals("a")) {//Hay una función para esto?
			//que devuelve exactamente command??
		}
		else if(command[0].equals("reset")||command[0].equals("r")){
			
		}
		else if(command[0].equals("list")||command[0].equals("l")) {
			//es necesario adaptar a las condiciones
		}
		else if(command[0].equals("none")||command[0].equals("n")) {
			
		}
		else if(command[0].equals("exit")||command[0].equals("e")) {
			continua = false;
		}
		else if(command[0].equals("help")||command[0].equals("h")) {//deve volver a preguntar el prompt y colver a ejecutar esto, while???
			//lo que sea
			continua = UserAction();
		}
		return continua;
	}
	
	

	/**
	 * Runs the game logic.
	 */
	public void run() {
		// TODO fill your code
		boolean condicion = true;
		while(condicion) {//en cada ciclo de juego:
			//TODO 1.Draw: Se pinta el tablero, se muestra la info del juego,... 
			this.printGame();
			//TODO 2.User Action: Hacer o no hacer una acción, pedir ayudao mostrar info del juego
			condicion = UserAction();
			
			//TODO 3. Game Action: Ordenador puede añadir un zombie, cando y done aleatorio
			
			//TODO 4. Update: Actualización de objetos en el tablero
			
			
		}
		
		
		
		
		
	}

}
