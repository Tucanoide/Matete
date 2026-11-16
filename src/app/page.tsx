import Header from '@/components/layout/Header';
import GameEngine from '@/components/game/GameEngine';
import { getInitialGameState, getGameCompletion } from '@/app/actions/game';
import GameOverPanel from '@/components/game/GameOverPanel';

export default async function Home() {
  const gameState = await getInitialGameState();
  // Sin palabra para entregar: o terminó el juego (PROG-09) o la base no tiene palabras.
  const completion = gameState ? null : await getGameCompletion();

  return (
    <main className="min-h-screen bg-[#050510] text-[#00ffff] font-sans selection:bg-[#ff00ff]/30">
      {/* Background Grid */}
      <div className="fixed inset-0 pointer-events-none bg-[radial-gradient(circle_at_50%_50%,_#1a1033_0%,_#050510_100%)] opacity-50" />
      <div 
        className="fixed inset-0 pointer-events-none opacity-[0.15]" 
        style={{ 
          backgroundImage: `linear-gradient(#00ffff 1px, transparent 1px), linear-gradient(90deg, #00ffff 1px, transparent 1px)`,
          backgroundSize: '40px 40px'
        }} 
      />

      <div className="relative z-10 max-w-5xl mx-auto px-4 py-8 md:py-12">
        <Header />

        {gameState ? (
          <GameEngine
            // Si el servidor devuelve otra palabra, nivel o saldo (p. ej. tras migrar el invitado), el juego
            // se reinicia con ese estado. needsUsername no va en la key: el juego lo aplica solo para cerrar el modal.
            key={`${gameState.word.wordId}:${gameState.levelId}:${gameState.neuronas}`}
            initialWord={gameState.word}
            initialLevel={gameState.levelId}
            initialNeedsUsername={gameState.needsUsername}
            initialNeuronas={gameState.neuronas}
            initialCosts={gameState.costs}
          />
        ) : completion?.completed ? (
          <GameOverPanel points={completion.points} />
        ) : (
          <div className="text-center py-20 bg-[#1a1033]/40 border-2 border-[#ff00ff]/30 rounded-2xl backdrop-blur-xl">
            <p className="text-xl font-mono">No hay palabras disponibles por ahora.</p>
          </div>
        )}
      </div>

    </main>
  );
}
