import sqlite3
from contextlib import asynccontextmanager
from datetime import datetime
from pathlib import Path
from typing import Dict, List, Optional

from fastapi import FastAPI, HTTPException, Query, status
from fastapi.exceptions import RequestValidationError
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import FileResponse, JSONResponse
from fastapi.staticfiles import StaticFiles

from backend.database import get_db_connection, init_db
from backend.models import (
    EventCreateIn,
    EventDetail,
    EventOut,
    RegistrationIn,
    RegistrationItemOut,
    RegistrationOut,
    StatsOut,
)
from backend.seed_data import seed_events

BASE_DIR = Path(__file__).resolve().parent.parent
FRONTEND_DIR = BASE_DIR / "frontend"


@asynccontextmanager
async def lifespan(app: FastAPI):
    # Ensure database tables exist and seed events on startup
    init_db()
    try:
        seed_events()
    except Exception as e:
        print(f"Warning during seed: {e}")
    yield


app = FastAPI(
    title="Zenith '26 Fest Management System API",
    description="REST API for Zenith '26 College Fest event portal and admin dashboard.",
    version="1.0.0",
    lifespan=lifespan,
)

# Custom exception handler for body validation to match PM2-02 400 specification
@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request, exc):
    return JSONResponse(
        status_code=status.HTTP_400_BAD_REQUEST,
        content={"detail": "Missing or invalid required fields in request body"},
    )

# Enable CORS for local development
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ==========================================
# API Endpoints
# ==========================================


@app.get(
    "/api/events",
    response_model=List[EventOut],
    summary="List events with live seat calculation",
)
def get_events(
    category: Optional[str] = Query(None, description="Filter events by category"),
    search: Optional[str] = Query(
        None, description="Search events by title (case-insensitive)"
    ),
):
    """Retrieve list of events with live available_seats count."""
    conn = get_db_connection()
    cursor = conn.cursor()

    query = """
        SELECT e.id, e.title, e.category, e.description, e.event_date, e.event_time,
               e.venue, e.capacity, e.fee, e.icon,
               (e.capacity - COALESCE(COUNT(r.id), 0)) AS available_seats
        FROM events e
        LEFT JOIN registrations r ON e.id = r.event_id
    """
    conditions = []
    params = []

    if category and category.strip().lower() != "all":
        conditions.append("LOWER(e.category) = LOWER(?)")
        params.append(category.strip())

    if search and search.strip():
        conditions.append("LOWER(e.title) LIKE LOWER(?)")
        params.append(f"%{search.strip()}%")

    if conditions:
        query += " WHERE " + " AND ".join(conditions)

    query += " GROUP BY e.id ORDER BY e.id ASC"

    cursor.execute(query, params)
    rows = cursor.fetchall()
    conn.close()

    events = [
        EventOut(
            id=row["id"],
            title=row["title"],
            category=row["category"],
            description=row["description"],
            event_date=row["event_date"],
            event_time=row["event_time"],
            venue=row["venue"],
            capacity=row["capacity"],
            fee=row["fee"],
            icon=row["icon"],
            available_seats=max(0, row["available_seats"]),
        )
        for row in rows
    ]
    return events


@app.get(
    "/api/events/{event_id}",
    response_model=EventDetail,
    summary="Get single event details",
)
def get_event_detail(event_id: int):
    """Retrieve a single event's details by id or return 404."""
    conn = get_db_connection()
    cursor = conn.cursor()

    query = """
        SELECT e.id, e.title, e.category, e.description, e.event_date, e.event_time,
               e.venue, e.capacity, e.fee, e.icon,
               (e.capacity - COALESCE(COUNT(r.id), 0)) AS available_seats
        FROM events e
        LEFT JOIN registrations r ON e.id = r.event_id
        WHERE e.id = ?
        GROUP BY e.id
    """
    cursor.execute(query, (event_id,))
    row = cursor.fetchone()
    conn.close()

    if not row:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Event with id {event_id} does not exist",
        )

    return EventDetail(
        id=row["id"],
        title=row["title"],
        category=row["category"],
        description=row["description"],
        event_date=row["event_date"],
        event_time=row["event_time"],
        venue=row["venue"],
        capacity=row["capacity"],
        fee=row["fee"],
        icon=row["icon"],
        available_seats=max(0, row["available_seats"]),
    )


