/**
 * Zenith '26 — 3D Diwali Celebrations Engine
 * Features:
 * 1. 3D Fireworks & Cracker Bursts (Rockets, Anar fountains, Chakri spinners, Sparklers)
 * 2. 3D Glowing Diya Lamps with realistic flickering flames & golden aura
 * 3. Floating 3D Sky Lanterns (Kandils) with warm ambient light
 * 4. Synthesized Festive Audio (Web Audio API - no external sound files needed)
 * 5. Interactive click-to-launch fireworks anywhere on the screen
 */

(function () {
  'use strict';

  // --- Configuration ---
  const CONFIG = {
    autoFireInterval: 3200, // milliseconds between auto fireworks
    maxParticles: 400,
    soundEnabled: true,
    festiveActive: true
  };

  // Web Audio Synthesizer for Festive Cracker Sounds
  let audioCtx = null;
  function getAudioContext() {
    if (!audioCtx) {
      const AudioContext = window.AudioContext || window.webkitAudioContext;
      if (AudioContext) audioCtx = new AudioContext();
    }
    if (audioCtx && audioCtx.state === 'suspended') {
      audioCtx.resume();
    }
    return audioCtx;
  }

  function playCrackerSound(type) {
    if (!CONFIG.soundEnabled) return;
    try {
      const ctx = getAudioContext();
      if (!ctx) return;

      const now = ctx.currentTime;
      if (type === 'whistle') {
        // Rocket launch whistle
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();
        osc.type = 'sine';
        osc.frequency.setValueAtTime(400, now);
        osc.frequency.exponentialRampToValueAtTime(1400, now + 0.35);
        gain.gain.setValueAtTime(0.04, now);
        gain.gain.exponentialRampToValueAtTime(0.001, now + 0.35);
        osc.connect(gain);
        gain.connect(ctx.destination);
        osc.start(now);
        osc.stop(now + 0.35);
      } else if (type === 'boom') {
        // Cracker explosion burst
        const bufferSize = ctx.sampleRate * 0.4;
        const buffer = ctx.createBuffer(1, bufferSize, ctx.sampleRate);
        const data = buffer.getChannelData(0);
        for (let i = 0; i < bufferSize; i++) {
          data[i] = (Math.random() * 2 - 1) * Math.exp(-i / (ctx.sampleRate * 0.08));
        }
        const noise = ctx.createBufferSource();
        noise.buffer = buffer;
        const filter = ctx.createBiquadFilter();
        filter.type = 'lowpass';
        filter.frequency.setValueAtTime(450, now);
        filter.frequency.exponentialRampToValueAtTime(80, now + 0.35);
        const gain = ctx.createGain();
        gain.gain.setValueAtTime(0.12, now);
        gain.gain.exponentialRampToValueAtTime(0.001, now + 0.38);
        noise.connect(filter);
        filter.connect(gain);
        gain.connect(ctx.destination);
        noise.start(now);
      } else if (type === 'sparkle') {
        // Sparkler sparkle sound
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();
        osc.type = 'triangle';
        osc.frequency.setValueAtTime(800 + Math.random() * 600, now);
        gain.gain.setValueAtTime(0.02, now);
        gain.gain.exponentialRampToValueAtTime(0.001, now + 0.1);
        osc.connect(gain);
        gain.connect(ctx.destination);
        osc.start(now);
        osc.stop(now + 0.1);
      }
    } catch (e) {
      // Audio not permitted without user gesture, fail silently
    }
  }

  // --- Particle Systems ---
  const particles = [];
  const rockets = [];
  const lanterns = [];

  const COLOR_PALETTES = [
    ['#FFD700', '#FFA500', '#FF4500', '#FF6347', '#FFE4B5'], // Golden Diwali
    ['#FF1493', '#C71585', '#9400D3', '#BA55D3', '#E6E6FA'], // Magenta & Purple
    ['#00FF7F', '#32CD32', '#00FA9A', '#7CFC00', '#ADFF2F'], // Emerald Spark
    ['#00BFFF', '#1E90FF', '#4169E1', '#87CEEB', '#FFFFFF'], // Electric Blue
    ['#FF8C00', '#FFD700', '#FF0000', '#FFFFFF', '#FFF8DC']  // Traditional Fire
  ];

  class Rocket {
    constructor(startX, startY, targetX, targetY, palette) {
      this.x = startX;
      this.y = startY;
      this.targetX = targetX;
      this.targetY = targetY;
      const angle = Math.atan2(targetY - startY, targetX - startX);
      const speed = 12 + Math.random() * 4;
      this.vx = Math.cos(angle) * speed;
      this.vy = Math.sin(angle) * speed;
      this.palette = palette || COLOR_PALETTES[Math.floor(Math.random() * COLOR_PALETTES.length)];
      this.color = this.palette[0];
      this.trail = [];
      this.dead = false;
      playCrackerSound('whistle');
    }

    update() {
      this.trail.push({ x: this.x, y: this.y, alpha: 0.9 });
      if (this.trail.length > 8) this.trail.shift();

      this.x += this.vx;
      this.y += this.vy;
      this.vy += 0.08; // mild gravity

      // Check if reached apex or target height
      if (this.vy >= 0 || this.y <= this.targetY) {
        this.dead = true;
        this.explode();
      }
    }

    draw(ctx) {
      ctx.save();
      for (let i = 0; i < this.trail.length; i++) {
        const pt = this.trail[i];
        ctx.beginPath();
        ctx.arc(pt.x, pt.y, 2, 0, Math.PI * 2);
        ctx.fillStyle = `rgba(255, 200, 100, ${pt.alpha * (i / this.trail.length)})`;
        ctx.shadowBlur = 8;
        ctx.shadowColor = '#FFA500';
        ctx.fill();
      }
      ctx.beginPath();
      ctx.arc(this.x, this.y, 3, 0, Math.PI * 2);
      ctx.fillStyle = '#FFFFFF';
      ctx.shadowBlur = 12;
      ctx.shadowColor = '#FFD700';
      ctx.fill();
      ctx.restore();
    }

    explode() {
      playCrackerSound('boom');
      const particleCount = 80 + Math.floor(Math.random() * 40);
      const style = Math.random();

      for (let i = 0; i < particleCount; i++) {
        let vx, vy;
        if (style > 0.4) {
          // Spherical 3D burst
          const angle = Math.random() * Math.PI * 2;
          const speed = (Math.random() * 7 + 2);
          const spreadZ = (Math.random() * 2 - 1); // 3D depth vector
          vx = Math.cos(angle) * speed;
          vy = Math.sin(angle) * speed;
        } else {
          // Heart or Ring formation
          const angle = (i / particleCount) * Math.PI * 2;
          const speed = 4 + Math.random() * 2;
          vx = Math.cos(angle) * speed;
          vy = Math.sin(angle) * speed;
        }

        const color = this.palette[Math.floor(Math.random() * this.palette.length)];
        particles.push(new Particle(this.x, this.y, vx, vy, color));
      }

      // Add a flash ring
      particles.push(new Shockwave(this.x, this.y, this.palette[0]));
    }
  }

  class Particle {
    constructor(x, y, vx, vy, color) {
      this.x = x;
      this.y = y;
      this.vx = vx;
      this.vy = vy;
      this.color = color;
      this.alpha = 1;
      this.decay = 0.012 + Math.random() * 0.015;
      this.friction = 0.95;
      this.gravity = 0.12;
      this.size = 2.5 + Math.random() * 2.5;
      this.flicker = Math.random() > 0.3;
    }

    update() {
      this.vx *= this.friction;
      this.vy *= this.friction;
      this.vy += this.gravity;
      this.x += this.vx;
      this.y += this.vy;
      this.alpha -= this.decay;
    }

    draw(ctx) {
      if (this.alpha <= 0) return;
      ctx.save();
      const currentAlpha = this.flicker && Math.random() > 0.4 ? this.alpha * 0.6 : this.alpha;
      ctx.globalAlpha = Math.max(0, currentAlpha);
      ctx.beginPath();
      ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2);
      ctx.fillStyle = this.color;
      ctx.shadowBlur = 10;
      ctx.shadowColor = this.color;
      ctx.fill();
      ctx.restore();
    }
  }

  class Shockwave {
    constructor(x, y, color) {
      this.x = x;
      this.y = y;
      this.color = color;
      this.radius = 2;
      this.maxRadius = 60 + Math.random() * 30;
      this.alpha = 0.8;
    }

    update() {
      this.radius += 4;
      this.alpha = Math.max(0, 1 - (this.radius / this.maxRadius));
    }

    draw(ctx) {
      if (this.alpha <= 0) return;
      ctx.save();
      ctx.globalAlpha = this.alpha * 0.5;
      ctx.beginPath();
      ctx.arc(this.x, this.y, this.radius, 0, Math.PI * 2);
      ctx.strokeStyle = this.color;
      ctx.lineWidth = 2.5;
      ctx.shadowBlur = 15;
      ctx.shadowColor = this.color;
      ctx.stroke();
      ctx.restore();
    }
  }

  // 3D Floating Lantern (Sky Kandil)
  class SkyLantern {
    constructor(canvasWidth, canvasHeight) {
      this.x = Math.random() * canvasWidth;
      this.y = canvasHeight + 40 + Math.random() * 100;
      this.vx = (Math.random() - 0.5) * 0.5;
      this.vy = -(0.5 + Math.random() * 0.6);
      this.size = 18 + Math.random() * 14;
      this.alpha = 0.85;
      this.wobble = Math.random() * Math.PI * 2;
      this.wobbleSpeed = 0.02 + Math.random() * 0.02;
    }

    update(canvasHeight) {
      this.wobble += this.wobbleSpeed;
      this.x += this.vx + Math.sin(this.wobble) * 0.4;
      this.y += this.vy;
      if (this.y < -60) {
        this.y = canvasHeight + 40;
        this.x = Math.random() * window.innerWidth;
      }
    }

    draw(ctx) {
      ctx.save();
      ctx.globalAlpha = this.alpha;
      ctx.translate(this.x, this.y);

      // Lantern Body
      const gradient = ctx.createLinearGradient(0, -this.size, 0, this.size);
      gradient.addColorStop(0, '#FF4500');
      gradient.addColorStop(0.6, '#FFA500');
      gradient.addColorStop(1, '#FFD700');

      ctx.beginPath();
      ctx.moveTo(-this.size * 0.4, -this.size * 0.6);
      ctx.lineTo(this.size * 0.4, -this.size * 0.6);
      ctx.lineTo(this.size * 0.55, this.size * 0.5);
      ctx.lineTo(-this.size * 0.55, this.size * 0.5);
      ctx.closePath();
      ctx.fillStyle = gradient;
      ctx.shadowBlur = 24;
      ctx.shadowColor = '#FF8C00';
      ctx.fill();

      // Flame core
      ctx.beginPath();
      ctx.arc(0, this.size * 0.25, this.size * 0.18, 0, Math.PI * 2);
      ctx.fillStyle = '#FFFFFF';
      ctx.shadowBlur = 15;
      ctx.shadowColor = '#FFFFE0';
      ctx.fill();

      ctx.restore();
    }
  }

  // --- Ground Anar / Flower Pot Fountain ---
  function launchAnarFountain(originX, originY) {
    playCrackerSound('sparkle');
    const sparkCount = 60;
    for (let i = 0; i < sparkCount; i++) {
      setTimeout(() => {
        const spread = (Math.random() - 0.5) * 4;
        const upwardSpeed = -(6 + Math.random() * 7);
        const p = new Particle(originX, originY, spread, upwardSpeed, Math.random() > 0.3 ? '#FFD700' : '#FFFFFF');
        p.decay = 0.025;
        p.gravity = 0.18;
        particles.push(p);
      }, i * 20);
    }
  }

  // --- Main Engine Initialization ---
  function initDiwaliEngine() {
    // 1. Create transparent background canvas for 3D Cracker Fireworks
    const canvas = document.createElement('canvas');
    canvas.id = 'diwali-fireworks-canvas';
    canvas.style.position = 'fixed';
    canvas.style.top = '0';
    canvas.style.left = '0';
    canvas.style.width = '100vw';
    canvas.style.height = '100vh';
    canvas.style.pointerEvents = 'none'; // let user click through to buttons
    canvas.style.zIndex = '35'; // above background, below modals and interactive buttons
    document.body.appendChild(canvas);

    const ctx = canvas.getContext('2d');

    function resize() {
      canvas.width = window.innerWidth;
      canvas.height = window.innerHeight;
    }
    window.addEventListener('resize', resize);
    resize();

    // Initialize 12 background sky lanterns
    for (let i = 0; i < 12; i++) {
      lanterns.push(new SkyLantern(canvas.width, canvas.height));
    }

    // Main Animation Loop
    function animate() {
      ctx.clearRect(0, 0, canvas.width, canvas.height);

      if (CONFIG.festiveActive) {
        // Draw & update lanterns
        for (let i = 0; i < lanterns.length; i++) {
          lanterns[i].update(canvas.height);
          lanterns[i].draw(ctx);
        }

        // Update & draw active rockets
        for (let i = rockets.length - 1; i >= 0; i--) {
          rockets[i].update();
          rockets[i].draw(ctx);
          if (rockets[i].dead) {
            rockets.splice(i, 1);
          }
        }

        // Update & draw particles
        for (let i = particles.length - 1; i >= 0; i--) {
          const p = particles[i];
          p.update();
          p.draw(ctx);
          if (p.alpha <= 0) {
            particles.splice(i, 1);
          }
        }
      }

      requestAnimationFrame(animate);
    }
    requestAnimationFrame(animate);

    // Auto-fire celebratory rockets
    setInterval(() => {
      if (!CONFIG.festiveActive) return;
      if (document.hidden) return;
      const startX = Math.random() * (canvas.width * 0.8) + (canvas.width * 0.1);
      const targetX = startX + (Math.random() - 0.5) * 160;
      const targetY = Math.random() * (canvas.height * 0.45) + 80;
      rockets.push(new Rocket(startX, canvas.height, targetX, targetY));
    }, CONFIG.autoFireInterval);

    // Click anywhere on body (except on interactive inputs/buttons) to launch user's firework rocket
    document.addEventListener('click', (e) => {
      if (!CONFIG.festiveActive) return;
      const target = e.target;
      // Do not intercept input fields, select dropdowns, or forms
      if (target.tagName === 'INPUT' || target.tagName === 'TEXTAREA' || target.tagName === 'SELECT') {
        return;
      }
      const startX = e.clientX + (Math.random() - 0.5) * 60;
      rockets.push(new Rocket(startX, canvas.height, e.clientX, e.clientY));
    });

    // 2. Inject 3D Diya CSS Styles & Animations
    injectDiwaliStyles();

    // 3. Inject Festive Header Bar & 3D Diyas into the Hero section
    injectFestiveUI();
  }

  function injectDiwaliStyles() {
    const style = document.createElement('style');
    style.id = 'diwali-3d-styles';
    style.textContent = `
      /* 3D Diya Container */
      .diya-3d-wrapper {
        position: relative;
        display: inline-flex;
        flex-direction: column;
        align-items: center;
        justify-content: flex-end;
        width: 72px;
        height: 72px;
        cursor: pointer;
        user-select: none;
        perspective: 600px;
        transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
      }
      .diya-3d-wrapper:hover {
        transform: scale(1.15) translateY(-4px);
      }
      .diya-3d-wrapper:active {
        transform: scale(0.95);
      }

      /* 3D Terracotta Earthen Bowl */
      .diya-clay-bowl {
        position: relative;
        width: 60px;
        height: 24px;
        background: radial-gradient(ellipse at 50% 20%, #B85028 0%, #7A280D 60%, #4A1405 100%);
        border-radius: 0 0 50px 50px / 0 0 30px 30px;
        box-shadow: 
          0 8px 20px rgba(0, 0, 0, 0.6),
          inset 0 4px 8px rgba(255, 140, 0, 0.4),
          inset 0 -3px 6px rgba(0, 0, 0, 0.7);
        border: 1px solid rgba(255, 165, 0, 0.3);
      }

      /* Diya Lip / Rim */
      .diya-clay-rim {
        position: absolute;
        top: -6px;
        left: 2px;
        width: 56px;
        height: 12px;
        background: radial-gradient(ellipse at 50% 50%, #D35400 0%, #A04000 70%, #6E2C00 100%);
        border-radius: 50%;
        border: 1px solid rgba(255, 215, 0, 0.4);
      }

      /* Brass Wick Base */
      .diya-wick-base {
        position: absolute;
        top: -3px;
        left: 24px;
        width: 8px;
        height: 6px;
        background: #2C1A0D;
        border-radius: 2px;
      }

      /* Realistic Flickering Flame */
      .diya-flame {
        position: absolute;
        bottom: 22px;
        left: 21px;
        width: 18px;
        height: 32px;
        background: linear-gradient(to top, rgba(255,255,255,0.95) 0%, #FFE066 30%, #FF9900 65%, #FF3300 95%, transparent 100%);
        border-radius: 50% 50% 35% 35% / 80% 80% 25% 25%;
        filter: drop-shadow(0 0 10px #FF9900) drop-shadow(0 0 20px #FF4500) drop-shadow(0 0 35px #FFD700);
        animation: diyaFlameFlicker 1.8s infinite ease-in-out alternate;
        transform-origin: 50% 90%;
      }

      /* Inner Flame Core */
      .diya-flame-inner {
        position: absolute;
        bottom: 2px;
        left: 4px;
        width: 10px;
        height: 18px;
        background: radial-gradient(ellipse at 50% 90%, #FFFFFF 0%, #FFF275 60%, transparent 100%);
        border-radius: 50% 50% 40% 40% / 80% 80% 30% 30%;
        animation: diyaInnerFlicker 0.9s infinite ease-in-out alternate;
      }

      /* Radiant Warm Ambient Light */
      .diya-aura {
        position: absolute;
        bottom: 10px;
        left: 5px;
        width: 50px;
        height: 50px;
        background: radial-gradient(circle, rgba(255, 170, 0, 0.45) 0%, rgba(255, 69, 0, 0.15) 55%, transparent 75%);
        border-radius: 50%;
        animation: diyaAuraPulse 2.4s infinite ease-in-out alternate;
        pointer-events: none;
      }

      @keyframes diyaFlameFlicker {
        0% { transform: scale(1) rotate(-1.5deg); }
        25% { transform: scale(1.06, 0.95) rotate(2deg) skewX(2deg); }
        50% { transform: scale(0.96, 1.05) rotate(-2.5deg); }
        75% { transform: scale(1.04, 0.98) rotate(1.5deg) skewX(-1.5deg); }
        100% { transform: scale(1.02, 1.02) rotate(-0.5deg); }
      }

      @keyframes diyaInnerFlicker {
        0% { transform: scale(1); opacity: 0.95; }
        50% { transform: scale(1.1, 0.95); opacity: 0.8; }
        100% { transform: scale(0.95, 1.05); opacity: 1; }
      }

      @keyframes diyaAuraPulse {
        0% { transform: scale(0.9); opacity: 0.5; }
        100% { transform: scale(1.2); opacity: 0.85; }
      }

      /* Festive Diwali Ribbon Banner */
      .diwali-celebration-banner {
        background: linear-gradient(90deg, rgba(230,81,0,0.85) 0%, rgba(142,36,170,0.9) 50%, rgba(230,81,0,0.85) 100%);
        border-bottom: 1px solid rgba(255,215,0,0.35);
        color: #FFF8E1;
        font-size: 13px;
        font-weight: 600;
        letter-spacing: 0.4px;
        padding: 8px 16px;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 12px;
        box-shadow: 0 4px 15px rgba(255,140,0,0.25);
        position: relative;
        z-index: 50;
      }

      /* Glowing Diwali Toggle Button */
      .diwali-toggle-btn {
        background: linear-gradient(135deg, #FF6F00 0%, #D81B60 100%);
        border: 1px solid rgba(255,215,0,0.4);
        color: #FFFFFF;
        font-weight: 700;
        font-size: 12px;
        padding: 6px 12px;
        border-radius: 9999px;
        display: inline-flex;
        align-items: center;
        gap: 6px;
        cursor: pointer;
        box-shadow: 0 0 15px rgba(255,140,0,0.4);
        transition: all 0.25s ease;
      }
      .diwali-toggle-btn:hover {
        transform: translateY(-1px) scale(1.04);
        box-shadow: 0 0 25px rgba(255,165,0,0.65);
      }
      .diwali-toggle-btn:active {
        transform: scale(0.96);
      }
    `;
    document.head.appendChild(style);
  }

  function create3DDiyaElement() {
    const wrapper = document.createElement('div');
    wrapper.className = 'diya-3d-wrapper';
    wrapper.title = 'Click to burst Diwali sparkles!';

    const aura = document.createElement('div');
    aura.className = 'diya-aura';

    const flame = document.createElement('div');
    flame.className = 'diya-flame';

    const flameInner = document.createElement('div');
    flameInner.className = 'diya-flame-inner';
    flame.appendChild(flameInner);

    const rim = document.createElement('div');
    rim.className = 'diya-clay-rim';

    const wick = document.createElement('div');
    wick.className = 'diya-wick-base';
    rim.appendChild(wick);

    const bowl = document.createElement('div');
    bowl.className = 'diya-clay-bowl';
    bowl.appendChild(rim);

    wrapper.appendChild(aura);
    wrapper.appendChild(flame);
    wrapper.appendChild(bowl);

    wrapper.addEventListener('click', (e) => {
      e.stopPropagation();
      const rect = wrapper.getBoundingClientRect();
      const centerX = rect.left + rect.width / 2;
      const centerY = rect.top + rect.height / 3;
      launchAnarFountain(centerX, centerY);
    });

    return wrapper;
  }

  function injectFestiveUI() {
    // 1. Insert Festive Banner above header
    const header = document.querySelector('header');
    const banner = document.createElement('div');
    banner.className = 'diwali-celebration-banner';
    banner.innerHTML = `
      <span class="text-base animate-pulse">🪔</span>
      <span><strong>Shubh Deepavali!</strong> Celebrate Zenith '26 with Diwali 3D Fireworks & Diyas. Tap anywhere to burst crackers!</span>
      <button id="burst-cracker-btn" class="diwali-toggle-btn ml-2">
        <span>💥 Burst Cracker</span>
      </button>
      <button id="toggle-sound-btn" class="px-2.5 py-1 text-xs rounded-full bg-slate-900/60 hover:bg-slate-800 text-amber-300 border border-amber-500/30 transition-all">
        <span id="sound-icon">🔊 Sound ON</span>
      </button>
    `;

    if (header && header.parentNode) {
      header.parentNode.insertBefore(banner, header);
    } else {
      document.body.prepend(banner);
    }

    // Connect Burst Cracker button
    const burstBtn = document.getElementById('burst-cracker-btn');
    if (burstBtn) {
      burstBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        const startX = window.innerWidth / 2 + (Math.random() - 0.5) * 200;
        const targetX = window.innerWidth / 2 + (Math.random() - 0.5) * 400;
        const targetY = 150 + Math.random() * 100;
        rockets.push(new Rocket(startX, window.innerHeight, targetX, targetY));
      });
    }

    // Connect Sound toggle
    const soundBtn = document.getElementById('toggle-sound-btn');
    const soundIcon = document.getElementById('sound-icon');
    if (soundBtn && soundIcon) {
      soundBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        CONFIG.soundEnabled = !CONFIG.soundEnabled;
        soundIcon.textContent = CONFIG.soundEnabled ? '🔊 Sound ON' : '🔇 Sound OFF';
        if (CONFIG.soundEnabled) getAudioContext();
      });
    }

    // 2. Place 3D Diyas flanking the Hero Section title
    const heroSection = document.querySelector('section');
    if (heroSection) {
      const heroContent = heroSection.querySelector('.max-w-7xl') || heroSection;
      const diyaContainer = document.createElement('div');
      diyaContainer.className = 'flex items-center justify-center gap-8 sm:gap-16 my-4';

      const leftDiya = create3DDiyaElement();
      const rightDiya = create3DDiyaElement();

      const diyaTagline = document.createElement('div');
      diyaTagline.className = 'text-center';
      diyaTagline.innerHTML = `
        <span class="text-xs uppercase tracking-widest text-amber-400 font-bold block mb-1">✨ FESTIVE CELEBRATION ✨</span>
        <span class="text-sm sm:text-base text-slate-300 font-medium">Click on any Diya lamp to ignite the Diwali Anar fireworks!</span>
      `;

      diyaContainer.appendChild(leftDiya);
      diyaContainer.appendChild(diyaTagline);
      diyaContainer.appendChild(rightDiya);

      const title = heroContent.querySelector('h1');
      if (title && title.parentNode) {
        title.parentNode.insertBefore(diyaContainer, title.nextSibling);
      } else {
        heroContent.appendChild(diyaContainer);
      }
    }
  }

  // Auto initialize when DOM is ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initDiwaliEngine);
  } else {
    initDiwaliEngine();
  }
})();
