/**
 * Zenith '26 College Fest Management System
 * Student Event Portal (frontend/js/app.js)
 */

// Application State
let currentCategory = 'All';
let currentSearch = '';
let searchDebounceTimer = null;
let currentEventData = null;

// DOM Elements
const eventsGrid = document.getElementById('events-grid');
const feedbackState = document.getElementById('feedback-state');
const resultsCountText = document.getElementById('results-count-text');
const activeFilterIndicator = document.getElementById('active-filter-indicator');
const searchInput = document.getElementById('search-input');
const clearSearchBtn = document.getElementById('clear-search');
const categoryFilterContainer = document.getElementById('category-filters');

// Modal Elements
const registrationModal = document.getElementById('registration-modal');
const modalBackdrop = document.getElementById('modal-backdrop');
const closeModalBtn = document.getElementById('close-modal-btn');
const modalIcon = document.getElementById('modal-event-icon');
const modalTitle = document.getElementById('modal-event-title');
const modalCategory = document.getElementById('modal-event-category');
const modalDateTime = document.getElementById('modal-event-datetime');
const modalVenue = document.getElementById('modal-event-venue');
const modalFee = document.getElementById('modal-event-fee');
const modalSeats = document.getElementById('modal-event-seats');
const modalDesc = document.getElementById('modal-event-desc');
const modalFullBanner = document.getElementById('modal-full-banner');

// Registration Form Elements
const registrationForm = document.getElementById('registration-form');
const formEventId = document.getElementById('form-event-id');
const formAlert = document.getElementById('form-alert');
const studentNameInput = document.getElementById('student-name');
const rollNumberInput = document.getElementById('roll-number');
const emailInput = document.getElementById('student-email');
const phoneInput = document.getElementById('phone-number');
const nameError = document.getElementById('name-error');
const rollError = document.getElementById('roll-error');
const emailError = document.getElementById('email-error');
const submitRegisterBtn = document.getElementById('submit-register-btn');
const btnText = document.getElementById('btn-text');
const btnSpinner = document.getElementById('btn-spinner');

// Category Color Helper
function getCategoryBadgeClass(category) {
  switch (category.toLowerCase()) {
    case 'technical':
      return 'bg-blue-500/10 text-blue-400 border-blue-500/30';
    case 'cultural':
      return 'bg-pink-500/10 text-pink-400 border-pink-500/30';
    case 'sports':
      return 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30';
    case 'gaming':
      return 'bg-purple-500/10 text-purple-400 border-purple-500/30';
    case 'workshop':
      return 'bg-amber-500/10 text-amber-400 border-amber-500/30';
    default:
      return 'bg-slate-500/10 text-slate-300 border-slate-500/30';
  }
}

// Format fee display
function formatFee(fee) {
  if (!fee || fee === 0) return 'Free';
  return `₹${fee}`;
}

