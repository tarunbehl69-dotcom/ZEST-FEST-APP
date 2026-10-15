# Zenith '26 — College Fest Management System (Android Mobile App)

A native Android mobile application and client-server system for managing the **Zenith '26** College Fest (November 14–15, 2026).

---

## 1. Project Overview

**Zenith '26 Fest Management System** provides:
- **Student Event Portal**: Browse 14 fest events, filter by 5 categories, search by event title in real time, view seat availability with capacity progress bars, open detailed event views, and register with client- and server-side validation.
- **Admin Operations Dashboard**: Monitor live fest statistics (total events, total registrations, overall capacity utilization percentage, upcoming events count), view category breakdowns, inspect events sorted by utilization descending (with high-demand warnings for events exceeding 90% capacity), and manage student registrations with event filtering and cancellation capabilities.

---

## 2. Technology Stack

| Layer | Technology | Purpose |
| :--- | :--- | :--- |
| **Mobile Frontend** | **Kotlin (Android Native)** | Modern native Android application built with **Jetpack Compose**, **Material 3**, Coroutines, StateFlow, and ViewModel architecture. |
| **Networking** | **Retrofit 2 + OkHttp 3** | REST API client with automatic JSON serialization (Gson) and HTTP logging. |
| **Backend** | **Python + FastAPI** | High-performance asynchronous REST API implementing business logic, capacity checks, and server-side validation. |
| **Server Engine** | **Uvicorn** | ASGI web server serving endpoints on `http://0.0.0.0:8001`. |
| **Database** | **SQLite (`fest.db`)** | Local relational database with live computed availability and foreign key constraints. |
| **Seed Data** | **openpyxl** | Automated database initialization reading 14 events from `data/events_seed.xlsx`. |
| **API Testing** | **Postman** | Postman collection covering 2xx, 4xx, and stretch goal endpoints. |

---

## 3. Directory Structure

```text
zenith26-fest-system/
├── app/                                    # Native Android Mobile App Module
│   ├── build.gradle.kts                    # App dependencies & Compose config
│   ├── proguard-rules.pro                  # ProGuard rules for models & Gson
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml          # App manifest & permissions
│           ├── java/com/zenith26/fest/
│           │   ├── ZenithApp.kt             # Application class
│           │   ├── MainActivity.kt          # Main activity & screen switching
│           │   ├── data/
│           │   │   ├── model/
│           │   │   │   ├── Event.kt         # Event data model (EventOut/EventDetail)
│           │   │   │   ├── Registration.kt  # Registration models (In, Out, Item)
│           │   │   │   ├── Stats.kt         # Live metrics model (StatsOut)
│           │   │   │   ├── EventCreateIn.kt # Admin event creation payload
│           │   │   │   └── ApiError.kt      # FastAPI error parser
│           │   │   └── remote/
│           │   │       ├── ZenithApiService.kt # Retrofit interface
│           │   │       └── ApiClient.kt     # Singleton with dynamic host IP config
│           │   └── ui/
│           │       ├── theme/               # Dark slate palette & typography
│           │       │   ├── Color.kt
│           │       │   ├── Type.kt
│           │       │   └── Theme.kt
│           │       ├── common/              # Reusable UI components
│           │       │   ├── TopNavBar.kt     # Header with mode badge & host settings
│           │       │   └── Components.kt    # Badges, status pills, gradient text
│           │       ├── student/             # Student Portal Screen & Modal
│           │       │   ├── StudentPortalScreen.kt
│           │       │   └── EventDetailModal.kt
│           │       ├── admin/               # Admin Dashboard Screen & Modal
│           │       │   ├── AdminDashboardScreen.kt
│           │       │   └── AddEventModal.kt
│           │       └── viewmodel/           # Architecture ViewModels
│           │           ├── StudentViewModel.kt
│           │           └── AdminViewModel.kt
│           └── res/
│               ├── values/                  # Strings, colors, styles
│               └── xml/
│                   └── network_security_config.xml # Cleartext HTTP support
├── backend/                                # Python FastAPI Backend
│   ├── main.py                             # API router & endpoints
│   ├── database.py                         # SQLite connection & schema initialization
│   ├── models.py                           # Pydantic schemas
│   └── seed_data.py                        # openpyxl spreadsheet seeder
├── data/
│   └── events_seed.xlsx                    # 14 Fest seed events spreadsheet
├── gradle/wrapper/                         # Gradle wrapper configuration
├── build.gradle.kts                        # Root project build file
├── settings.gradle.kts                     # Gradle settings configuration
├── gradle.properties                       # Gradle JVM settings
├── gradlew                                 # Gradle wrapper Unix script
├── requirements.txt                        # Python dependencies
├── zenith26-api.postman_collection.json    # Postman test collection
├── Postman-Testing-Commands-Zenith26.md    # API test command reference
└── README.md                               # Project documentation
```

---

## 4. Setup and Running

### Step 1: Start the FastAPI Backend

1. Open a terminal in the project directory:
   ```bash
   cd /Users/tarunbehl/Desktop/app/zenith26-fest-system
   ```

2. Activate the virtual environment (created with Python 3.14 / 3.10+):
   ```bash
   source venv/bin/activate
   ```

3. Ensure dependencies are installed:
   ```bash
   pip install -r requirements.txt
   ```

4. Initialize and run the FastAPI server:
   ```bash
   uvicorn backend.main:app --reload --host 0.0.0.0 --port 8001
   ```
   *Note: Binding to `0.0.0.0` allows connections from localhost, Android emulators (`10.0.2.2`), and physical Android phones over local Wi-Fi.*

5. Open your browser and navigate to `http://localhost:8001/docs` to verify that the Swagger interactive API documentation is online and all endpoints are listed.

---

### Step 2: Run the Android Application

