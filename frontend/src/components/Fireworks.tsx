import { useEffect, useState } from 'react';

interface Particle {
  id: number;
  x: number;
  y: number;
  color: string;
  duration: number;
  delay: number;
}

export function Fireworks() {
  const [particles, setParticles] = useState<Particle[]>([]);

  useEffect(() => {
    const colors = ['#10b981', '#34d399', '#6ee7b7', '#a7f3d0', '#d1fae5', '#059669'];
    const newParticles: Particle[] = [];

    // Create 80 particles from center
    for (let i = 0; i < 80; i++) {
      newParticles.push({
        id: i,
        x: 50 + (Math.random() - 0.5) * 10,
        y: 50 + (Math.random() - 0.5) * 10,
        color: colors[Math.floor(Math.random() * colors.length)],
        duration: 1.5 + Math.random() * 0.5,
        delay: Math.random() * 0.1,
      });
    }

    setParticles(newParticles);

    const timer = setTimeout(() => {
      setParticles([]);
    }, 2000);

    return () => clearTimeout(timer);
  }, []);

  return (
    <>
      <style>{`
        @keyframes burst {
          0% {
            opacity: 1;
            transform: translate(0, 0) scale(1);
          }
          100% {
            opacity: 0;
            transform: translate(var(--tx), var(--ty)) scale(0);
          }
        }
      `}</style>

      <div
        style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          pointerEvents: 'none',
          overflow: 'hidden',
          zIndex: 50,
        }}
      >
        {particles.map((particle) => {
          const angle = (particle.id / particles.length) * Math.PI * 2;
          const distance = 300 + Math.random() * 200;
          const tx = Math.cos(angle) * distance;
          const ty = Math.sin(angle) * distance;

          return (
            <div
              key={particle.id}
              style={{
                position: 'fixed',
                left: `${particle.x}%`,
                top: `${particle.y}%`,
                width: '8px',
                height: '8px',
                backgroundColor: particle.color,
                borderRadius: '50%',
                '--tx': `${tx}px`,
                '--ty': `${ty}px`,
                animation: `burst ${particle.duration}s ease-out ${particle.delay}s forwards`,
                pointerEvents: 'none',
              } as any}
            />
          );
        })}
      </div>
    </>
  );
}
