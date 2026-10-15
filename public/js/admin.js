/**
 * Zenith '26 College Fest Management System
 * Admin Operations & Monitoring (frontend/js/admin.js)
 */

// State
let allEvents = [];
let allRegistrations = [];
let activeTab = 'events'; // 'events' | 'registrations'

// DOM Elements - Stats
const statTotalEvents = document.getElementById('stat-total-events');
const statTotalRegistrations = document.getElementById('stat-total-registrations');
const statUtilizationPct = document.getElementById('stat-utilization-pct');
const statCapacityRatio = document.getElementById('stat-capacity-ratio');
const statUtilizationBar = document.getElementById('stat-utilization-bar');
const statUpcomingEvents = document.getElementById('stat-upcoming-events');
const categoryBreakdownContainer = document.getElementById('category-breakdown-container');
const lastUpdatedText = document.getElementById('last-updated-text');
const refreshAllBtn = document.getElementById('refresh-all-btn');
const refreshIcon = document.getElementById('refresh-icon');

// DOM Elements - Tabs & Views
const tabEventsBtn = document.getElementById('tab-events-btn');
const tabRegistrationsBtn = document.getElementById('tab-registrations-btn');
const viewEventsTable = document.getElementById('view-events-table');
const viewRegistrationsTable = document.getElementById('view-registrations-table');
const tabEventsCount = document.getElementById('tab-events-count');
const tabRegsCount = document.getElementById('tab-regs-count');

// DOM Elements - Tables
const eventsTableBody = document.getElementById('events-table-body');
const registrationsTableBody = document.getElementById('registrations-table-body');
const filterEventSelect = document.getElementById('filter-event-select');
const regsTableCount = document.getElementById('regs-table-count');

// DOM Elements - Add Event Modal (Stretch Goal)
const addEventModal = document.getElementById('add-event-modal');
const addModalBackdrop = document.getElementById('add-modal-backdrop');
const openAddEventBtn = document.getElementById('open-add-event-btn');
const closeAddModalBtn = document.getElementById('close-add-modal-btn');
const addEventForm = document.getElementById('add-event-form');
const addEventAlert = document.getElementById('add-event-alert');

// Category Badge Color
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

function getLocalEventsList() {
  let list;
  try { list = JSON.parse(localStorage.getItem('zenith_local_events')); } catch (e) { list = null; }
  if (!list || !Array.isArray(list) || list.length === 0) {
    list = JSON.parse(JSON.stringify(SEED_EVENTS));
    try { localStorage.setItem('zenith_local_events', JSON.stringify(list)); } catch (e) {}
  }
  return list;
}

function getLocalRegistrationsList() {
  try {
    return JSON.parse(localStorage.getItem('zenith_local_registrations')) || [];
  } catch (e) {
    return [];
  }
}

function computeLocalStats() {
  const events = getLocalEventsList();
  const regs = getLocalRegistrationsList();
  const totalCap = events.reduce((sum, e) => sum + e.capacity, 0);
  const utilPct = totalCap > 0 ? Math.round(((regs.length / totalCap) * 100) * 10) / 10 : 0;
  const catCounts = {};
  events.forEach(e => {
    catCounts[e.category] = (catCounts[e.category] || 0) + 1;
  });
  return {
    total_events: events.length,
    total_registrations: regs.length,
    total_capacity: totalCap,
    overall_utilization_pct: utilPct,
    upcoming_events_count: events.length,
    events_by_category: catCounts
  };
}

