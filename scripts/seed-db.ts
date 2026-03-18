import 'dotenv/config';
import pg from 'pg';

const SUCCESS_MESSAGES = [
  "Correcto. \nTu divergencia sigue aumentando",
  "Acertaste. \nTus engranajes se van aflojando",
  "Bien. \nYa estás con un pié fuera de la caja",
  "Superluper. \nVas quitando herrumbre a tus neuronas",
  "La pucha. \nEres una barbaridad",
  "Tremendo. \nAunque ésta era fácil, pero la próxima te saca una cana",
  "Revolución. \nTus neuronas están como locas",
  "Eres suertudo \no tu pensamiento lateral está muy desarrollado",
  "Tu mate te está funcionando muy bien",
  "Tienes el cerebro frito con aceite de lubricar motores",
  "Patapufete. \nSi sigues adivinando se nos van a acabar las palabras",
  "Sambomba. \nSe ha generado un nuevo circuito en tu cerebro",
  "Caramba que vas muy bien. Apostaría que ya puedas descubrir la traducción combinada de TheySmokeDos",
  "Sorprendente. \nTienes una gran capacidad de abandonar la lógica. Tu pareja tiene razón",
  "Te luciste. \nEstás cerca de graduarte en pensamiento divergente",
  "Caracoles. \nSe nota que has practicado en el viaje",
  "Cáspitas. \nTu cociente intelectual debería llamarse multiplicador intelectual",
  "Impactante. \nDebes tener un procesador de cuatro núcleos",
  "Chanfle. \nYa te mereces tu propia estatua del pensador",
  "Excelente. \nTe ganaste un premio Eric de aplausos y campanadas\nClap ton Clap ton",
  "Yuuujuuu. \nEres el primero en acertar esta palabra a esta hora en este lugar.\nY con esas medias!",
  "En breve tu cabeza quedará chica para la expansión que está sufriendo tu cerebro",
  "Boing boing. \nTus neuronas están dando saltos increíbles",
  "Cáspitas. \nEso fue muy rápido. Espero que estés jugando limpio. Sino ve a asearte",
  "Soberbio. \nEl pitecantropus estaría impresionado con tu evolución",
  "Lativa te queda chica. \nTu performance es superlativa",
  "Fantástico. \nYa se debe estar gestando tu propio club de fans",
  "Tu capacidad de resolución se está volviendo infinita",
  "Fenómeno. \nEvidentemente tienes un ecosistema cerebral hiper desarrollado",
  "Impresionante lo que has logrado este último tiempo",
  "Recórcholis. \nEn cualquier momento empiezas a inventar tus propios matetes",
  "Piedra libre. \nEl orden ayuda a encontrar y el caos a descubrir.\nSi, si, te ha ayudado el caos",
  "Te mereces un anterior a lo que me pertenece.\nClaro Claro: Un premio",
  "Patapufete. \nTu tienes habilidad para salir de la caja pero por favor cierra la tapa para que no se escapen otros",
  "Caramba. \nTus habilidades matetísticas dejan paticonfuso a cualquiera",
  "Tus neuronas están decididas a abandonar el blanco y negro",
  "Matete está provocando una migración masiva de neuronas. La lógica convencional ya no tiene espacio en tu cerebro",
  "Zás! \nMatete está reconfigurando tus neuronas",
  "Upalalá \nLa parte colorida de tu cerebro parece un arcoiris",
  "Santos Matetes Batman, este jugador podría ayudarnos a derrotar al Acertijo",
  "Carambolas \nResuelves muy bien los Matetes, podrías dedicarte a.... \nLo siento, es una habilidad completamente inútil",
  "Con tanto Matete ¿te parece que empiezas a sentir el movimiento de tus neuronas? \nEs extraño. Posiblemente tengas piojos",
  "El ejercicio mental te rejuvenece. Cada matete resuelto te hace ver 39 segundos más joven.",
  "Deberían nominarte para el premio intergaláctico de Matetes. Si existiera, claro.",
  "Santos tragos barman! \nA algunas personas Matete les hace crecer tanto las neuronas que le empujan el pelo hacia afuera y las confunden con el tío cosa.",
  "Resolviste hábilmente este Matete, pero tengo que advertirte que el próximo produce insomnio.",
  "Esferas de Caram! \nLogras sorprenderme con tus habilidades. \n Bah, no es cierto, pero te quería levantar el ánimo.",
  "A la little ball. \nHubiera jurado que esta la salteabas, me monja-encendiste.",
  "Si pudiste resolver este Matete, el problemita que te está dando vueltas en la cabeza es pan comido.",
  "Otro Matete resuelto es otro pequeño puente entre tus hemisferios cerebrales.",
  "A la pipetuá! Deberían inventar un superheroe en tu nombre, algo así como Supermantete.",
  "A esta altura del juego es posible que te descubras analizando tooodas las palabras buscando Matetes. El diagnóstico es Matetitis.",
  "Acertaste y te ganaste un consejo: No pidas peras al olmo. Son mucho más ricas al borgoña.",
  "Uii Uii. \nEstás haciendo rechinar la estantería de tu pensamiento lógico.",
  "Cataplún! \nLógica lógica derrumbada. Bienvenida lógica ilógica."
];

const FAIL_MESSAGES = [
  "¡Chan! \nEsa neurona se tomó el día libre.",
  "Casi... \nPero la lógica te jugó una mala pasada.",
  "¡Puf! \nTu hemisferio derecho se fue a dormir.",
  "Error de síncopa. \nInténtalo de nuevo con más ganas.",
  "¡Ups! \nSe te escapó el pensamiento lateral.",
  "Nop. \nEsa palabra no vive en este matete.",
  "¡Zopenco! (Con cariño). \nRevisa tus engranajes.",
  "Hummm... \n¿Seguro que no quieres pedir una pista?",
  "¡Plop! \nComo Condorito, te caíste de espalda.",
  "Frío, frío... \nestás en la Antártida del matete."
];

async function main() {
  const pool = new pg.Pool({ connectionString: process.env.DATABASE_URL });

  try {
    console.log('Seeding settings...');
    await pool.query(
      `INSERT INTO "Setting" ("id", "value") VALUES ($1, $2)
       ON CONFLICT ("id") DO UPDATE SET "value" = EXCLUDED."value"`,
      ['costo_pista', '5']
    );

    console.log('Seeding messages...');
    await pool.query('DELETE FROM "GameMessage"');
    
    for (const content of SUCCESS_MESSAGES) {
      await pool.query(
        'INSERT INTO "GameMessage" ("id", "content", "type") VALUES ($1, $2, $3)',
        [crypto.randomUUID ? crypto.randomUUID() : Math.random().toString(36).substring(2), content, 'success']
      );
    }

    for (const content of FAIL_MESSAGES) {
      await pool.query(
        'INSERT INTO "GameMessage" ("id", "content", "type") VALUES ($1, $2, $3)',
        [crypto.randomUUID ? crypto.randomUUID() : Math.random().toString(36).substring(2), content, 'fail']
      );
    }

    console.log('Seeding complete!');
  } catch (error) {
    console.error('Seeding failed:', error);
  } finally {
    await pool.end();
  }
}

main();
