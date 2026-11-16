# Feature Specification: Hardening de seguridad y bugs de producción

**Feature Branch**: `001-hardening`

**Created**: 2026-11-15

**Status**: Draft

**Input**: User description: "001-hardening: Endurecer la seguridad y corregir los bugs de producción de Matete antes de sumar funciones sociales. Alcance S-1, S-2, S-3, S-4, S-5, B-1/B-2 y B-3 del CODE_REVIEW.md. Fuera de alcance: costos de Level (B-4), funciones sociales, refactor de GameEngine. La experiencia de juego no debe cambiar."

## Contexto

Matete va a sumar funciones sociales (comentarios, desafíos entre amigos, ranking de amigos) para recuperar la comunidad que dejó la app mobile. Esas funciones amplifican cualquier falla de identidad o de puntaje: si una cuenta se puede suplantar, alguien comenta en nombre de otro; si los puntos se pueden inflar, los desafíos y el ranking pierden sentido. Esta feature cierra esas fallas primero.

Hoy, según el code review del 2026-08-06 verificado contra producción:

- Cualquiera que conozca el identificador interno de otro jugador puede tomar su cuenta, y esos identificadores se publican en el ranking.
- La solución de cada palabra viaja al navegador y la validación ocurre ahí.
- Un acierto se puede acreditar infinitas veces: 364 de los 949 puntos totales (38 %) no corresponden a ninguna palabra adivinada.
- El tiempo de resolución lo informa el navegador.
- Al loguearse con Google, un invitado con progreso hace fallar la migración, y cada recarga vuelve a sumar sus puntos. Afecta a 35 de los 46 invitados actuales.
- La estructura de la base no se puede reconstruir desde el repositorio.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Mi cuenta no la puede usar otra persona (Priority: P1)

Como jugador (invitado o registrado), quiero que nadie pueda hacerse pasar por mí manipulando datos de su navegador, para que mi progreso, mis puntos y mi nombre en el ranking sean míos.

**Why this priority**: Es la falla más grave (suplantación de cuenta) y es prerrequisito de toda función social, porque ahí la identidad pasa a ser pública.

**Independent Test**: Tomar los datos que la app expone públicamente (ranking, respuestas de cualquier pantalla) y la credencial de invitado de un jugador A; intentar desde otro navegador fabricar o alterar la credencial para operar como un jugador B. Debe fallar en todos los casos.

**Acceptance Scenarios**:

1. **Given** un jugador invitado con progreso, **When** alguien copia la credencial de invitado y le altera cualquier carácter, **Then** la app la rechaza y trata al visitante como uno nuevo, sin acceso al progreso de ningún jugador existente.
2. **Given** el ranking global y el de nivel visibles, **When** se inspecciona todo lo que la app envía al navegador, **Then** no aparece ningún identificador interno de usuario.
3. **Given** un jugador registrado con Google, **When** alguien presenta una credencial de invitado que apunta a su cuenta, **Then** la app no le da acceso a esa cuenta.
4. **Given** un invitado que ya jugaba antes de este cambio, **When** vuelve a entrar dentro de los 30 días posteriores al despliegue, **Then** conserva su progreso y su credencial pasa a ser verificable.
5. **Given** una credencial antigua que apunta a una cuenta registrada, **When** se presenta en cualquier momento, **Then** se rechaza.

---

### User Story 2 - Los puntos reflejan lo que realmente jugué (Priority: P1)

Como jugador, quiero que los puntos, las neuronas y los tiempos del ranking reflejen palabras realmente resueltas, para que competir tenga sentido.

**Why this priority**: Sin esto, el ranking actual ya está distorsionado y los desafíos entre amigos serían trampeables en minutos.

**Independent Test**: Resolver una palabra y luego intentar, sin volver a resolverla, que se acredite de nuevo (repitiendo el pedido, enviando una respuesta inventada o informando un tiempo falso). Los totales del jugador no deben cambiar.

**Acceptance Scenarios**:

