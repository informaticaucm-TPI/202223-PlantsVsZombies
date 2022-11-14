<!-- TOC start -->
- [Assignment 2 (Part II): Plants versus Zombies Extended](#práctica-2-parte-ii-plantas-contra-zombis-extended)
- [Basic extensions to the game](#extensiones-básicas-del-juego)
  * [New game elements](#incorporación-de-nuevos-objetos-de-juego)
    + [The cherry bomb plant](#cherrybomb-plant)
    + [The wall-nut plant](#wall-nut-plant)
    + [The buckethead zombie](#buckethead-zombie)
    + [The sporty zombie](#sporty-zombie)
    + [The explosive zombie](#explosive-zombie)
  * [New commands](#comandos)
    + [The `listzombies` command](#listzombiescommand)
    + [The `cheatplant` ommand](#addplantcheatcommand)
    + [The `addzombie` command](#addzombiecommand)
- [Changing the mechanism for obtaining suncoins](#cambiando-la-mecánica-para-conseguir-suncoins)
  * [Description](#detalles-de-la-mecánica)
  * [Implementation](#implementación-de-la-mecánica)
- [Implementation details](#detalles-de-implementación)
    + [The use of static variables](#variables-estáticas)
      - [Generated suns and caught suns](#soles-generados-y-soles-cogidos)
      - [The `catch` command](#catchcommand)
  * [The `GameAction` interface and the `ExplosionAction` class](#la-interfaz-gameactions-y-las-acciones-explosionaction)
- [Tests](#pruebas)
<!-- TOC end -->

<!-- TOC --><a name="práctica-2-parte-ii-plantas-contra-zombis-extended"></a>
# Assignment 2 (Part II): Plants versus Zombies Extended

**Submission: 28th November at 09:00hrs**

**Objective:** inheritance, polymorphism, abstract classes and interfaces

We start with a **warning**:

> If you break encapsulation, use methods that return lists or make any use of the Java constructs
`instanceof` or `getClass()` (with the exception of the possible use of `getClass()` in `equals`
methods as shown in the slides) you will automatically fail the assignment. The same applies to
the use of any *DIY getClass* (i.e. construction designed to reveal the concrete subclass of `GameObject`)
such as a set of `isX` methods, one for each subclass `X` of `GameObject`, where in the particular
subclass `Y`, the method `isY` returns true and the other methods return false.

**NOTE:** We recommend you read all of the problem statement before you start to implement your solution,
since it explains all the functionality that you are required to implement before providing some indications
about how you should implement it.

<!-- TOC --><a name="extensiones-básicas-del-juego"></a>
# Basic extensions of the game

Having refactored the code in Part I of this assignment in order to make it more
extendable, among other desirable properties, we now add new functionality by extending both of the
inheritance hierarchies introduced in part I, namely, the *command hierarchy* (i.e. the inheritance
hierarchy headed by the `Command` class) and the *game object hierarchy* (i.e. the inheritance
hierarchy headed by the `GameObject` class). That is to say, we introduce new commands and new game
objects, starting with the latter.

But before proceeding, ensure that you update the classes `GamePrinter` and `Messages` that we provide
in the template of this assignment since they have been modified with respect to the versions provided
in the template of the previous assignment. In particular, the `Messages` class contains the names of
the new game elements.

<!-- TOC --><a name="incorporación-de-nuevos-objetos-de-juego"></a>
## New game elements

First, if you have not already done so, you should create two subclasses of `GameObject` called `Plant`
and `Zombie` from which all the different zombie and plant classes inherit.

You should then extend the `GameObject` hierarchy to include the following new plants:

- `CherryBomb`
- `WallNut`

and the following new zombies:

- `BucketHead`
- `Sporty`
- `ExplosiveZombie`

The three new zombies inflict the same damage as the common zombie of the previous assignment and
appear with the same probability.

If your refactored code is well-structured, the introduction of these new elements should require
very little modification.

After adding the new plants, the output of the `list` command should be as follows:

```
Command > l

[DEBUG] Executing: l

Available plants:
[S]unflower: cost='20' suncoins, damage='0', endurance='1'
[P]eashooter: cost='50' suncoins, damage='1', endurance='3'
[W]all-[N]ut: cost='50' suncoins, damage='0', endurance='10'
[C]herry-Bomb: cost='50' suncoins, damage='10', endurance='2'
```

and the output of a game in which instances of the new zombies have appeared should
now have the following aspect:

```
Number of cycles: 20
Sun coins: 130
Remaining zombies: 5
           0              1              2              3              4              5              6              7              8       
     ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── 
  0 |              |              |              |              |              |     Z[05]    |              |              |              
     ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── 
  1 |              |              |              |              |              |              |              |    Bz[08]    |              
     ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── 
  2 |              |              |              |              |              |    Sz[02]    |              |              |              
     ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── 
  3 |              |              |              |              |              |    Ez[05]    |              |              |              
     ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── 
```


<!-- TOC --><a name="cherrybomb-plant"></a>
### The cherry bomb plant

Two cycles after planting, this kamikaze plant (to be implemented by a class called
`CherryBomb`) explodes, inflicting *10* points of damage on all neighburing
zombies (i.e. up, down, left, right and the four diagonals, though note that if the cherry bomb is on the edge
of the board, it will have fewer neighbours); it dies in its own explosion. It can also be
eaten by zombies, having resistance *2*. Its cost is *50* suncoins.

> NOTE: A dead cherry-bomb plant cannot explode.

The symbol used in its textual representation is a lower-case `c` which becomes an upper-case `C` in
the cycle immediately before exploding.

<!-- TOC --><a name="wall-nut-plant"></a>
### The wall-nut plant

This plant (to be implemented by a class called `WallNut`) does not damage the zombies and
simply acts as a barrier to them, having resistance *10*.
It costs *50* suncoins. The symbol used in its textual representation is the string `WN`.

<!-- TOC --><a name="buckethead-zombie"></a>
### The buckethead zombie

This zombie (to be implemented by a class called `BucketHeadZombie`) is slower but more resistant than
the common zombie: it moves every *4* cycles and has
resistance *8*.

The symbol used in its textual representation is the string `Bz`.

<!-- TOC --><a name="sporty-zombie"></a>
### The sporty zombie

This zombie (to be implemented by a class called `SportyZombie`) is faster but less resistant
than the common zombie: it moves every cycle and has
resistence *2*

The symbol used in its textual representation is the string `Sz`.

<!-- TOC --><a name="explosive-zombie"></a>
### The explosive zombie

This zombie (to be implemented by a class called `ExplosiveZombie`) has the same velocity and
resistance as a common zombie but if it is killed by a plant, it explodes causing *3* points
of damage to all neighbouring plants.

The symbol used in its textual representation is the string `Ez`.

<!-- TOC --><a name="comandos"></a>
## New commands

We now extend the command hierarchy by adding new commands, in particular, two commands
to *cheat* in the game, with a view to facilitating the debugging (it is supposed that these
commands would not be available in a production release).

After adding the new commands introduced below, the output of the help command should be as follows:

```
Command > h

[DEBUG] Executing: h

Available commands:
[a]dd <plant> <col> <row>: add a plant in position (col, row)
[l]ist: print the list of available plants
[r]eset [<level> <seed>]: start a new game (if level and seed are both provided, they are used to initialize the game)
[h]elp: print this help message
[e]xit: terminate the program
[n]one | "": skip user action for this cycle
[l]ist[Z]ombies: print the list of available zombies
[a]dd[Z]ombie <zombie> <col> <row>: add a zombie in position (col, row)
[C]heat[P]lant <plant> <col> <row>: add a plant in position (col, row) without consuming suncoins
```

<!-- TOC --><a name="listzombiescommand"></a>
### The `listzombies` command.

This command (to be implemented via a class called `ListZombiesCommand`) lists the available zombies. To
implement this and the following command, if you have not already done so, you should create a zombie
factory, similar to the plant factory that you created in Part I of this assignment.

```
Command > lz

[DEBUG] Executing: lz

Available zombies:
Zombie: speed='2', damage='1', endurance='5'
BucketHead Zombie: speed='4', damage='1', endurance='8'
Sporty Zombie: speed='1', damage='1', endurance='2'
Explosive Zombie: speed='2', damage='1', endurance='5'
```

<!-- TOC --><a name="addplantcheatcommand"></a>
### The `cheatplant` command

This command (to be implemented via a class called `AddPlantDebugCommand`) is used to add a plant to
the game, the difference with the `add` command being that it does not consume any suncoins. Similarly
to the `add` command, use of this command causes the game to be updated.

<!-- TOC --><a name="addzombiecommand"></a>
### The `addzombie` command

This command (to be implemented via a class called `AddZombieDebugCommand`) is used to manually add a zombie
of the specified type either on any free tile of the board or in the column where the zombies appear
(column 8)

The implementation of the parsing of the zombie-name command parameter by the zombie factory should be
done in the same way as the parsing of the plant-name command parameter by the plant factory. Recall
that the names of the different plants and zombies to be used in these parameters are to be found
in the `Messages` class. In the final version of A2, you should implement both of these parsing tasks
in the same way as the command name was parsed in the `Command` class; you should **not** use
the code for the
`getAvailablePlants` method provided in the template, which returns a *weakly read-only* [^1] shallow
copy of the list of available plants (using `Collections.unmodifiableList`, or the Java 9+ version,
`List.copyOf`); see also the FAQ (in Spanish) regarding this point.

[^1]: The read-only protection only applies to adding, deleting or replacing elements of the list
itself -- any attempt to do so will provoke an `UnsupportedOperationException` at run-time -- but if the
contents of the list are mutable objects, the list is not in any way protected against modification
of these contents, so the read-only/unmodifiable property is weak and returning such a list from a
public method could still be considered to be breaking encapsulation.

Similarly to the `add` command, use of this command causes the game to be updated.

<!-- TOC --><a name="cambiando-la-mecánica-para-conseguir-suncoins"></a>
# Changing the mechanism for obtaining suncoins.

Until now, the sunflowers updated
the number of suncoins automatically after a certain number of cycles. We now modify the mechanism
by which suncoins are obtained,
making it more similar to that of the original game, so that the sunflowers generate suns
that the player has to explicitly catch in order to obtain suncoins. This will involve changes
to multiple parts of the code.

To do so will involve introducing a new class, `Sun`, and a new command to
catch the suns that appear on the board `catch <col> <row>`. The output of the help command
will then be follows:

```
Command > h

[DEBUG] Executing: h

Available commands:
[a]dd <plant> <col> <row>: add a plant in position (col, row)
[l]ist: print the list of available plants
[r]eset [<level> <seed>]: start a new game (if level and seed are both provided, they are used to initialize the game)
[h]elp: print this help message
[e]xit: terminate the program
[n]one | "": skip user action for this cycle
[l]ist[Z]ombies: print the list of available zombies
[a]dd[Z]ombie <zombie> <col> <row>: add a zombie in position (col, row)
[C]heat[P]lant <plant> <col> <row>: add a plant in position (col, row) without consuming suncoins
[C]atch <col> <row>: catch a sun, if posible, in position (col, row)
```

<!-- TOC --><a name="detalles-de-la-mecánica"></a>
## Description

The behaviour of the new suncoins mechanism is as follows:

- Every three cycles, each sunflower (i.e. each instance of the `Sunflower` class on the board)
adds a sun on the same tile that it occupies.

- Every five cycles, a sun is generated in a random position on the board.

- A sun can share a position with a plant, a zombie or another sun, with no limit on the number of suns
sharing any position.

- Multiple suns in a position are represented as a single sun in the textual representation of
the board printed after each state change of the game.

- Suns are available to be caught for *10* cycles only, after which they disappear from the game.

- The player obtains *10* suncoins for each sun caught; if there are multiple suns in the position
specified in the catch command, all of them are caught by a single use of the command.

- Suns can only be caught once per cycle.

> Note: The implementation of the `GamePrinter` class provided in the new template has been modified
in order to be able to print a sun in the same position as a plant or a zombie.

Suns are represented by the string `*[XX]` (where `XX` are the cycles left before that sun will
disappear) so that the printing of the board should now have the following aspect:

```
Number of cycles: 28
Sun coins: 10
Remaining zombies: 2
Generated suns: 18
Caught suns: 0
           0              1              2              3              4              5              6              7              8       
     ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── 
  0 |  S[01] *[02] |     *[02]    |              |              |              |  *[06]Sz[02] |              |              |              
     ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── 
  1 |     S[01]    |              |              |              |              |              |              |     *[05]    |              
     ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── 
  2 |              |              |              |              |              |     *[05]    |              |              |              
     ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── 
  3 |              |     *[08]    |              |              |              |              |     *[08]    |              |              
     ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── 
```

<!-- TOC --><a name="implementación-de-la-mecánica"></a>
## Implementation

Implementing the new suncoins mechanism requires introducing the following changes:

- Create a new class called `Sun`. Recall that objects of this class cannot be in the game for more than
*10* cycles.

- Modify the behaviour of the `Sunflower` class so that it create objects of class `Sun` instead of
updating the number of suncoins.

- Create a class called `SunManager`, similar to the `ZombieManager` class,  which encapsulates the
random generation of suns.

- Modify the`Game` class in order to delegate the random generation of suns to the `SunsManager`.

- Create a new class called `CatchCommand` used to catch suns (max. of once per cycle). Use of
this command does not cause the game to be updated.

- Modify the game information so that, as well as displaying the current number of suncoins, it also
displays the number of generated suns (whether generated by a sunflower or randomly-generated by the
`SunManager`, whether caught or not) and the number of caught suns.

> **NOTE**: When specifying the initial value of the countdown-until-disappear of objects of the
`Sun` class, take into account that immediately after being added (i.e. in the same game cycle), a
sun's countdown will be updated.

<!-- TOC --><a name="detalles-de-implementación"></a>
# Implementation details

In this section we provide some extra implementation details concerning the implementation of
some of the functionalities described in the previous section.

<!-- TOC --><a name="variables-estáticas"></a>
### The use of static variables

<!-- TOC --><a name="soles-generados-y-soles-cogidos"></a>
#### Generated suns and caught suns

Implementing the counter of generated suns and that of caught suns in the `Game` class or
the `GameObjectContainer` class would break abstraction. A good way to implement these counters is
as static attributes of the `Sun` class, which can be updated in the `onEnter()` method and
either in the `onExit()` method or in the method of the `Sun` class that implements a sun being
caught (which should be declared in the `GameItem` interface)[^2]. Remember to
reset these counters when the `reset` command is used.

[^2]: If you have not already done so, you could also implement the counter of the number of zombies
on the board via a static attribute of the `Zombie` class in a similar way (instead of storing this
counter in a method of the ZombieManager and having to update it via the game delegating to the
zombie manager on receiving an "I'm dead"-call from a zombie).

<!-- TOC --><a name="catchcommand"></a>
#### The `catch` command

In order to implement the restriction that a `catch` command can only be executed once on each cycle,
we need to store the information as to whether or not a `catch` command has already been executed on that
cycle. We could use a static boolean attribute of the `CatchCommand` class to do this but how and when
should such an attribute be updated? Clearly, this should be done during the updating of the game,
the simplest way being for the game to notify the command class on each cycle by invoking a
method called, say, `newCycle`. Since this functionality could also be useful for other commands we
could implement a `newCycle` method in the `Command` class which simply passes on the notification
to each of the `Command` subclasses via the invocation of a method called, say, `newCycleStarted`.
The `newCycle` method itself should be static since it is independent of any command instance. The
implementation in the command class would then have the following aspect:

```java
public static void newCycle() {
    for(Command c : AVAILABLE_COMMANDS) {
        c.newCycleStarted();
    }
}

/**
   * Notifies the {@link Command} that a new cycle has started.
   */
protected void newCycleStarted() {
}
```

<!-- TOC --><a name="la-interfaz-gameactions-y-las-acciones-explosionaction"></a>
## The `GameAction` interface and the `ExplosionAction` class

A correct implementation of the new game elements `CherryBomb` y `ExplosiveZombie` requires
**explosion chaining**: e.g. a cherry bomb explodes killing a neighbouring explosive zombie,
which then also explodes damaging a neighbouring plant (which may not be a neighbour of the
exploding cherry bomb).

To implement this behaviour, we need to make two important changes to the game

- In the appropriate life-cycle method of the game elements involved, we must execute any
actions that must occur on dying or on being killed.

- In the `update` method of the `Game`, we must repeatedly remove dead elements (before
being removed, the appropriate life-cycle method must be called) until there are no more
such elements to be removed.

A good way of implementing this type of cascading behaviour, which also has the benefit of
imposing an order of execution on the actions involved, is to define an interface called
`GameAction`:

```java
public interface GameAction {
	void execute(GameWorld game);
}
```

and then define different types of action as classes that implement this interface. Objects of
these action classes can be generated and placed in a queue (an attribute of the `Game` class),
to be executed in LIFO or FIFO order at the appropriate point in the cycle.

In our case, we would like to implement two kinds of explosion action: one, generated by a
cherry bomb, that damages zombies and another, generated by an explosive zombie, that damages
plants. We can implement both of them in a single `ExplosionAction`, distinguishing the two cases
using a simple boolean attribute. When a game element explodes, instead of directly implementing
the explosion, in the appropriate life-cycle method a new `ExplosionAction` object is created
and given to the game to be placed in the action queue. After each removal of dead elements, the
game executes the actions on the action queue, which may create new dead elements.

Though the java libraries contain a class for implementing a simple stack (`java.util.Stack`),
the javadoc of this class recommends that it not be used! Instead it recommends using the
`java.util.Deque` interface to specify a LIFO queue (this interface is also recommended for
specifying a FIFO queue). For this reason, we will implement a LIFO queue using the
`java.util.ArrayDeque` class in a variable typed by the interface `java.util.Deque`.

Below we provide a skeleton of the code for managing the game actions in the `update`
method of the `Game` class.

```java
private Deque<GameAction> actions;

// ...

public void reset(Level level, long seed) {
    // ...
    this.actions = new ArrayDeque<>();
}

public void update() {

    // 1. Execute pending actions
		executePendingActions();

		// 2. Execute game Actions

		// 3. Game object updates

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

public void pushAction(GameAction gameAction) {
    this.actions.addLast(gameAction);
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
```


<!-- TOC --><a name="pruebas"></a>
# Tests

We have adapted the existing test cases to this assignment and **we will add additional test cases** shortly.
The package `tp1.p2.pruebas.parte1` contains the tests of part I of this assignment and the package
`tp1.p2.pruebas.parte2` contains the tests that are new to part II of this assignment.
