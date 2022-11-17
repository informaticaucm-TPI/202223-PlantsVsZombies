<!-- TOC start -->
- [Práctica 2 (Parte II): Plantas contra zombis Extended](#práctica-2-parte-ii-plantas-contra-zombis-extended)
- [Extensiones básicas del juego](#extensiones-básicas-del-juego)
  * [Incorporación de nuevos objetos de juego](#incorporación-de-nuevos-objetos-de-juego)
    + [CherryBomb plant](#cherrybomb-plant)
    + [Wall-Nut plant](#wall-nut-plant)
    + [BucketHead zombie](#buckethead-zombie)
    + [Sporty zombie](#sporty-zombie)
    + [Explosive zombie](#explosive-zombie)
  * [Comandos](#comandos)
    + [ListZombiesCommand](#listzombiescommand)
  * [AddPlantCheatCommand](#addplantcheatcommand)
  * [AddZombieCommand](#addzombiecommand)
- [Cambiando la mecánica para conseguir *suncoins*](#cambiando-la-mecánica-para-conseguir-suncoins)
  * [Detalles de la mecánica](#detalles-de-la-mecánica)
  * [Implementación de la mecánica](#implementación-de-la-mecánica)
- [Detalles de implementación](#detalles-de-implementación)
    + [Variables estáticas](#variables-estáticas)
      - [Soles generados y soles cogidos](#soles-generados-y-soles-cogidos)
      - [CatchCommand](#catchcommand)
  * [La interfaz `GameAction`s y las acciones `ExplosionAction`](#la-interfaz-gameactions-y-las-acciones-explosionaction)
- [Pruebas](#pruebas)
<!-- TOC end -->
<!-- TOC --><a name="práctica-2-parte-ii-plantas-contra-zombis-extended"></a>
# Práctica 2 (Parte II): Plantas contra zombis Extended

**Entrega: Semana del 28 Noviembre**

**Objetivo:** Herencia, polimorfismo, clases abstractas e interfaces.

**Preguntas Frecuentes**: Como es habitual que tengáis dudas (es normal) las iremos recopilando en este [documento de preguntas frecuentes](../faq.md). Para saber los últimos cambios que se han introducido [puedes consultar la historia del documento](https://github.com/informaticaucm-TPI/202223-PlantsVsZombies/commits/main/enunciados/faq.md).

En esta práctica vamos a extender el código con nuevas funcionalidades. Antes de comenzar recordad la **advertencia**:

> La falta de encapsulación, el uso de métodos que devuelvan listas, y el uso de `instanceof` o `getClass()` tiene como consecuencia un **suspenso directo** en la práctica. Es incluso peor implementar un `instanceof` casero, por ejemplo así: cada subclase de la clase `GameObject` contiene un conjunto de métodos `esX`, uno por cada subclase X de `GameObject`; el método `esX` de la clase X devuelve `true` y los demás métodos `esX` de la clase X devuelven `false`.


**NOTA:** Te recomendamos que leas completamente el enunciado antes de ponerte a implementar la funcionalidad solicitada. Primero explicamos las nuevas funcionalidades que solicitamos y posteriormente proporcionamos algunos detalles acerca de cómo puedes implementar estas funcionalidades.

<!-- TOC --><a name="extensiones-básicas-del-juego"></a>
# Extensiones básicas del juego

En esta segunda parte, hemos ajustado nuevamente las clases `GamePrinter` y `Messages` para facilitarte la tarea. Recuerda, actualizar tu código con el que te proporcionamos en la plantilla.

<!-- TOC --><a name="incorporación-de-nuevos-objetos-de-juego"></a>
## Incorporación de nuevos objetos de juego

Ahora que tenemos nuestra factoría y hemos utilizado herencia para generalizar los objetos de juego va a ser muy sencillo extender el juego con nuevas plantas y zombis. De esta forma, vamos a incluir las siguientes plantas:

- `CherryBomb`
- `WallNut`

En cuanto a los zombies vamos a añadir los siguientes:

- `BucketHead`
- `Sporty`
- `ExplosiveZombie`

Los tres nuevos zombis ejercen el mismo daño que el zombi común de la primera práctica. Los diferentes tipos de zombis aparecen con la misma probabilidad.

Para hacer estos cambios organiza tu jerarquía de objetos de juego para tengan como base `GameObject`, del que heredan `Plant` y `Zombie`, de los cuales heredan las clases concretas de plantas y zombies. Mientras implementes esta nueva jerarquía, analiza qué clases de tu jerarquía deberían de ser clases abstractas.

Si has planteado bien la lista de objetos y el juego no deberías tener que hacer ningún cambio importante, lo único que tendrás que hacer es crear las clases y registrar los nuevos objetos en sus respectivas factorías. 

Así quedará nuestra lista de plantas.

```
Command > l

[DEBUG] Executing: l

Available plants:
[S]unflower: cost='20' suncoins, damage='0', endurance='1'
[P]eashooter: cost='50' suncoins, damage='1', endurance='3'
[W]all-[N]ut: cost='50' suncoins, damage='0', endurance='10'
[C]herry-Bomb: cost='50' suncoins, damage='10', endurance='2'
```


Así es como veremos la pantalla con los nuevos tipos de objetos.

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
### CherryBomb plant

Es una planta que explota y quita *10* puntos de daño a todos los zombis que están a su alrededor (a distancia de 1 casilla). La planta una vez que explota muere. La explosión ocurre dos ciclos después de ser plantada. Los zombis también se pueden comer a la planta, que tiene resistencia *2*. Su coste es *50* suncoins.

> NOTA: La planta tiene que estar viva para poder explotar.

Su representación será una `c` minúscula, que pasará a mayúsculas `C` justo el ciclo anterior a explotar.

<!-- TOC --><a name="wall-nut-plant"></a>
### Wall-Nut plant

Es una planta que sirve como barrera. Tiene coste *50* suncoins y resistencia *10*. Está planta no produce ningún daño.

Su representación será `WN`.

<!-- TOC --><a name="buckethead-zombie"></a>
### BucketHead zombie

Es un zombie más lento pero más resistente. Camina un paso cada *4* ciclos y tiene resistencia *8*.

Su representación será `Bz`.

<!-- TOC --><a name="sporty-zombie"></a>
### Sporty zombie

Es un zombie más rápido pero menos resistente. Camina un paso cada ciclo y tiene resistencia *2*.

Su representación será `Sz`.

<!-- TOC --><a name="explosive-zombie"></a>
### Explosive zombie

Tiene la misma velocidad y resistencia que un zombie básico, pero si una planta lo mata explota haciendo *3* de daño a todas las plantas que tiene a su alrededor. 

Su representación será `Ez`.

<!-- TOC --><a name="comandos"></a>
## Comandos

Vamos a introducir algunos comandos adicionales para *hacer trampas* en el juego, pero que nos permitirán realizar las pruebas de la aplicación de manera más sencilla.

Al terminar de añadir todos los comandos, si solicitamos la ayuda de la aplicación se deberá generar la siguiente salida:
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
```

<!-- TOC --><a name="listzombiescommand"></a>
### ListZombiesCommand

Este comando lista los zombies disponibles en el juego.

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
## AddPlantCheatCommand

Este comando es similar a `AddPlantCommand`, es decir, permite añadir una planta al juego, pero en este caso no se consumen suncoins.

Este comando **hace que se actualice el juego** (al igual que cuando se añade una planta).

<!-- TOC --><a name="addzombiecommand"></a>
## AddZombieCommand

Este comando permite añadir un zombie al juego en una casilla que se encuentre libre dentro del terreno de juego o en la columna donde aparecen los zombies (columna 8).

Para simplificar la implementación de este comando, el parámetro que permite seleccionar el tipo de zombie es de tipo `int` y debe admitir un valor entre `[0, ZombieFactory.getAvailableZombies().size())`.

Este comando **hace que se actualice el juego** (al igual que cuando se añade una planta).

<!-- TOC --><a name="cambiando-la-mecánica-para-conseguir-suncoins"></a>
# Cambiando la mecánica para conseguir *suncoins*

El siguiente cambio afecta a más partes del juego, por lo que es más delicado. Hasta ahora los `Sunflower`s actualizaban el número de *suncoins* automáticamente pasados un número de ciclos concretos. Vamos a cambiar esta mecánica para que se parezca más a la mecánica del juego original, es decir, ahora los `Sunflower`s van a generar *soles* que el jugador va a tener que recoger. Cuando el jugador recoge un *sol* este le proporcionará *suncoins*.

Vamos a representar los soles como un nuevo tipo de objeto a mediante la clase `Sun`. Y el jugador tendrá que coger los `Sun`s con el nuevo comando `catch col row`. La lista de comandos disponibles quedará del siguiente modo:

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
```

<!-- TOC --><a name="detalles-de-la-mecánica"></a>
## Detalles de la mecánica

El comportamiento completo de la mecánica de juego es el siguiente:

- Cada `Sunflower` en vez de añadir *suncoins* directamente al jugador cuando pasan 3 ciclos, añadirá un `Sun` en una casilla aleatoria.

- Además, aleatoriamente cada *5* ciclos se genera un sun en una posición aleatoria del tablero.

- Puede haber más 1 un sol en una casilla. Si hay más de uno, sólo se pinta 1.

- Sí puede haber una planta o un zombie en la misma casilla de un sol.

- Los soles estarán disponibles en el juego durante *10* ciclos. Si el usuario no coge los soles, estos desaparecerán.

- El usuario tiene que coger los soles; solo se puede coger soles (si hay mas de uno en la misma casilla), una vez por turno / ciclo. Cada sol suma *10* suncoins como en la práctica anterior.

> Aclaración: El `GamePrinter` que te proporcionamos como plantilla ya está ajustado para que en cada casi se puedan pintar 1 zombie o 1 planta y 1 sol. Si hay **más de un sol** en una casilla **sólo se pintará 1** pero **se recogerán todos** con el comando `catch`.

Los soles se representarán con el símbolo `*[XX]` (donde XX son los ciclos que le quedan antes de desaparecer). Como puede haber una planta o un zombie en la misma casilla de un sol, e incluso los zombies pueden caminar por encima de los soles, a la hora de pintar el tablero se tendrá que generar de la siguiente manera, concatenando el símbolo del sol con el del objeto de juego que haya en la casilla:

```
Number of cycles: 28
Sun coins: 10
Remaining zombies: 2
Generated suns: 18
Catched suns: 0
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
## Implementación de la mecánica

Para implementar esta parte vamos a proceder con los siguientes cambios:

- Vamos a añadir una clase `Sun`. Recuerda que los soles sólo estarán como mucho *10* ciclos en el juego.

- Los `Sunflowers`, en lugar de actualizar el número de *suncoins*, generarán un nuevo `Sun`.

- Crearemos una clase `SunsManager` que se encargará de centralizar la generación de soles aleatorios. Esta clase es similar a `ZombiesManager`. Esta acción sólo se realiza 1 vez por turno / ciclo.

- Actualiza `Game` para delegar en `SunsManager` la generación aleatoria de soles.

- Crearemos el comando `CatchCommand` que delegará en el juego para intentar coger los soles. Recuerda que esta acción sólo se puede realizar 1 vez por turno / ciclo.

- Actualiza la información del estado del juego para saber el número de `Sun`s generados y los que el jugador ha cogido. Los soles generados son aquellos que se han añadido al juego (se hayan cogido o no) y los soles cogidos son aquellos que el usuario ha cogido realmente con el comando `catch col row`

> **NOTA**: Ten en cuenta que al añadir `Sun`s al `GameObjectContainer` por lo que va a recibir inmediatamente `update()`. Ajusta su tiempo de vida para que al añadirlo al juego realmente estén *10* ciclos presentes.

<!-- TOC --><a name="detalles-de-implementación"></a>
# Detalles de implementación

A continuación te explicamos algunos detalles que te facilitarán la implementación de algunas de las nuevas funcionalidades de la aplicación.

<!-- TOC --><a name="variables-estáticas"></a>
### Variables estáticas

<!-- TOC --><a name="soles-generados-y-soles-cogidos"></a>
#### Soles generados y soles cogidos

Una información que se muestra en el juego es el número de soles que se han generado, ya sea porque los han generado los `Sunflower`s o porque lo haya generado `SunsManager`. Para llevar esta contabilidad, deberíamos de repartir la responsabilidad entre varias clases (e.g. `Sunflower` y `SunsManager`) o romper la encapsulación y abstracción si intentamos implementarla en `Game` o `GameObjectContainer` ya que tendríamos que llevar un contador que actualizar cuando los `Sun` entran en juego y cuando son cogidos.

Así que la manera de hacerlo es introducir un par de *atributos estáticos* en la clase `Sun` que nos permita llevar esta contabilidad. Estos atributos se actualizará a través de los métodos `onEnter()`, `onExit()` u otros métodos que definas en la interfaz `GameItem` para representar la acción de juego de coger el sol.

> NOTA: recuerda reiniciar los contadores cuando reinicies la partida.

En  general las variables estáticas son una manera efectiva de controlar el comportamiento de diferentes instancias de objetos de la misma clase, ya que podríamos decir que una variable estática es una variable global o compartida por todos los objetos de la clase.

<!-- TOC --><a name="catchcommand"></a>
#### CatchCommand

Siguiendo el mismo razonamiento que para los soles, necesitamos tener un atributo estático que permita controlar si ya se ha ejecutado un comando `CatchCommand` en un turno / ciclo concreto.

> NOTA: Este comando *no actualiza* el juego.

Como esta funcionalidad podría ser adecuada para otros comandos, podemos implementar un comportamiento común para todos los comandos del siguiente modo:

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

El método `Command#newCycle()` se debe invocar como parte el bucle de juego en `Game#update()`.

<!-- TOC --><a name="la-interfaz-gameactions-y-las-acciones-explosionaction"></a>
## La interfaz `GameAction`s y las acciones `ExplosionAction`

La introducción de los nuevos objetos `CherryBomb` y `ExplosiveZombie` introduce una complicación adicional en el juego y es que **es necesario gestionar explosiones encadenadas**. Esto es, si una `CherryBomb` explota matando a un `ExplosiveZombie` este a su vez también explota, por lo que puede matar a alguna planta que tenga a su alcance. A continuación mostramos el escenario descrito.

```
Number of cycles: 28
Sun coins: 130
Remaining zombies: 5
           0              1              2              3              4              5              6              7              8       
     ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── 
  0 |              |              |              |              |              |              |              |              |              
     ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── 
  1 |              |              |              |              |              |              |              |              |              
     ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── 
  2 |              |              |              |              |              |     C[02]    |              |              |              
     ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── 
  3 |              |              |              |              |     WN[04]   |    Ez[05]    |              |              |              
     ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── 
```

Para poder implementar este comportamiento tenemos que realizar dos cambios importantes en el juego:
- Eliminar los objetos muertos en `Game#update()` 1 o más veces. Es decir, hay que eliminar objetos muertos mientras haya eliminado algún objeto en la pasada anterior.
- Ejecutar acciones de juego como resultado de haber matado / o salir del juego uno de los objetos.

Si no tenemos cuidado a la hora de diseñar e implementar esta funcionalidad, complicaríamos en exceso el código de `Game` y `GameObjectContainer`. Además, es probable que repartiéramos la responsabilidad de la actualización del juego entre varias clases.

Para flexibilizar el diseño vamos a introducir la interfaz `GameAction`:

```java
public interface GameAction {
	void execute(GameWorld game);
}
```
Esta interfaz nos permitirá representar acciones de juego, como una explosión. En nuestro caso tendremos que implementar la clase `ExplosionAction` que nos permitirá implementar el comportamiento de atacar un zombie o una planta (dependiendo de qué objeto haya causado la explosión) en las casillas adyacentes a una posición concreta. En el siguiente escenario, como resultado de explotar la `CherryBomb` de la casilla (5, 2) se ven afectadas las casillas marcadas con **X**. Como resultado de la explosión, también explotará el `ExplosiveZombie` de (5, 3) afectando a las casillas marcadas con **Y**.

```
           0              1              2              3              4              5              6              7              8       
     ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── 
  0 |              |              |              |              |              |              |              |              |              
     ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── 
  1 |              |              |              |              |      X       |      X       |      X       |              |              
     ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── 
  2 |              |              |              |              |      X Y     |     C[02] Y  |      X Y     |              |              
     ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── 
  3 |              |              |              |              |   WN[04] X Y |    Ez[05] X  |      X Y     |              |              
     ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── ────────────── 
```

Para lograr este comportamiento vamos a implementar una pila de `GameAction`s que se ejecutaran al comienzo y al final de cada ciclo del juego. De este modo, cuando un objeto de juego va a explotar, en vez de ejecutarse inmediatamente, se apila una instancia de `ExplosionAction` que se ejecutará cuando sea necesario. Como resultado de esa ejecución, es posible que otros objetos apilen otros efectos, que a su vez serán ejecutados una vez finalizada la ejecución del efecto actual. Como resultado adicional, la pila de acciones de juego nos permite definir un orden consistente en la ejecución de las acciones que nos facilita la depuración de la aplicación.

Pese a que existe la clase `java.util.Stack` su propio javadoc nos indica que es más recomendable utilizar una clase que implemente la interfaz `java.util.Deque` por lo que en nuestro caso hemos decidido utilizar la clase `java.util.ArrayDeque`.

A continuación mostramos un extracto del código necesario para poder gestionar las `GameAction` dentro de `Game`.

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
# Pruebas

Hemos adaptado los casos de prueba existentes a esta práctica y **añadiremos casos de prueba adicionales** antes de la entrega de la misma.

Para facilitar la revisión de la entrega de la parte I y el desarrollo de la parte II, hemos creado el paquete `tp1.p2.pruebas.parte1` para alojar los tests según estaban en la parte I y el paquete  `tp1.p2.pruebas.parte2` que contienen los tests de la parte II.