// 1. Fetch and Display Stats (GET /api/stats)
async function loadStats() {
  const candidates = [
    '/api/stats',
    'http://localhost:8001/api/stats',
    'https://fruit-colleague-brooklyn-threatening.trycloudflare.com/api/stats'
  ];

  let stats = null;
  for (const url of candidates) {
    try {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 2000);
      const res = await fetch(url, { signal: controller.signal });
      clearTimeout(timeoutId);
      if (res.ok) {
        stats = await res.json();
        break;
      }
    } catch (e) {}
  }

  if (!stats) {
    stats = computeLocalStats();
  }

  statTotalEvents.textContent = stats.total_events;
  statTotalRegistrations.textContent = stats.total_registrations;
  statUtilizationPct.textContent = `${stats.overall_utilization_pct}%`;
  statCapacityRatio.textContent = `${stats.total_registrations} / ${stats.total_capacity} seats`;
  statUtilizationBar.style.width = `${Math.min(100, stats.overall_utilization_pct)}%`;
  statUpcomingEvents.textContent = stats.upcoming_events_count;

  renderCategoryBreakdown(stats.events_by_category);
  lastUpdatedText.textContent = `Updated at ${new Date().toLocaleTimeString()}`;
}

// Render category breakdown counts
function renderCategoryBreakdown(categories) {
  categoryBreakdownContainer.innerHTML = '';
  const entries = Object.entries(categories);
  if (entries.length === 0) {
    categoryBreakdownContainer.innerHTML =
      '<span class="text-xs text-slate-500">No categorized events found.</span>';
    return;
  }

  entries.forEach(([cat, count]) => {
    const badge = document.createElement('div');
    badge.className = `flex items-center gap-2 px-3 py-1.5 rounded-xl border ${getCategoryBadgeClass(
      cat
    )} bg-slate-900/80 text-xs font-semibold shadow-sm`;
    badge.innerHTML = `
      <span>${escapeHtml(cat)}</span>
      <span class="w-5 h-5 rounded-full bg-slate-800 text-slate-200 flex items-center justify-center font-bold text-[11px]">${count}</span>
    `;
    categoryBreakdownContainer.appendChild(badge);
  });
}

// 2. Fetch and Display Events Table (GET /api/events)
async function loadEvents() {
  eventsTableBody.innerHTML = `
    <tr>
      <td colspan="7" class="py-12 text-center text-slate-400">
        <div class="flex items-center justify-center gap-2">
          <div class="w-4 h-4 border-2 border-purple-500 border-t-transparent rounded-full animate-spin"></div>
          <span>Loading event utilization metrics...</span>
        </div>
      </td>
    </tr>
  `;

  const candidates = [
    '/api/events',
    'http://localhost:8001/api/events',
    'https://fruit-colleague-brooklyn-threatening.trycloudflare.com/api/events'
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
        break;
      }
    } catch (e) {}
  }

  if (!events || !Array.isArray(events) || events.length === 0) {
    events = getLocalEventsList();
  }

  allEvents = events;
  tabEventsCount.textContent = events.length;
  populateEventFilterDropdown(events);

  const eventsWithUtil = events.map((ev) => {
    const registeredCount = ev.capacity - ev.available_seats;
    const utilPct = ev.capacity > 0 ? (registeredCount / ev.capacity) * 100 : 0;
    return {
      ...ev,
      registeredCount,
      utilPct: Math.round(utilPct * 10) / 10,
    };
  });

  eventsWithUtil.sort((a, b) => b.utilPct - a.utilPct);
  renderEventsTable(eventsWithUtil);
}

