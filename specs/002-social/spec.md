# Feature Specification: Funciones sociales (compartir, comentarios, seguir, desafíos)

**Feature Branch**: `002-social`

**Created**: 2026-11-15

**Status**: Draft

**Input**: User description: "Quisiera que los jugadores registrados puedan interactuar, tipo red social. Quiero que se arme una comunidad: la app mobile generó una comunidad en Facebook y todavía preguntan dónde se puede jugar. Hagamos (1) comentarios por palabra visibles solo después de resolverla, (2) desafíos entre amigos por link, (3) compartir el resultado estilo Wordle y (5) seguir jugadores y ranking de amigos. Las palabras creadas por la comunidad quedan para más adelante."

## Contexto

La app mobile de Matete dejó un grupo de Facebook activo que sigue preguntando dónde jugar. La versión web ya existe, pero no le da a esa comunidad nada para hacer juntos: no hay forma de mostrar un resultado, comentar una definición, competir con un amigo o seguir a alguien.

Esta feature suma cuatro funciones asincrónicas (sin chat ni tiempo real, por el límite de procesos del hosting). Se apoya en `001-hardening`: la identidad no falsificable y la validación en servidor son las que impiden que alguien comente en nombre de otro o gane un desafío con un tiempo inventado.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Compartir lo que resolví sin spoilear (Priority: P1)

Como jugador, quiero compartir mi resultado en el grupo de Facebook o por WhatsApp sin revelar la palabra, para mostrar cómo me fue e invitar a otros a jugar.

**Why this priority**: Es la función más barata y la que trae gente: cada resultado compartido lleva el link del juego al grupo que hoy pregunta dónde jugar.

**Independent Test**: Resolver una palabra, tocar "Compartir", pegar el texto en cualquier lado. Debe mostrar nivel, cantidad de letras, tiempo, pistas usadas y el link, y no debe contener la palabra ni permitir deducirla.

**Acceptance Scenarios**:

1. **Given** una palabra recién acertada, **When** el jugador toca "Compartir", **Then** se abre el menú de compartir del dispositivo o, si no existe, el texto se copia al portapapeles y se avisa "Copiado".
2. **Given** el texto generado, **When** se lo lee, **Then** contiene el nivel, una fila de casilleros con una marca distinta para cada letra revelada por pista, el tiempo y el link al juego, y no contiene la solución ni la definición.
3. **Given** el tiempo y las pistas del texto, **When** se los compara con lo que registró el servidor, **Then** coinciden (el jugador no puede compartir un tiempo distinto al real desde la interfaz).
4. **Given** un invitado sin cuenta, **When** resuelve una palabra, **Then** también puede compartir (compartir no crea nada en el servidor).

---

### User Story 2 - Comentar la palabra que acabo de resolver (Priority: P1)

Como jugador registrado, quiero leer y dejar comentarios sobre una palabra después de resolverla, para charlar sobre la definición con otros que también la resolvieron, sin arruinarle la palabra a nadie.

**Why this priority**: Es la conversación que hoy pasa en Facebook, traída al juego. Las definiciones de Matete son juegos de palabras: comentarlas es natural.

**Independent Test**: Con dos cuentas, A resuelve una palabra y comenta. B, que todavía no la resolvió, no puede ver ni pedir esos comentarios; cuando la resuelve, los ve. Un comentario reportado por tres personas deja de verse.

**Acceptance Scenarios**:

