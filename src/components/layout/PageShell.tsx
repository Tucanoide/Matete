import Header from '@/components/layout/Header';

// Fondo y header comunes de las páginas secundarias (mismo look que la home).
export default function PageShell({ children }: { children: React.ReactNode }) {
  return (
    <main className="min-h-screen bg-[#050510] text-[#00ffff] font-sans selection:bg-[#ff00ff]/30">
      <div className="fixed inset-0 pointer-events-none bg-[radial-gradient(circle_at_50%_50%,_#1a1033_0%,_#050510_100%)] opacity-50" />
      <div
        className="fixed inset-0 pointer-events-none opacity-[0.15]"
        style={{
          backgroundImage: `linear-gradient(#00ffff 1px, transparent 1px), linear-gradient(90deg, #00ffff 1px, transparent 1px)`,
          backgroundSize: '40px 40px',
        }}
      />
      <div className="relative z-10 max-w-3xl mx-auto px-4 py-8 md:py-12">
        <Header />
        {children}
      </div>
    </main>
  );
}