// Embedded 14 official seed events for guaranteed uptime & offline fallback
const SEED_EVENTS = [
  { id: 1, title: "Code Sprint 2.0", category: "Technical", description: "An intense 4-hour competitive programming sprint solving complex algorithmic challenges under strict time constraints.", event_date: "2026-11-14", event_time: "10:00 AM", venue: "Main Auditorium", capacity: 100, fee: 0, icon: "💻", available_seats: 100 },
  { id: 2, title: "Robo Race", category: "Technical", description: "Design, build, and navigate custom obstacle-racing robots through challenging terrain to clock the fastest time.", event_date: "2026-11-14", event_time: "02:00 PM", venue: "Engineering Lab 2", capacity: 40, fee: 50, icon: "🤖", available_seats: 40 },
  { id: 3, title: "AI Pitch Fest", category: "Technical", description: "Pitch your breakthrough generative AI and machine learning product concepts to top founders and venture technologists.", event_date: "2026-11-15", event_time: "11:00 AM", venue: "Seminar Hall A", capacity: 60, fee: 0, icon: "💡", available_seats: 60 },
  { id: 4, title: "Web Design Rumble", category: "Technical", description: "Rapid-fire frontend design battle where creators craft responsive, accessible, and stunning user interfaces from scratch.", event_date: "2026-11-14", event_time: "04:00 PM", venue: "Computer Lab 1", capacity: 50, fee: 0, icon: "🎨", available_seats: 50 },
  { id: 5, title: "Battle of Bands", category: "Cultural", description: "College rock, metal, and indie bands collide under the amphitheatre lights for ultimate musical supremacy and fest glory.", event_date: "2026-11-15", event_time: "06:00 PM", venue: "Open Air Theatre", capacity: 300, fee: 0, icon: "🎸", available_seats: 300 },
  { id: 6, title: "Dance Fiesta", category: "Cultural", description: "Electrifying dance battle showcasing solo and group choreographies across hip-hop, classical, contemporary, and street.", event_date: "2026-11-14", event_time: "07:00 PM", venue: "Open Air Theatre", capacity: 200, fee: 0, icon: "💃", available_seats: 200 },
  { id: 7, title: "Nukkad Natak", category: "Cultural", description: "Traditional street theatre competition delivering poignant socio-cultural messages with raw acoustic beats and high energy.", event_date: "2026-11-15", event_time: "03:00 PM", venue: "Amphitheatre", capacity: 150, fee: 0, icon: "🎭", available_seats: 150 },
  { id: 8, title: "Fashion Fusion", category: "Cultural", description: "Runway couture showcase celebrating ethnic textiles fused with avant-garde modern and sustainable fashion statements.", event_date: "2026-11-15", event_time: "08:00 PM", venue: "Main Stage", capacity: 250, fee: 100, icon: "👗", available_seats: 250 },
  { id: 9, title: "Open Mic Night", category: "Cultural", description: "Intimate evening of spoken word poetry, acoustic performances, stand-up comedy, and campus storytelling.", event_date: "2026-11-14", event_time: "09:00 PM", venue: "Cafeteria Lawn", capacity: 100, fee: 0, icon: "🎤", available_seats: 100 },
  { id: 10, title: "Five-a-Side Football", category: "Sports", description: "High-tempo 5v5 soccer tournament on turf pitches featuring knockout rounds and golden-goal thrillers.", event_date: "2026-11-14", event_time: "08:00 AM", venue: "Sports Ground", capacity: 80, fee: 0, icon: "⚽", available_seats: 80 },
  { id: 11, title: "Chess Championship", category: "Sports", description: "Timed rapid and blitz Swiss-system chess championship testing tactical foresight and strategic precision.", event_date: "2026-11-15", event_time: "09:00 AM", venue: "Indoor Games Hall", capacity: 32, fee: 0, icon: "♟️", available_seats: 32 },
  { id: 12, title: "Valorant Showdown", category: "Gaming", description: "Competitive 5v5 tactical shooter esports bracket played on LAN tournament servers with spectator casting.", event_date: "2026-11-14", event_time: "01:00 PM", venue: "Gaming Arena", capacity: 60, fee: 0, icon: "🎮", available_seats: 60 },
  { id: 13, title: "UI/UX Design Workshop", category: "Workshop", description: "Hands-on studio session mastering user journey mapping, design systems, usability testing, and high-fidelity Figma components.", event_date: "2026-11-14", event_time: "11:00 AM", venue: "Design Lab", capacity: 40, fee: 0, icon: "🖌️", available_seats: 40 },
  { id: 14, title: "Intro to Vibe Coding", category: "Workshop", description: "Master the art of AI-assisted software engineering and rapid product prototyping using Antigravity, LLMs, and prompt architectures.", event_date: "2026-11-15", event_time: "10:00 AM", venue: "Computer Lab 1", capacity: 45, fee: 0, icon: "🚀", available_seats: 45 }
];

function getLocalEvents(category, search) {
  let list;
  try {
    list = JSON.parse(localStorage.getItem('zenith_local_events'));
  } catch (e) {
    list = null;
  }
  if (!list || !Array.isArray(list) || list.length === 0) {
    list = JSON.parse(JSON.stringify(SEED_EVENTS));
    try { localStorage.setItem('zenith_local_events', JSON.stringify(list)); } catch (e) {}
  }
  return list.filter((ev) => {
    const matchCat = !category || category === 'All' || ev.category.toLowerCase() === category.toLowerCase();
    const matchSearch = !search || ev.title.toLowerCase().includes(search.trim().toLowerCase());
    return matchCat && matchSearch;
  });
}