1. **Given** una palabra recién acertada, **When** el jugador abre "Comentarios", **Then** ve los comentarios visibles de esa palabra, del más nuevo al más viejo, con el nombre de usuario, la foto y hace cuánto se escribió.
2. **Given** una palabra que el jugador no acertó (en juego, salteada o de otro nivel), **When** pide sus comentarios por cualquier medio, **Then** el servidor no devuelve ninguno, ni siquiera la cantidad.
3. **Given** un jugador registrado con nombre de usuario que acertó la palabra, **When** escribe un comentario de 1 a 280 caracteres, **Then** se publica y aparece primero en la lista.
4. **Given** un invitado o un registrado sin nombre de usuario, **When** abre los comentarios, **Then** puede leerlos pero, en lugar del campo para escribir, ve una invitación a ingresar con Google o a elegir nombre de usuario.
5. **Given** un jugador que acaba de comentar, **When** intenta comentar otra vez antes de 30 segundos, o supera 20 comentarios en 24 horas, **Then** se rechaza con un mensaje claro y no se publica.
6. **Given** un comentario ajeno, **When** un jugador registrado lo reporta, **Then** queda registrado una sola vez por persona, y al llegar a 3 reportes de personas distintas el comentario deja de verse para todos.
7. **Given** un comentario propio, **When** su autor lo borra, **Then** deja de verse para todos.
8. **Given** el administrador, **When** entra a la página de moderación, **Then** ve los comentarios reportados u ocultos con su cantidad de reportes y puede ocultarlos o volver a mostrarlos.

---

### User Story 3 - Seguir jugadores y competir con mis amigos (Priority: P2)

Como jugador registrado, quiero seguir a otros jugadores y ver un ranking solo con ellos y conmigo, para competir con gente que conozco en lugar de con el top 10 global.

**Why this priority**: Le da sentido al ranking para la mayoría, que nunca va a estar en el top global. Depende de que haya gente registrada (lo que traen US1 y US2).

**Independent Test**: A sigue a B buscándolo por nombre de usuario. La pestaña "Amigos" del ranking muestra a A y a B ordenados por puntos. A deja de seguir a B y B desaparece de la pestaña.

**Acceptance Scenarios**:

1. **Given** un jugador registrado, **When** busca un nombre de usuario existente en la pestaña "Amigos", **Then** lo encuentra y puede seguirlo.
2. **Given** el ranking global o de nivel, **When** un jugador registrado lo mira, **Then** cada fila de otro jugador con nombre de usuario tiene un botón "Seguir" o "Siguiendo".
3. **Given** un jugador que sigue a otros, **When** abre la pestaña "Amigos", **Then** ve a esas personas y a sí mismo, ordenados por puntos y luego por menor tiempo total, con su propia fila resaltada.
4. **Given** un jugador que sigue a alguien, **When** toca "Siguiendo", **Then** lo deja de seguir y desaparece de su ranking de amigos.
5. **Given** cualquier jugador, **When** intenta seguirse a sí mismo, seguir a alguien que no existe o a un invitado, **Then** se rechaza.
6. **Given** un invitado, **When** abre la pestaña "Amigos", **Then** ve una invitación a ingresar con Google.

---

### User Story 4 - Desafiar a un amigo con una palabra (Priority: P2)

Como jugador registrado, quiero mandarle a un amigo un link con una palabra que acabo de resolver, para ver si la resuelve más rápido que yo.

**Why this priority**: Es la función más competitiva y la que más invita a volver, pero también la más compleja. Se apoya en compartir (US1) para distribuir el link.

**Independent Test**: A resuelve una palabra en 42 segundos y crea un desafío. B abre el link, ingresa con Google, resuelve la palabra en 30 segundos. Los dos ven en la página del desafío que B ganó, con tiempos medidos por el servidor.

**Acceptance Scenarios**:

1. **Given** una palabra recién acertada por un jugador registrado con nombre de usuario, **When** toca "Desafiar", **Then** obtiene un link único y el texto para compartirlo, con su tiempo y sin la palabra.
2. **Given** el link de un desafío, **When** cualquiera lo abre (incluso sin cuenta), **Then** ve quién desafía, la cantidad de letras, el tiempo a superar y el resultado de quienes ya jugaron, sin la definición ni la palabra. La vista previa en Facebook o WhatsApp muestra el nombre del desafiante y el tiempo.
3. **Given** el link abierto por alguien sin sesión de Google, **When** quiere aceptar, **Then** se le pide ingresar con Google y vuelve al mismo desafío.
4. **Given** un jugador registrado que acepta, **When** empieza, **Then** ve la definición y el pool de letras; el tiempo corre desde ese momento y lo mide el servidor.
5. **Given** un desafío en curso, **When** el jugador escribe la palabra correcta, **Then** ve su tiempo, el del desafiante y si ganó; el resultado queda en la página del desafío.
6. **Given** un desafío en curso, **When** el jugador se rinde, **Then** se registra como no resuelto y se le muestra la palabra.
7. **Given** un jugador que ya jugó ese desafío, **When** vuelve al link, **Then** ve el resultado y no puede jugarlo de nuevo.
8. **Given** el creador de un desafío, **When** abre su propio link, **Then** ve los resultados pero no puede jugarlo.
9. **Given** un jugador que ya había acertado esa palabra en el juego normal, **When** juega el desafío, **Then** puede hacerlo, pero su resultado se marca "ya la conocía" y no cuenta como victoria.
10. **Given** un desafío, **When** se juega, **Then** no se cobran ni se acreditan neuronas ni puntos, no hay pistas ni salto, y el progreso del juego normal no cambia.
11. **Given** un jugador registrado, **When** abre "Mis desafíos", **Then** ve los desafíos que creó y los que jugó, con la cantidad de participantes y su resultado.

---

### Edge Cases

- Alguien comparte o desafía con una palabra que todavía no acertó (pidiéndolo a mano al servidor): se rechaza.
- Un comentario contiene solo espacios, saltos de línea de más o supera 280 caracteres después de recortar espacios: se rechaza o se normaliza antes de guardar.
- Un comentario contiene HTML o scripts: se muestra como texto plano, nunca se interpreta.
- Dos reportes simultáneos del mismo jugador sobre el mismo comentario: cuenta uno.
- Un jugador reporta su propio comentario: se rechaza (para eso puede borrarlo).
- Un jugador cambia su nombre de usuario (hoy no es posible desde la interfaz): sus seguidores lo siguen igual, porque el seguimiento no depende del nombre.
- Se pide un desafío con un código inexistente o mal formado: página "Este desafío no existe".
- El jugador abre el desafío en dos pestañas: es un solo intento; el tiempo corre desde la primera apertura.
- El jugador abre el desafío, lo deja y vuelve al día siguiente: el tiempo sigue corriendo desde la primera apertura (no hay pausa).
- Un invitado con progreso crea cuenta con Google: sus palabras acertadas pasan a la cuenta (001) y con ellas el acceso a sus comentarios.
- Se juega un desafío sobre una palabra de un nivel que el jugador todavía no alcanzó: está permitido; si después la encuentra en el juego normal, la juega igual (la conoce).
- Se intenta seguir a más de 500 jugadores: se rechaza.

## Requirements *(mandatory)*

### Functional Requirements

**Compartir (US1)**

- **FR-001**: Tras acertar una palabra, el sistema MUST ofrecer "Compartir", que usa el menú nativo del dispositivo si existe y, si no, copia el texto al portapapeles y lo confirma.
- **FR-002**: El texto compartido MUST incluir el nivel, una fila de casilleros (uno por letra, marcando distinto las reveladas por pista), el tiempo, la cantidad de pistas y el link al juego, y MUST NOT incluir la solución ni la definición.
- **FR-003**: El tiempo y las pistas del texto MUST ser los que registró el servidor para ese acierto.
- **FR-004**: Compartir MUST estar disponible también para invitados.

**Comentarios (US2)**

