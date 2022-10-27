!-- TOC start -->
- [Assignment 2 (Part I): Plants versus zombis refactored](#práctica-2-parte-i-plantas-contra-zombis-refactored)
  * [Introduction](#introducción)
  * [Refactorisation of the solution to the previous assignment](#refactorización-de-la-solución-de-la-práctica-anterior)
    + [The command pattern](#patrón-command)
    + [The reset command](#comando-reset)
    + [The `Game` class and its different uses](#la-clase-game-y-sus-diferentes-usos)
    + [Inheritance and Polymorphism](#herencia-y-polimorfismo)
    + [The `GameObjectContainer` class](#gameobjectcontainer)
    + [The factory pattern](#patrón-factory)
      - [Implementation](#implementación)
    + [GameItem y callbacks](#gameitem-y-callbacks)
  * [Tests](#pruebas)
<!-- TOC end -->
<!-- TOC --><a name="práctica-2-parte-i-plantas-contra-zombis-refactored"></a>
# Assignment 2 (Part I): Plants versus zombis refactored

**Submission: 7th of November at 09:00hrs**
 
**Objective:** inheritance, polymorphism, abstract classes and interfaces

<!-- TOC --><a name="introducción"></a>
## Introducción

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
### The command pattern

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
     The explanation of why the class `GameWorld` is used instead of the class `Game` is given below.

- The `Controller` class: the controller class contains much less code then in the previous assignment since
  a large part of its functionality is now delegated to the specific command classes, as explained below.

[^2]: Strictly speaking, the parsing phase should only check properties of the input data that do not involve
any semantics so, for example, the property of coordinates of being on or off the board should not be checked
in the parsing phase.

In the previous assignment, the parsing (i.e. finding out which command is to be executed and, when appropriate,
with which parameter values) was carried out directly via a switch (or `if-else` ladder) contained in (or called
from) the **Game loop** of the the `run` method of the controller, with one case for each different command.

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
If the execution is successful and the state of the game has changed, it prints the board and if the
command is invalid, it prints an error message.

The most important part of this loop is the following line of code:
```java
Command command = Command.parse(words);
```

The key point is that the controller only handles abstract commands so it doesn't know which concrete 
command is being executed nor exactly what this concrete command does. This is the dynamic-binding
mechanism that allows us to easily add new specific commands.

The **`parse(String[])`** method is a static method of the `Command` class that is responsible for
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
game to execute the cooresponding action.

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

As can be seen, all commands inherit from the `Command` class. The `Command` class is abstract, so the 
concrete commands implement the functionality:

* The execute method performs the action on the game (actually a `GameWorld` that we will explain later) 
and returns a value of type `ExecutionResult` that indicates the result of the execution of the command: 
if it has succeeded or not, the error message if necessary, and if it is necessary to paint the game.
`ExecutionResult` is a [Java Record](https://www.geeksforgeeks.org/what-are-java-records-and-how-to-use-them-alongside-constructors-and-methods/) that allows us to return multiple values from a method in Java 
(as if it were a tuple or datatype like C/C++ structs).

* The **`create(String[])`** method returns an instance of the particular command. Because each command processes
its own parameters itself, this method will return `this` or create a new instance of the same class.  
In case that the text entered by the user does not correspond to the command, then the `create(String[])` 
method will return `null`.

<!-- TOC --><a name="comando-reset"></a>
### Comando Reset

Vamos a modificar ligeramente el comportamiento del comando reset de la Práctica 1 con el objetivo de facilitar las pruebas, de modo que sea posible cambiar el nivel y la semilla de juego sin tener que parar y volver a arrancar el juego.

El `ResetCommand` debe ser suficientemente flexible como para o bien recibir 0 parámetros y, por tanto, utilizar el nivel y semilla proporcionado como parámetro de línea de comandos, o bien recibir 2 parámetros (mismo tipo y orden que en la línea de comandos) que se utilizarán para reiniciar la partida.

Ten en cuenta que al resetear el juego, también se debe reiniciar la instancia de `Random` que se utiliza para generar la partida.

<!-- TOC --><a name="la-clase-game-y-sus-diferentes-usos"></a>
### La clase Game y sus diferentes usos

La clase `Game` se utiliza en diferentes partes de la aplicación: `Controller`, `GamePrinter`, etc. con diferentes objetivos. Hasta ahora, si implementamos un nuevo método `Game` para que pueda ser utilizado en una parte específica de la aplicación (en especial si es público), queda a disposición del resto de la aplicación.

Esta situación nos puede inducir a no tener una separación clara en la lógica de nuestra aplicación ya que desde cualquier parte de la aplicación podemos llamar a los métodos de `Game`. Para evitar este problema y limitar el *acoplamiento* en nuestro código, podemos hacer uso de las `interface` de Java.

Mediante las *Java interface* podemos declarar de manera específica las dependencias / necesidades de una parte de nuestro código. En nuestro vamos a definir dos interfaces:

- `GameStatus`: Incluirá todos los métodos que necesita `GamePrinter` para "conocer" el estado del mundo y poder hacer su trabajo.

```java
public interface GameStatus {
    int getCycle();
    // ...
}
```

- `GameWorld`: Incluirá todos los métodos que necesitan los comandos y los objetos del juego (plantas, zombies, etc.), para saber qué pasa en el *mundo del juego*. Esta es una interfaz privilegiada que permite poder realizar acciones en el juego.

```java
public interface GameWorld {

    public static final int NUM_ROWS = 4;

    public static final int NUM_COLS = 8;

    void playerQuits();

    ExecutionResult update();

    // ...
}
```

Así, `Game` implementará estas dos interfaces diferenciadas [^3].

```java
public class Game implements GameStatus, GameWorld {

    public static final int INITIAL_SUNCOINS = 50;

    private boolean playerQuits;

    //...
}
```

[^3]: Una manera más adecuada para que los métodos de `GameWorld`, que son privilegiados, no fueran visibles más allá de los colaboradores más cercanos de `Game` (e.g. `GameObject` y `Command`) sería utilizar utilizar *clases internas* como verás en TP II.

<!-- TOC --><a name="herencia-y-polimorfismo"></a>
### Herencia y polimorfismo

Con el patrón *Command* se busca poder introducir nuevos comandos sin cambiar el código del controlador. De la misma manera, queremos poder introducir nuevos objetos de juego sin tener que modificar el resto del código. La clave es que `Game` no maneje objetos específicos, sino que maneje objetos de una entidad abstracta que vamos a llamar `GameObject`. De esta entidad abstracta heredan el resto de objetos del juego. Como todos los elementos del juego van a ser `GameObject`s, compartirán la mayoría de atributos y métodos, y cada uno de los objetos concretos será el encargado de implementar su propio comportamiento. 

Todos los `GameObject`s tienen una posición en el juego y una serie de métodos que llamamos durante cada ciclo del juego, por ejemplo, cuando necesitan hacer algo propio de ese objeto en un momento concreto de su ciclo de vida:

- `onEnter()`: Se llama cuando el objeto entra en el juego.
- `update()`: Se llama en cada iteración del bucle de juego.
- `onExit()`: Se llama cuando el objeto sale del juego, desapareciendo.
- `isAlive()`: Es verdadero si el objeto sigue vivo, o falso, si hay que eliminarlo del juego.

Es normal que en objetos sencillos haya algunos de estos métodos vacíos o con funcionalidad trivial. 

A continuación se muestra el esqueleto del código de la clase `GameObject`. Más adelante describimos el uso del interfaz `GameItem` utilizado para representar acciones y operaciones dentro de los elementos que se encuentran en el tablero de juego.


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

Este código lo tendrás que ir extendiendo y modificando a lo largo de las prácticas, para cada objeto del juego que hereda de la clase `GameObject`. 

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

