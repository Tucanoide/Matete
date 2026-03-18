import { PrismaClient } from '@prisma/client';
const prisma = new PrismaClient();
async function main() {
  try {
    const longWords = await prisma.word.findMany();
    const filtered = longWords.filter(w => w.word.length > 14);
    if (filtered.length > 0) {
      console.log('PALABRAS LARGAS (>14):', filtered.map(f => `${f.word} (${f.word.length})`).join(', '));
    } else {
      console.log('No se encontraron palabras mayores a 14 caracteres.');
    }
  } catch (err) {
    console.error('Error:', err);
  } finally {
    await prisma.$disconnect();
  }
}
main();