- **FR-005**: El sistema MUST devolver los comentarios de una palabra únicamente a quien la acertó; a cualquier otro MUST NOT devolverle comentarios ni su cantidad.
- **FR-006**: Solo un jugador registrado con nombre de usuario que acertó la palabra MUST poder publicar un comentario en ella.
- **FR-007**: El sistema MUST validar en el servidor que el comentario tenga entre 1 y 280 caracteres tras recortar espacios y colapsar más de dos saltos de línea seguidos.
- **FR-008**: El sistema MUST rechazar un comentario si su autor publicó otro hace menos de 30 segundos o si ya publicó 20 en las últimas 24 horas, también ante pedidos simultáneos.
- **FR-009**: Los comentarios MUST mostrarse del más nuevo al más viejo, de a 20, con nombre de usuario, foto y fecha relativa, y siempre como texto plano.
- **FR-010**: Un jugador registrado MUST poder reportar un comentario ajeno una sola vez; al acumular 3 reportes de personas distintas, el comentario MUST ocultarse automáticamente.
- **FR-011**: El autor MUST poder borrar su propio comentario; un comentario borrado no vuelve a mostrarse.
- **FR-012**: El sistema MUST ofrecer a los administradores (lista configurada fuera del código, por email de Google) una página de moderación con los comentarios reportados u ocultos y la acción de ocultar o volver a mostrar cada uno. A cualquier otra persona MUST negarle esa página y esas acciones.
- **FR-013**: Volver a mostrar un comentario ocultado por reportes MUST impedir que se oculte de nuevo automáticamente con los reportes ya existentes.

**Seguir y ranking de amigos (US3)**

- **FR-014**: Un jugador registrado MUST poder seguir y dejar de seguir a otro jugador registrado que tenga nombre de usuario, identificándolo por ese nombre. Seguir es unilateral y no requiere aceptación.
- **FR-015**: El sistema MUST rechazar seguirse a sí mismo, seguir a un usuario inexistente o sin cuenta de Google, y superar 500 seguidos. Seguir dos veces a la misma persona MUST NOT duplicar nada.
- **FR-016**: El sistema MUST ofrecer una búsqueda de jugadores por prefijo de nombre de usuario (mínimo 2 caracteres, máximo 10 resultados).
- **FR-017**: El ranking MUST tener una pestaña "Amigos" con el jugador y sus seguidos, ordenados por puntos descendentes y tiempo total ascendente, con la fila propia resaltada.
- **FR-018**: Las filas del ranking global y de nivel MUST indicar, para un jugador registrado, si sigue a cada jugador con nombre de usuario, y permitir seguir o dejar de seguir desde ahí.
- **FR-019**: El sistema MUST NOT exponer a quién sigue cada jugador ni quiénes lo siguen a terceros, ni ningún identificador interno de usuario.

**Desafíos (US4)**

- **FR-020**: Un jugador registrado con nombre de usuario MUST poder crear un desafío sobre una palabra que acertó; el desafío guarda el tiempo y las pistas que el servidor registró en ese acierto. Crear dos veces el desafío de la misma palabra MUST devolver el mismo link.
- **FR-021**: Cada desafío MUST tener un link con un código aleatorio no adivinable; el sistema MUST limitar a 20 los desafíos creados por jugador en 24 horas.
- **FR-022**: La página del desafío MUST mostrar a cualquiera el nombre del desafiante, la cantidad de letras, su tiempo y los resultados de los participantes, y MUST NOT mostrar la definición ni la solución a quien no lo jugó. Su vista previa al compartirla MUST mostrar el desafiante y el tiempo.
- **FR-023**: Jugar un desafío MUST requerir sesión de Google; sin sesión, la página MUST ofrecer ingresar y volver al mismo desafío.
- **FR-024**: Cada jugador MUST tener un solo intento por desafío; el tiempo MUST medirlo el servidor desde la primera vez que se le entrega la palabra del desafío hasta el acierto.
- **FR-025**: El intento MUST validarse en el servidor igual que en el juego normal; un intento incorrecto no tiene costo. El jugador MUST poder rendirse; al hacerlo o al acertar, se le muestra la palabra.
- **FR-026**: El creador MUST NOT poder jugar su propio desafío.
- **FR-027**: Si el jugador ya había acertado esa palabra en el juego normal antes de empezar el desafío, su resultado MUST marcarse "ya la conocía" y MUST NOT contar como victoria.
- **FR-028**: Jugar un desafío MUST NOT modificar neuronas, puntos, tiempo total, nivel ni progreso del juego normal, y MUST NOT ofrecer pistas ni salto.
- **FR-029**: Un participante gana si acertó, no la conocía y su tiempo es menor que el del desafiante. Los resultados MUST ordenarse: aciertos primero, por menor tiempo.
- **FR-030**: Un jugador registrado MUST poder ver "Mis desafíos": los que creó (con cantidad de participantes) y los que jugó (con su resultado), los más recientes primero.

