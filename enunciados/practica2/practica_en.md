!-- TOC start -->
- [Assignment 2 (Part I): Plants versus zombis refactored](#práctica-2-parte-i-plantas-contra-zombis-refactored)
  * [Introduction](#introducción)
  * [Refactorisation of the solution to the previous assignment](#refactorización-de-la-solución-de-la-práctica-anterior)
    + [Restructuring the code for parsing and executing the commands](#patrón-command)
    + [Extending the functionality of the reset command](#comando-reset)
    + [Using interfaces to define different perspectives on the Game class](#la-clase-game-y-sus-diferentes-usos)
    + [Restructuring the code for handling the elements of the game](#herencia-y-polimorfismo)
    + [The `GameObjectContainer` class](#gameobjectcontainer)
    + [The factory pattern](#patrón-factory)
      - [Implementation](#implementación)
    + [GameItem y callbacks](#gameitem-y-callbacks)
  * [Tests](#pruebas)
<!-- TOC end -->
<!-- TOC --><a name="práctica-2-parte-i-plantas-contra-zombis-refactored"></a>
# Assignment 2 (Part I): Plants versus zombis refactored

**Submission: 7th of November at 09:00hrs**  (submission of part I is optional, in order to receive feedback)
 
**Objective:** inheritance, polymorphism, abstract classes and interfaces

<!-- TOC --><a name="introducción"></a>
## Introduction

In this assignment we apply the mechanisms offered by OOP to improve and extend the code developed
in the previous assignment in the following ways:

- In *Part I* of Assignment 2, we refactor [^1] the code of [Assignment 1](../practica1/practica1_en.md)
in order to prepare it for the extensions to be made in *Part II*. Note that
since this part of the assignment consists only of refactoring, the resulting implementation should
pass the same tests as the implementation of the previous assignment. The
refactoring consists of creating the following two inheritance hierarchies.

    * The first inheritance hierarchy concerns the treatment of the commands introduced by the user
at the keyboard and will be constructed by removing some code from the controller `run`
method of the previous assignment and distributing its functionality among a set of classes.

    * The second inheritance hierarchy will be used to organise the game objects which represent
the different creatures appearing in the game, thereby the repetition of code that occurred
in the previous assignment. This inheritance hierarchy will also enable us to use a
single data structure to store the state of the game, instead of using a different list for each type
of game object.

- In *Part II* of Assignment 2, we extend the game by adding new commands and new game objects. Thanks
to the structure introduced in the refactoring of part I, the extensions of part II can be carried out
relatively easily, by modifying very little of the existing code, and the resulting code will be robust
and reliable.

[^1]: Refactoring means changing the structure of the code (to improve it, presumably) without changing
its functionality (i.e. without changing what it does).

<!-- TOC --><a name="refactorización-de-la-solución-de-la-práctica-anterior"></a>
## Refactoring the solution of the previous assignment

<!-- TOC --><a name="patrón-command"></a>
### Restructuring the code for parsing and executing the commands

The first refactoring task concerns the commands, i.e. the different actions that the user of the 
game can carry out, such as adding a plant, listing the available plants, asking for help, etc. Our
refactoring objective is to structure the code in such a way as to facilitate the addition of new
commands (or the deletion of old ones). As one would expect, this is a very well-known problem
in OOP which has solutions that are very well tried and tested, so we do not need to invent our own.
The solution we will use is a variant of the *Command design pattern*, one of the twenty-three
software patterns presented in the foundational software patterns book "Design Patterns: Elements of
Reusable Object-Oriented Software" first published in 1994. The general idea of the command
pattern is to encapsulate each command in its own class.

Our presentation of the Command pattern involves the following classes:

- The `Command` class: an abstract class that encapsulates the functionality common to all the 
  commands.

- Specific command classes, in this assignment `AddPlantCommand`, `HelpCommand`, `ExitCommand` etc.,
  that are concrete subclasses of the abtract `Command` class.

 Each concrete command subclass has (at least) the following methods:

    * a method, or methods, for parsing the words of the input string. In the code provided, the parsing is
      divided into two stages, implemented by the following two methods:

      `matchCommand(String)`: parses the first word of the input string, checking whether it corresponds
      to the name of the command in question, returning the value `null` if it does not and the value returned
      by the `create` method if it does.

      `create(String[])`: parses the remaining words of the input string (contained in the array of strings
      passed via its only parameter), if there are any, checking whether they correspond to valid command
      arguments [^2]. If they do not, it prints an error message and returns `null` and if they do, it
      creates and returns an object of the same command subclass, which stores the values of the parsed
      command arguments in attributes.

   * `execute(GameWorld)`: executes the action of the command, in most cases modifying the state of the game
     The explanation of why the type `GameWorld` is used instead of the type `Game` is given below.

- The `Controller` class: the controller class contains much less code then in the previous assignment since
  a large part of its functionality is now delegated to the specific command classes, as explained below.

[^2]: Strictly speaking, the parsing phase should only check properties of the input data that do not involve
any semantics so, for example, the property of coordinates of being on or off the board should not be checked
in the parsing phase.

In the previous assignment, the parsing (i.e. finding out which command is to be executed and, when appropriate,
with which parameter values) was carried out directly via a switch (or `if-else` ladder) contained in (or called
from) the **Game loop** of the `run` method of the controller, with one case for each different command.

In this assignment, the drastically slimmed-down code of the controller `run` method will look something like
the following (your code does not have to be identical but should have the same structure):

```java
while (!game.isFinished() && !game.isPlayerQuits()) {

    // 1. Draw
    if (refreshDisplay) {
        printGame();
    }

    // 2. User action
    String[] words = prompt();

    if (words.length == 0) {
        System.out.println(error(Messages.UNKNOWN_COMMAND));
    } else {
        Command command = Command.parse(words);
        if (command != null) {
            // 3-4. Game Action & Update
            refreshDisplay = game.execute(command);
        } else {
            refreshDisplay = false;
        }
    }
}
```

In the loop, while the game has not finished, the program reads a command from the console, parses it
to obtain the corresponding command object and then calls the execute method of this command object.
If the execution is successful and the state of the game has changed, it prints the board and the
game-state information, while if the command is invalid, it prints an error message.

The most important part of this loop is the following line of code:
```java
Command command = Command.parse(words);
```

The key point is that the controller only handles abstract commands so it doesn't know which concrete 
command is being created and executed nor exactly what this concrete command does. This is the
dynamic-binding mechanism that allows us to easily add new specific commands.

The **`parse(String[])`** method is a static method of the `Command` class that is responsible
for finding which
specific command corresponds to the user's input. It does so by calling the `matchCommand(String)` on an
object of each specific command class in turn (it loops through the `AVAILABLE_COMMANDS` list, which
contains exactly one instance of each of the concrete command subclasses) until one of them returns a
non-null value in the form of a concrete command object. If all of them return `null`, meaning
that the input text does not correspond to any of the available commands, it prints
the *unknown command* message.

The skeleton of the code is as follows:
```java
public abstract class Command {

    private static final List<Command> AVAILABLE_COMMANDS = Arrays.asList(
        new AddPlantCommand(),
        new ListPlantsCommand(),
        new ResetCommand(),
        new HelpCommand(),
        // ...
    );


    public static Command parse(String[] commandWords) {
        //... 
    }

    //...
}
```

After receiving a `Command` object from the `parse` method, the controller simply asks the
game to execute the corresponding action.

All concrete commands have a series of details: `name`, `shortcut`, `detail`, etc. For example the
specific command  `HelpCommand` has the following code:

```java
public static final name, shortcut, details;
public class HelpCommand extends Command {

	protected String getName() {
		return Messages.COMMAND_HELP_NAME;
	}

	protected String getShortcut() {
		return Messages.COMMAND_HELP_SHORTCUT;
	}

	public String getDetails() {
		return Messages.COMMAND_HELP_DETAILS;
	}

    // ...
}
```

As already stated, the specific command classes inherit from the `Command` class and must
implement the abstract `execute` method. This method executes the action associated to
the corresponding command by simply calling a method of the game (via the type `GameWorld`
as explained below). The `execute` method returns a value of type
`ExecutionResult` that indicates whether the command succeeded or not, the error message if
necessary, and whether or not to print the board and the game-state information.
`ExecutionResult` is a [Java Record](https://www.geeksforgeeks.org/what-are-java-records-and-how-to-use-them-alongside-constructors-and-methods/),
a class whose purpose is to encapsulate multiple values in order to return multiple values
from a Java method (similar to returning a tuple or a C/C++ struct).
Specific command classes that represent commands with parameters should overwrite the
implementation of the **`create(String[])`** method contained in the `Command` class since this
implementation is only valid for classes that represent commands with no parameters.

<!-- TOC --><a name="comando-reset"></a>
### Extending the functionality of the reset command

We now modify the behaviour of the reset command of the previous assignment
so that the user can choose to perform a reset with, or without, changing the level and the seed. Enabling
the level and the seed to be changed without restarting the game will facilitate testing.
To that end, the parsing of the reset command should take into account that it can be called
either without arguments or with two arguments where, in the latter case, the arguments have
the same type and the same order as the ones used when starting the game. In the latter case
also the seed of the `Random` object must be reset (or a new `Random` object created).

<!-- TOC --><a name="la-clase-game-y-sus-diferentes-usos"></a>
### Using interfaces to define different perspectives on the Game class

The `Game` class is used by functionally different parts of the application: e.g. the `Controller`
uses methods of the `Game` class for one purpose while the `GamePrinter` uses other methods of the
`Game` class for a different purpose. However, currently, all of the methods of the `Game` are
visible and available to all parts of the application. One way of limiting this *coupling* while
at the same
time specifying more clearly the dependencies between different parts of the application, without
breaking the `Game` class into smaller classes, is to use the Java `interface` construct[^3]. The
idea is to divide the `Game` functionality into separate packets, each containing the methods used
by a different part of the application, and then declare each such packet in a different interface.
Here, we define two such interfaces:

- `GameStatus`: includes all the method used by the `GamePrinter` to find out about the state of
the game

```java
public interface GameStatus {
    int getCycle();
    // ...
}
```

- `GameWorld`: includes all the methods used by the commands and the game objects to modify
the state of the game.

```java
public interface GameWorld {

    public static final int NUM_ROWS = 4;

    public static final int NUM_COLS = 8;

    void playerQuits();

    ExecutionResult update();

    // ...
}
```

If the `Game` class implements both of these interfaces then, if required, the type of objects of
class `Game` can be declared to be either one of these interfaces.

```java
public class Game implements GameStatus, GameWorld {

    public static final int INITIAL_SUNCOINS = 50;

    private boolean playerQuits;

    //...
}
```
[^3]: Another way of ensuring that any state-changing methods are only visible to close collaborators
of `Game` (e.g. `GameObject` and `Command`) would be to use *inner classes* which will be presented
in TPII.

<!-- TOC --><a name="herencia-y-polimorfismo"></a>
### Restructuring the code for handling the elements of the game

In the same way as the code structure known as the *Command pattern* enables new commands to be introduced with
minimal changes to the existing code (and, in particular, without changing the code of the controller),
we would also like to be able to introduce new game objects with only minimal changes to the existing code.
Just as the key to obtaining this desired property in the case of the command pattern was that the
controller does not know which concrete command is being handled, the key to this in the case at hand
is that the game does not know which specific element of the game is being handled. To that end, we
define an abstract class called `GameObject` from which the concrete classes, each representing a different
element of the game, then derive. Each concrete class inherits attributes and methods from the
`GameObject` class and implements its own behaviour by
 - providing implementations for the abstract methods that it inherits,
 - possibly overwriting some of the non-abstract methods that it inherits,
 - possibly adding new methods.
To specify the attributes and methods of the `GameObject` class, we need to think about the behaviour that
is common to all the elements of the game.

All elements of the game have a position on the board so the `GameObject` will contain the corresponding
attributes (or attribute, if you decide to define a `Position` class: if you do, make sure that it is
immutable). A way of decomposing the behaviour of the elements of the game into methods that has proved
useful is according to their life-cycle as follows:

- `onEnter()`: invoked when this element enters the game
- `update()`: invoked to evolve this element on each cycle of the game
- `onExit()`: invoked when this element leaves or is removed from the game
- `isAlive()`: indicates whether or not an object has any lives left, returning true if it does and false if it does not.

Note that in many simple objects these methods will be trivial and some will even be empty.

We now provide the skeleton of the code for the `GameObject` class. The `GameItem` interface implemented
by this class is described below

```java
public abstract class GameObject implements GameItem {

	protected GameWorld game;

	protected int col;

	protected int row;

	GameObject(GameWorld game, int col, int row) {
		this.game = game;
		this.col = col;
		this.row = row;
	}

	public boolean isInPosition(int col, int row) {
		return this.col == col && this.row == row;
	}

	public int getCol() {
		return col;
	}

	public int getRow() {
		return row;
	}

	abstract protected String getSymbol();

	abstract public String getDescription();

	abstract public boolean isAlive();

	abstract public void onEnter();
	
	abstract public void onExit();

    // ...
}
```

<!-- TOC --><a name="gameobjectcontainer"></a>
###  `GameObjectContainer`

En nuestra práctica queremos que el `Game` sea lo más simple posible y, aunque es la clase principal de nuestro programa, su labor (responsabilidad) es coordinar al resto de las clases, y lo hace *delegando*.

La delegación consiste en lo siguiente: cuando están correctamente programados, los métodos de `Game` son muy pequeños y lo que hacen es llamar a los métodos de otros objetos (colaborar) que son los que realmente hacen el trabajo. Uno de los objetos en los que delega es `GameObjectContainer`.

El `GameObjectContainer`  es el almacén de objetos del juego (para acortar escribiremos contenedor). Es el encargado de actualizarlos, borrarlos, etc. Para el almacén podemos utilizar cualquier tipo de colección. Nosotros por simplicidad vamos a usar un `ArrayList` de `GameObject`s cuya declaración es así:

```java

public class GameObjectContainer {

	private List<GameObject> gameObjects;

	public GameObjectContainer() {
		gameObjects = new ArrayList<>();
	}
    //...
```

Es muy importante que los detalles de la implementación del `GameObjectContainer` sean privados. Eso permite cambiar el tipo de colección sin tener que modificar código en el resto de la práctica. 

En relación a la primera práctica, hay varios aspectos que van a cambiar en la estructura de esta práctica:

- Sólo tenemos un contenedor para todos los objetos concretos de juego.

- Desde el `Game` y el *contenedor* sólo manejamos abstracciones de los objetos, por lo que no podemos distinguir de qué clase son los objetos que están dentro del contenedor, una vez añadidos.

- Toda la lógica del juego estará en los objetos de juego. Cada clase concreta conoce sus detalles acerca de cómo se actualiza, qué pasa cuando ataca o es atacada, etc. En nuestro caso, también tenemos `ZombieManager` que seguirá teniendo la lógica de gestión de los zombies, pero los zombies estarán dentro del *contenedor*. 

- Para asegurarnos de que el `Game` está bien programado, no podrá tener ninguna referencia a `GameObject`s concretos, solo podrá tener referencias al *contenedor*.

<!-- TOC --><a name="patrón-factory"></a>
### Patrón Factory

El patrón *Factory* es otro de los patrones más utilizados. Al igual que con el patrón *Command*, no vamos a estudiar este patrón de manera rigurosa sino que vamos a adaptarlo a nuestras necesidades concretas.

Una *Factoria* es *responsable de crear objetos evitando exponer la lógica de creación al cliente*. En la primera versión de la práctica, la lógica de creación de plantas está fuertemente acoplada con el controlador de la aplicación. La  forma de incorporar una nueva planta es la de incluir un nuevo bloque al switch o if's que tenemos en el método run. Seguramente tu código se parece a este:

```java
  case "sunflower":
  case "s":
    result = game.addSunflower(col, row);
    break;
  
  case "peashooter":
  case "p":
    result = game.addPeashooter(col, row);
    break;
```

En nuestra nueva versión queremos que se parezca a esto:

```java
Plant plant = PlantFactory.spawnPlant(this.plantName, game, col, row);
game.addPlant(plant);
```

Haciendo uso del patrón *Factory*, podemos extraer la lógica de creación a una clase dedicada exclusivamente a ello. De esta manera añadir o eliminar una planta de la lista es tan sencillo como crear la clase correspondiente y modificar la *Factory*. Así, los cambios en la lista ya no afectarán al controlador o al juego. Con esta propuesta, la lógica de creación está desacoplada de la lógica del juego y puede evolucionar de forma independiente.

<!-- TOC --><a name="implementación"></a>
#### Implementación

En patrón *Factory* se combina muy bien con el patrón *Command*, ya que:
- Cuando ejecutamos un comando `add plant col row` podemos delegar la creación de la planta a la factoría.
- Cuando ejecutamos un comando `list` para saber cuáles son las plantas disponibles, podemos preguntar a la factoría qué  `AVAILABLE_PLANTS` tiene. El siguiente código muestra el esqueleto de la factoría:

```java
public class PlantFactory {
  
    private static final List<Plant> AVAILABLE_PLANTS = Arrays.asList(
        // ...
	);  


	public static Plant spawnPlant(String plantName, GameWorld game, int col, int row) {
        // ...
    }

	public static List<Plant> getAvailablePlants() {
		return Collections.unmodifiableList(AVAILABLE_PLANTS);
	}
    // ...
}
```
El método `getAvailablePlants()` lo usará el comando `ListPlantsCommand` para mostrar la información de las plantas disponibles.

En la primera versión de la práctica sólo teníamos dos tipos de plantas y un tipo de zombi, pero nuestro objetivo es poder extenderla de manera sencilla incorporando nuevos objetos de juego con diferentes características.

Aunque para la *parte I* no es necesario, también puedes crear una `ZombieFactory` para utilizarla en `ZombieManager` y en un posible `AddZombieCommand`. Este comando te puede ser de utilidad para depurar, ya que además del comportamiento aleatorio de la Práctica 1, también podrás colocar los zombis a tu antojo.


<!-- TOC --><a name="gameitem-y-callbacks"></a>
### GameItem y callbacks

Ya hemos resuelto la abstracción de los objetos, el almacenamiento y la creación. Ahora nos queda una cuestión muy importante y quizás la más compleja. Para ello debes entender bien el problema. 

Al usar la clase abstracta `GameObject`, una vez que un objeto se mete en el juego ya no sabemos qué clase de objeto es. Podríamos saberlo usando `instanceof` o `getClass()`, pero eso está **terminantemente prohibido** en la práctica.

El problema es el siguiente: cuando un `Zombie` o un `Peashooter` quiere atacar algo no sabemos si en una casilla adyacente hay una *planta* o `Zombie`.

Para resolver este problema vamos a hacer lo siguiente. En primer lugar vamos a usar un interfaz `GameItem` para encapsular los métodos relacionados con las interacciones / acciones dentro del juego. La clase `GameObject` implementará dicho interfaz. El objetivo es que todos los objetos del juego deben tener la posibilidad de interactuar entre ellos.

```java
public interface GameItem {
	boolean receiveZombieAttack(int damage);

    void kill();
    // ...
}
```

Las colisiones se podrían comprobar desde `Game`, desde los `GameObject`. Como `Zombie` es el único elemento que se mueve, su método `update()` se podría implementar de la siguiente manera:

```java
public void update() {
    //...
    GameObject other = game.getGameObjectInPosition(col, row);
    if(other != null && other.getClass() == "Sunflower") {  
        ((Sunflower) other).setAlive(false);
    }
    //...
}
```

Aunque te pueda parecer que el código es correcto (de hecho funciona), es un **ejemplo de mala aplicación de la programación orientada a objetos**.

Este ejemplo de código, por un lado, rompe la abstracción y encapsulación (ha sido necesario crear un mutador `setAlive()`) y por otro, hace que el código sea poco mantenible porque tendremos que modificar el `Zombie` para cada nuevo tipo de planta.

Otra opción que estaría igual de mal consiste en implementar métodos que simulen el comportamiento de `Object.getClass()` o del operador `instanceof` **para todos los objetos del juego**, por ejemplo `isSunflower()`:

```java
public void update() {
    //...
    GameObject other = game.getGameObjectInPosition(col, row);
    if(other != null && other.isSunflower()) {  
        ((Sunflower) other).setAlive(false);
    }
    //...
}
```

Ambos ejemplos muestran uno de los errores habituales de la programación orientada a objetos:

1. Reidentificar el tipo del objeto que estamos procesando y 
2. utilizar una instrucción condicional para aplicar un comportamiento u otro; 

Además, la clase `Zombie` está acumulando demasiadas responsabilidades que no debería tener.

Lo que queremos es que *la funcionalidad esté en los propios objetos de juego*, para que sea fácil extenderla y modificarla sin afectar a otros objetos. Para ello vamos a usar el interfaz que veíamos arriba, de la siguiente manera:


```java
public void update() {
    //...
    GameObject other = game.getGameObjectInPosition(col, row);
    if(other != null ) {  
        other.receiveZombieAttack(this.damage);
    }
    //...
}
```

Todos los objetos implementan `receiveZombieAttack(int damage)`, y es precisamente en ese método donde debemos implementar la lógica que gestiona un ataque zombie. Por ejemplo, el `receiveZombieAttack` de `Sunflower` le aplicaremos el daño que le inflije el zombie, pero en la clase `Zombie` no hacemos nada. 

Esta solución es un comienzo ya que cada objeto gestiona las diferentes acciones del juego. El problema es que rompemos la encapsulación al devolver un objeto `GameObject` de `Game`. Aunque la clase `GameObject` implemente la interfaz `GameItem`, es recomendable, siempre que se pueda, interactuar utilizando los métodos definidos en la interfaz y no a través de una clase que implemente dicha interfaz. Cabe recordar que en Java sólo tenemos disponible herencia simple, **pero es posible implementar diferentes interfaces** que definen diferentes contratos dentro de la aplicación.

Para solucionar el problema debemos hacer que dos objetos sólo se comuniquen a través del interfaz, que es una abstracción o contrato entre ellos. Para ello vamos a usar la siguiente estructura:


```java
public void update() {
    //...
    GameItem item = game.getGameItemInPosition(col, row);
    if(item != null ) {  
        item.receiveZombieAttack(this.damage);
    }
    //...
}
```

El código es muy similar al anterior, pero usamos el *interface* como tipo de datos. Así ya **no rompemos la encapsulación**, ya que sólo se conectan con abstracciones.

Por el momento, el interfaz `GameItem` es muy sencillo pero en las extensiones tendremos que añadir nuevos métodos para implementar interacciones más complejas.


<!-- TOC --><a name="pruebas"></a>
## Pruebas

Recuerda que una vez terminada la refactorización, la práctica debe funcionar exactamente igual que en la versión anterior y debe pasar los mismos tests, aunque tendremos muchas más clases. 

Así, conseguimos dejar preparada la estructura para añadir fácilmente nuevos comandos y objetos de juego en la *Parte II*.

Para simplificar las pruebas, vamos a "abusar" del soporte de [JUnit](https://junit.org/) dentro de Eclipse para facilitar nuestras pruebas de comparación de la salida de nuestro programa. JUnit es un marco de trabajo para poder realizar pruebas automatizadas al código de tu aplicación Java. Seguramente verás como utilizar JUnit en otras asignaturas de la carrera.

Como parte de la plantilla de la práctica se incluye la clase `tp1.p2.PlantsVsZombiesTests` que es una clase de pruebas JUnit. Esta clase contiene una prueba para cada uno de los casos de prueba de la Práctica 1.

Antes de poder ejecutar las pruebas que incluye, tenemos que añadir JUnit a nuestro proyecto. Para ello, tenemos que ir a las propiedades del proyecto *Project > Properties*, seleccionamos *Java Build Path* y vamos a la pestaña *Libraries*. Allí pulsamos en el botón **Add Library...**

![](./imgs/00-ProjectProjerties.jpg)

En la nueva ventana seleccionamos *JUnit* y pulsamos en el botón *Finish* 

![](./imgs/01-AddJUnit.jpg)

Al volver a la ventana de las propiedades del proyecto, pulsamos en el botón *Aply and Close*.

Si lo hemos configurado correctamente, al pulsar con el botón derecho del ratón sobre el fichero `PlatsVsZombiesTests.java` e ir al menú *Run As*, debería de aparecer la opción **JUnit Test**.

![](./imgs/02-RunAsJUnitTest.jpg)

Si ejecutamos las pruebas se mostrará una vista en Eclipse donde podremos ver el resultado de las pruebas, lanzar las que hayan fallado de manera individualizada o todas a la vez. **Recuerda** que utilizamos las pruebas JUnit simplemente para comparar la salida de tu programa con la salida esperada. Si quieres ver el detalle tendrás que aplicar el mismo procedimiento que en la Práctica 1.

![](./imgs/03-JUnitFailed.jpg)

![](./imgs/04-JUnitPass.jpg)