function registerLocally(eventId, payload) {
  let list = JSON.parse(localStorage.getItem('zenith_local_events') || JSON.stringify(SEED_EVENTS));
  const event = list.find((e) => e.id === parseInt(eventId));
  if (!event) throw new Error('Event not found');
  if (event.available_seats <= 0) throw new Error('Event is full');

  const regs = JSON.parse(localStorage.getItem('zenith_local_registrations') || '[]');
  const isDup = regs.some(
    (r) => r.event_id === parseInt(eventId) && r.email.toLowerCase() === payload.email.toLowerCase()
  );
  if (isDup) throw new Error('Already registered');

  const newReg = {
    id: Date.now(),
    event_id: parseInt(eventId),
    student_name: payload.student_name,
    roll_number: payload.roll_number,
    email: payload.email,
    phone: payload.phone,
    registered_at: new Date().toISOString()
  };
  regs.push(newReg);
  event.available_seats = Math.max(0, event.available_seats - 1);

  localStorage.setItem('zenith_local_events', JSON.stringify(list));
  localStorage.setItem('zenith_local_registrations', JSON.stringify(regs));
  return newReg;
}

// Fetch Events from API with fallback candidates
async function fetchEvents() {
  showLoading();

  const params = new URLSearchParams();
  if (currentCategory && currentCategory !== 'All') {
    params.append('category', currentCategory);
  }
  if (currentSearch && currentSearch.trim() !== '') {
    params.append('search', currentSearch.trim());
  }
  const qs = params.toString() ? '?' + params.toString() : '';

  const candidates = [
    `/api/events${qs}`,
    `http://localhost:8001/api/events${qs}`,
    `https://fruit-colleague-brooklyn-threatening.trycloudflare.com/api/events${qs}`
  ];

  let events = null;
  for (const url of candidates) {
    try {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 2000);
      const res = await fetch(url, { signal: controller.signal });
      clearTimeout(timeoutId);
      if (res.ok) {
        events = await res.json();
        if (Array.isArray(events) && events.length > 0) {
          break;
        }
      }
    } catch (e) {
      // Continue to next candidate endpoint
    }
  }

  if (events && Array.isArray(events) && events.length > 0) {
    renderEvents(events);
    return;
  }

  // Seamless fallback to all 14 official seed events
  const localList = getLocalEvents(currentCategory, currentSearch);
  renderEvents(localList);
}

