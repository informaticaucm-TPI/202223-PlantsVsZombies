<!-- TOC start -->
- [Assignment 2 (Part I): Plants versus zombis refactored](#práctica-2-parte-i-plantas-contra-zombis-refactored)
  * [Introduction](#introducción)
  * [Refactoring the solution of the previous assignment](#refactorización-de-la-solución-de-la-práctica-anterior)
    + [Restructuring the code responsible for parsing and executing the commands](#patrón-command)
    + [Extending the functionality of the reset command](#comando-reset)
    + [Using interfaces to define different perspectives on the `Game` class](#la-clase-game-y-sus-diferentes-usos)
    + [Restructuring the code responsible for handling the elements of the game](#herencia-y-polimorfismo)
    + [Delegating functionality of the `Game` to the `GameObjectContainer`](#gameobjectcontainer)
    + [Encapsulating the object-creation logic](#patrón-factory)
      - [Implementating the *Factory pattern*](#implementación)
    + [Interactions between elements of the game](#gameitem-y-callbacks)
  * [Tests](#pruebas)
<!-- TOC end -->
<!-- TOC --><a name="práctica-2-parte-i-plantas-contra-zombis-refactored"></a>
# Assignment 2 (Part I): Plants versus zombis refactored

**Submission: 7th of November at 09:00hrs** (submission of part I is optional, in order to receive feedback)
 
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
method of the previous assignment and distributing its functionality among a set of classes,
see the section [*Restructuring the code for parsing and executing the commands*](#patrón-command)

    * The second inheritance hierarchy will be used to organise the game objects which represent
the different creatures appearing in the game, thereby avoiding a lot of repetition of code that was
used in the previous assignment. This inheritance hierarchy will also enable us to use a
single data structure to store the state of the game, instead of using a different list for each type
of game objects,
see the section [*Restructuring the code for handling the elements of the game*](#herencia-y-polimorfismo)

- In *Part II* of Assignment 2, we extend the game by adding new commands and new game objects. Thanks
to the structure introduced in the refactoring of part I, the extensions of part II can be carried out
relatively easily, by modifying very little of the existing code, and the resulting code will be robust
and reliable.

[^1]: Refactoring means changing the structure of the code (to improve it, presumably) without changing
its functionality (i.e. without changing what it does).

<!-- TOC --><a name="refactorización-de-la-solución-de-la-práctica-anterior"></a>
## Refactoring the solution of the previous assignment

<!-- TOC --><a name="patrón-command"></a>
### Restructuring the code responsible for parsing and executing the commands

The first refactoring task concerns the commands, i.e. the different actions that the user of the 
game can carry out, such as adding a plant, listing the available plants, asking for help, etc. Our
refactoring objective is to structure the code in such a way as to facilitate the addition of new
commands (or the deletion of old ones). As one would expect, this is a very well-known problem
in OOP which has solutions that are very well tried and tested, so we do not need to invent our own.
The solution we will use is a variant of the *Command design pattern*, one of the twenty-three
software patterns presented in the foundational software patterns book "Design Patterns: Elements of
Reusable Object-Oriented Software" first published in 1994. The general idea of the *Command
pattern* is to encapsulate each command in its own class.

Our presentation of the *Command pattern* involves the following classes:

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
any semantics so, for example, the property of whether a tile is occupied or not should not be checked
in this phase.

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
### Using interfaces to define different perspectives on the `Game` class

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
### Restructuring the code responsible for handling the game elements

In the same way as the code structure known as the *Command pattern* enables new commands to be introduced with
minimal changes to the existing code (and, in particular, without changing the code of the controller),
we would also like to be able to introduce new game objects with only minimal changes to the existing code.
Just as the key to obtaining this desirable property in the case of the *Command pattern* was that the
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
immutable, using a Java Record would be a good way to do this). A way of decomposing the behaviour of the
elements of the game into methods that has proved useful is according to their life-cycle as follows:

- `onEnter()`: invoked when this element enters the game
- `update()`: invoked to evolve this element on each cycle of the game
- `onExit()`: invoked when this element leaves or is removed from the game
- `isAlive()`: indicates whether or not this element has any lives left, returning true if it does and false if it does not.

Note that in many simple objects these methods will be trivial and some will even be empty.

We now provide the skeleton of the code for the `GameObject` class. The `GameItem` interface implemented
by this class is described below.

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
###  Delegating functionality of the `Game` to the `GameObjectContainer`

In the same way as we simplified the `Controller` class, it would also be a good idea to simplify the `Game` class
by reducing its role to that of coordinating the other classes. This can be done by delegating the functionality
of the `Game` class to other classes, reducing the body of many of its methods to mere invocations of methods of
other classes, which is where the work is really carried out. One such class is the `GameObjectContainer` (this
class is the assignment 2 version of the `Board` class that you may have implemented in assignment 1, together
with some elements of the list classes of assignment 1).

The `GameObjectContainer` encapsulates the data structure used to store the objects representing elements of the
game (hereinafter we will refer to the unique instance of this class as the container) as well as the methods
for updating them, deleting them etc. For the store itself, we can use any type of collection (and since it
is encapsulated, we can change to another type of collection any time we want without affecting the rest of
the code); for simplicity,
we propose to use the `ArrayList` class, typed using the `List` interface, declared as follows:

```java
public class GameObjectContainer {

	private List<GameObject> gameObjects;

	public GameObjectContainer() {
		gameObjects = new ArrayList<>();
	}
    //...
```

Notice that, thanks to the use of the `GameObject` abstract class and associated inheritance hierarchy:

- The container encapsulates a single store for all
  the different elements of the game (rather than a store for each type of element as in assignment 1),

- Similarly to the methods of the `Game` class, the methods of the container class do not know which
  element of the game are being stored and manipulated, dealing only in abstractions. Neither the
  `Game` nor the `GameObjectContainer` should contain any references to concrete subclasses of the
  `GameObject` class.

- Most of the logic of the game is in the game object methods (i.e. the methods of the concrete
  subclasses of the `GameObject` class). Each class knows how it is updated, what happens when it is
  attacked, etc. The code provided still assumes the use of a `ZombieManager` but with a reduced role
  w.r.t. assignment 1, since the zombies themselves are now stored by the container in the common store.

<!-- TOC --><a name="patrón-factory"></a>
### Encapsulating the object-creation logic

We will use a variant of the *Factory pattern*, another well-known software pattern,
to encapsulate the object-creation logic in a dedicated class, called a factory class, thereby hiding
it from the invoker of the factory class methods. For
example, in the previous version of the assignment, the plant-creation
logic was in `run` method of the controller (with the use of the *Command pattern*, this code will
probably now be found in the `execute` method of the `AddPlantCommand` class) and looked something 
like the following:
 
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

If we create a plant factory according to the *Factory pattern*, the corresponding code
can look more like the following:

```java
Plant plant = PlantFactory.spawnPlant(plantName, game, col, row);
game.addPlant(plant);
```

With the decoupling of the object-creation logic from the game logic and the command
logic that the *Factory pattern* provides, adding a new type of plant is as simple as
adding a new type of command: simply create the new class and then add an object of
that class to the `AVAILABLE_PLANTS` array of the factory class. No other code need
by changed. Deleting a type of plant is also easy.

<!-- TOC --><a name="implementación"></a>
#### Implementing the *Factory pattern*

The *Command pattern* and the *Factory pattern* are eminently compatible since:

- on executing the command `add plant col row` from the `execute` of the `AddPlantCommand` we can
  delegate the creation of the plant to the factory.
- on executing `list` to find out what are the available plants, the `execute` method of the
  `ListPlantsCommand` can call a plant factory method which returns the required information based
  on the content of the `AVAILABLE_PLANTS` list.

The skeleton code of the plant factory is as follows:

```java
public class PlantFactory {
  
    private static final List<Plant> AVAILABLE_PLANTS = Arrays.asList(
        // ...
	);  


	public static Plant spawnPlant(String plantName, GameWorld game, int col, int row) {
        // ...
    }

        // probably safer to return a string (similar to the help message mechanism)
	public static List<Plant> getAvailablePlants() {
		return List.copyOf(AVAILABLE_PLANTS);
	}
    // ...
}
```

The `getAvailablePlants()` method will be called from the `execute` method of the `ListPlantsCommand`.

In the previous assignment, we only had two types of plants and one type of zombie but in Part II
of this assignment we will add new types of plants and zombies, each with its own characteristics
and behaviour.

Finally, you may also wish to create a `ZombieFactory` class, to be used by the `ZombieManager` class,
and an `AddZombieCommand` that adds a zombie in a position provided by the user, to be used for
debugging purposes.

<!-- TOC --><a name="gameitem-y-callbacks"></a>
### Interactions between elements of the game.

We now turn our attention to the interaction between different elements of the game.
How can we detect when interactions should take place and then execute them? First,
we consider using the following code in the `update` method of the `Zombie` class:

```java
public void update() {
    //...
    GameObject other = game.getGameObjectInPosition(col, row);
    if(other != null && other.getClass() == Sunflower.class) {  
        ((Sunflower) other).setAlive(false);
    }
    //...
}
```

Though this code works, it is not object-oriented since it breaks abstraction
and encapsulation; notice that we would have to change the zombie code each time we
introduce a new type of plant. Moreover, 
**the use of `getClass` or `instanceof` is absolutely forbidden in the assignments
and you will receive a mark of 0 points if you use either of them**
since it is a way of avoiding the use of dynamic binding and, therefore, correct OOP.

Another equally-poor attempt at a solution is to use a kind of DIY `getClass` or
`instanceof`, by defining one method for each concrete subclass of `GameObject`,
`isSunflower()`, `isPeashooter()`, etc. 
and including an implementation of each of these methods in every such subclass,
where each method returns true in the correponding subclass and false in the other
subclasses.

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

Clearly, adding a new type of plant in code using this kind of DIY `getClass` or
`instanceof` would involve modifying all the concrete subclasses of `GameObject`.
**Any solution using a DIY `getClass` or `instanceof` such as this is also absolutely
forbidden and, again, you will receive a mark of 0 points if you use it.**

Both of these examples manifest a typical error of novice OO programmers:

1. Obtain the actual type of the object being processed (using `getClass`, `instanceof`
   or some DIY version of these).
2. Use a conditional instruction to decide which behaviour to carry out
   in function of the actual type.
   
This incorrect approach also leads to giving responsibilities to classes (in this case 
the `Zombie` class) which should not have them, thereby making maintenance
and evolution of the code much more difficult.

We require a solution in which each concrete subclass of `GameObject` is responsible
for its own behaviour and for processing its own data, so that modifying or extending
this behaviour does not require any modification to other classes. To that end,
in our next attempt we define a method of the `Zombie` class called `receiveZombieAttack`:

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

Each `GameObject` subclass contains a `receiveZombieAttack(int damage)` method
which implements the behaviour of objects of that class when they are attacked by
a zombie (in the zombie class, the method body is empty since zombies to not
attack zombies). This solution is on the right track, in that each class is
responsible for its own behaviour, but the method `getGameObjectInPosition`
breaks encapsulation by returning an object that is the value of an attribute;
if you return a pointer to private (mutable) data, clearly, it is no longer private.

To solve this problem, we can use a Java interface, in this case `GameItem`, as
the declared type of the object returned by the `getGameItemInPosition` method,
thereby restricting what the caller of the `getGameItemInPosition` method can do
with this object. The `GameItem` interface is then implemented by the `GameObject`
class.

```java
public interface GameItem {
    boolean receiveZombieAttack(int damage);
    // ...
}

public class GameObject implements GameItem {
    // ...
}

public class Zombie extends GameObject {
    //...

    public void update() {
        //...
        GameItem item = game.getGameItemInPosition(col, row);
        if(item != null ) {  
            item.receiveZombieAttack(this.damage);
        }
        //...
    }
}
```

With the above code, the caller of the `getGameItemInPosition` method cannot do
anything with the object returned by this method except call the
`receiveZombieAttack` method on it, since this is the only method that is defined
in the declared type (i.e. the `GameItem` interface).

For the moment, the `GameItem` interface is very simple but as we extend our program
we will need to add more methods to it in order to add other types of interactions 
between different elements of the game.

<!-- TOC --><a name="pruebas"></a>
## Pruebas

Recall that the functionality of the refactored code should be exactly the same as that of the original code,
for which reason, the refactored code should pass exactly the same black-box (i.e. functional) system-level
tests as the original code.

To simplify the testing procedure, we are going to slightly "misuse" the [JUnit](https://junit.org/) support provided by
Eclipse. JUnit is a widely-used unit-testing framework for Java applications.

Together with the template for the solution of the assignment, we include the class `tp1.p2.PlantsVsZombiesTests`  which
contains JUnit tests, one for each of the test cases of the previous assignment.

Before trying to execute the JUnit tests, you will need to add the JUnit libraries
to your project by choosing *Project > Properties* on the Eclipse menu and then selecting *Java Build Path* and finally
the *Libraries* tab. Next click on the **Add Library...** button.

![](./imgs/00-ProjectProjerties.jpg)

In the new window, choose *JUnit* and click on the *Finish* button.

![](./imgs/01-AddJUnit.jpg)

On returning to the project properties window, click on the button *Apply and Close*.
Now that you have included the JUnit libraries, if you click with the right button on the file `PlatsVsZombiesTests.java`
and then choose *Run As*, there should now be a new option available called **JUnit Test**.

![](./imgs/02-RunAsJUnitTest.jpg)

On choosing the JUnit Test option, Eclipse opens a window which allows us to choose which of the tests we want to execute and
to then visualise the result of their exeuction. Note that the JUnit test-execution results simply tell us which of the tests
passed and which failed where, in our case, a test fails if there is any difference between the output of the program and the
expected output. To obtain more information about the failed tests in order to understand *why* a test failed, you will need
to use the test procedure of the previous assignment. 

![](./imgs/03-JUnitFailed.jpg)

![](./imgs/04-JUnitPass.jpg)
