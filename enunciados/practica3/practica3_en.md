# Assignment 3: Introducing exceptions and input-output in the Plants vs Zombies application

**Submission: December 12th at 09:00 hrs**

**Objective:** Exception-handling and file-handling

# Introduction

In this assigment we extend the functionality of the *Plants vs Zombies* game of the previous
assignment in two ways:

- *Exception handling*: errors that may occur during the execution of the application
  can be more effectively dealt with using the exception mechanism of the Java language.
  As well as making the program more robust, 
  this mechanism enables the user to be informed about the occurrence of an error in whatever
  level of detail is considered appropriate, while at the same time providing a great deal of
  flexibility in regard to where the error is handled (and the error message printed).

- *File handling*: a useful addition to the application would be the facility to save
   and load high-scores for each level of the game. Clearl, this requires defining the
   scoring to be used, here, with points given for each zombie killed.
  
# Exception handling

In this section, we present the execptions that should be handled by the application and
give some information about their implementation, as well as providing a sample execution.

You will have observed in the previous assignment, that there are circumstances in which a command may fail,
either in its parsing or in its execution. The execution of the `add` command, for example, will
fail if the player does not have enough suncoins, or if the position chosen by the user to add the
plant is already occupied by another exclusive game object or is not even on the board. In
the previous assignment, if an error occured during the `execute` method of a command, the
method returned an `ExecutionResult` record with the `boolean`-valued *success*
attribute set to false and also containing an error message, though we still sometimes had
to print an error message from objects other than the controller.

In the present assignment, we will use the Java exception mechanism for propagating error
information. An exception
mechanism provides a flexible communication channel between the location in the code where an
error occurs and the location in the code where that error is handled, along which any required
data concerning the occurrence of the error can be sent from the former to the
latter[^1]. In many cases, the data concerning
the occurrence of the error that is transmitted from one code location to another via an exception
consists simply of an error message, and the handling of this error consists simply of sending that
message to the standard output to be displayed on the screen. In the general case, however,
more data about the error and its context may be transmitted between code locations and the
error-handling may require more complex actions than simply printing a message to the screen.

[^1]: The error-code mechanism of C and C++ is somewhat primitive in
comparison, though it is also much less computationally costly, which is why C++ retains it
as well as having an exception mechanism similar to that of Java (though this exception mechanism
is less type-checked and more difficult to use than the Java equivalent).

We will use the exception mechanism to ensure that all interaction with the user is located in
the controller and errors messages (and any other error data) will be propagated using exceptions,
so we no longer need the `ExecutionResult` class. In this section, we will not deal with exceptions
arising in the file-handling; these will be discussed in the file-handling section of this document.

## Concrete types of exception

First, we discuss handling exceptions thrown by the system, that is, those that are not
explicitly thrown by the programmer (most of which are also created by the programmer, though
the programmer could also explicitly throw system exceptions). You should at least handle the
following exception:

- `NumberFormatException`, which is thrown when an attempt is made to parse the String-representation
of a number and convert it to the corresponding value of type `int`, `long` or `float` etc. in the
case where the input String does not, in fact, represent a number and cannot, therefore, be so converted.

Regarding exceptions thrown by the programmer, you should create the following three exception classes
(and then throw and catch objects of these classes).

- `CommandParseException`: to be thrown when an error occurs during the parsing of a command (i.e.
during the execution of the `parse()` method of the `Command` class); examples of errors of this type
are unknown command, incorrect number of parameters, invalid parameter type, etc.

- `CommandExecuteException`: to be thrown when an error occurs during the execution of a command
(i.e. during the execution of the `execute()` method of the corresponding `Command` subclass);
examples of errors of this type are not having enough *suncoins* to execute the selected command or
trying to place a game object on a cell where it is no allowed to be placed.

- `GameException`: a convenience superclass of the previous two exception classes.

You will also need to define the following exceptions:

- `InvalidPositionException`: thrown when the position provided by the user is off the board or
is occupied by an exclusive object. The exception should store the coordinates of the position
that caused the error.

- `NotCatchablePositionException`: thrown when the position provided by the user to catch a catchable
object (in our case, a sun) does not contain any such object.

- `NotEnoughCoinsException`: thrown when it is not possible to carry out a command entered by the
user due to the player not having enough *suncoins* to carry it out (i.e. having less suncoins than
the cost of the command).

- `RecordException`: thrown when an error occurs while reading a record from, or writing a record to,
a file.


### General aspects of the implementation

Now that the exception mechanism has taken over the task of passing error messages between different
parts of the program, in particular, we no longer need to use `ExecutionResult` objects to pass such
messages from the commands to the controller. Accordingly, the `execute()` method of the commands
should now simply return a boolean value to indicate whether or not the command has resulted in a
change of the state of the game, in which case the new game state should be printed to the screen.
The `ExecutionResult` class can be simply deleted.

The main aspects of the implementation are as follows:

- With the use of the exception mechanism, we can now ensure that **all** printing to the screen is done
from the control part of the program (the
controller together with the commands), with the exception of the following printing in the 
`PlantsVsZombies` class: printing error messages if an error occurs when parsing the command-line parameters
and printing the level and the seed to be used. Moreover, almost all of this printing from the control
part of the program can actually be done from the controller, the exceptions being those classes that
represent commands whose purpose is to print a string (`HelpCommand`, `ListPlantsCommand` and
`ListZombiesCommand`) and the `ResetCommand` class, which, like the `PlantsVsZombies` class, should
print the level and seed to be used.

- You should implement the exception classes described above.

- The header of the methods `create(String[] parameters)` and `parse(String[] commandWords)` of the
  `Command` class should declare that they can throw `CommandParseException`. For example, the former
  method could throw an exception if the command entered by the user has parameters but the default
  `create()` method (that of the `Command` class) has not been overwritten in a concrete subclass (so
  either that command should not have parameters or the programmer should have overwritten the
  `create()` method for that command).

  ```java
      throw new CommandParseException(Messages.COMMAND_INCORRECT_PARAMETER_NUMBER);
  ```

  An example of when the latter method can throw such an exception is if the input string does not
  match any of the existing commands (rather than returning `null` and obliging the controller to treat
  the case with an *if-then-else* structure).

  ```java
     throw new CommandParseException(Messages.UNKNOWN_COMMAND);
  ```

  The header of the method `execute()` should declare that it can throw `CommandExecuteException`

- The controller `run()` method should capture all the exceptions and print the corresponding error
  messages so will now have the following aspect:

  ```java
	while (!game.isFinished() && !game.isPlayerQuits()) {

		// 1. Draw
		if (refreshDisplay) {
			printGame();
		}

		String[] words = prompt();
		try {
			refreshDisplay = false;
			// 2-4. User action & Game Action & Update
			Command command = Command.parse(words);
			refreshDisplay = game.execute(command);
		} catch (GameException) {
			System.out.println(error(e.getMessage()));
		}
	}
  ``` 

It is often good practice in exception-handling to catch a low-level exception and then throw a high-level exception
that *wraps* it. The high-level message will contain a high-level (less specific) message. For example, in the 
parsing of the `AddPlantCommand` we could have the following code:

```java
  } catch (NumberFormatException nfe) {
    throw new CommandParseException(Messages.INVALID_POSITION.formatted(parameters[1], parameters[2]), nfe);
  }
```

# Scores and high scores

In this assignment, we are going to implement a scoring system for the Plants vs Zombies game. Points are obtained
as follows: for each zombie that is removed from the game, the player obtains 10 points if it dies by being eaten
and 20 points if it dies in an explosion. The current score is printed along with the other game information each
time the board is printed.

```
Command > 
[DEBUG] Executing: 

Number of cycles: 55
Sun coins: 180
Remaining zombies: 0
Generated suns: 42
Caught suns: 32
Score: 20
           0              1              2              3              4              5              6              7              8       
     ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── 
  0 |     S[01]    |  P[03] *[03] |              |              |              |              |              |              |              
     ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── 
  1 |     S[01]    |              |              |              |              |              |              |     *[07]    |              
     ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── 
  2 |     *[01]    |     P[03]    |     *[05]    |              |              |              |              |     *[10]    |              
     ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── 
  3 |              |  P[03] *[04] |     *[08]    |              |              |     *[09]    |    Bz[02]    |              |              
     ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── 

```

## Saving and loading high scores

We now implement storing and loading of high-scores, which here will be called "records", where loading
occurs on starting the program and on using the reset command. We will store the records in a file
called `record.txt` with the following format:

```
HARD:20
EASY:20
INSANE:40
```

We will encapsulate the record functionality in a class called `Record` which is therefore responsible for:
- storing the records for each level in a file called `record.txt`
  * the high-score for the different levels can appear in any order
  * the high-scores are integers
- loading the records for each level from a file called `record.txt`
  * an attempt to load a file that does not exist or is corrupted provokes a `RecordException` and the game terminates.

> Note: If the record for a particular level doesn't exist, the default value `0` should be used.

To facilitate the testing of this functionality, we create a new command `record`, with abbreviation `o`, represented
by a class called `ShowRecordCommand` which displays the record for the current level.

```
Command > o

[DEBUG] Executing: o

INSANE record is 30
```

The output of the help command will now be as follows:

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
[a]dd[Z]ombie <idx> <col> <row>: add a zombie in position (col, row)
[C]heat[P]lant <plant> <col> <row>: add a plant in position (col, row) without consuming suncoins
[C]atch <col> <row>: catch a sun, if posible, in position (col, row)
Rec[o]rd: show record of the current level
Command > 
```

# Test cases

The A3 tests in the class `tp1.p3.pruebas.PlantsVsZombiesTests` have been adapted as follows:
- the tests of the previous assignment modified to show incorrect positions
- tests have been added to check the chaining of explosions
- tests have been added to check the records work correctly
