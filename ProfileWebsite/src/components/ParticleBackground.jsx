
import React, { useRef, useEffect } from 'react';

export default function ParticleBackground() {
  const canvasRef = useRef(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    const ctx = canvas.getContext('2d');
    let width = window.innerWidth;
    let height = window.innerHeight;
    canvas.width = width;
    canvas.height = height;

    // Helper to detect dark mode
    function isDark() {
      return document.body.classList.contains('dark');
    }

    // Particle setup for dark mode
    const particles = Array.from({ length: 80 }, () => ({
      x: Math.random() * width,
      y: Math.random() * height,
      r: Math.random() * 1.8 + 0.7,
      dx: (Math.random() - 0.5) * 0.3,
      dy: (Math.random() - 0.5) * 0.3,
    }));

    // Wave setup for light mode
    const waveCount = 3;
    const waveColors = ['#007bff22', '#007bff33', '#007bff44'];
    let waveOffset = 0;

    function drawWaves() {
      ctx.clearRect(0, 0, width, height);
      for (let w = 0; w < waveCount; w++) {
        ctx.beginPath();
        for (let x = 0; x <= width; x += 2) {
          const y = 60 + w * 40 + Math.sin((x + waveOffset + w * 60) * 0.012 + w) * (18 + w * 8) + w * 18;
          ctx.lineTo(x, y + height * 0.7);
        }
        ctx.strokeStyle = waveColors[w];
        ctx.lineWidth = 3 - w;
        ctx.globalAlpha = 0.7 - w * 0.2;
        ctx.stroke();
        ctx.closePath();
        ctx.globalAlpha = 1;
      }
    }

    function drawParticles() {
      ctx.clearRect(0, 0, width, height);
      for (let p of particles) {
        ctx.beginPath();
        ctx.arc(p.x, p.y, p.r, 0, 2 * Math.PI);
        ctx.fillStyle = 'rgba(255,255,255,0.7)';
        ctx.shadowColor = '#ffe066';
        ctx.shadowBlur = 8;
        ctx.fill();
        ctx.closePath();
      }
    }

    function updateParticles() {
      for (let p of particles) {
        p.x += p.dx;
        p.y += p.dy;
        if (p.x < 0 || p.x > width) p.dx *= -1;
        if (p.y < 0 || p.y > height) p.dy *= -1;
      }
    }

    function animate() {
      if (isDark()) {
        drawParticles();
        updateParticles();
      } else {
        drawWaves();
        waveOffset += 2;
      }
      requestAnimationFrame(animate);
    }
    animate();

    function handleResize() {
      width = window.innerWidth;
      height = window.innerHeight;
      canvas.width = width;
      canvas.height = height;
    }
    window.addEventListener('resize', handleResize);
    return () => {
      window.removeEventListener('resize', handleResize);
    };
  }, []);

  return (
    <canvas
      ref={canvasRef}
      style={{
        position: 'fixed',
        top: 0,
        left: 0,
        width: '100vw',
        height: '100vh',
        zIndex: 0,
        pointerEvents: 'none',
      }}
    />
  );
}