1. **Given** una palabra en juego, **When** el jugador inspecciona todo lo que recibió su navegador antes de resolverla, **Then** no puede reconstruir la solución completa (solo ve la definición, la cantidad de letras, el pool de letras desordenado y las letras ya reveladas por pista).
2. **Given** una palabra en juego, **When** el jugador completa los casilleros con la respuesta correcta, **Then** ve el mismo festejo de siempre y recibe +10 neuronas y +1 punto una sola vez.
3. **Given** una palabra en juego, **When** completa los casilleros con una respuesta incorrecta, **Then** ve el mismo mensaje de error de siempre y sus totales no cambian.
4. **Given** una palabra ya acertada, **When** se repite el pedido de acreditación cualquier cantidad de veces, **Then** los totales del jugador quedan igual que tras el primer acierto.
5. **Given** una palabra resuelta, **When** se registra el tiempo, **Then** el tiempo es el transcurrido desde que la palabra se le entregó al jugador, sin importar lo que informe el navegador.
6. **Given** un jugador con saldo justo para una pista, **When** pide dos pistas al mismo tiempo, **Then** se cobra como máximo una y el saldo nunca queda negativo.

---

### User Story 3 - Loguearme con Google no rompe ni duplica mi progreso (Priority: P2)

Como invitado que decide registrarse con Google, quiero que mi progreso pase a mi cuenta una sola vez y completo, para no perder lo jugado ni quedar con puntos que no gané.

**Why this priority**: Es el camino natural de invitado a registrado, que es justamente lo que exigen las funciones sociales. Hoy falla para 35 de 46 invitados.

**Independent Test**: Jugar como invitado (acertar, saltar, pedir pistas), loguearse con Google y recargar la página varias veces. La cuenta debe quedar con el progreso del invitado sumado una sola vez.

**Acceptance Scenarios**:

1. **Given** un invitado con palabras acertadas y salteadas, **When** se loguea con Google por primera vez, **Then** su cuenta registrada contiene ese progreso, sus puntos y su tiempo, y el usuario invitado deja de existir.
2. **Given** una migración ya completada, **When** el jugador recarga la página o vuelve a loguearse, **Then** los totales no cambian.
3. **Given** un invitado que gastó neuronas por debajo del saldo inicial, **When** se migra, **Then** la cuenta registrada no pierde neuronas.
4. **Given** una falla a mitad de la migración, **When** se reintenta, **Then** el resultado final es el mismo que si no hubiera fallado (nada queda sumado a medias).
5. **Given** un invitado y una cuenta registrada que resolvieron la misma palabra, **When** se migra, **Then** esa palabra cuenta una sola vez.

---

### User Story 4 - La app se puede volver a instalar desde cero (Priority: P3)

Como responsable de Matete, quiero poder levantar la base de datos desde cero solo con lo que está en el repositorio, para recuperar el servicio o montar un ambiente de pruebas sin depender del estado actual de producción.

**Why this priority**: No afecta al jugador hoy, pero sin esto no hay forma confiable de probar las funciones sociales ni de recuperarse de una pérdida de la base.

**Independent Test**: Crear una base vacía, aplicar las migraciones del repositorio, cargar los datos de juego y jugar una partida completa.

**Acceptance Scenarios**:

1. **Given** una base vacía, **When** se aplican las migraciones del repositorio, **Then** la estructura resultante es equivalente a la de producción y la app funciona (juego, pistas, username, mensajes, configuración).
2. **Given** la base de producción actual, **When** se aplica el proceso de migración, **Then** no se pierde ni altera ningún dato existente.

---

### Edge Cases

- Credencial de invitado vacía, malformada, expirada o firmada con otra clave: se trata como visitante nuevo, sin error visible.
- Credencial de invitado válida que apunta a un usuario borrado (por ejemplo, tras una migración a Google): se descarta y se trata como visitante nuevo.
- Intento enviado para una palabra que no es la que el jugador tiene en juego, de otro nivel, inexistente o ya resuelta: se rechaza sin acreditar nada.
- Intento con longitud distinta a la de la palabra: se rechaza como incorrecto.
- Mayúsculas/minúsculas: la comparación no distingue mayúsculas, igual que hoy.
- Pista de letra en una palabra con todas las letras ya reveladas o con el límite de 2 alcanzado: se rechaza sin cobrar (comportamiento actual).
- Saltar una palabra ya acertada: se rechaza sin cobrar.
- Jugador que abre la misma palabra en dos pestañas: el tiempo se mide desde la primera entrega y el acierto se acredita una sola vez.
- Palabra entregada antes del despliegue de este cambio (sin momento de entrega registrado): el tiempo se mide desde el primer pedido posterior al despliegue.
- Migración invitado→Google cuando el invitado no tiene progreso: se borra el invitado y la credencial sin error.
- Pedidos concurrentes de migración (dos pestañas que se loguean a la vez): la migración ocurre una sola vez.

