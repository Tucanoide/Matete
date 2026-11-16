'use client';

import React from 'react';
import { motion } from 'framer-motion';

interface Particle {
  id: number;
  dx: number;
  dy: number;
  color: string;
  size: number;
}

const COLORS = ['#00ffff', '#ff00ff', '#ffffff', '#0088ff'];

function makeParticles(): Particle[] {
  return Array.from({ length: 40 }, (_, i) => ({
    id: i,
    dx: (Math.random() - 0.5) * 800,
    dy: (Math.random() - 0.5) * 800,
    color: COLORS[Math.floor(Math.random() * COLORS.length)],
    size: Math.random() * 8 + 4,
  }));
}

export default function VictoryParticles() {
  // Se generan una sola vez al montar (inicializador perezoso): el render queda puro.
  const [particles] = React.useState<Particle[]>(makeParticles);

  return (
    <div className="absolute inset-0 pointer-events-none overflow-hidden flex items-center justify-center">
      {particles.map((p) => (
        <motion.div
          key={p.id}
          initial={{ x: 0, y: 0, opacity: 1, scale: 1 }}
          animate={{
            x: p.dx,
            y: p.dy,
            opacity: 0,
            scale: 0,
          }}
          transition={{
            duration: 1.5,
            ease: "easeOut",
          }}
          style={{
            position: 'absolute',
            width: p.size,
            height: p.size,
            backgroundColor: p.color,
            borderRadius: '50%',
            boxShadow: `0 0 10px ${p.color}`,
          }}
        />
      ))}
    </div>
  );
}
