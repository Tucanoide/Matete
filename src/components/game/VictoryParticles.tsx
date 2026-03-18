'use client';

import React from 'react';
import { motion } from 'framer-motion';

interface Particle {
  id: number;
  x: number;
  y: number;
  color: string;
  size: number;
}

export default function VictoryParticles() {
  const [particles, setParticles] = React.useState<Particle[]>([]);

  React.useEffect(() => {
    const colors = ['#00ffff', '#ff00ff', '#ffffff', '#0088ff'];
    const newParticles = Array.from({ length: 40 }).map((_, i) => ({
      id: i,
      x: 0,
      y: 0,
      color: colors[Math.floor(Math.random() * colors.length)],
      size: Math.random() * 8 + 4,
    }));
    setParticles(newParticles);
  }, []);

  return (
    <div className="absolute inset-0 pointer-events-none overflow-hidden flex items-center justify-center">
      {particles.map((p) => (
        <motion.div
          key={p.id}
          initial={{ x: 0, y: 0, opacity: 1, scale: 1 }}
          animate={{
            x: (Math.random() - 0.5) * 800,
            y: (Math.random() - 0.5) * 800,
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