**Transversales**

- **FR-031**: Toda acción social MUST resolver la identidad en el servidor (sesión de Google o cookie firmada de 001) y MUST NOT recibir ni devolver identificadores internos de usuario.
- **FR-032**: Todo texto de la interfaz nueva MUST estar en español rioplatense.
- **FR-033**: Ninguna función nueva MUST usar conexiones persistentes ni consultas periódicas automáticas: los datos se cargan al abrir cada panel o página.
- **FR-034**: El juego normal (modos, pistas, saltos, niveles, ranking global y de nivel) MUST seguir funcionando igual.

### Key Entities

- **Comentario**: texto de un jugador registrado sobre una palabra. Tiene autor, palabra, texto, fecha y estado (visible, oculto por reportes, oculto por admin, borrado por su autor), y si un admin lo restauró.
- **Reporte**: marca de un jugador sobre un comentario ajeno. Uno por jugador y comentario.
- **Seguimiento**: relación unilateral "A sigue a B" entre jugadores registrados, con fecha.
- **Desafío**: palabra propuesta por un jugador, con código público, tiempo y pistas del desafiante y fecha.
- **Intento de desafío**: participación de un jugador en un desafío. Tiene inicio, fin, tiempo, si acertó, si se rindió y si ya conocía la palabra. Uno por jugador y desafío.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Desde el acierto, compartir el resultado lleva como máximo 2 toques.
- **SC-002**: En el 100 % de los casos probados, ni el texto compartido, ni la página pública del desafío, ni ninguna respuesta del servidor a quien no acertó una palabra contienen su solución o sus comentarios.
- **SC-003**: Un comentario con 3 reportes de personas distintas deja de verse en la siguiente carga de cualquier jugador.
- **SC-004**: El resultado de un desafío lo decide exclusivamente el tiempo medido en el servidor; ningún valor enviado por el navegador lo modifica.
- **SC-005**: Jugar un desafío deja intactos neuronas, puntos, tiempo total, nivel y progreso de quien lo juega.
- **SC-006**: Cada panel o página social carga en menos de 1 segundo con 1000 comentarios en una palabra o 500 seguidos.
- **SC-007**: Las suites existentes de 001 siguen en verde (el juego normal no cambió).

## Assumptions

- **Compartir lo pueden usar invitados.** La constitución reserva lo social a usuarios de Google, pero compartir no crea datos en el servidor ni expone a nadie: es el canal para traer al grupo de Facebook. Comentar, seguir y desafiar sí requieren Google.
- **Jugar un desafío requiere Google.** Mantiene la regla de la constitución y evita que se creen invitados descartables para inflar resultados. Si frena la llegada desde Facebook, se puede revisar después.
- **"Resolver" significa acertar.** Una palabra salteada no habilita ver sus comentarios ni desafiar con ella.
- **Se comenta solo desde la pantalla de acierto.** No hay historial de palabras resueltas para volver a sus comentarios después; queda para más adelante.
- **Sin respuestas, likes ni menciones** en comentarios. Sin notificaciones: los resultados de un desafío se ven entrando a su página o a "Mis desafíos".
- **Los desafíos no vencen.** Se pueden jugar mientras exista el link.
- **Conocer la palabra por un desafío** no cambia el juego normal: si después aparece en su nivel, se juega igual.
- **El administrador** es el dueño del juego; se configura por email en el entorno del servidor, sin pantalla para gestionarlo.
- **El texto compartido usa el dominio público del juego** desde donde se juega.
- **Fuera de alcance:** palabras creadas por la comunidad, chat, perfiles públicos, notificaciones, bloqueo entre jugadores, edición de comentarios y cambio de nombre de usuario.
