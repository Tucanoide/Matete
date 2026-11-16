# Feature Specification: Mi recorrido

**Feature Branch**: `003-recorrido`
**Created**: 2026-11-16
**Status**: Draft
**Input**: "En algún lugar de la app puedo ver un resumen de mi recorrido? niveles, aciertos, etc. No quiero que se vean las respuestas, sino la historia de mi juego." Alcance acordado con el dueño: resumen, avance por nivel y desafíos; **sin** actividad (días, racha, últimas palabras). Disponible también para invitados. Acceso desde la navegación del header.

## User Scenarios & Testing

### User Story 1 — Ver mi recorrido (P1)

Un jugador (invitado o con Google) abre "Mi recorrido" y ve un resumen de su juego y su avance por nivel, sin que aparezca ninguna palabra.

**Why this priority**: es el pedido completo; sin esto no hay feature.

**Independent Test**: un jugador con palabras acertadas y salteadas en dos niveles abre la página y ve los totales y una fila por nivel con los números correctos; la respuesta del servidor no contiene ninguna palabra ni ids internos.

**Acceptance Scenarios**:

1. **Given** un invitado que acertó 3 palabras (una con pista) y salteó 1 en el nivel 1, **When** abre Mi recorrido, **Then** ve nivel actual, puntos, neuronas, 3 acertadas, 1 salteada, tiempo total, promedio y mejor tiempo, 1 pista usada, 2 resueltas sin pistas, y una fila "Nivel 1" con 3 de N acertadas y 1 salteada.
2. **Given** un visitante sin cookie ni sesión, **When** abre Mi recorrido, **Then** ve una invitación a jugar y no se crea ningún usuario.
3. **Given** un invitado, **When** abre Mi recorrido, **Then** ve una invitación a entrar con Google para guardar su recorrido y figurar en el ranking, y no ve posición en el ranking.
4. **Given** un usuario de Google, **When** abre Mi recorrido, **Then** ve su posición en el ranking global.

### User Story 2 — Mis desafíos en el recorrido (P2)

Un usuario de Google con alias ve cuántos desafíos creó, cuántos jugó y cuántos ganó.

**Independent Test**: con 2 desafíos creados y 3 jugados (2 ganados, uno de ellos "ya la conocía"), la página muestra 2 / 3 / 1.

**Acceptance Scenarios**:

1. **Given** un usuario de Google con alias, **When** abre Mi recorrido, **Then** ve creados, jugados y ganados, con la misma regla de victoria que los desafíos (acertó, no la conocía y tardó menos que el creador).
2. **Given** un invitado o un usuario sin alias, **When** abre Mi recorrido, **Then** no ve la sección de desafíos.

### Edge Cases

- Jugador sin ninguna palabra terminada: totales en 0, promedio y mejor tiempo "—", sin división por cero.
- Aciertos con `timeTaken` nulo o 0 (datos viejos): cuentan como acertadas pero no entran en promedio ni mejor tiempo.
- Nivel actual sin progreso todavía: aparece igual, en 0.
- Palabras empezadas sin terminar: se informan aparte ("en curso"), no como acertadas ni salteadas.

## Requirements

### Functional Requirements

- **FR-001**: La app MUST ofrecer una página "Mi recorrido" accesible desde la navegación del header para cualquier visitante.
- **FR-002**: La página MUST mostrar nivel actual, puntos, neuronas, palabras acertadas, salteadas y en curso, tiempo total, tiempo promedio y mejor tiempo de las acertadas, pistas de letra usadas y palabras resueltas sin pistas.
- **FR-003**: La página MUST mostrar, por cada nivel con progreso y por el nivel actual, total de palabras del nivel, acertadas, salteadas, tiempo promedio y pistas usadas.
- **FR-004**: Ninguna respuesta de la página MUST incluir palabras, definiciones ni `User.id` (constitución, principios 1 y 2).
- **FR-005**: Para usuarios de Google, la página MUST mostrar su posición en el ranking global con el mismo orden que el ranking (puntos desc, tiempo asc, neuronas desc).
- **FR-006**: Para usuarios de Google con alias, la página MUST mostrar desafíos creados, jugados (terminados) y ganados.
- **FR-007**: Abrir la página MUST NOT crear usuarios ni cookies.
- **FR-008**: Para invitados, la página MUST invitar a entrar con Google.

### Key Entities

Sin entidades nuevas. Lee `User`, `Progress`, `Word`, `Level`, `Challenge` y `ChallengeAttempt`.

## Success Criteria

- **SC-001**: Un jugador encuentra su recorrido en un clic desde cualquier página.
- **SC-002**: Los números coinciden con la base (verificado por tests de integración contra Postgres).
- **SC-003**: Ninguna palabra aparece en la respuesta (verificado por test).

## Assumptions

- No hay historia de neuronas: solo se muestra el saldo actual.
- Los tiempos anteriores al deploy de 001 pueden ser imprecisos; se muestran igual.
- La página es de solo lectura y se calcula en cada visita (volumen chico: cientos de filas por jugador).