1. Open **Android Studio**.
2. Select **Open** and select the `/Users/tarunbehl/Desktop/app/zenith26-fest-system` folder.
3. Allow Gradle to sync the project.
4. Launch an **Android Emulator** (Pixel 7 / 8 running API 26+) or connect a physical device via USB debugging.
5. Click **Run 'app'** (`Shift + F10`).

#### Pre-Compiled Android APK:
A production-ready debug APK has been built directly and is available in multiple convenient locations:
- `/Users/tarunbehl/Desktop/app/zenith26-app-debug.apk`
- `/Users/tarunbehl/Desktop/app/zenith26-fest-system/app/build/outputs/apk/debug/app-debug.apk`
- `~/Downloads/zenith26-app-debug.apk`

#### Installing the APK via ADB:
If you have an Android device or emulator connected:
```bash
/Users/tarunbehl/Library/Android/sdk/platform-tools/adb install -r /Users/tarunbehl/Desktop/app/zenith26-app-debug.apk
```

#### Installing on a Physical Phone without ADB:
1. AirDrop, email, or copy `zenith26-app-debug.apk` from your `Downloads` folder to your Android phone.
2. Tap the file in your phone's File Manager / Downloads and select **Install**.
3. In the app, tap the **Settings icon (⚙️)** in the top navigation bar to enter your computer's local Wi-Fi IP address (e.g. `http://192.168.1.x:8001/`) or your Vercel deployment URL.

---

### Step 3: Deploying to Vercel (Permanent Cloud Hosting)

The project includes built-in Vercel serverless support for deploying both the **FastAPI Backend**, the **Interactive Web Portal**, and the **Direct APK Download**:

#### Option A: Quick CLI Deploy (Fastest)
1. In your terminal, navigate to the project directory:
   ```bash
   cd /Users/tarunbehl/Desktop/app/zenith26-fest-system
   ```
2. Run the Vercel deployment command:
   ```bash
   npx vercel
   ```
3. Follow the one-time interactive prompts to link your Vercel account and deploy.
4. For production release:
   ```bash
   npx vercel --prod
   ```

#### Option B: Deploy via GitHub
1. Push `zenith26-fest-system` to a GitHub repository.
2. Go to [vercel.com/new](https://vercel.com/new) and click **Import** on your repo.
3. If importing the root workspace, set **Root Directory** to `zenith26-fest-system`.
4. Click **Deploy**.

#### What Vercel Hosts Automatically:
- **FastAPI Endpoints**: `https://<your-project>.vercel.app/api/events`, `/api/stats`, `/api/registrations`, etc.
- **Swagger Documentation**: `https://<your-project>.vercel.app/docs`
- **Student Event Portal**: `https://<your-project>.vercel.app/`
- **Admin Operations Dashboard**: `https://<your-project>.vercel.app/admin`
- **Direct APK Download**: `https://<your-project>.vercel.app/zenith26-app-debug.apk`
- **SQLite Database**: Automatically initializes and persists in `/tmp/fest.db` with all 14 official seed events.

---

## 5. API Contracts & Endpoints

| Method | Endpoint | Description | Status Codes |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/events` | List all events with live `available_seats`. Supports `?category=` and `?search=`. | `200 OK` |
| `GET` | `/api/events/{id}` | Retrieve details for a single event. | `200 OK`, `404 Not Found` |
| `POST` | `/api/events/{id}/register` | Register a student with name, roll number, email, and phone. | `201 Created`, `400 Bad Request`, `409 Conflict` |
| `GET` | `/api/registrations` | List all registrations with event titles. Supports `?event_id=`. | `200 OK` |
| `GET` | `/api/stats` | Live aggregated fest statistics for the admin dashboard. | `200 OK` |
| `POST` | `/api/events` | *Stretch Goal*: Admin creates a new fest event. | `201 Created`, `400 Bad Request` |
| `DELETE` | `/api/registrations/{id}` | *Stretch Goal*: Admin cancels a student registration. | `200 OK`, `404 Not Found` |

---

## 6. Testing with Postman

1. Open Postman.
2. Click **Import** and select `zenith26-api.postman_collection.json`.
3. The collection is pre-configured with the variable `base_url` set to `http://localhost:8001`.
4. Run the requests in order:
   - **GET /api/events**: Confirm all 14 seed events are returned with live computed seats.
   - **GET /api/events/1**: Retrieve Code Sprint 2.0.
   - **GET /api/events/999**: Confirm `404 Not Found`.
   - **POST /api/events/1/register (Success)**: Confirm `201 Created` with `registered_at`.
   - **POST /api/events/1/register (Empty Name)**: Confirm `400 Bad Request`.
   - **POST /api/events/1/register (Invalid Email)**: Confirm `400 Bad Request`.
   - **POST /api/events/1/register (Duplicate Email)**: Confirm `409 Conflict` ("Already registered").
   - **GET /api/stats**: Verify `overall_utilization_pct` and registration counts update live.

---

## 7. Verification Checklist

- [x] SQLite schema created with `events` and `registrations` tables.
- [x] All 14 seed events read from `data/events_seed.xlsx` via openpyxl without hardcoding.
- [x] Pydantic models validate data structures and API contracts.
- [x] FastAPI server implements independent server-side validation ("never trust the client").
- [x] Native Kotlin Android app reproduces the exact Tailwind dark visual design, colors, badges, and layout.
- [x] Student portal supports category filter chips and real-time title search calling the backend API.
- [x] Registration modal provides client validation, server error messages, and immediate seat count refresh.
- [x] Admin dashboard displays 4 live stat cards, category breakdown, sorted event utilization table (>90% highlighted), and registration filtering.
- [x] Stretch goals implemented: admin event creation and registration deletion.
- [x] Postman collection and test guide included and verified.