function renderEventsTable(events) {
  eventsTableBody.innerHTML = '';
  if (events.length === 0) {
    eventsTableBody.innerHTML = `
      <tr>
        <td colspan="7" class="py-8 text-center text-slate-500">No events found in the database.</td>
      </tr>
    `;
    return;
  }

  events.forEach((ev) => {
    const isHighDemand = ev.utilPct >= 90;
    const isFull = ev.available_seats === 0;

    const row = document.createElement('tr');
    row.className = `transition-colors hover:bg-slate-800/40 ${
      isHighDemand ? 'bg-amber-500/5 hover:bg-amber-500/10' : ''
    }`;

    row.innerHTML = `
      <td class="py-3 px-4 font-semibold text-white">
        <div class="flex items-center gap-2.5">
          <span class="text-xl p-1 bg-slate-800 rounded-lg">${ev.icon || '⚡'}</span>
          <div>
            <div class="font-bold text-slate-100">${escapeHtml(ev.title)}</div>
            <div class="text-[11px] text-slate-400">ID #${ev.id}</div>
          </div>
        </div>
      </td>
      <td class="py-3 px-4">
        <span class="px-2 py-0.5 rounded-full text-[11px] font-semibold border ${getCategoryBadgeClass(
          ev.category
        )}">
          ${escapeHtml(ev.category)}
        </span>
      </td>
      <td class="py-3 px-4 text-slate-300">
        <div>${ev.event_date}</div>
        <div class="text-[11px] text-slate-400">${ev.event_time}</div>
      </td>
      <td class="py-3 px-4 text-slate-300">
        ${escapeHtml(ev.venue)}
      </td>
      <td class="py-3 px-4">
        <span class="font-bold ${isHighDemand ? 'text-amber-400' : 'text-slate-200'}">
          ${ev.registeredCount}
        </span>
        <span class="text-slate-500">/ ${ev.capacity}</span>
      </td>
      <td class="py-3 px-4">
        <div class="flex items-center gap-2 min-w-[120px]">
          <div class="w-16 bg-slate-800 h-2 rounded-full overflow-hidden">
            <div class="h-full ${
              isFull ? 'bg-red-500' : isHighDemand ? 'bg-amber-400' : 'bg-purple-500'
            }" style="width: ${Math.min(100, ev.utilPct)}%"></div>
          </div>
          <span class="text-xs font-bold ${
            isHighDemand ? 'text-amber-400 font-extrabold' : 'text-slate-300'
          }">
            ${ev.utilPct}%
          </span>
        </div>
      </td>
      <td class="py-3 px-4">
        ${
          isFull
            ? `<span class="px-2 py-0.5 rounded-md text-[10px] font-extrabold uppercase bg-red-500/20 text-red-400 border border-red-500/30">FULL</span>`
            : isHighDemand
            ? `<span class="px-2 py-0.5 rounded-md text-[10px] font-extrabold uppercase bg-amber-500/20 text-amber-300 border border-amber-500/30">⚠️ &gt;90% Cap</span>`
            : `<span class="px-2 py-0.5 rounded-md text-[10px] font-medium bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">Open (${ev.available_seats})</span>`
        }
      </td>
    `;
    eventsTableBody.appendChild(row);
  });
}

// Populate the event filter dropdown
function populateEventFilterDropdown(events) {
  const currentSelected = filterEventSelect.value;
  filterEventSelect.innerHTML = '<option value="">All Events</option>';
  events.forEach((ev) => {
    const opt = document.createElement('option');
    opt.value = ev.id;
    opt.textContent = `${ev.icon || ''} ${ev.title} (ID: ${ev.id})`;
    filterEventSelect.appendChild(opt);
  });
  if (currentSelected) {
    filterEventSelect.value = currentSelected;
  }
}

// 3. Fetch and Display Registrations (GET /api/registrations)
async function loadRegistrations(eventId = '') {
  registrationsTableBody.innerHTML = `
    <tr>
      <td colspan="8" class="py-12 text-center text-slate-400">
        <div class="flex items-center justify-center gap-2">
          <div class="w-4 h-4 border-2 border-purple-500 border-t-transparent rounded-full animate-spin"></div>
          <span>Loading registration records...</span>
        </div>
      </td>
    </tr>
  `;

  const qs = eventId ? `?event_id=${eventId}` : '';
  const candidates = [
    `/api/registrations${qs}`,
    `http://localhost:8001/api/registrations${qs}`,
    `https://fruit-colleague-brooklyn-threatening.trycloudflare.com/api/registrations${qs}`
  ];

  let registrations = null;
  for (const url of candidates) {
    try {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 2000);
      const res = await fetch(url, { signal: controller.signal });
      clearTimeout(timeoutId);
      if (res.ok) {
        registrations = await res.json();
        break;
      }
    } catch (e) {}
  }

  if (!registrations || !Array.isArray(registrations)) {
    const localRegs = getLocalRegistrationsList();
    registrations = eventId ? localRegs.filter(r => r.event_id === parseInt(eventId)) : localRegs;
  }

  allRegistrations = registrations;
  tabRegsCount.textContent = registrations.length;
  regsTableCount.textContent = `Showing ${registrations.length} registration${
    registrations.length === 1 ? '' : 's'
  }`;

  renderRegistrationsTable(registrations);
}

