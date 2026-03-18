import Header from '@/components/layout/Header';
import GameEngine from '@/components/game/GameEngine';
import { getInitialGameState } from '@/app/actions/game';

export default async function Home() {
  const gameState = await getInitialGameState();
  const firstLevel = gameState?.level;
  const firstWord = gameState?.word;

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

        {firstWord && firstLevel ? (
          <GameEngine 
            initialWord={firstWord.word}
            initialDescription={firstWord.description}
            initialLevel={firstLevel.id}
            initialWordId={firstWord.id}
            initialRevealedIndices={gameState?.revealedIndices}
          />
        ) : (
          <div className="text-center py-20 bg-[#1a1033]/40 border-2 border-[#ff00ff]/30 rounded-2xl backdrop-blur-xl">
            <p className="text-xl font-mono animate-pulse">Cargando base de datos...</p>
          </div>
        )}
      </div>

      <footer className="fixed bottom-4 right-4 text-[#00ffff]/30 text-xs font-mono tracking-widest uppercase pointer-events-none">
        Retro System v4.1 // Matete Desktop
      </footer>
    </main>
  );
}