## Requirements *(mandatory)*

### Functional Requirements

**Identidad (S-1)**

- **FR-001**: La credencial de invitado DEBE ser verificable por el servidor de modo que no pueda fabricarse ni alterarse sin conocer un secreto del servidor.
- **FR-002**: La credencial de invitado NO DEBE ser legible ni modificable por el código que corre en el navegador.
- **FR-003**: Una credencial de invitado DEBE dar acceso únicamente a usuarios invitados; nunca a una cuenta registrada.
- **FR-004**: Los invitados que ya jugaban antes del cambio DEBEN conservar su progreso mediante una transición de 30 días desde el despliegue: durante ese plazo, una credencial antigua (sin firmar) se acepta solo si apunta a un usuario invitado, nunca a una cuenta registrada, y se reemplaza en ese mismo momento por una credencial verificable. Vencido el plazo, las credenciales antiguas se rechazan.
- **FR-005**: Ninguna respuesta enviada al navegador (ranking incluido) DEBE contener identificadores internos de usuario.

**Validación de la respuesta (S-2)**

- **FR-006**: El navegador NO DEBE recibir la solución de una palabra no resuelta. Recibe la definición, la cantidad de letras, un pool de 14 letras desordenado (letras de la solución más distractores) y las letras reveladas por pista con su posición.
- **FR-007**: El servidor DEBE ser el único que decide si un intento es correcto, y DEBE responder correcto o incorrecto sin revelar qué letras fallaron.
- **FR-008**: Solo un intento correcto validado por el servidor DEBE acreditar puntos y neuronas.
- **FR-009**: Una pista de letra DEBE devolver la letra revelada y su posición, ya que el navegador no conoce la solución.
- **FR-010**: Tras un acierto, el navegador PUEDE recibir la solución completa para mostrarla.

**Acreditación y economía (S-3, S-5)**

- **FR-011**: Acreditar un acierto DEBE ser idempotente: una palabra suma +10 neuronas y +1 punto como máximo una vez por jugador.
- **FR-012**: El cobro de pistas y saltos DEBE ser atómico: el saldo nunca queda negativo, aun con pedidos simultáneos.
- **FR-013**: El servidor DEBE rechazar sin cobrar las pistas y saltos sobre palabras ya acertadas o salteadas.
- **FR-014**: Los valores de recompensa y de costo vigentes (+10/+1 por acierto, costo de pista desde la configuración actual, 15 por salto) NO cambian en esta feature.

**Tiempo (S-4)**

- **FR-015**: El servidor DEBE registrar el momento en que entrega cada palabra a un jugador.
- **FR-016**: El tiempo de resolución DEBE calcularse en el servidor como la diferencia entre el acierto y esa entrega; cualquier tiempo informado por el navegador se ignora.
- **FR-017**: El temporizador visible en pantalla PUEDE seguir corriendo en el navegador como referencia visual.

**Migración invitado→Google (B-1, B-2)**

- **FR-018**: La migración DEBE completarse sin error cuando el invitado tiene progreso.
- **FR-019**: La migración DEBE ser atómica: o se aplica completa (progreso, puntos, tiempo, nivel, neuronas, borrado del invitado) o no se aplica nada.
- **FR-020**: La migración DEBE ocurrir como máximo una vez por invitado, aunque se recargue la página o haya pedidos simultáneos.
- **FR-021**: Las neuronas transferidas DEBEN ser el excedente sobre el saldo inicial, con piso cero: la cuenta registrada nunca pierde neuronas por migrar.
- **FR-022**: Si invitado y cuenta registrada resolvieron la misma palabra, DEBE contarse una sola vez en puntos y en tiempo.