function renderRegistrationsTable(registrations) {
  registrationsTableBody.innerHTML = '';
  if (registrations.length === 0) {
    registrationsTableBody.innerHTML = `
      <tr>
        <td colspan="8" class="py-12 text-center text-slate-500">
          No registrations found for this selection.
        </td>
      </tr>
    `;
    return;
  }

  registrations.forEach((reg) => {
    const row = document.createElement('tr');
    row.className = 'transition-colors hover:bg-slate-800/40';

    row.innerHTML = `
      <td class="py-3 px-4 font-mono text-slate-400">#${reg.id}</td>
      <td class="py-3 px-4 font-semibold text-white">${escapeHtml(reg.student_name)}</td>
      <td class="py-3 px-4 font-mono text-purple-300 text-xs">${escapeHtml(reg.roll_number)}</td>
      <td class="py-3 px-4 text-slate-300">
        <a href="mailto:${encodeURIComponent(reg.email)}" class="hover:underline hover:text-purple-300">${escapeHtml(reg.email)}</a>
      </td>
      <td class="py-3 px-4 text-slate-400">${reg.phone ? escapeHtml(reg.phone) : '—'}</td>
      <td class="py-3 px-4">
        <span class="font-medium text-slate-200">${escapeHtml(reg.event_title)}</span>
      </td>
      <td class="py-3 px-4 text-xs text-slate-400 whitespace-nowrap">${reg.registered_at}</td>
      <td class="py-3 px-4 text-right">
        <button 
          data-reg-id="${reg.id}" 
          data-reg-name="${escapeHtml(reg.student_name)}" 
          data-reg-event="${escapeHtml(reg.event_title)}"
          class="delete-reg-btn px-2.5 py-1 rounded-lg text-xs font-semibold text-red-400 hover:text-white bg-red-500/10 hover:bg-red-600 border border-red-500/20 active:scale-95 transition-all"
          title="Cancel registration"
        >
          Cancel
        </button>
      </td>
    `;
    registrationsTableBody.appendChild(row);
  });

  // Attach delete buttons
  document.querySelectorAll('.delete-reg-btn').forEach((btn) => {
    btn.addEventListener('click', () => {
      const regId = btn.getAttribute('data-reg-id');
      const regName = btn.getAttribute('data-reg-name');
      const regEvent = btn.getAttribute('data-reg-event');
      handleDeleteRegistration(regId, regName, regEvent);
    });
  });
}

// 4. Cancel Registration (Stretch Goal 2: DELETE /api/registrations/{id})
async function handleDeleteRegistration(regId, studentName, eventTitle) {
  const confirmed = confirm(
    `Are you sure you want to cancel the registration for "${studentName}" in "${eventTitle}"?\n\nThis will immediately free up one seat for the event.`
  );
  if (!confirmed) return;

  try {
    const res = await fetch(`/api/registrations/${regId}`, {
      method: 'DELETE',
    });

    if (res.ok) {
      // Re-fetch all data to ensure immediate seat count update
      await loadStats();
      await loadEvents();
      await loadRegistrations(filterEventSelect.value);
    } else {
      const data = await res.json();
      alert(`Error cancelling registration: ${data.detail || res.statusText}`);
    }
  } catch (err) {
    console.error('Delete registration error:', err);
    alert('Network error while cancelling registration.');
  }
}