@app.post(
    "/api/events/{event_id}/register",
    response_model=RegistrationOut,
    status_code=status.HTTP_201_CREATED,
    summary="Register a student for an event",
)
def register_for_event(event_id: int, payload: RegistrationIn):
    """
    Server-side validation and registration.
    Checks:
    - Event exists (404)
    - Name, roll number, and email are not empty (400)
    - Email contains '@' (400)
    - Event is not full (409)
    - Same email has not already registered for this event (409)
    """
    conn = get_db_connection()
    cursor = conn.cursor()

    # 1. Verify event exists
    cursor.execute("SELECT id, capacity FROM events WHERE id = ?", (event_id,))
    event = cursor.fetchone()
    if not event:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Event with id {event_id} not found",
        )

    # 2. Server-side string field validation
    student_name = payload.student_name.strip() if payload.student_name else ""
    roll_number = payload.roll_number.strip() if payload.roll_number else ""
    email = payload.email.strip().lower() if payload.email else ""
    phone = payload.phone.strip() if payload.phone else None

    if not student_name:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Student name cannot be empty",
        )
    if not roll_number:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Roll number cannot be empty",
        )
    if not email:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Email cannot be empty",
        )

    # 3. Server-side email format validation
    if "@" not in email:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid email: must contain '@'",
        )

    # 4. Check capacity (live count)
    cursor.execute(
        "SELECT COUNT(*) AS reg_count FROM registrations WHERE event_id = ?",
        (event_id,),
    )
    reg_count = cursor.fetchone()["reg_count"]
    if reg_count >= event["capacity"]:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Event is full",
        )

    # 5. Check duplicate registration by email
    cursor.execute(
        "SELECT id FROM registrations WHERE event_id = ? AND LOWER(email) = ?",
        (event_id, email),
    )
    duplicate = cursor.fetchone()
    if duplicate:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Already registered for this event with this email",
        )

    # 6. Insert registration
    registered_at = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    cursor.execute(
        """
        INSERT INTO registrations (event_id, student_name, roll_number, email, phone, registered_at)
        VALUES (?, ?, ?, ?, ?, ?)
        """,
        (event_id, student_name, roll_number, email, phone, registered_at),
    )
    conn.commit()
    new_reg_id = cursor.lastrowid
    conn.close()

    return RegistrationOut(
        id=new_reg_id,
        event_id=event_id,
        student_name=student_name,
        registered_at=registered_at,
    )


@app.get(
    "/api/registrations",
    response_model=List[RegistrationItemOut],
    summary="List registrations with event titles",
)
def get_registrations(
    event_id: Optional[int] = Query(None, description="Filter registrations by event id"),
):
    """Retrieve registrations, optionally filtered by event_id."""
    conn = get_db_connection()
    cursor = conn.cursor()

    query = """
        SELECT r.id, r.event_id, r.student_name, r.roll_number, r.email, r.phone, r.registered_at,
               e.title AS event_title
        FROM registrations r
        JOIN events e ON r.event_id = e.id
    """
    params = []
    if event_id:
        query += " WHERE r.event_id = ?"
        params.append(event_id)

    query += " ORDER BY r.id DESC"

    cursor.execute(query, params)
    rows = cursor.fetchall()
    conn.close()

    return [
        RegistrationItemOut(
            id=row["id"],
            event_id=row["event_id"],
            student_name=row["student_name"],
            roll_number=row["roll_number"],
            email=row["email"],
            phone=row["phone"],
            registered_at=row["registered_at"],
            event_title=row["event_title"],
        )
        for row in rows
    ]


