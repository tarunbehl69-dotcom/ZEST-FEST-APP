/**
 * Zenith '26 — Photorealistic 3D Diwali Celebrations Engine
 * 
 * Realistic Pyrotechnics & Illumination:
 * 1. Multi-Stage Shell Fireworks (Kamuro Willow, Peony, Brocade Crackle) with Additive Blending
 * 2. Anar Golden Fountains & Ground Chakri 3D Spinning Wheels
 * 3. 100-Wala Red Cracker Ladi with fluttering red paper confetti & rapid-fire bursts
 * 4. Realistic 3D Terracotta Earthen Diya with 3D Parallax Tilt, 4-layer fluid flame & rising micro-embers
 * 5. Screen Illumination (Ambient Flash) synchronized with inverse-square falloff
 * 6. Binaural / Stereo Panned Acoustic Synthesis (Speed-of-sound delay, sub-bass thud, popcorn crackles)
 * 7. 3D Floating Sky Lanterns (Kandils) with warm undulating inner glow
 * 8. Zero external dependencies — pure Canvas 2D + Web Audio API + CSS 3D
 */

(function () {
  'use strict';

  // --- Configuration ---
  const CONFIG = {
    autoFireInterval: 3200,
    soundEnabled: true,
    festiveActive: true,
    gravity: 0.12,
    airResistance: 0.965,
    maxParticles: 800
  };

  // Restore sound preference from localStorage if available
  try {
    const savedSound = localStorage.getItem('zenith_diwali_sound');
    if (savedSound !== null) CONFIG.soundEnabled = savedSound === 'true';
  } catch (e) {}

  // --- Realistic Spatial Web Audio Synthesizer ---
  let audioCtx = null;
  function getAudioContext() {
    if (!audioCtx) {
      const AC = window.AudioContext || window.webkitAudioContext;
      if (AC) audioCtx = new AC();
    }
    if (audioCtx && audioCtx.state === 'suspended') {
      audioCtx.resume();
    }
    return audioCtx;
  }

  function createPanner(xNorm) {
    if (!audioCtx) return null;
    try {
      if (audioCtx.createStereoPanner) {
        const panner = audioCtx.createStereoPanner();
        // Clamped between -0.85 and 0.85
        panner.pan.value = Math.max(-0.85, Math.min(0.85, (xNorm * 2) - 1));
        return panner;
      }
    } catch (e) {}
    return null;
  }

  const SoundEngine = {
    // Whistling ascent with Doppler pitch bend
    playAscent(xNorm = 0.5) {
      if (!CONFIG.soundEnabled) return;
      try {
        const ctx = getAudioContext();
        if (!ctx) return;
        const now = ctx.currentTime;

        const osc = ctx.createOscillator();
        const oscGain = ctx.createGain();
        const filter = ctx.createBiquadFilter();

        osc.type = 'sawtooth';
        osc.frequency.setValueAtTime(280, now);
        osc.frequency.exponentialRampToValueAtTime(1450, now + 0.42);

        filter.type = 'bandpass';
        filter.frequency.setValueAtTime(550, now);
        filter.frequency.exponentialRampToValueAtTime(1700, now + 0.42);
        filter.Q.value = 6.0;

        oscGain.gain.setValueAtTime(0.025, now);
        oscGain.gain.exponentialRampToValueAtTime(0.0008, now + 0.42);

        osc.connect(filter);
        filter.connect(oscGain);

        const panner = createPanner(xNorm);
        if (panner) {
          oscGain.connect(panner);
          panner.connect(ctx.destination);
        } else {
          oscGain.connect(ctx.destination);
        }

        osc.start(now);
        osc.stop(now + 0.43);
      } catch (e) {}
    },

    // High energy explosive blast with deep sub-bass thump & distance delay
    playBreak(type = 'peony', xNorm = 0.5, distanceDelayMs = 90) {
      if (!CONFIG.soundEnabled) return;
      setTimeout(() => {
        try {
          const ctx = getAudioContext();
          if (!ctx) return;
          const now = ctx.currentTime;
          const panner = createPanner(xNorm);

          // 1. Deep Sub-Bass Thump (75Hz -> 24Hz)
          const subOsc = ctx.createOscillator();
          const subGain = ctx.createGain();
          subOsc.type = 'sine';
          subOsc.frequency.setValueAtTime(80, now);
          subOsc.frequency.exponentialRampToValueAtTime(24, now + 0.38);

          subGain.gain.setValueAtTime(0.26, now);
          subGain.gain.exponentialRampToValueAtTime(0.001, now + 0.42);

          subOsc.connect(subGain);
          if (panner) {
            subGain.connect(panner);
            panner.connect(ctx.destination);
          } else {
            subGain.connect(ctx.destination);
          }
          subOsc.start(now);
          subOsc.stop(now + 0.42);

          // 2. High-energy explosive blast noise
          const bufferSize = Math.floor(ctx.sampleRate * 0.42);
          const buffer = ctx.createBuffer(1, bufferSize, ctx.sampleRate);
          const data = buffer.getChannelData(0);
          for (let i = 0; i < bufferSize; i++) {
            data[i] = (Math.random() * 2 - 1) * Math.exp(-i / (ctx.sampleRate * 0.055));
          }
          const noise = ctx.createBufferSource();
          noise.buffer = buffer;

          const noiseFilter = ctx.createBiquadFilter();
          noiseFilter.type = 'lowpass';
          noiseFilter.frequency.setValueAtTime(type === 'willow' ? 700 : 1300, now);
          noiseFilter.frequency.exponentialRampToValueAtTime(140, now + 0.38);

          const noiseGain = ctx.createGain();
          noiseGain.gain.setValueAtTime(0.22, now);
          noiseGain.gain.exponentialRampToValueAtTime(0.001, now + 0.42);

          noise.connect(noiseFilter);
          noiseFilter.connect(noiseGain);
          if (panner) {
            noiseGain.connect(panner);
          } else {
            noiseGain.connect(ctx.destination);
          }
          noise.start(now);

          // 3. Staggered Trailing Crackles (Popcorn sizzle)
          const crackleCount = type === 'crackle' ? 26 : (type === 'willow' ? 14 : 8);
          for (let i = 0; i < crackleCount; i++) {
            const delay = 0.12 + Math.random() * 0.95;
            setTimeout(() => {
              if (!CONFIG.soundEnabled || !audioCtx) return;
              const cTime = audioCtx.currentTime;
              const cOsc = audioCtx.createOscillator();
              const cGain = audioCtx.createGain();
              cOsc.type = 'triangle';
              cOsc.frequency.setValueAtTime(800 + Math.random() * 1400, cTime);
              cGain.gain.setValueAtTime(0.02 + Math.random() * 0.035, cTime);
              cGain.gain.exponentialRampToValueAtTime(0.0001, cTime + 0.04);
              cOsc.connect(cGain);
              cGain.connect(audioCtx.destination);
              cOsc.start(cTime);
              cOsc.stop(cTime + 0.04);
            }, delay * 1000);
          }
        } catch (e) {}
      }, distanceDelayMs);
    },

    // Continuous Anar fountain roaring sizzle
    playAnar(xNorm = 0.5) {
      if (!CONFIG.soundEnabled) return;
      try {
        const ctx = getAudioContext();
        if (!ctx) return;
        const now = ctx.currentTime;
        const duration = 2.4;
        const bufferSize = Math.floor(ctx.sampleRate * duration);
        const buffer = ctx.createBuffer(1, bufferSize, ctx.sampleRate);
        const data = buffer.getChannelData(0);
        for (let i = 0; i < bufferSize; i++) {
          data[i] = (Math.random() * 2 - 1) * 0.65;
        }
        const noise = ctx.createBufferSource();
        noise.buffer = buffer;

        const filter = ctx.createBiquadFilter();
        filter.type = 'bandpass';
        filter.frequency.setValueAtTime(2400, now);
        filter.Q.value = 3.2;

        const gain = ctx.createGain();
        gain.gain.setValueAtTime(0.01, now);
        gain.gain.linearRampToValueAtTime(0.14, now + 0.25);
        gain.gain.exponentialRampToValueAtTime(0.001, now + duration);

        noise.connect(filter);
        filter.connect(gain);
        const panner = createPanner(xNorm);
        if (panner) {
          gain.connect(panner);
          panner.connect(ctx.destination);
        } else {
          gain.connect(ctx.destination);
        }
        noise.start(now);
      } catch (e) {}
    },

    // Snappy firecracker string pop (Ladi)
    playLadiPop(xNorm = 0.5) {
      if (!CONFIG.soundEnabled) return;
      try {
        const ctx = getAudioContext();
        if (!ctx) return;
        const now = ctx.currentTime;

        const osc = ctx.createOscillator();
        const gain = ctx.createGain();
        osc.type = 'sine';
        osc.frequency.setValueAtTime(140 + Math.random() * 100, now);
        osc.frequency.exponentialRampToValueAtTime(30, now + 0.06);

        gain.gain.setValueAtTime(0.24 + Math.random() * 0.1, now);
        gain.gain.exponentialRampToValueAtTime(0.001, now + 0.065);

        // Add snap noise
        const snapLen = Math.floor(ctx.sampleRate * 0.05);
        const sBuf = ctx.createBuffer(1, snapLen, ctx.sampleRate);
        const sData = sBuf.getChannelData(0);
        for (let i = 0; i < snapLen; i++) sData[i] = (Math.random() * 2 - 1);
        const snap = ctx.createBufferSource();
        snap.buffer = sBuf;

        const snapGain = ctx.createGain();
        snapGain.gain.setValueAtTime(0.2, now);
        snapGain.gain.exponentialRampToValueAtTime(0.001, now + 0.05);

        osc.connect(gain);
        snap.connect(snapGain);

        const panner = createPanner(xNorm);
        if (panner) {
          gain.connect(panner);
          snapGain.connect(panner);
          panner.connect(ctx.destination);
        } else {
          gain.connect(ctx.destination);
          snapGain.connect(ctx.destination);
        }

        osc.start(now);
        osc.stop(now + 0.07);
        snap.start(now);
      } catch (e) {}
    }
  };

  // --- Authentic Pyrotechnic Color Palettes ---
  const PALETTES = [
    {
      name: 'Kamuro Willow (Pure Incandescent Gold)',
      colors: ['#FFFFFF', '#FFF7D6', '#FFD700', '#FFB703', '#FB8500', '#D47E00'],
      style: 'willow'
    },
    {
      name: 'Diwali Saffron & Royal Crimson',
      colors: ['#FFFFFF', '#FFE066', '#FF8C00', '#FF2A2A', '#D90429', '#7209B7'],
      style: 'peony'
    },
    {
      name: 'Emerald Sparkler (Barium Green)',
      colors: ['#FFFFFF', '#B9FFB9', '#00FF66', '#00CC44', '#009933', '#004D1A'],
      style: 'peony'
    },
    {
      name: 'Royal Sapphire & Electric Violet',
      colors: ['#FFFFFF', '#E0AAFF', '#C77DFF', '#7B2CBF', '#00F0FF', '#0077B6'],
      style: 'peony'
    },
    {
      name: 'Brocade Crackle Starburst',
      colors: ['#FFFFFF', '#FFE8A3', '#FFD166', '#F77F00', '#E63946'],
      style: 'crackle'
    }
  ];

  // --- State Collections ---
  const rockets = [];
  const particles = [];
  const smokePuffs = [];
  const groundFountains = [];
  const chakris = [];
  const confettiBits = [];
  const lanterns = [];
  let flashAlpha = 0;
  let flashColor = '#FFD700';

  // --- Volumetric Smoke Puff ---
  class Smoke {
    constructor(x, y, vx, vy, size) {
      this.x = x;
      this.y = y;
      this.vx = vx;
      this.vy = vy;
      this.size = size;
      this.alpha = 0.32 + Math.random() * 0.18;
      this.growth = 0.3 + Math.random() * 0.35;
      this.decay = 0.0035 + Math.random() * 0.0035;
      this.rotation = Math.random() * Math.PI * 2;
      this.spin = (Math.random() - 0.5) * 0.02;
    }
    update() {
      this.x += this.vx;
      this.y += this.vy;
      this.size += this.growth;
      this.alpha -= this.decay;
      this.rotation += this.spin;
    }
    draw(ctx) {
      if (this.alpha <= 0) return;
      ctx.save();
      ctx.globalAlpha = Math.max(0, this.alpha);
      const grad = ctx.createRadialGradient(this.x, this.y, 0, this.x, this.y, this.size);
      grad.addColorStop(0, 'rgba(180, 180, 200, 0.45)');
      grad.addColorStop(0.5, 'rgba(110, 110, 130, 0.18)');
      grad.addColorStop(1, 'rgba(30, 30, 45, 0)');
      ctx.fillStyle = grad;
      ctx.beginPath();
      ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2);
      ctx.fill();
      ctx.restore();
    }
  }

  // --- Photorealistic Pyrotechnic Spark with Glowing Trail ---
  class SparkParticle {
    constructor(x, y, vx, vy, palette, style = 'peony') {
      this.x = x;
      this.y = y;
      this.vx = vx;
      this.vy = vy;
      this.palette = palette;
      this.style = style;
      this.age = 0;
      this.life = style === 'willow' ? (95 + Math.floor(Math.random() * 45)) : (55 + Math.floor(Math.random() * 35));
      this.size = style === 'willow' ? (2.8 + Math.random() * 1.6) : (2.2 + Math.random() * 1.5);
      this.trail = [];
      this.maxTrail = style === 'willow' ? 8 : 4;
      this.twinkleOffset = Math.random() * Math.PI * 2;
      this.hasPopped = false;
    }

    update() {
      this.age++;
      this.trail.push({ x: this.x, y: this.y });
      if (this.trail.length > this.maxTrail) this.trail.shift();

      this.vx *= CONFIG.airResistance;
      this.vy *= CONFIG.airResistance;
      this.vy += (this.style === 'willow' ? 0.075 : CONFIG.gravity);

      this.x += this.vx;
      this.y += this.vy;

      // Drop occasional wispy smoke when cooling
      if (this.age > this.life * 0.65 && Math.random() > 0.88) {
        smokePuffs.push(new Smoke(this.x, this.y, this.vx * 0.15, this.vy * 0.15, 3.5));
      }

      // Brocade Crackle secondary micro-bursts
      if (this.style === 'crackle' && !this.hasPopped && this.age > this.life * 0.72 && Math.random() > 0.55) {
        this.hasPopped = true;
        for (let i = 0; i < 3; i++) {
          const a = Math.random() * Math.PI * 2;
          const s = Math.random() * 2.2 + 0.8;
          const p = new SparkParticle(this.x, this.y, Math.cos(a) * s, Math.sin(a) * s, this.palette, 'peony');
          p.life = 16;
          p.size = 1.6;
          particles.push(p);
        }
      }
    }

    draw(ctx) {
      const progress = this.age / this.life;
      if (progress >= 1) return;

      const alpha = Math.pow(1 - progress, 1.2);
      const colorIndex = Math.min(
        this.palette.colors.length - 1,
        Math.floor(progress * this.palette.colors.length)
      );
      const color = this.palette.colors[colorIndex];

      // 1. Draw persistent light ribbon
      if (this.trail.length > 1) {
        ctx.beginPath();
        ctx.moveTo(this.trail[0].x, this.trail[0].y);
        for (let i = 1; i < this.trail.length; i++) {
          ctx.lineTo(this.trail[i].x, this.trail[i].y);
        }
        ctx.strokeStyle = color;
        ctx.lineWidth = this.size * 0.75;
        ctx.globalAlpha = alpha * 0.45;
        ctx.stroke();
      }

      // 2. Optical Bloom & Scintillation Core
      const twinkle = Math.sin(this.age * 0.45 + this.twinkleOffset) * 0.25 + 0.75;
      ctx.globalAlpha = Math.max(0, alpha * twinkle);

      // Outer Glow Aura
      ctx.beginPath();
      ctx.arc(this.x, this.y, this.size * 2.0, 0, Math.PI * 2);
      ctx.fillStyle = color;
      ctx.shadowBlur = this.style === 'willow' ? 14 : 9;
      ctx.shadowColor = color;
      ctx.fill();

      // Incandescent White Core
      ctx.beginPath();
      ctx.arc(this.x, this.y, Math.max(1, this.size * 0.65), 0, Math.PI * 2);
      ctx.fillStyle = progress < 0.22 ? '#FFFFFF' : color;
      ctx.shadowBlur = 4;
      ctx.shadowColor = '#FFFFFF';
      ctx.fill();
    }
  }

  // --- Ascending Aerial Rocket ---
  class Rocket {
    constructor(startX, startY, targetX, targetY) {
      this.x = startX;
      this.y = startY;
      this.targetX = targetX;
      this.targetY = targetY;
      const angle = Math.atan2(targetY - startY, targetX - startX);
      const speed = 15.5 + Math.random() * 4.5;
      this.vx = Math.cos(angle) * speed;
      this.vy = Math.sin(angle) * speed;
      this.palette = PALETTES[Math.floor(Math.random() * PALETTES.length)];
      this.trail = [];
      this.dead = false;
      this.sparkTimer = 0;
      SoundEngine.playAscent(startX / window.innerWidth);
    }

    update() {
      this.trail.push({ x: this.x, y: this.y });
      if (this.trail.length > 12) this.trail.shift();

      this.x += this.vx;
      this.y += this.vy;
      this.vy += 0.085; // subtle arc

      this.sparkTimer++;
      if (this.sparkTimer % 2 === 0) {
        smokePuffs.push(new Smoke(this.x, this.y + 4, (Math.random() - 0.5) * 0.7, 1.2, 4.5));
        // Exhaust golden sparks
        const p = new SparkParticle(
          this.x,
          this.y + 3,
          (Math.random() - 0.5) * 1.6,
          Math.random() * 2.5 + 1.2,
          PALETTES[0],
          'peony'
        );
        p.life = 20;
        p.size = 1.8;
        particles.push(p);
      }

      if (this.vy >= 0 || this.y <= this.targetY) {
        this.dead = true;
        this.explode();
      }
    }

    draw(ctx) {
      // Draw fiery propulsion exhaust
      for (let i = 0; i < this.trail.length; i++) {
        const pt = this.trail[i];
        const progress = i / this.trail.length;
        ctx.beginPath();
        ctx.arc(pt.x, pt.y, 2.8 * progress, 0, Math.PI * 2);
        ctx.fillStyle = `rgba(255, 170, 30, ${progress * 0.8})`;
        ctx.shadowBlur = 12;
        ctx.shadowColor = '#FF8C00';
        ctx.fill();
      }

      // Blazing White-Hot Rocket Head
      ctx.beginPath();
      ctx.arc(this.x, this.y, 4.2, 0, Math.PI * 2);
      ctx.fillStyle = '#FFFFFF';
      ctx.shadowBlur = 18;
      ctx.shadowColor = '#FFD700';
      ctx.fill();
    }

    explode() {
      const xNorm = this.x / window.innerWidth;
      SoundEngine.playBreak(this.palette.style, xNorm, 80);

      // Trigger ambient screen flash
      flashAlpha = 0.38;
      flashColor = this.palette.colors[2] || '#FFD700';

      const count = this.palette.style === 'willow' ? 140 : 110;
      for (let i = 0; i < count; i++) {
        const angle = Math.random() * Math.PI * 2;
        // 3D spherical burst velocity
        const speed = (Math.random() * 8.5 + 2.0);
        const vx = Math.cos(angle) * speed;
        const vy = Math.sin(angle) * speed;
        particles.push(new SparkParticle(this.x, this.y, vx, vy, this.palette, this.palette.style));
      }

      // Central explosion flash cloud
      for (let i = 0; i < 9; i++) {
        const a = Math.random() * Math.PI * 2;
        const sp = Math.random() * 1.8;
        smokePuffs.push(new Smoke(this.x, this.y, Math.cos(a) * sp, Math.sin(a) * sp, 14));
      }
    }
  }

  // --- Towering Golden Anar Fountain ---
  class GroundAnar {
    constructor(x, y) {
      this.x = x;
      this.y = y;
      this.age = 0;
      this.duration = 130;
      this.dead = false;
      SoundEngine.playAnar(x / window.innerWidth);
    }

    update() {
      this.age++;
      if (this.age > this.duration) {
        this.dead = true;
        return;
      }

      // Bell curve eruptive intensity
      const intensity = Math.sin((this.age / this.duration) * Math.PI);
      const sparkRate = Math.floor(intensity * 10 + 3);

      for (let i = 0; i < sparkRate; i++) {
        const spread = (Math.random() - 0.5) * 0.72;
        const angle = -Math.PI / 2 + spread;
        const speed = (intensity * 8.5 + 6.0) + Math.random() * 6;
        const vx = Math.cos(angle) * speed;
        const vy = Math.sin(angle) * speed;
        const p = new SparkParticle(this.x, this.y, vx, vy, PALETTES[0], 'willow');
        p.life = 45 + Math.random() * 25;
        p.size = 2.4;
        particles.push(p);
      }

      // Billowing fragrant smoke
      if (Math.random() > 0.35) {
        smokePuffs.push(new Smoke(
          this.x + (Math.random() - 0.5) * 12,
          this.y - 12,
          (Math.random() - 0.5) * 1.4,
          -1.6 - Math.random() * 0.8,
          7
        ));
      }
    }

    draw(ctx) {
      // Dazzling incandescence at cone apex
      ctx.save();
      const radius = 35 + Math.sin(this.age * 0.4) * 12;
      const grad = ctx.createRadialGradient(this.x, this.y, 0, this.x, this.y, radius);
      grad.addColorStop(0, 'rgba(255, 255, 255, 0.95)');
      grad.addColorStop(0.3, 'rgba(255, 215, 0, 0.7)');
      grad.addColorStop(0.7, 'rgba(255, 100, 0, 0.35)');
      grad.addColorStop(1, 'rgba(255, 50, 0, 0)');
      ctx.fillStyle = grad;
      ctx.beginPath();
      ctx.arc(this.x, this.y, radius, 0, Math.PI * 2);
      ctx.fill();
      ctx.restore();
    }
  }

  // --- Ground Chakri (Spinning Firewheel with 3D Perspective) ---
  class GroundChakri {
    constructor(x, y) {
      this.x = x;
      this.y = y;
      this.age = 0;
      this.duration = 140;
      this.angle = 0;
      this.spinSpeed = 0.38;
      this.dead = false;
      SoundEngine.playAnar(x / window.innerWidth);
    }

    update() {
      this.age++;
      this.angle += this.spinSpeed;
      if (this.age > this.duration) {
        this.dead = true;
        return;
      }

      // Sputter & skitter on floor slightly
      this.x += (Math.random() - 0.5) * 1.2;

      // 3 opposing spiral fire nozzles
      for (let arm = 0; arm < 3; arm++) {
        const jetAngle = this.angle + (arm * (Math.PI * 2 / 3));
        const speed = 7.0 + Math.random() * 5.0;
        const vx = Math.cos(jetAngle) * speed;
        // 3D perspective floor squish (0.32 height ratio)
        const vy = Math.sin(jetAngle) * speed * 0.32;
        const p = new SparkParticle(this.x, this.y, vx, vy, PALETTES[1], 'peony');
        p.life = 36;
        p.size = 2.2;
        particles.push(p);
      }
    }

    draw(ctx) {
      ctx.save();
      // Glowing 3D ground ellipse
      ctx.beginPath();
      ctx.ellipse(this.x, this.y, 52, 18, 0, 0, Math.PI * 2);
      ctx.fillStyle = 'rgba(255, 140, 0, 0.3)';
      ctx.shadowBlur = 24;
      ctx.shadowColor = '#FF8C00';
      ctx.fill();

      // Spinning metallic core
      ctx.beginPath();
      ctx.ellipse(this.x, this.y, 14, 5, 0, 0, Math.PI * 2);
      ctx.fillStyle = '#FFFFFF';
      ctx.shadowBlur = 10;
      ctx.shadowColor = '#FFD700';
      ctx.fill();
      ctx.restore();
    }
  }

  // --- 100-Wala Red Cracker Ladi (Garland Burst) ---
  class CrackerLadi {
    constructor(startX, startY) {
      this.startX = startX;
      this.startY = startY;
      this.count = 28;
      this.current = 0;
      this.interval = 55; // ms between pops
      this.active = true;
      this.popNext();
    }

    popNext() {
      if (this.current >= this.count) {
        this.active = false;
        return;
      }

      const x = this.startX + (this.current % 4 - 1.5) * 18;
      const y = this.startY + this.current * 10;
      const xNorm = x / window.innerWidth;

      SoundEngine.playLadiPop(xNorm);

      // Micro bright flash
      flashAlpha = Math.max(flashAlpha, 0.22);
      flashColor = '#FFF5CC';

      // Blast sparks
      for (let i = 0; i < 18; i++) {
        const a = Math.random() * Math.PI * 2;
        const sp = 2.5 + Math.random() * 5.0;
        const p = new SparkParticle(x, y, Math.cos(a) * sp, Math.sin(a) * sp, PALETTES[1], 'peony');
        p.life = 22;
        p.size = 2.0;
        particles.push(p);
      }

      // Red paper confetti fragments
      for (let i = 0; i < 6; i++) {
        confettiBits.push({
          x: x,
          y: y,
          vx: (Math.random() - 0.5) * 4,
          vy: -Math.random() * 3 - 1,
          size: 4 + Math.random() * 4,
          color: Math.random() > 0.2 ? '#D90429' : '#FFD700',
          rotation: Math.random() * 360,
          spin: (Math.random() - 0.5) * 10,
          alpha: 1.0,
          age: 0,
          life: 80 + Math.random() * 40
        });
      }

      // Smoke puff
      smokePuffs.push(new Smoke(x, y, (Math.random() - 0.5) * 0.8, -0.8, 8));

      this.current++;
      setTimeout(() => this.popNext(), this.interval + (Math.random() - 0.5) * 20);
    }
  }

  // --- 3D Floating Sky Lanterns (Kandils) ---
  class SkyLantern {
    constructor(w, h) {
      this.x = Math.random() * w;
      this.y = h + 50 + Math.random() * 200;
      this.vx = (Math.random() - 0.5) * 0.35;
      this.vy = -(0.5 + Math.random() * 0.55);
      this.size = 22 + Math.random() * 14;
      this.wobble = Math.random() * Math.PI * 2;
    }

    update(h) {
      this.wobble += 0.02;
      this.x += this.vx + Math.sin(this.wobble) * 0.4;
      this.y += this.vy;
      if (this.y < -90) {
        this.y = h + 60;
        this.x = Math.random() * window.innerWidth;
      }
    }

    draw(ctx) {
      ctx.save();
      ctx.translate(this.x, this.y);

      // Octagonal Lantern Silhouette & Radial Warm Glow
      const grad = ctx.createLinearGradient(0, -this.size * 0.6, 0, this.size * 0.6);
      grad.addColorStop(0, '#E65100');
      grad.addColorStop(0.45, '#FF8F00');
      grad.addColorStop(1, '#FFD54F');

      ctx.beginPath();
      ctx.moveTo(-this.size * 0.38, -this.size * 0.55);
      ctx.lineTo(this.size * 0.38, -this.size * 0.55);
      ctx.lineTo(this.size * 0.52, this.size * 0.45);
      ctx.lineTo(-this.size * 0.52, this.size * 0.45);
      ctx.closePath();

      ctx.fillStyle = grad;
      ctx.shadowBlur = 32;
      ctx.shadowColor = '#FF8F00';
      ctx.fill();

      // Inner Flickering Candle Flame
      const flamePulse = Math.sin(this.wobble * 3) * 0.15 + 0.85;
      ctx.beginPath();
      ctx.arc(0, this.size * 0.22, this.size * 0.16 * flamePulse, 0, Math.PI * 2);
      ctx.fillStyle = '#FFFFFF';
      ctx.shadowBlur = 20;
      ctx.shadowColor = '#FFFDE7';
      ctx.fill();

      ctx.restore();
    }
  }

  // --- Main Animation Engine Initialization ---
  function initEngine() {
    // 1. Fireworks Overlay Canvas
    const canvas = document.createElement('canvas');
    canvas.id = 'diwali-canvas-layer';
    canvas.style.position = 'fixed';
    canvas.style.top = '0';
    canvas.style.left = '0';
    canvas.style.width = '100vw';
    canvas.style.height = '100vh';
    canvas.style.pointerEvents = 'none';
    canvas.style.zIndex = '35';
    document.body.appendChild(canvas);

    const ctx = canvas.getContext('2d');

    function resize() {
      canvas.width = window.innerWidth;
      canvas.height = window.innerHeight;
    }
    window.addEventListener('resize', resize);
    resize();

    // Spawn 10 background sky lanterns
    for (let i = 0; i < 10; i++) {
      lanterns.push(new SkyLantern(canvas.width, canvas.height));
    }

    // High-performance Render Loop
    function render() {
      // 1. Trail fade pass (Persistence of Vision)
      ctx.globalCompositeOperation = 'source-over';
      ctx.fillStyle = 'rgba(2, 6, 23, 0.24)';
      ctx.fillRect(0, 0, canvas.width, canvas.height);

      if (CONFIG.festiveActive) {
        // 2. Ambient Screen Flash
        if (flashAlpha > 0.005) {
          ctx.save();
          ctx.globalAlpha = flashAlpha;
          ctx.fillStyle = flashColor;
          ctx.fillRect(0, 0, canvas.width, canvas.height);
          ctx.restore();
          flashAlpha *= 0.87;
        }

        // 3. Sky Lanterns
        for (let i = 0; i < lanterns.length; i++) {
          lanterns[i].update(canvas.height);
          lanterns[i].draw(ctx);
        }

        // 4. Volumetric Smoke (Source-Over)
        for (let i = smokePuffs.length - 1; i >= 0; i--) {
          smokePuffs[i].update();
          smokePuffs[i].draw(ctx);
          if (smokePuffs[i].alpha <= 0) smokePuffs.splice(i, 1);
        }

        // 5. Red Paper Confetti (Ladi remnants)
        for (let i = confettiBits.length - 1; i >= 0; i--) {
          const c = confettiBits[i];
          c.age++;
          c.x += c.vx;
          c.y += c.vy;
          c.vy += 0.09;
          c.rotation += c.spin;
          c.alpha = 1 - (c.age / c.life);
          if (c.age >= c.life) {
            confettiBits.splice(i, 1);
            continue;
          }
          ctx.save();
          ctx.translate(c.x, c.y);
          ctx.rotate((c.rotation * Math.PI) / 180);
          ctx.fillStyle = c.color;
          ctx.globalAlpha = Math.max(0, c.alpha);
          ctx.fillRect(-c.size / 2, -c.size / 2, c.size, c.size * 0.7);
          ctx.restore();
        }

        // 6. Pyrotechnic Sparks & Rockets with ADDITIVE BLENDING (Photorealistic Bloom!)
        ctx.globalCompositeOperation = 'lighter';

        // Draw Ground Chakris
        for (let i = chakris.length - 1; i >= 0; i--) {
          chakris[i].update();
          chakris[i].draw(ctx);
          if (chakris[i].dead) chakris.splice(i, 1);
        }

        // Draw Ground Anars
        for (let i = groundFountains.length - 1; i >= 0; i--) {
          groundFountains[i].update();
          groundFountains[i].draw(ctx);
          if (groundFountains[i].dead) groundFountains.splice(i, 1);
        }

        // Draw Rockets
        for (let i = rockets.length - 1; i >= 0; i--) {
          rockets[i].update();
          rockets[i].draw(ctx);
          if (rockets[i].dead) rockets.splice(i, 1);
        }

        // Draw Sparks
        for (let i = particles.length - 1; i >= 0; i--) {
          particles[i].update();
          particles[i].draw(ctx);
          if (particles[i].age >= particles[i].life) particles.splice(i, 1);
        }
      }

      requestAnimationFrame(render);
    }
    requestAnimationFrame(render);

    // Auto launch celebratory shells every ~3.2s
    setInterval(() => {
      if (!CONFIG.festiveActive || document.hidden) return;
      const startX = Math.random() * (canvas.width * 0.75) + (canvas.width * 0.12);
      const targetX = startX + (Math.random() - 0.5) * 220;
      const targetY = Math.random() * (canvas.height * 0.42) + 75;
      rockets.push(new Rocket(startX, canvas.height, targetX, targetY));
    }, CONFIG.autoFireInterval);

    // Click anywhere to launch a firework rocket directly to that spot
    document.addEventListener('click', (e) => {
      if (!CONFIG.festiveActive) return;
      const t = e.target;
      if (t.tagName === 'INPUT' || t.tagName === 'TEXTAREA' || t.tagName === 'SELECT' || t.closest('button')) return;
      const startX = e.clientX + (Math.random() - 0.5) * 80;
      rockets.push(new Rocket(startX, canvas.height, e.clientX, e.clientY));
    });

    injectStyles();
    injectUI();
  }

  // --- Dynamic CSS 3D Styles ---
  function injectStyles() {
    if (document.getElementById('diwali-realistic-styles')) return;
    const style = document.createElement('style');
    style.id = 'diwali-realistic-styles';
    style.textContent = `
      /* True 3D Terracotta Diya with Interactive Parallax */
      .diwali-3d-diya-stage {
        perspective: 900px;
        display: inline-flex;
        flex-direction: column;
        align-items: center;
        justify-content: flex-end;
        width: 100px;
        height: 100px;
        cursor: pointer;
        user-select: none;
        transition: transform 0.25s cubic-bezier(0.2, 0.8, 0.2, 1);
        position: relative;
      }
      .diwali-3d-diya-stage:hover {
        transform: scale(1.15) translateY(-6px);
      }
      .diwali-3d-diya-stage:active {
        transform: scale(0.92);
      }

      /* 3D Container with Dynamic Tilt */
      .diya-3d-wrapper {
        position: relative;
        width: 100%;
        height: 100%;
        transform-style: preserve-3d;
        display: flex;
        align-items: center;
        justify-content: center;
      }

      /* Clay Terracotta Bowl */
      .diya-earthen-bowl {
        position: relative;
        width: 82px;
        height: 32px;
        background: radial-gradient(ellipse at 50% 20%, #C85A27 0%, #872E0F 55%, #461405 100%);
        border-radius: 0 0 75px 75px / 0 0 40px 40px;
        box-shadow: 
          0 14px 30px rgba(0, 0, 0, 0.85),
          inset 0 4px 10px rgba(255, 180, 50, 0.5),
          inset 0 -5px 10px rgba(0, 0, 0, 0.85);
        border: 1px solid rgba(255, 185, 60, 0.4);
        margin-top: 26px;
      }

      /* Ghee Reservoir & Hand-Carved Rim */
      .diya-oil-rim {
        position: absolute;
        top: -9px;
        left: 2px;
        width: 78px;
        height: 18px;
        background: radial-gradient(ellipse at 50% 50%, #522C00 0%, #2E1500 50%, #D45400 82%, #8E2B00 100%);
        border-radius: 50%;
        border: 1px solid rgba(255, 215, 0, 0.5);
        box-shadow: inset 0 2px 6px rgba(255, 215, 0, 0.3);
      }

      /* Charred Cotton Wick */
      .diya-cotton-wick {
        position: absolute;
        top: -6px;
        left: 34px;
        width: 10px;
        height: 10px;
        background: radial-gradient(circle, #150A00 45%, #663300 100%);
        border-radius: 3px;
        box-shadow: 0 0 4px rgba(255, 100, 0, 0.6);
      }

      /* Layer 1: Photorealistic Fluid Flickering Flame */
      .diya-flame-envelope {
        position: absolute;
        bottom: 30px;
        left: 28px;
        width: 26px;
        height: 46px;
        background: linear-gradient(to top, rgba(255,255,255,0.98) 0%, #FFE066 22%, #FF9900 58%, #E63900 90%, transparent 100%);
        border-radius: 50% 50% 35% 35% / 80% 80% 25% 25%;
        filter: 
          drop-shadow(0 0 8px #FF8C00) 
          drop-shadow(0 0 20px #FF4500) 
          drop-shadow(0 0 38px #FFD700);
        animation: realisticFlameFlutter 1.6s infinite ease-in-out alternate;
        transform-origin: 50% 92%;
        pointer-events: none;
      }

      /* Layer 2: Blue Combustion Base (Real hydrocarbon flame signature) */
      .diya-flame-blue-base {
        position: absolute;
        bottom: -2px;
        left: 7px;
        width: 12px;
        height: 8px;
        background: radial-gradient(ellipse at 50% 100%, #00C8FF 0%, rgba(30,144,255,0.65) 55%, transparent 100%);
        border-radius: 50%;
        filter: blur(1px);
      }

      /* Layer 3: White-Hot Core */
      .diya-flame-hot-core {
        position: absolute;
        bottom: 5px;
        left: 7px;
        width: 12px;
        height: 22px;
        background: radial-gradient(ellipse at 50% 85%, #FFFFFF 0%, #FFF3B0 60%, transparent 100%);
        border-radius: 50% 50% 35% 35% / 80% 80% 25% 25%;
        animation: realisticCorePulse 0.8s infinite ease-in-out alternate;
      }

      /* Ambient Pulsing Glow */
      .diya-radiation-aura {
        position: absolute;
        bottom: 12px;
        left: 10px;
        width: 80px;
        height: 80px;
        background: radial-gradient(circle, rgba(255, 180, 0, 0.45) 0%, rgba(255, 70, 0, 0.18) 55%, transparent 75%);
        border-radius: 50%;
        animation: auraBreathing 2.0s infinite ease-in-out alternate;
        pointer-events: none;
      }

      /* Ascending Micro Sparkles */
      .diya-micro-ember {
        position: absolute;
        width: 3px;
        height: 3px;
        background: #FFD700;
        border-radius: 50%;
        box-shadow: 0 0 6px #FF8C00;
        animation: ascendEmber 1.8s infinite ease-out;
        opacity: 0;
      }
      .diya-micro-ember:nth-child(1) { left: 35px; animation-delay: 0.2s; }
      .diya-micro-ember:nth-child(2) { left: 45px; animation-delay: 0.8s; }
      .diya-micro-ember:nth-child(3) { left: 52px; animation-delay: 1.4s; }

      @keyframes ascendEmber {
        0% { transform: translateY(0) scale(1); opacity: 0.9; }
        50% { transform: translateY(-25px) translateX(4px) scale(0.8); opacity: 0.6; }
        100% { transform: translateY(-50px) translateX(-6px) scale(0.2); opacity: 0; }
      }

      @keyframes realisticFlameFlutter {
        0% { transform: scale(1) rotate(-1.5deg); }
        22% { transform: scale(1.08, 0.93) rotate(2.2deg) skewX(2.5deg); }
        48% { transform: scale(0.94, 1.06) rotate(-3.0deg); }
        72% { transform: scale(1.05, 0.97) rotate(2.0deg) skewX(-1.8deg); }
        100% { transform: scale(1.02, 1.02) rotate(-1.0deg); }
      }

      @keyframes realisticCorePulse {
        0% { transform: scale(0.94); opacity: 0.95; }
        50% { transform: scale(1.09, 0.95); opacity: 0.8; }
        100% { transform: scale(0.97, 1.05); opacity: 1; }
      }

      @keyframes auraBreathing {
        0% { transform: scale(0.85); opacity: 0.5; }
        100% { transform: scale(1.25); opacity: 0.95; }
      }

      /* Festive Action Bar */
      .diwali-ribbon {
        background: linear-gradient(90deg, #BF360C 0%, #4A148C 50%, #BF360C 100%);
        border-bottom: 1px solid rgba(255, 215, 0, 0.45);
        padding: 9px 16px;
        color: #FFFDE7;
        font-size: 13px;
        font-weight: 600;
        display: flex;
        flex-wrap: wrap;
        align-items: center;
        justify-content: center;
        gap: 10px;
        box-shadow: 0 4px 22px rgba(255, 110, 0, 0.35);
        position: relative;
        z-index: 50;
      }

      .festive-action-btn {
        background: linear-gradient(135deg, #FF6F00 0%, #C2185B 100%);
        border: 1px solid rgba(255, 215, 0, 0.55);
        color: #FFFFFF;
        font-weight: 700;
        font-size: 11.5px;
        padding: 5px 13px;
        border-radius: 9999px;
        display: inline-flex;
        align-items: center;
        gap: 5px;
        cursor: pointer;
        box-shadow: 0 0 14px rgba(255, 140, 0, 0.5);
        transition: all 0.22s cubic-bezier(0.34, 1.56, 0.64, 1);
      }
      .festive-action-btn:hover {
        transform: translateY(-2px) scale(1.06);
        box-shadow: 0 0 22px rgba(255, 170, 0, 0.75);
      }
      .festive-action-btn:active {
        transform: scale(0.94);
      }
    `;
    document.head.appendChild(style);
  }

  // --- Create Interactive 3D Terracotta Diya Component ---
  function createRealisticDiya() {
    const stage = document.createElement('div');
    stage.className = 'diwali-3d-diya-stage';
    stage.title = 'Click to ignite golden Anar fountain!';

    const wrapper = document.createElement('div');
    wrapper.className = 'diya-3d-wrapper';

    const aura = document.createElement('div');
    aura.className = 'diya-radiation-aura';

    const ember1 = document.createElement('div');
    ember1.className = 'diya-micro-ember';
    const ember2 = document.createElement('div');
    ember2.className = 'diya-micro-ember';
    const ember3 = document.createElement('div');
    ember3.className = 'diya-micro-ember';

    const flame = document.createElement('div');
    flame.className = 'diya-flame-envelope';

    const blueBase = document.createElement('div');
    blueBase.className = 'diya-flame-blue-base';
    flame.appendChild(blueBase);

    const hotCore = document.createElement('div');
    hotCore.className = 'diya-flame-hot-core';
    flame.appendChild(hotCore);

    const bowl = document.createElement('div');
    bowl.className = 'diya-earthen-bowl';

    const rim = document.createElement('div');
    rim.className = 'diya-oil-rim';

    const wick = document.createElement('div');
    wick.className = 'diya-cotton-wick';
    rim.appendChild(wick);
    bowl.appendChild(rim);

    wrapper.appendChild(aura);
    wrapper.appendChild(ember1);
    wrapper.appendChild(ember2);
    wrapper.appendChild(ember3);
    wrapper.appendChild(flame);
    wrapper.appendChild(bowl);
    stage.appendChild(wrapper);

    // 3D Mouse Parallax Tilt
    stage.addEventListener('mousemove', (e) => {
      const rect = stage.getBoundingClientRect();
      const x = (e.clientX - rect.left) / rect.width - 0.5;
      const y = (e.clientY - rect.top) / rect.height - 0.5;
      wrapper.style.transform = `rotateY(${x * 32}deg) rotateX(${-y * 24}deg)`;
    });

    stage.addEventListener('mouseleave', () => {
      wrapper.style.transform = 'rotateY(0deg) rotateX(0deg)';
    });

    // Clicking Diya ignites a towering golden Anar fountain!
    stage.addEventListener('click', (e) => {
      e.stopPropagation();
      const rect = stage.getBoundingClientRect();
      groundFountains.push(new GroundAnar(rect.left + rect.width / 2, rect.top + rect.height / 2));
    });

    return stage;
  }

  // --- Inject Festive Navigation & Controls ---
  function injectUI() {
    const existingRibbon = document.querySelector('.diwali-ribbon') || document.querySelector('.diwali-celebration-banner');
    if (existingRibbon) existingRibbon.remove();

    const header = document.querySelector('header');
    const ribbon = document.createElement('div');
    ribbon.className = 'diwali-ribbon';
    ribbon.innerHTML = `
      <div class="flex items-center gap-2">
        <span class="text-xl animate-bounce">🪔</span>
        <span><strong>Shubh Deepavali!</strong> Celebrate Zenith '26 with Photorealistic 3D Fireworks & Diyas. Tap anywhere to launch rockets!</span>
      </div>
      <div class="flex items-center gap-2 flex-wrap">
        <button id="btn-firework-burst" class="festive-action-btn">
          <span>🚀 Sky-Shot</span>
        </button>
        <button id="btn-anar-fountain" class="festive-action-btn">
          <span>✨ Anar Fountain</span>
        </button>
        <button id="btn-chakri-wheel" class="festive-action-btn">
          <span>🌀 Ground Chakri</span>
        </button>
        <button id="btn-ladi-cracker" class="festive-action-btn">
          <span>💥 100-Wala Ladi</span>
        </button>
        <button id="btn-audio-toggle" class="px-3 py-1 text-xs rounded-full bg-slate-900/80 hover:bg-slate-800 text-amber-300 border border-amber-500/40 transition-all font-semibold">
          <span id="audio-status-label">${CONFIG.soundEnabled ? '🔊 Sound ON' : '🔇 Sound OFF'}</span>
        </button>
      </div>
    `;

    if (header && header.parentNode) {
      header.parentNode.insertBefore(ribbon, header);
    } else {
      document.body.prepend(ribbon);
    }

    // Connect action triggers
    document.getElementById('btn-firework-burst')?.addEventListener('click', (e) => {
      e.stopPropagation();
      const startX = window.innerWidth / 2 + (Math.random() - 0.5) * 180;
      const targetX = window.innerWidth / 2 + (Math.random() - 0.5) * 380;
      const targetY = 110 + Math.random() * 140;
      rockets.push(new Rocket(startX, window.innerHeight, targetX, targetY));
    });

    document.getElementById('btn-anar-fountain')?.addEventListener('click', (e) => {
      e.stopPropagation();
      const rect = e.target.getBoundingClientRect();
      groundFountains.push(new GroundAnar(rect.left + rect.width / 2, rect.top));
    });

    document.getElementById('btn-chakri-wheel')?.addEventListener('click', (e) => {
      e.stopPropagation();
      chakris.push(new GroundChakri(window.innerWidth / 2 + (Math.random() - 0.5) * 120, window.innerHeight - 85));
    });

    document.getElementById('btn-ladi-cracker')?.addEventListener('click', (e) => {
      e.stopPropagation();
      new CrackerLadi(window.innerWidth / 2, window.innerHeight * 0.35);
    });

    document.getElementById('btn-audio-toggle')?.addEventListener('click', (e) => {
      e.stopPropagation();
      CONFIG.soundEnabled = !CONFIG.soundEnabled;
      try {
        localStorage.setItem('zenith_diwali_sound', CONFIG.soundEnabled ? 'true' : 'false');
      } catch (err) {}
      const lbl = document.getElementById('audio-status-label');
      if (lbl) lbl.textContent = CONFIG.soundEnabled ? '🔊 Sound ON' : '🔇 Sound OFF';
      if (CONFIG.soundEnabled) getAudioContext();
    });

    // 2. Insert 3D Terracotta Diyas in the Hero Section
    const heroSection = document.querySelector('section');
    if (heroSection) {
      // Clear any prior diya elements
      heroSection.querySelectorAll('.diya-row-container').forEach(el => el.remove());

      const heroContent = heroSection.querySelector('.max-w-7xl') || heroSection;
      const diyaRow = document.createElement('div');
      diyaRow.className = 'diya-row-container flex items-center justify-center gap-6 sm:gap-16 my-6';

      const leftDiya = createRealisticDiya();
      const rightDiya = createRealisticDiya();

      const caption = document.createElement('div');
      caption.className = 'text-center';
      caption.innerHTML = `
        <span class="text-xs uppercase tracking-widest text-amber-400 font-bold block mb-1">✨ PHOTOREALISTIC 3D DIYAS & FIREWORKS ✨</span>
        <span class="text-xs sm:text-sm text-slate-300 font-medium">Click on any 3D Diya to ignite an authentic Diwali Anar spark fountain!</span>
      `;

      diyaRow.appendChild(leftDiya);
      diyaRow.appendChild(caption);
      diyaRow.appendChild(rightDiya);

      const title = heroContent.querySelector('h1');
      if (title && title.parentNode) {
        title.parentNode.insertBefore(diyaRow, title.nextSibling);
      } else {
        heroContent.appendChild(diyaRow);
      }
    }
  }

  // Initialize once DOM is ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initEngine);
  } else {
    initEngine();
  }
})();