// 5. Add Event (Stretch Goal 1: POST /api/events)
addEventForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  addEventAlert.className = 'hidden';

  const title = document.getElementById('add-title').value.trim();
  const category = document.getElementById('add-category').value;
  const icon = document.getElementById('add-icon').value.trim() || '⚡';
  const desc = document.getElementById('add-desc').value.trim();
  const date = document.getElementById('add-date').value.trim();
  const time = document.getElementById('add-time').value.trim();
  const venue = document.getElementById('add-venue').value.trim();
  const capacity = parseInt(document.getElementById('add-capacity').value, 10);
  const fee = parseInt(document.getElementById('add-fee').value, 10) || 0;

  if (!title || !category || !venue || !date || !time) {
    showAddEventAlert('Please fill in all required fields.', false);
    return;
  }

  if (capacity <= 0) {
    showAddEventAlert('Capacity must be greater than zero.', false);
    return;
  }

  const payload = {
    title,
    category,
    description: desc,
    event_date: date,
    event_time: time,
    venue,
    capacity,
    fee,
    icon,
  };

  try {
    const res = await fetch('/api/events', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });

    if (res.status === 201) {
      showAddEventAlert('Event created successfully!', true);
      addEventForm.reset();
      await loadStats();
      await loadEvents();
      setTimeout(() => {
        closeAddModal();
      }, 1000);
    } else {
      const data = await res.json();
      showAddEventAlert(data.detail || 'Failed to create event.', false);
    }
  } catch (err) {
    console.error('Add event error:', err);
    showAddEventAlert('Network error creating event.', false);
  }
});

function showAddEventAlert(message, isSuccess) {
  addEventAlert.className = `p-3 rounded-xl text-xs font-semibold ${
    isSuccess
      ? 'bg-emerald-500/10 text-emerald-300 border border-emerald-500/30'
      : 'bg-red-500/10 text-red-300 border border-red-500/30'
  }`;
  addEventAlert.textContent = message;
}

// Modal Handlers
openAddEventBtn.addEventListener('click', () => {
  addEventAlert.className = 'hidden';
  addEventModal.classList.remove('hidden');
  document.body.classList.add('overflow-hidden');
});

function closeAddModal() {
  addEventModal.classList.add('hidden');
  document.body.classList.remove('overflow-hidden');
}

closeAddModalBtn.addEventListener('click', closeAddModal);
addModalBackdrop.addEventListener('click', closeAddModal);

// Filter Dropdown change
filterEventSelect.addEventListener('change', () => {
  loadRegistrations(filterEventSelect.value);
});

// Refresh button
refreshAllBtn.addEventListener('click', async () => {
  refreshIcon.classList.add('animate-spin');
  await loadStats();
  await loadEvents();
  await loadRegistrations(filterEventSelect.value);
  setTimeout(() => {
    refreshIcon.classList.remove('animate-spin');
  }, 500);
});

// Tabs Switching
tabEventsBtn.addEventListener('click', () => {
  activeTab = 'events';
  tabEventsBtn.className =
    'tab-btn active-tab pb-3 text-sm font-semibold border-b-2 border-purple-500 text-purple-400 flex items-center gap-2';
  tabRegistrationsBtn.className =
    'tab-btn pb-3 text-sm font-semibold border-b-2 border-transparent text-slate-400 hover:text-slate-200 flex items-center gap-2';
  viewEventsTable.classList.remove('hidden');
  viewRegistrationsTable.classList.add('hidden');
});

tabRegistrationsBtn.addEventListener('click', () => {
  activeTab = 'registrations';
  tabRegistrationsBtn.className =
    'tab-btn active-tab pb-3 text-sm font-semibold border-b-2 border-purple-500 text-purple-400 flex items-center gap-2';
  tabEventsBtn.className =
    'tab-btn pb-3 text-sm font-semibold border-b-2 border-transparent text-slate-400 hover:text-slate-200 flex items-center gap-2';
  viewRegistrationsTable.classList.remove('hidden');
  viewEventsTable.classList.add('hidden');
  loadRegistrations(filterEventSelect.value);
});

// Escape HTML utility
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
document.addEventListener('DOMContentLoaded', async () => {
  await loadStats();
  await loadEvents();
  await loadRegistrations();
});