**Base de datos reproducible (B-3)**

- **FR-023**: Las migraciones del repositorio DEBEN producir, sobre una base vacía, una estructura equivalente a la de producción.
- **FR-024**: Aplicar las migraciones a la base de producción NO DEBE perder ni alterar datos existentes.
- **FR-025**: El saldo inicial de neuronas DEBE ser uno solo en todo el sistema: 50, el valor que hoy rige en producción.

**Datos históricos**

- **FR-026**: Los puntos de cada jugador en producción DEBEN recalcularse una única vez para que sean iguales a la cantidad de palabras que realmente acertó. El tiempo total se recalcula igual (suma de los tiempos de esas palabras), porque sufrió el mismo defecto y se deriva exactamente de lo jugado. Antes de recalcular DEBE existir un respaldo de los valores anteriores.
- **FR-027**: Las neuronas acumuladas NO se recalculan: no hay registro de los gastos y cualquier cálculo sería aproximado.

**Sin cambios para el jugador**

- **FR-028**: Los modos pool y teclado, las pistas, los saltos, los niveles, el modal de Level Up, el ranking, los sonidos y los mensajes DEBEN comportarse igual que antes para un jugador que juega honestamente.

### Key Entities

- **Credencial de invitado**: prueba de identidad de un visitante no registrado. Verificable por el servidor, invisible para el código del navegador, con vencimiento.
- **Jugador (User)**: invitado o registrado. Tiene puntos, neuronas, nivel y tiempo total. Su identificador interno nunca sale del servidor.
- **Progreso (Progress)**: relación jugador–palabra. Registra cuándo se entregó la palabra, si fue acertada o salteada, las letras reveladas y el tiempo calculado por el servidor.
- **Palabra (Word)**: su solución solo sale del servidor una vez resuelta.
- **Migración de invitado**: operación única que funde un invitado en una cuenta registrada.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 0 formas conocidas de operar como otro jugador a partir de datos visibles en el navegador (verificado con los escenarios de la User Story 1).
- **SC-002**: 0 palabras no resueltas cuya solución completa pueda obtenerse inspeccionando lo que recibe el navegador.
- **SC-003**: Repetir un acierto 100 veces deja los totales del jugador idénticos a los de un único acierto.
- **SC-004**: Después del despliegue y del recálculo, para el 100 % de los jugadores, los puntos coinciden exactamente con las palabras acertadas (hoy la diferencia total es de 364 puntos).
- **SC-008**: Ningún invitado que haya jugado en los 30 días previos al despliegue pierde su progreso al volver.
- **SC-005**: El 100 % de las migraciones invitado→Google con progreso terminan bien, y recargar 10 veces después no cambia los totales.
- **SC-006**: Una base nueva se levanta desde el repositorio y permite jugar una partida completa sin pasos manuales sobre la estructura.
- **SC-007**: Un jugador honesto no nota diferencias: la respuesta a un intento sigue siendo percibida como inmediata (menos de 1 segundo en condiciones normales) y todos los tests de experiencia de juego existentes siguen pasando.

## Assumptions

- Las funciones sociales y el ranking siguen siendo solo para usuarios registrados con Google; los invitados no aparecen en el ranking (comportamiento actual).
- El pool de 14 letras revela qué letras forman la palabra pero no su orden; se acepta como parte del diseño del juego.
- La comparación del intento conserva la regla actual (no distingue mayúsculas). Si existen palabras con tildes, se mantiene el tratamiento actual.
- Los costos de pista y salto siguen como hoy; conectarlos a los valores de cada nivel (B-4) es una feature aparte.
- El saldo inicial queda en 50 porque es el valor que rige en producción y en el schema; los 150 de la migración vieja y del fallback de sesión son residuos.
- El despliegue es a la misma infraestructura actual (Hostinger) y no se agregan servicios externos.
- La verificación de la User Story 4 se hace contra una copia o ambiente de prueba, nunca aplicando cambios experimentales sobre producción.
