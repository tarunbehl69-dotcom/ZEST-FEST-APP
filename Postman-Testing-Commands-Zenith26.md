# Zenith '26 API Testing Commands & Postman Guide

**Project:** Zenith '26 College Fest Management System  
**Base URL:** `http://localhost:8001`  
**Interactive Docs:** `http://localhost:8001/docs`

---

## 1. Postman Desktop Setup

1. Open **Postman Desktop**.
2. Click **Import** (top left).
3. Drag & drop `zenith26-api.postman_collection.json` located in your project folder.
4. Set the Collection Variable `base_url` to `http://localhost:8001` (pre-configured).
5. Ensure your FastAPI server is running:
   ```bash
   uvicorn backend.main:app --reload
   ```

---

## 2. All 18 Test Requests & Expected Responses

### Folder 1: Events Endpoints

#### Request 1: List All Events (200 OK)
- **Method & URL:** `GET http://localhost:8001/api/events`
- **Expected Status:** `200 OK`
- **Expected Result:** JSON array of 14 event objects. Each object contains `available_seats` equal to `capacity` (initially).

```bash
curl -X GET "http://localhost:8001/api/events"
```

#### Request 2: Filter by Category (200 OK)
- **Method & URL:** `GET http://localhost:8001/api/events?category=Technical`
- **Expected Status:** `200 OK`
- **Expected Result:** JSON array with 4 technical events (`Code Sprint 2.0`, `Robo Race`, `AI Pitch Fest`, `Web Design Rumble`).

```bash
curl -X GET "http://localhost:8001/api/events?category=Technical"
```

#### Request 3: Search by Title (200 OK)
- **Method & URL:** `GET http://localhost:8001/api/events?search=Football`
- **Expected Status:** `200 OK`
- **Expected Result:** 1 event (`Five-a-Side Football`).

```bash
curl -X GET "http://localhost:8001/api/events?search=Football"
```

#### Request 4: Combined Filter and Search (200 OK)
- **Method & URL:** `GET http://localhost:8001/api/events?category=Cultural&search=Dance`
- **Expected Status:** `200 OK`
- **Expected Result:** 1 event (`Dance Fiesta`).

```bash
curl -X GET "http://localhost:8001/api/events?category=Cultural&search=Dance"
```

#### Request 5: Empty Search Result (200 OK)
- **Method & URL:** `GET http://localhost:8001/api/events?search=NonExistentEvent`
- **Expected Status:** `200 OK`
- **Expected Result:** Empty array `[]`.

```bash
curl -X GET "http://localhost:8001/api/events?search=NonExistentEvent"
```

#### Request 6: Get Single Event Details (200 OK)
- **Method & URL:** `GET http://localhost:8001/api/events/1`
- **Expected Status:** `200 OK`
- **Expected Body:**
```json
{
  "id": 1,
  "title": "Code Sprint 2.0",
  "category": "Technical",
  "description": "An intense 4-hour competitive programming sprint solving complex algorithmic challenges under strict time constraints.",
  "event_date": "2026-11-14",
  "event_time": "10:00 AM",
  "venue": "Main Auditorium",
  "capacity": 100,
  "fee": 0,
  "icon": "💻",
  "available_seats": 100
}
```

```bash
curl -X GET "http://localhost:8001/api/events/1"
```

#### Request 7: Event Not Found (404 Not Found)
- **Method & URL:** `GET http://localhost:8001/api/events/999`
- **Expected Status:** `404 Not Found`
- **Expected Body:**
```json
{
  "detail": "Event with id 999 does not exist"
}
```

```bash
curl -X GET "http://localhost:8001/api/events/999"
```

---

### Folder 2: Registration Endpoints & Server Validation

#### Request 8: Successful Registration (201 Created)
- **Method & URL:** `POST http://localhost:8001/api/events/1/register`
- **Headers:** `Content-Type: application/json`
- **Request Body:**
```json
{
  "student_name": "Aarav Sharma",
  "roll_number": "23CS101",
  "email": "aarav.sharma@college.edu",
  "phone": "+91 98765 43210"
}
```
- **Expected Status:** `201 Created`
- **Expected Body:**
```json
{
  "id": 1,
  "event_id": 1,
  "student_name": "Aarav Sharma",
  "registered_at": "2026-10-12 12:00:00"
}
```

```bash
curl -X POST "http://localhost:8001/api/events/1/register" \
  -H "Content-Type: application/json" \
  -d '{"student_name":"Aarav Sharma","roll_number":"23CS101","email":"aarav.sharma@college.edu","phone":"+91 98765 43210"}'
```

#### Request 9: Duplicate Registration for Same Event (409 Conflict)
- **Method & URL:** `POST http://localhost:8001/api/events/1/register`
- **Headers:** `Content-Type: application/json`
- **Request Body:** Same body as Request 8 (`aarav.sharma@college.edu`)
- **Expected Status:** `409 Conflict`
- **Expected Body:**
```json
{
  "detail": "Already registered for this event with this email"
}
```

```bash
curl -X POST "http://localhost:8001/api/events/1/register" \
  -H "Content-Type: application/json" \
  -d '{"student_name":"Aarav Sharma","roll_number":"23CS101","email":"aarav.sharma@college.edu"}'
```