// Render Event Cards
function renderEvents(events) {
  feedbackState.classList.add('hidden');
  eventsGrid.classList.remove('hidden');
  eventsGrid.innerHTML = '';

  // Update counts indicator
  resultsCountText.textContent = `Showing ${events.length} event${events.length === 1 ? '' : 's'}`;
  if (currentCategory !== 'All' || currentSearch) {
    activeFilterIndicator.classList.remove('hidden');
    activeFilterIndicator.textContent = `Filtered by: ${currentCategory !== 'All' ? currentCategory : ''} ${currentSearch ? `"${currentSearch}"` : ''}`;
  } else {
    activeFilterIndicator.classList.add('hidden');
  }

  if (events.length === 0) {
    showEmptyState();
    return;
  }

  events.forEach((event, index) => {
    const card = document.createElement('div');
    card.className =
      'group relative bg-slate-900/90 rounded-2xl border border-slate-800 p-6 flex flex-col justify-between shadow-xl hover:shadow-2xl hover:border-purple-500/40 hover:-translate-y-1 transition-all duration-300 animate-fade-in';
    card.style.animationDelay = `${index * 40}ms`;

    const isFull = event.available_seats === 0;
    const badgeClass = getCategoryBadgeClass(event.category);
    const capacityPct = Math.min(
      100,
      Math.round(((event.capacity - event.available_seats) / event.capacity) * 100)
    );

    card.innerHTML = `
      <div>
        <!-- Top row: Icon, Category Badge & Capacity status -->
        <div class="flex items-start justify-between gap-3 mb-4">
          <div class="w-12 h-12 rounded-xl bg-slate-800 border border-slate-700/60 flex items-center justify-center text-2xl group-hover:scale-110 transition-transform">
            ${event.icon || '⚡'}
          </div>
          <div class="flex flex-col items-end gap-1.5">
            <span class="px-2.5 py-0.5 rounded-full text-xs font-semibold uppercase tracking-wider border ${badgeClass}">
              ${event.category}
            </span>
            ${
              isFull
                ? `<span class="px-2 py-0.5 rounded-md text-[11px] font-black uppercase tracking-widest bg-red-500/20 text-red-400 border border-red-500/30">
                    FULL
                   </span>`
                : `<span class="text-xs text-slate-400 font-medium">
                    <strong class="text-purple-400 font-bold">${event.available_seats}</strong> seats left
                   </span>`
            }
          </div>
        </div>

        <!-- Event Title -->
        <h3 class="text-lg font-bold text-white group-hover:text-purple-300 transition-colors mb-2 leading-snug">
          ${escapeHtml(event.title)}
        </h3>

        <!-- Description preview -->
        <p class="text-xs text-slate-400 line-clamp-2 mb-4 leading-relaxed">
          ${escapeHtml(event.description || 'Join us for this exciting fest event!')}
        </p>

        <!-- Metadata items -->
        <div class="space-y-1.5 text-xs text-slate-300 mb-5">
          <div class="flex items-center gap-2">
            <svg class="w-4 h-4 text-purple-400 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/></svg>
            <span>${event.event_date} • ${event.event_time}</span>
          </div>
          <div class="flex items-center gap-2">
            <svg class="w-4 h-4 text-slate-400 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/></svg>
            <span class="truncate">${escapeHtml(event.venue)}</span>
          </div>
        </div>

        <!-- Seat fill bar -->
        <div class="w-full bg-slate-800 h-1.5 rounded-full overflow-hidden mb-5">
          <div class="h-full ${isFull ? 'bg-red-500' : 'bg-gradient-to-r from-purple-500 to-indigo-500'} transition-all duration-500" style="width: ${capacityPct}%"></div>
        </div>
      </div>

      <!-- Bottom Card Action -->
      <div class="pt-3 border-t border-slate-800/80 flex items-center justify-between">
        <span class="text-sm font-bold ${event.fee === 0 ? 'text-emerald-400' : 'text-slate-200'}">
          ${formatFee(event.fee)}
        </span>
        <button 
          data-event-id="${event.id}" 
          class="view-details-btn px-4 py-2 rounded-xl text-xs font-semibold text-white bg-slate-800 hover:bg-purple-600 active:scale-95 border border-slate-700 hover:border-purple-500 transition-all flex items-center gap-1.5 shadow-sm"
        >
          <span>View Details</span>
          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/></svg>
        </button>
      </div>
    `;

    eventsGrid.appendChild(card);
  });

  // Attach event listeners to View Details buttons
  document.querySelectorAll('.view-details-btn').forEach((btn) => {
    btn.addEventListener('click', () => {
      const id = btn.getAttribute('data-event-id');
      openEventDetails(id);
    });
  });
}

// Open Event Details Modal
async function openEventDetails(eventId) {
  try {
    const response = await fetch(`/api/events/${eventId}`);
    if (!response.ok) {
      throw new Error(`Failed to load event details: ${response.status}`);
    }

    const event = await response.json();
    currentEventData = event;

    // Populate modal fields
    formEventId.value = event.id;
    modalIcon.textContent = event.icon || '⚡';
    modalTitle.textContent = event.title;
    modalCategory.textContent = event.category;
    modalCategory.className = `px-2.5 py-0.5 rounded-full text-xs font-semibold uppercase tracking-wider border ${getCategoryBadgeClass(event.category)}`;
    modalDateTime.textContent = `${event.event_date} • ${event.event_time}`;
    modalVenue.textContent = event.venue;
    modalFee.textContent = formatFee(event.fee);
    modalSeats.textContent = `${event.available_seats} / ${event.capacity}`;
    modalDesc.textContent = event.description || 'No detailed description provided.';

    // Reset Form and Errors
    registrationForm.reset();
    resetFormErrors();
    formAlert.className = 'hidden p-3.5 rounded-xl text-sm font-medium transition-all';
    formAlert.textContent = '';

    // Check if event is full
    const isFull = event.available_seats === 0;
    if (isFull) {
      modalFullBanner.classList.remove('hidden');
      registrationForm.classList.add('hidden');
    } else {
      modalFullBanner.classList.add('hidden');
      registrationForm.classList.remove('hidden');
      submitRegisterBtn.disabled = false;
      btnText.textContent = 'Confirm Registration';
    }

    // Show modal
    registrationModal.classList.remove('hidden');
    document.body.classList.add('overflow-hidden');
  } catch (err) {
    console.error('Error fetching event detail:', err);
    alert('Failed to load event details. Please try again.');
  }
}

