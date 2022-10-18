# Assignment 1: Plants vs. Zombies

**Submission deadline: October 17th, 09:00**
 
**Objetive:** Introduction to object orientation and to Java; use of arrays and enumerations; string handling with the `String` class; input-output on the console.

[//]: # "Bizarre syntax to get markdown comments that do not get transferred to the HTML"
[//]: # "Not sure if I will produce a FAQ in English so, for the moment, I won't talk about it."
[//]: # "**Preguntas Frecuentes**: Como es habitual que tengáis dudas (es normal) las iremos recopilando en este"
[//]: # "[documento de preguntas frecuentes](../faq.md). Para saber los últimos cambios que se han introducido"
[//]: # "[puedes consultar la historia del documento](https://github.com/informaticaucm-TPI/2022-23_PlantasVsZombies/commits/main/enunciados/faq.md)"

<!-- TOC start -->
  * [Copy detection](#control-de-copias)
- [1. Description of the assignment](#1-descripción-de-la-práctica)
  * [1.1 Introduction](#11-introducción)
  * [1.2. Details of the assignment](#12-detalles-sobre-la-práctica)
  * [Game objects](#objetos-del-juego)
    + [Peashooter](#planta-lanzaguisantes)
    + [Sunflower](#girasol)
    + [Common zombie](#zombi-común)
- [2. Game organisation](#2-organización-del-juego)
  * [2.1 Draw](#21-draw)
  * [2.2 User Action](#22-user-action)
    + [Observations regarding the commands](#observaciones-sobre-los-comandos)
  * [2.3 Game actions](#23-game-actions)
  * [2.4 Update](#24-update)
- [3. Implementation details](#3-detalles-de-implementación)
  * [3.1 Initialisation](#31-inicialización-del-juego)
  * [3.2 Classes of the application](#32-clases-que-componen-nuestra-aplicación)
    + [3.3 Observations regarding the implementation](#33-observaciones-a-la-implementación)
- [4. Submission of the assignment](#4-entrega-de-la-práctica)
- [5. Tests](#5-pruebas)
<!-- TOC end -->

<!-- TOC --><a name="control-de-copias"></a>
## Copy detection

For each of the TP assigmments, all the submissions from all the different TP groups will be checked using anti-plagiarism software, firstly, by comparing all of them pairwise and, secondly, by searching to see if any of the code of any of them is copied from other sources on the Internet (without explicit permission from the lecturer)[^1]. Any plagiarism detected will be reported to the *Comité de Actuación ante Copias* which, after interviewing the student or students in question, will decide whether further action is appropriate, and if so, will propose one of the following sanctions:
- A grade of zero for the TP-course exam session (*convocatoria*) to which the assignment belongs.
- A grade of zero for both TP-course exam sessions (*convocatorias*) for that year.
- Opening of disciplinary proceedings (*apertura de un expediente académico*) with the relevant university authority (*Inspección de Servicios*).

<!-- TOC --><a name="1-descripción-de-la-práctica"></a>
# 1. Description of the assignment

<!-- TOC --><a name="11-introducción"></a>
## 1.1 Introduction

Plants vs. Zombies is a popular *Tower Defense* type video game. According to the Wikipedia:

> As a horde of zombies approaches along several parallel lanes, the player must defend the home by putting down plants, which fire projectiles at the zombies or otherwise detrimentally affect them... Each plant has a different style of defense, such as shooting, exploding, and blocking. Different types of zombies have their own special behaviors and their own weaknesses to different plants.

The game is played on a board composed of square tiles on which the player places the different types of plants. The zombies appear on the r.h.s. of the board and move towards the l.h.s. If one of the zombies manages to breach the defenses and reach the l.h.s. of the board, the player loses. To resist the zombie attack, the player must destroy all the zombies, in which case, the player can go to the next level of the game. At each level, new types of plants and zombies with different abilities appear.

![Captura de pantalla de Plant vs Zombies](imgs/plants_vs_zombies.jpg)

In the original game, there are a multitude of plants, each with their own behaviour, cost, growing time, resistance to zombies and damage-inflicting capability (on zombies), some of these being:

- **Peashooter**: Shoots peas at the zombies.
- **Sunflower**: Generates sun.
- **Cherry bomb**: Blows up any zombies in the surrounding area.
- **Wall-nut**: Blocks the zombies and protects the plants.
- **Snow pea**: Shoots frozen peas which slow the zombies down and damage them.
- **Repeater**: Shoots two peas at the same time.
- **...**

![Catálogo de plantas del juego original](imgs/plants_vs_zombies_almanaq.jpg)

In the original game, each plant has a cost which is measured in sun. Sun can be obtained from certain types of plants (sunflowers) or from the sky. The player can accumulate sun and use it to grow new plants.

There are also different types of zombies to be combatted, some of these being :

- **Common zombie**: Commmon garden zombie.
- **Conehead zombie**: The traffic cone on its head makes it more resistant than the common garden zombie.
- **Pole vaulting zombie**: Jumps over the first plant that it encounters.
- **Buckethead zombie**: The bucket on its head makes it very resistant. 
- **Dancing zombie**: Originally based on Michael Jackson in Thriller but changed after complaints from his estate after his death.
- **...**

Many free versions of the Plants vs. Zombies game are available for different platforms. If you have never played it, we suggest you play one of these versions.

In the first assignment, we will build a very simple version of the Plants vs. Zombies game which we will make progressively more complex in the following assignments, though in all the assignments, the game will have a simple console output, i.e. in TP1 we will not build a GUI. In the next section, we describe exactly what must be implemented for the first assignment.

<!-- TOC --><a name="12-detalles-sobre-la-práctica"></a>
## 1.2. Details of the assignment

The game is played on a board of **4 × 8** tiles (4 rows and 8 columns). Each tile can contain zero or one entities, where an entity is a plant or a zombie. In our simplified version of the game, we only consider two types of plant, Peashooters and Sunflowers, and one type of zombie, Common Zombies, and sun coins can only be obtained from sunflowers. The zombies advance from right to left and win the game if they reach the l.h.s. of the board. The player wins by killing a number of zombies that is fixed *a priori* for each level (the level cannot change during a session in our simplified version of the game). Therefore, the game terminates if, during an update, either a zombie reaches the l.h.s. of the board (zombies win) or the stipulated number of zombies has been destroyed (player wins). At the start of the game, the board is empty and the player has 50 sun coins.


```
Level: EASY
Random generator initialized with seed: 25
Number of cycles: 0
Sun coins: 50
Remaining zombies: 3

   ──────── ──────── ──────── ──────── ──────── ──────── ──────── ──────── 
  |        |        |        |        |        |        |        |        |
   ──────── ──────── ──────── ──────── ──────── ──────── ──────── ──────── 
  |        |        |        |        |        |        |        |        |
   ──────── ──────── ──────── ──────── ──────── ──────── ──────── ──────── 
  |        |        |        |        |        |        |        |        |
   ──────── ──────── ──────── ──────── ──────── ──────── ──────── ──────── 
  |        |        |        |        |        |        |        |        |
   ──────── ──────── ──────── ──────── ──────── ──────── ──────── ──────── 

Command > 
```

<!-- TOC --><a name="objetos-del-juego"></a>
## Entities in the Game

In this section, we describe the properties and behaviour of the entities that appear in the version of the game to be implemented in the first assignment.

<!-- TOC --><a name="planta-lanzaguisantes"></a>
### Peashooter

- **Behaviour**: Shoots peas at the zombies inflicting damage on them.
- **Cost**: 50 sun coins.
- **Endurance**: 3 damage points.
- **Frequency**: 1 pea per cycle.
- **Damage**: Each pea causes 1 damage point.
- **Range**: Can only fire straight forward (i.e. along a row of the board).

<!-- TOC --><a name="girasol"></a>
### Sunflower

- **Behaviour**: Produces sun coins.
- **Cost**: 20 sun coins.
- **Endurance**: 1 damage point.
- **Frequency**: Generates 10 sun coins every three cycles.
- **Damage**: 0 (Does not cause any damage to the zombies).

<!-- TOC --><a name="zombi-común"></a>
### Common Zombie

- **Behaviour**: : Advances, inflicting damage on plants and eventually eating them.
- **Resistence**: 5 damage points.
- **Damage**: 1 damage point.
- **Speed**: 1 tile every 2 cycles.


<!-- TOC --><a name="2-organización-del-juego"></a>
# 2. Game design

Typically, videogames execute a series of actions repeatedly in a loop until the game (or a level) is finished. This control loop can be quite complicated, as is the case with the [Unity game loop](https://docs.unity3d.com/Manual/ExecutionOrder.html), for example. In our case, each cycle of the control loop comprises a small number of actions, namely:

1. ***Draw***: The program prints a textual representation of the state of the game, consisting of the state of the board together with some additional information (see later) on the screen.

2. ***User action***: The player executes a command, to add a plant at a specific position on the board, to ask for information about the available commands, etc. The player can also (explicitly) choose to perform no action and just let time pass (to accumulate sun coins from the sunflowers on the board, for example).

3. ***Game action***: The program may add a zombie on one of the rows of the board (in this game, always in the rightmost column). Whether or not a zombie is placed on the board in a given cycle is (pseudo-)random, with a probability that depends on the level. Where on the board a zombie is placed is also (pseudo-)random.

4. ***Update***: All the entities of the board are updated: the peashooters shoot peas, the zombies advance, the sunflowers produce sun coins, dead plants and zombies are removed from the board, etc.

Next, we give more detail about each part of the control loop.

<!-- TOC --><a name="21-draw"></a>
## 2.1 Draw

In each cycle, the current state of the board is printed, along with the following information: the number of the current cycle (initially 0), the amount of accumulated sun coins and the number of zombies left to appear. The board is printed on the standard output using ASCII characters. Here we show a sample cycle output:

```
Number of cycles: 15
Sun coins: 10
Remaining zombies: 1

   ──────── ──────── ──────── ──────── ──────── ──────── ──────── ──────── 
  |  S[01] | P [03] |        |        |        |  Z[03] |        |        |
   ──────── ──────── ──────── ──────── ──────── ──────── ──────── ──────── 
  |  S[01] |        |        |        |        |        |        |        |
   ──────── ──────── ──────── ──────── ──────── ──────── ──────── ──────── 
  |        |        |        |        |        |        |        |        |
   ──────── ──────── ──────── ──────── ──────── ──────── ──────── ──────── 
  |        |        |        |        |        |        |        |        |
   ──────── ──────── ──────── ──────── ──────── ──────── ──────── ──────── 

Command > 
```

The number of lives remaining appears in square brackets next to each entity (plant or zombie) on the board. The output finishes with the game prompt, `Command >`, prompting the user to enter the next action.


<!-- TOC --><a name="22-user-action"></a>
## 2.2 User Action

When prompted to enter a command, the player can choose to execute one of the following commands:

- `add <plant> <col> <row>`: add a new plant of type `plant` in position (`col`, `row`). The player can only add one plant per cycle and then only if they have accumulated enough sun coins to pay for it. A plant can only be added in an empty position (i.e. in a position that is not already occupied by a plant or a zombie).

- `reset`: restart the game, i.e go back to the initial configuration.

- `list`: show the available plants with information about their cost and the damage they inflict. In this version:

```
Command > list
[DEBUG] Executing: list

[S]unflower: cost = 20 sun coins, damage = 0, endurance = 1
[P]eashooter: cost = 50 sun coins, damage = 1, endurance = 3
```

- `none`: don't perform any user action.

- `exit`: display the message `Game Over` and then close the application.

- `help`: display the help message for each of the commands; each such help message comprises the name of a command, followed by a colon, followed by a brief description of what the command does.

```
Command > help
[a]dd <plant> <col> <row>: add a plant in position (col, row).
[l]ist: print the list of available plants.
[r]eset: start a new game.
[h]elp: print this help message.
[e]xit: terminate the program.
[n]one | "": skip a cycle.
```

<!-- TOC --><a name="observaciones-sobre-los-comandos"></a>
### Observations about the commands:

- The application must allow commands written in lower-case letters, upper-case letters or a mixture of the two.
- Plants can be identified either by their initial or by their full name, so the options are: `[p]eashooter`, `[s]unflower`
- Commands can be identified either by their initial or by the full command name, so the options are `[a]dd`, `[n]one`, `[l]ist`, `[h]elp`, `[e]xit`.
- An empty command is identified with the `none` commmand that causes the game to skip to the *game action* phase of the control loop without performing any user action.
- If a command is badly written, doesn't exist or can't be executed, an error message is displayed.
- After a command that doesn't change the state of the game (including a command that results in an error message), **the board (and associated information) is not displayed**. 

<!-- TOC --><a name="23-game-actions"></a>
## 2.3 Game action

In this first assignment, the computer's behaviour is pseudo-random, with the probability depending on the level. We define thee levels `EASY`, `HARD` and `INSANE`, where the level determines:

- The total number of zombies that will appear in the game.
- The frequency of apparition of the zombies.

The frequency determines the probability that a zombie will appear in a given cycle. Thus, if the frequency is 0.2, a zombie will appear approximately every 5 cycles, though their actual appearance may be more, or less, spaced out since it is (pseudo-)random.

| Nivel   | Número de Zombis | Frecuencia |
| :---:   | :---:            | :---:      |
|`EASY`   | 3                | 0.1        |
|`HARD`   | 5                | 0.2        |
|`INSANE` | 10               | 0.3        |

When a zombie appears, the program places it (pseudo-)randomly on one of the rows. In order to be able to repeat a given execution, when starting the program, the player can optionally provide a *seed* for the pseudo-random behaviour; if the program is given the same value for the seed on start-up and the player performs exactly the same moves, the exact same execution will be reproduced. If a seed is not provided, one solution is to generate a seed using the system date and time; another is to ask the JRE to choose a seed by creating the object of the `Random` class using the no-argument constructor.

It would be of interest to try out different values for the total number and frequency of zombies, with a view to finding the best combinations (those that are the most fun to play).

<!-- TOC --><a name="24-update"></a>
## 2.4 Update

The updates that occur on each cycle are:

- The Sunflowers produce sun coins.
- The Peashooters shoot the zombies that are in reach. A pea hits and damages the first zombie to the right of the peashooter in the same row (if there is one).
- The Zombies advance (a zombie cannot advance if it has another zombie or a plant in its way).
- If a Zombie is adjacent to a plant (on the next tile in the same row), it inflicts damage on the plant.
- If a plant or a Zombie's endurance goes down to 0, it is removed from the board (in the original game, plants get eaten while zombies turn to ashes but in this version they both simply disappear).

The game finishes if, during an update, all the zombies are destroyed or one of the zombies goes off the l.h.s of the board. When the game terminates, the appropriate message, one of `Player wins` or `Zombies win`, is displayed on the screen.

Updates of the game elements must always be carried out in the same order: first Sunflowers, then Peashooters, then Zombies. Within each category, the individual elements are to be updated in the order in which they were introduced into the game. Note that the state of the game at the end of the *update* phase of a cycle may depend on the update order; we have chosen one particular order so that all solutions will have the same behaviour in this regard.


<!-- TOC --><a name="3-detalles-de-implementación"></a>
# 3. Implementation details 

The implementation we propose for the first assignment is not optimal. In particular, it will inevitably contain repeated code, making it less readable, less maintainable, less testable and more prone to inconsistencies being introduced when modified, c.f. the programming principle known as **DRY (Don't Repeat Yourself)**. In the next assignment, we will be in a position to improve this implementation using the relevant OOP tools, in particular inheritance and polymorphism (which you are **not** allowed to use in this assignment), and we will refactorise the code using these tools, thereby avoiding unnecessary repetition.

In this assignment, we implement a reduced number of functionalities; in particular, we only consider two types of plants, *peashooter* and *sunflower*, and only one type of zombie, *common zombie*.

<!-- TOC --><a name="31-inicialización-del-juego"></a>
## 3.1 Initialisation of the game

Initially, the player has 50 sun coins and the board is empty.

The program must accept one obligatory parameter and one optional command-line parameter.
- The first, called `level`, is the game level.
- The second, called `seed`, is the value of the seed to be used to initialise **all** the pseudo-random behaviour of the game.

![Opciones de ejecución](imgs/args.png)


<!-- TOC --><a name="32-clases-que-componen-nuestra-aplicación"></a>
## 3.2 Main classes of our application

The program is to be started by executing the class `tp1.p1.PlantsVsZombies`, for which reason you are advised to place all the classes developed in the assignment in the package `tp1.p1` (or subpackages of this package). Your implementation must contain at least the following classes:

- `Sunflower`, `Peashooter`, `Zombie`: These three classes encapsulate the behaviour of the entities of the game. They have attributes such as their position `x, y`, the number of remaining lives, etc. They also have an attribute in which they store the game, that is, an instance of the class `Game`, in order for objects of these classes to use methods of the `Game` class to check whether or not they can perform a given action.

- `SunflowerList`, `PeashooterList`, `ZombieList`: Each of these three classes presents the functionality of a list of elements of the corresponding entity. We suggest that you implement each of them using a java `array` together with methods to manipulate this array.

- `Game`: This class encapsulates the logic of the game and the state of the game. To this end, it stores the list of sunflowers, the list of peashooters and the list of zombies currently in the game, as well as the current cycle number and the current number of sun coins obtained by the player. It also stores an object of the class `Random`, used to generate the pseudo-random values. Finally, it contains the method `update`, which updates the state of all the entities in the game. 

You will also need the following classes:

- `ZombieManager`: This is an auxilliary class used to count how many zombies are left to appear. It has a public method `boolean shouldAddZombie` which executes in each cycle to decide whether or not a zombie is to be added in that cycle (recall that this behaviour has a pseudorandom element).

- `Level`: This class is an enumeration that encapsulates the possible levels of the game. 

- `GamePrinter`: This class has a `toString` method to print the current state of the game as shown above. It will need to access methods of the `game` to do so. 

- `Controller`: This class contains the `run` method which in turn contains the loop controlling the execution of the game. Until the game terminates, this method repeatedly prompts the player to enter a command, then calls a method of the `Game` class to execute this command and, if appropriate, prints the new state of the game. The `Controller` class has an attribute to store an element of the `Game` class and an attribute called `in` to store an instance of the `Scanner` class, which is used to obtain the data from the standard input (by default, the keyboard). 

- `PlantsVsZombies`: This class contains the `main` method of the application. This method reads the command-line parameters (there will be one or two of them), creates the `game`, passing it the seed and the level, creates the `controller`, passing it the `game` and the `scanner`, and then invokes the `run` method of the `controller`.


<!-- TOC --><a name="33-observaciones-a-la-implementación"></a>
### 3.3 Observations about the implementación

- The executing application only needs one game, one controller and one scanner. Accordingly, in any execution of the application there will only ever be a single instance of the `Game` class, a single instance of the `Controller` class and a single instance of the `Scanner` class (which we refer to as the `game` object, the `controller` object and the `scanner` object, or simply the `game`, the `controller` and the `scanner`). At any given time, you will also only need a single instance of each of the list classes, though you may decide to create a new one each time a reset is used[^2].

- We provide you with some templates containing part of the code for the application.

- In the test files you can find execution traces; the output of your solution to this assignment should coincide with these examples.

### 3.4 Pedogogical Observations

The rest of the information needed to implement the assignment will be provided by the lecturer during the lectures and lab classes. The lecturer will give indications of which aspects of the implementation are considered obligatory in order to accept the assignment as correct and which aspects are left to the students' judgement.

Note also that in a *Problem-Based Learning* approach, the student is required to search for the knowledge they need to solve the problem at hand and to apply this knowledge to solving the problem *before* the pertinent information and solutions is presented in lectures. Many studies have shown that knowledge is more easily absorbed and retained if it is acquired in this way, by the student working under the lecturer's guidance but independently. Moreover, perhaps the most important ability to be acquired at university is how to learn independently ("learning to learn").


<!-- TOC --><a name="4-entrega-de-la-práctica"></a>
# 4. Submission

The assignment must be submitted as a single compressed (with *zip*) archive via the Campus Virtual submission mechanism not later than the date and time indicated at the start of this document. The zip archive should contain at least the following[^3].

- A folder called `src` containing the Java source code comprising your solution to the assignment,
- a file called `students.txt`, containing the names of the members of your group.

You may also be asked to include a folder called `doc` containing the API documentation in HTML format generated automatically from the Java source code of your solution using the *javadoc* tool.

**Do not include the `.class` files resulting from the compilation in your submission**. These files should **definitely not** be mixed with the source code files in the `src` folder of your project; instead, they should be in a folder called `bin` (see Assignment 0) which you should not submit.

> **Note**: Recall that you can use the option `File > Export` to help you to generate the zip archive.

<!-- TOC --><a name="5-pruebas"></a>
# 5. Tests

Together with the problem statement you will be provided with a folder containing program traces comprising pairs of files with the following nomenclature:

- `00-easy_25-input.txt`: the input of test case `0` with level `easy` and seed `5`.
- `00-easy_25-expected.txt`: the expected output of the above test case.

In Eclipse, you can redirect the standard input and the standard output to use a file (instead of using the keyboard and the screen, respectively, which is the default), by configuring the `Common` tab of the `Run Configurations` window as shown in the following figure.

![Redirección entrada y salida](imgs/runConfigurationsCommon.png)

There are many free programs to visually compare text files and Eclipse, in fact, integrates such a tool. To use it, simply select two text files, click on the right mouse button and in the pop-up menu choose `Compare With > Each other`.

![Cómo comparar dos archivos en Eclipse](imgs/Eclipse_Compare_EachOther.png)

A new window clearly showing any differences between the two files will then appear.

![Salida de la herramienta de comparación de archivos de Eclipse](imgs/Eclipse_Compare_EachOther_Output.png)

Take great care with the **order of instructions** in the updating of the game state since this can affect the program output considerably. Of course, our output may contain an error, so if you detect any inconsistencies, please communicate them to your lecturer so that we can correct them. Finally, be sure to test your program with other executions, not just those of the test cases that we provide. We may use other tests in the correction of the assignments.


[^1]: If you decide to store your code in a remote repository, e.g. in a free version-control system with a view to facilitating collaboration with your lab partner, make sure your code is not in reach of search engines. Also, do not accept any code from, or provide any code to, anyone other than your lab partner or your course lecturer (e.g. do not accept any code from, or provide any code to, an employee of a private academy).

[^2]: When you reset a game, you have a choice between re-initialising the objects used by the game in the previous incarnation in order to use them in the next incarnation, or creating new objects to be used in the next incarnation and forgetting about the objects used in the previous incarnation. The latter option, which relies on the garbage collector freeing the memory occupied by the objects from the previous incarnation, is usually simpler and is perfectly reasonable in any application for which it will not result in a huge number of objects being created (overwhelming the garbage collection services).

[^3]: You may also include the project information files generated by Eclipse.