#### Request 10: Validation Error - Missing Student Name (400 Bad Request)
- **Method & URL:** `POST http://localhost:8001/api/events/1/register`
- **Request Body:**
```json
{
  "student_name": "",
  "roll_number": "23CS101",
  "email": "student@college.edu"
}
```
- **Expected Status:** `400 Bad Request`
- **Expected Body:**
```json
{
  "detail": "Student name cannot be empty"
}
```

```bash
curl -X POST "http://localhost:8001/api/events/1/register" \
  -H "Content-Type: application/json" \
  -d '{"student_name":"","roll_number":"23CS101","email":"student@college.edu"}'
```

#### Request 11: Validation Error - Missing Roll Number (400 Bad Request)
- **Method & URL:** `POST http://localhost:8001/api/events/1/register`
- **Request Body:**
```json
{
  "student_name": "Rohan Gupta",
  "roll_number": "",
  "email": "rohan@college.edu"
}
```
- **Expected Status:** `400 Bad Request`

```bash
curl -X POST "http://localhost:8001/api/events/1/register" \
  -H "Content-Type: application/json" \
  -d '{"student_name":"Rohan Gupta","roll_number":"","email":"rohan@college.edu"}'
```

#### Request 12: Validation Error - Invalid Email without '@' (400 Bad Request)
- **Method & URL:** `POST http://localhost:8001/api/events/1/register`
- **Request Body:**
```json
{
  "student_name": "Rohan Gupta",
  "roll_number": "23CS105",
  "email": "invalidemailaddress"
}
```
- **Expected Status:** `400 Bad Request`
- **Expected Body:**
```json
{
  "detail": "Invalid email: must contain '@'"
}
```

```bash
curl -X POST "http://localhost:8001/api/events/1/register" \
  -H "Content-Type: application/json" \
  -d '{"student_name":"Rohan Gupta","roll_number":"23CS105","email":"invalidemailaddress"}'
```

#### Request 13: Register for Non-Existent Event (404 Not Found)
- **Method & URL:** `POST http://localhost:8001/api/events/999/register`
- **Expected Status:** `404 Not Found`
- **Expected Body:**
```json
{
  "detail": "Event with id 999 not found"
}
```

```bash
curl -X POST "http://localhost:8001/api/events/999/register" \
  -H "Content-Type: application/json" \
  -d '{"student_name":"Rohan Gupta","roll_number":"23CS105","email":"rohan@college.edu"}'
```

---

### Folder 3: Admin Queries & Live Stats

#### Request 14: List All Registrations (200 OK)
- **Method & URL:** `GET http://localhost:8001/api/registrations`
- **Expected Status:** `200 OK`
- **Expected Result:** List of all registration objects with student name, roll number, email, phone, timestamp, and joined `event_title`.

```bash
curl -X GET "http://localhost:8001/api/registrations"
```

#### Request 15: Filter Registrations by Event ID (200 OK)
- **Method & URL:** `GET http://localhost:8001/api/registrations?event_id=1`
- **Expected Status:** `200 OK`

```bash
curl -X GET "http://localhost:8001/api/registrations?event_id=1"
```

#### Request 16: Live Metrics (GET /api/stats) (200 OK)
- **Method & URL:** `GET http://localhost:8001/api/stats`
- **Expected Status:** `200 OK`
- **Expected Body:**
```json
{
  "total_events": 14,
  "total_registrations": 1,
  "total_capacity": 1437,
  "overall_utilization_pct": 0.1,
  "upcoming_events_count": 14,
  "events_by_category": {
    "Cultural": 5,
    "Technical": 4,
    "Sports": 2,
    "Workshop": 2,
    "Gaming": 1
  }
}
```

```bash
curl -X GET "http://localhost:8001/api/stats"
```

---

### Folder 4: Stretch Goals (Admin Operations)

#### Request 17: Admin Adds Event (POST /api/events) (201 Created)
- **Method & URL:** `POST http://localhost:8001/api/events`
- **Headers:** `Content-Type: application/json`
- **Request Body:**
```json
{
  "title": "Drone Racing Grand Prix",
  "category": "Technical",
  "description": "High-speed FPV drone obstacle racing.",
  "event_date": "2026-11-15",
  "event_time": "03:30 PM",
  "venue": "Campus Grounds",
  "capacity": 30,
  "fee": 100,
  "icon": "🛸"
}
```
- **Expected Status:** `201 Created`

```bash
curl -X POST "http://localhost:8001/api/events" \
  -H "Content-Type: application/json" \
  -d '{"title":"Drone Racing Grand Prix","category":"Technical","description":"High-speed FPV drone obstacle racing.","event_date":"2026-11-15","event_time":"03:30 PM","venue":"Campus Grounds","capacity":30,"fee":100,"icon":"🛸"}'
```

#### Request 18: Admin Cancels Registration (DELETE /api/registrations/{id}) (200 OK)
- **Method & URL:** `DELETE http://localhost:8001/api/registrations/1`
- **Expected Status:** `200 OK`
- **Expected Body:**
```json
{
  "detail": "Registration successfully cancelled",
  "id": 1
}
```

```bash
curl -X DELETE "http://localhost:8001/api/registrations/1"
```
