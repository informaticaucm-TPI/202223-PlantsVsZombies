# Preguntas Frecuentes

## Práctica 2

### ¿En qué orden actúan los objetos del juego?

En la P2 los objetos del juego se actualizan por orden de creación (el contenedor de objetos ha de encargarse de mantener ese orden). Por ejemplo, en un ciclo en el que un zombie pueda atacar a un girasol, el girasol generará soles si y sólo si ha sido creado en el mismo ciclo en el que apareció el zombie (porque las acciones del usuario son anteriores a las de la máquina) o en uno anterior.

### PlantFactory#getAvailablePlants() vs PlantFactory#listPlants()

En la plantilla que tenéis disponible hemos optado como decisión de diseño devolver una lista no modificable (Utilizando `[Collections#unmodifiableList(java.util.List)](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Collections.html#unmodifiableList(java.util.List))`) para que podáis generar la salida desde el comando `ListPlantsCommand`.

Otra opción podría ser implementar un método `PlantFactory#listPlants()` que se encargaría de generar directamente la cadena que se utiliza directamente en `ListPlantsCommand`. Esta solución tiene la ventaja de evitar potenciales problemas con los elementos de la lista (que sí que son modificables), pero tiene la desventaja de que le estamos asignando una responsabilidad adicional y específica del juego a la factoría.

## Práctica 1

### ¿Los Peashooter comienzan a disparar al entrar en juego?

Sí, siguiendo un comportamiento similar al juego original, los `Peashooters` empiezan a disparar justo al entrar en juego.

### ¿Los zombies se añaden fuera del tablero?

Sí, como en el juego original, los zombies aparecen fuera del tablero. En la P1, no los vamos visualizar los ciclos que están fuera del tablero, pero a partir de la P2, vamos a poder visualizar (tests incluidos) cómo aparecen fuera del tablero de juego.

### ¿Qué tiene que ocurrir en una situación en la que haya un zombie y un peashooter que pueden dañarse mutuamente?

Esta situación puede darse cuando el zombie y el peashooter están en casillas adyacentes de la misma fila. En esta primera práctica los peashooter tienen prioridad respecto de los zombies, es decir, actúan antes (se actualizan antes). Por lo tanto, lo primero que ocurre es que el peashooter ataca al zombie. Solo después de este ataque puede el zombie atacar al peashooter. En concreto, si el zombie muere como resultado del ataque del peashooter ya no puede atacar.

### Al probar los casos de prueba nos hemos dado cuenta de que los zombies y los sunflowers no cuentan como ciclo para sus acciones el ciclo en el que se crean. Por ejemplo, el sunflower la primera vez tarda 4 en vez de 3 ciclos en generar soles. ¿Tiene que ser así?

Así es. Sunflowers y zombies no cuentan el ciclo en el que se crean.

### Al resetear el juego, ¿el comportamiento aleatorio tiene que ser igual que al principio?

Sí, es necesario reutilizar la semilla (seed), es decir, hay que volver a crear el objeto Random con la misma semilla (`this.random = new Random(this.seed)`).

### Para que ganen los zombies tienen que estar, por así decirlo, en la columna -1, ¿no? Es decir, no basta con llegar a la columna más a la izquierda sino pasarla.

Así es, los zombies ganan cuando son capaces de salir del tablero por la izquierda del mismo.

### Se podría dar la situación en la que todas las posiciones de la última columna están ocupadas por plantas y no se puedan generar zombies?

Puede darse la situación en la que la última columna esté ocupada por plantas pero esa situación no bloquea a los zombies. Los zombies se crean fuera del tablero y desde fuera del tablero podrían atacar a las plantas en la última columna.
