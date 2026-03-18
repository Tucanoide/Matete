'use client';

import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Trophy, Clock, Brain, User, X } from 'lucide-react';
import { getGlobalRanking, getLevelRanking } from '@/app/actions/game';

interface RankingBoardProps {
  onClose: () => void;
  currentLevel: number;
}

export default function RankingBoard({ onClose, currentLevel }: RankingBoardProps) {
  const [activeTab, setActiveTab] = useState<'global' | 'level'>('global');
  const [rankingData, setRankingData] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchRanking = async () => {
      setIsLoading(true);
      try {
        if (activeTab === 'global') {
          const data = await getGlobalRanking();
          setRankingData(data);
        } else {
          const data = await getLevelRanking(currentLevel);
          setRankingData(data);
        }
      } catch (error) {
        console.error('Error fetching ranking:', error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchRanking();
  }, [activeTab, currentLevel]);

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  return (
    <motion.div 
      initial={{ opacity: 0, scale: 0.9 }}
      animate={{ opacity: 1, scale: 1 }}
      exit={{ opacity: 0, scale: 0.9 }}
      className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-[#050510]/95 backdrop-blur-xl"
    >
      <div className="w-full max-w-2xl bg-[#1a1033]/80 border-2 border-[#00ffff]/30 rounded-3xl overflow-hidden shadow-[0_0_50px_rgba(0,255,255,0.1)]">
        {/* Header */}
        <div className="flex justify-between items-center p-4 md:p-6 border-b border-[#00ffff]/20">
          <div className="flex items-center gap-3">
            <Trophy className="w-5 h-5 md:w-6 md:h-6 text-[#ff00ff]" />
            <h2 className="text-xl md:text-2xl font-black italic text-[#00ffff]">HALL OF FAME</h2>
          </div>
          <button 
            onClick={onClose}
            className="p-1.5 md:p-2 hover:bg-[#ff00ff]/20 rounded-full transition-colors"
          >
            <X className="w-6 h-6 text-[#ff00ff]" />
          </button>
        </div>

        {/* Tabs */}
        <div className="flex border-b border-[#00ffff]/10 bg-[#050510]/40">
          <button 
            onClick={() => setActiveTab('global')}
            className={`flex-1 py-4 font-mono text-sm tracking-widest uppercase transition-all ${
              activeTab === 'global' ? 'text-[#00ffff] border-b-2 border-[#00ffff] bg-[#00ffff]/5' : 'text-[#00ffff]/40 hover:text-[#00ffff]/70'
            }`}
          >
            Global Top 10
          </button>
          <button 
            onClick={() => setActiveTab('level')}
            className={`flex-1 py-4 font-mono text-sm tracking-widest uppercase transition-all ${
              activeTab === 'level' ? 'text-[#00ffff] border-b-2 border-[#00ffff] bg-[#00ffff]/5' : 'text-[#00ffff]/40 hover:text-[#00ffff]/70'
            }`}
          >
            Nivel {currentLevel}
          </button>
        </div>

        {/* List */}
        <div className="p-4 md:p-6 h-[400px] overflow-y-auto custom-scrollbar">
          {isLoading ? (
            <div className="flex flex-col items-center justify-center h-full gap-4">
              <div className="w-12 h-12 border-4 border-[#ff00ff]/20 border-t-[#ff00ff] rounded-full animate-spin" />
              <p className="font-mono text-xs text-[#ff00ff] animate-pulse">CARGANDO TRANSMISIÓN...</p>
            </div>
          ) : rankingData.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-full text-[#00ffff]/40 font-mono">
              <Brain className="w-12 h-12 mb-4 opacity-20" />
              <p>NO HAY DATOS EN ESTE SECTOR</p>
            </div>
          ) : (
            <div className="space-y-3">
              {rankingData.map((user, index) => (
                <motion.div
                  key={user.id}
                  initial={{ x: -20, opacity: 0 }}
                  animate={{ x: 0, opacity: 1 }}
                  transition={{ delay: index * 0.05 }}
                  className="flex items-center gap-4 p-4 rounded-xl bg-[#050510]/60 border border-[#00ffff]/10 hover:border-[#00ffff]/30 transition-all group"
                >
                  <div className={`w-8 h-8 flex items-center justify-center font-black italic rounded-lg ${
                    index === 0 ? 'bg-yellow-400 text-black' : 
                    index === 1 ? 'bg-slate-300 text-black' : 
                    index === 2 ? 'bg-amber-600 text-black' : 
                    'text-[#00ffff]/60'
                  }`}>
                    {index + 1}
                  </div>
                  
                  <div className="w-10 h-10 rounded-full border border-[#ff00ff]/40 overflow-hidden bg-[#1a1033]">
                    {user.image ? (
                      <img src={user.image} alt={user.name} className="w-full h-full object-cover" />
                    ) : (
                      <div className="w-full h-full flex items-center justify-center bg-[#ff00ff]/10">
                        <User className="w-6 h-6 text-[#ff00ff]/50" />
                      </div>
                    )}
                  </div>

                  <div className="flex-1 min-w-0">
                    <p className="font-bold text-[#00ffff] group-hover:text-white transition-colors truncate">
                      {user.name || 'Anónimo'}
                    </p>
                    <div className="flex flex-wrap gap-2 md:gap-4 mt-1">
                      <div className="flex items-center gap-1 text-[10px] font-mono text-[#ff00ff]/80 whitespace-nowrap">
                        <Trophy className="w-3 h-3" /> {activeTab === 'level' ? user.levelPoints : user.points} P
                      </div>
                      <div className="flex items-center gap-1 text-[10px] font-mono text-[#00ffff]/80 whitespace-nowrap">
                        <Clock className="w-3 h-3" /> {formatTime(activeTab === 'level' ? user.levelTime : user.totalTime)}
                      </div>
                      {activeTab === 'global' && (
                        <div className="flex items-center gap-1 text-[10px] font-mono text-green-400/80 whitespace-nowrap">
                          <Brain className="w-3 h-3" /> {user.neuronas} N
                        </div>
                      )}
                    </div>
                  </div>
                </motion.div>
              ))}
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="p-4 bg-[#050510]/40 border-t border-[#00ffff]/10 text-center text-[10px] font-mono text-[#00ffff]/20 tracking-[0.2em] uppercase">
          Actualizado en tiempo real // Sincronización Cuántica Ok
        </div>
      </div>
    </motion.div>
  );
}