@app.get(
    "/api/stats",
    response_model=StatsOut,
    summary="Live fest metrics for admin dashboard",
)
def get_stats():
    """Compute fest stats live from SQLite without stale cached values."""
    conn = get_db_connection()
    cursor = conn.cursor()

    # Total events and capacity
    cursor.execute("SELECT COUNT(*) AS total_events, COALESCE(SUM(capacity), 0) AS total_capacity FROM events")
    event_stats = cursor.fetchone()
    total_events = event_stats["total_events"]
    total_capacity = event_stats["total_capacity"]

    # Total registrations
    cursor.execute("SELECT COUNT(*) AS total_regs FROM registrations")
    total_regs = cursor.fetchone()["total_regs"]

    # Overall utilization pct
    utilization_pct = 0.0
    if total_capacity > 0:
        utilization_pct = round((total_regs / total_capacity) * 100.0, 1)

    # Upcoming events count (date >= today's ISO date)
    today_iso = datetime.now().strftime("%Y-%m-%d")
    cursor.execute("SELECT COUNT(*) AS upcoming FROM events WHERE event_date >= ?", (today_iso,))
    upcoming_events_count = cursor.fetchone()["upcoming"]

    # Events by category
    cursor.execute("SELECT category, COUNT(*) AS count FROM events GROUP BY category ORDER BY count DESC")
    cat_rows = cursor.fetchall()
    events_by_category: Dict[str, int] = {r["category"]: r["count"] for r in cat_rows}

    conn.close()

    return StatsOut(
        total_events=total_events,
        total_registrations=total_regs,
        total_capacity=total_capacity,
        overall_utilization_pct=utilization_pct,
        upcoming_events_count=upcoming_events_count,
        events_by_category=events_by_category,
    )


# ==========================================
# Stretch Goal Endpoints
# ==========================================


@app.post(
    "/api/events",
    response_model=EventOut,
    status_code=status.HTTP_201_CREATED,
    summary="Add a new event (Admin Stretch Goal)",
)
def create_event(payload: EventCreateIn):
    """Admin endpoint to create a new fest event with server validation."""
    title = payload.title.strip()
    category = payload.category.strip()
    venue = payload.venue.strip()

    if not title or not category or not venue:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Title, category, and venue are required",
        )
    if payload.capacity <= 0:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Capacity must be greater than zero",
        )

    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute(
        """
        INSERT INTO events (title, category, description, event_date, event_time, venue, capacity, fee, icon)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """,
        (
            title,
            category,
            payload.description or "",
            payload.event_date.strip(),
            payload.event_time.strip(),
            venue,
            payload.capacity,
            payload.fee,
            payload.icon or "🎉",
        ),
    )
    conn.commit()
    new_event_id = cursor.lastrowid
    conn.close()

    return EventOut(
        id=new_event_id,
        title=title,
        category=category,
        description=payload.description or "",
        event_date=payload.event_date.strip(),
        event_time=payload.event_time.strip(),
        venue=venue,
        capacity=payload.capacity,
        fee=payload.fee,
        icon=payload.icon or "🎉",
        available_seats=payload.capacity,
    )


@app.delete(
    "/api/registrations/{registration_id}",
    summary="Cancel a registration (Admin Stretch Goal)",
)
def delete_registration(registration_id: int):
    """Admin endpoint to cancel a student registration."""
    conn = get_db_connection()
    cursor = conn.cursor()

    cursor.execute("SELECT id FROM registrations WHERE id = ?", (registration_id,))
    existing = cursor.fetchone()
    if not existing:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Registration with id {registration_id} not found",
        )

    cursor.execute("DELETE FROM registrations WHERE id = ?", (registration_id,))
    conn.commit()
    conn.close()

    return {
        "detail": "Registration successfully cancelled",
        "id": registration_id,
    }


# ==========================================
# Frontend Static Mounting (Step 4)
# ==========================================

# Mount /js directory for JS assets if frontend exists
if (FRONTEND_DIR / "js").exists():
    app.mount("/js", StaticFiles(directory=FRONTEND_DIR / "js"), name="js")


@app.get("/", include_in_schema=False)
async def serve_index():
    """Serve the student portal index.html if present, else API status."""
    if (FRONTEND_DIR / "index.html").exists():
        return FileResponse(FRONTEND_DIR / "index.html")
    return {
        "status": "online",
        "service": "Zenith '26 Fest Management System API",
        "frontend": "Native Android Mobile App (Kotlin)",
        "docs": "/docs",
        "endpoints": ["/api/events", "/api/registrations", "/api/stats"],
    }


@app.get("/admin.html", include_in_schema=False)
async def serve_admin():
    """Serve the admin dashboard admin.html if present, else API status."""
    if (FRONTEND_DIR / "admin.html").exists():
        return FileResponse(FRONTEND_DIR / "admin.html")
    return {
        "status": "online",
        "service": "Zenith '26 Admin API",
        "frontend": "Native Android Admin Dashboard (Kotlin)",
    }