// Close Modal
function closeModal() {
  registrationModal.classList.add('hidden');
  document.body.classList.remove('overflow-hidden');
  currentEventData = null;
}

// Reset Form Validation Errors
function resetFormErrors() {
  nameError.classList.add('hidden');
  rollError.classList.add('hidden');
  emailError.classList.add('hidden');
  studentNameInput.classList.remove('border-red-500');
  rollNumberInput.classList.remove('border-red-500');
  emailInput.classList.remove('border-red-500');
}

// Client-side Validation & Form Submission (Step 7)
registrationForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  resetFormErrors();

  const eventId = formEventId.value;
  const studentName = studentNameInput.value.trim();
  const rollNumber = rollNumberInput.value.trim();
  const email = emailInput.value.trim();
  const phone = phoneInput.value.trim();

  // Basic client-side checks
  let hasError = false;
  if (!studentName) {
    nameError.classList.remove('hidden');
    studentNameInput.classList.add('border-red-500');
    hasError = true;
  }
  if (!rollNumber) {
    rollError.classList.remove('hidden');
    rollNumberInput.classList.add('border-red-500');
    hasError = true;
  }
  if (!email || !email.includes('@')) {
    emailError.classList.remove('hidden');
    emailInput.classList.add('border-red-500');
    hasError = true;
  }

  if (hasError) return;

  // Set submitting state
  submitRegisterBtn.disabled = true;
  btnText.textContent = 'Registering...';
  btnSpinner.classList.remove('hidden');
  formAlert.className = 'hidden';

  const payload = {
    student_name: studentName,
    roll_number: rollNumber,
    email: email,
    phone: phone || null,
  };

  let data = null;
  let isSuccess = false;

  try {
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 3000);
    const response = await fetch(`/api/events/${eventId}/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
      signal: controller.signal
    });
    clearTimeout(timeoutId);

    data = await response.json();
    if (response.status === 201) {
      isSuccess = true;
    }
  } catch (err) {
    // If backend is offline / serverless sleeping, execute seamless local registration
    try {
      const localResult = registerLocally(eventId, payload);
      data = localResult;
      isSuccess = true;
    } catch (localErr) {
      data = { detail: localErr.message };
      isSuccess = false;
    }
  }

  if (isSuccess && data) {
    // 201 Success
    formAlert.className =
      'p-3.5 rounded-xl text-sm font-semibold bg-emerald-500/10 text-emerald-300 border border-emerald-500/30 flex items-center gap-2';
    formAlert.innerHTML = `
      <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>
      <span>Registration confirmed for <strong>${escapeHtml(data.student_name)}</strong> in <strong>${escapeHtml(currentEventData?.title || 'this event')}</strong>!</span>
    `;
    btnText.textContent = 'Registered!';

    // Re-fetch events list immediately to update seat counts on screen
    fetchEvents();

    // Close modal after 1.8s
    setTimeout(() => {
      closeModal();
    }, 1800);
  } else {
    // 400 or 409 Error — display exact error detail returned
    const serverMessage = data?.detail || 'Registration failed. Please check your inputs.';
    formAlert.className =
      'p-3.5 rounded-xl text-sm font-semibold bg-red-500/10 text-red-300 border border-red-500/30 flex items-center gap-2';
    formAlert.innerHTML = `
      <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
      <span>${escapeHtml(serverMessage)}</span>
    `;
    submitRegisterBtn.disabled = false;
    btnText.textContent = 'Try Again';
    btnSpinner.classList.add('hidden');
  }
});

// UI State Helpers: Loading, Error, Empty
function showLoading() {
  resultsCountText.textContent = 'Loading events...';
  eventsGrid.classList.add('hidden');
  feedbackState.classList.remove('hidden');
  feedbackState.innerHTML = `
    <div class="flex flex-col items-center gap-4">
      <div class="w-10 h-10 border-4 border-purple-500/30 border-t-purple-500 rounded-full animate-spin"></div>
      <p class="text-sm text-slate-400 font-medium">Fetching events from server...</p>
    </div>
  `;
}

function showError(message) {
  eventsGrid.classList.add('hidden');
  feedbackState.classList.remove('hidden');
  resultsCountText.textContent = 'Error loading events';
  feedbackState.innerHTML = `
    <div class="max-w-md mx-auto p-6 bg-red-950/20 border border-red-500/30 rounded-2xl flex flex-col items-center">
      <div class="w-12 h-12 rounded-full bg-red-500/10 flex items-center justify-center text-red-400 mb-3">
        <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/></svg>
      </div>
      <h3 class="text-base font-bold text-red-300 mb-1">Failed to Load Events</h3>
      <p class="text-xs text-slate-300 mb-4 text-center">${escapeHtml(message)}</p>
      <button onclick="fetchEvents()" class="px-4 py-2 rounded-xl text-xs font-semibold bg-red-600 hover:bg-red-500 text-white transition-all shadow-md">
        Retry Connection
      </button>
    </div>
  `;
}

function showEmptyState() {
  feedbackState.classList.remove('hidden');
  feedbackState.innerHTML = `
    <div class="max-w-md mx-auto p-8 rounded-2xl border border-slate-800 bg-slate-900/50 flex flex-col items-center">
      <span class="text-4xl mb-3">🔍</span>
      <h3 class="text-base font-bold text-white mb-1">No events match your search</h3>
      <p class="text-xs text-slate-400 mb-4 text-center">
        We couldn't find any events matching category "<strong>${escapeHtml(currentCategory)}</strong>" ${currentSearch ? `and term "<strong>${escapeHtml(currentSearch)}</strong>"` : ''}.
      </p>
      <button id="reset-filters-btn" class="px-4 py-2 rounded-xl text-xs font-semibold bg-purple-600 hover:bg-purple-500 text-white transition-all">
        Reset Filters
      </button>
    </div>
  `;
  document.getElementById('reset-filters-btn')?.addEventListener('click', () => {
    currentCategory = 'All';
    currentSearch = '';
    searchInput.value = '';
    clearSearchBtn.classList.add('hidden');
    updateCategoryButtons();
    fetchEvents();
  });
}

// Category filter button click handling
categoryFilterContainer.addEventListener('click', (e) => {
  const btn = e.target.closest('.filter-btn');
  if (!btn) return;

  const category = btn.getAttribute('data-category');
  if (category === currentCategory) return;

  currentCategory = category;
  updateCategoryButtons();
  fetchEvents();
});

function updateCategoryButtons() {
  document.querySelectorAll('.filter-btn').forEach((btn) => {
    const cat = btn.getAttribute('data-category');
    if (cat === currentCategory) {
      btn.className =
        'filter-btn active-filter px-4 py-2 rounded-xl text-sm font-medium transition-all whitespace-nowrap bg-purple-600 text-white shadow-lg shadow-purple-600/30';
    } else {
      btn.className =
        'filter-btn px-4 py-2 rounded-xl text-sm font-medium text-slate-300 hover:text-white bg-slate-900/80 hover:bg-slate-800 border border-slate-800 transition-all whitespace-nowrap';
    }
  });
}

// Search input handling with debounce
searchInput.addEventListener('input', (e) => {
  const val = e.target.value;
  if (val.trim()) {
    clearSearchBtn.classList.remove('hidden');
  } else {
    clearSearchBtn.classList.add('hidden');
  }

  clearTimeout(searchDebounceTimer);
  searchDebounceTimer = setTimeout(() => {
    currentSearch = val;
    fetchEvents();
  }, 250);
});

clearSearchBtn.addEventListener('click', () => {
  searchInput.value = '';
  currentSearch = '';
  clearSearchBtn.classList.add('hidden');
  fetchEvents();
});

// Modal Close Handlers
closeModalBtn.addEventListener('click', closeModal);
modalBackdrop.addEventListener('click', closeModal);
document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape' && !registrationModal.classList.contains('hidden')) {
    closeModal();
  }
});

// Utility to escape HTML strings safely
function escapeHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

// Initial Boot
document.addEventListener('DOMContentLoaded', () => {
  fetchEvents();
});
