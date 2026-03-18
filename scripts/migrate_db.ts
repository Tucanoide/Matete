import "dotenv/config";
import pg from 'pg';
import initSqlite from 'sqlite3';
import { promisify } from 'util';

async function migrate() {
  const db = new initSqlite.Database('./Matete B4A/bases/BaseJv57SEc.sqlite');
  const all = promisify(db.all.bind(db));
  
  const pool = new pg.Pool({ connectionString: process.env.DATABASE_URL });

  try {
    console.log('Starting direct migration...');

    // Migrate Levels
    const levels: any = await all('SELECT * FROM Niveles');
    console.log(`Found ${levels.length} levels in SQLite.`);
    
    for (const level of levels) {
      await pool.query(
        `INSERT INTO "Level" ("id", "name", "description", "costJump", "costLetter", "costHelp", "messageStart", "messageEnd")
         VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
         ON CONFLICT ("id") DO UPDATE SET
          "name" = EXCLUDED."name",
          "description" = EXCLUDED."description",
          "costJump" = EXCLUDED."costJump",
          "costLetter" = EXCLUDED."costLetter",
          "costHelp" = EXCLUDED."costHelp",
          "messageStart" = EXCLUDED."messageStart",
          "messageEnd" = EXCLUDED."messageEnd"`,
        [
          level.idnivel, // confirmed lowercase
          level.nombre, // confirmed lowercase
          level.imagenfondo || "",
          level.costosaltar || 15,
          level.costoletra || 2,
          level.costoayuda || 1,
          level.mensajeinicio || "",
          level.mensajefinal || ""
        ]
      );
    }
    console.log(`Migrated ${levels.length} levels.`);

    // Migrate Words
    const words: any = await all('SELECT * FROM Palabras_Almacen');
    console.log(`Found ${words.length} words in SQLite.`);
    
    for (const word of words) {
      await pool.query(
        `INSERT INTO "Word" ("id", "word", "description", "difficulty", "type", "levelId", "help01", "help02", "help03", "dictionary", "dictionaryWord", "premium")
         VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12)
         ON CONFLICT ("id") DO UPDATE SET
          "word" = EXCLUDED."word",
          "description" = EXCLUDED."description",
          "levelId" = EXCLUDED."levelId"`,
        [
          word.idpalabra,
          word.palabra.toUpperCase(),
          word.descripcion,
          word.dificultad || "normal",
          word.tipopalabra || "",
          word.idnivel,
          word.ayuda01 || "",
          word.ayuda02 || "",
          word.ayuda03 || "",
          word.Diccionario || "",
          word.PalabraDiccionario || "",
          word.premium || "N"
        ]
      );
    }
    console.log(`Migrated ${words.length} words.`);

    console.log('Migration completed successfully!');
  } catch (error) {
    console.error('Migration failed:', error);
  } finally {
    db.close();
    await pool.end();
  }
}

migrate();
