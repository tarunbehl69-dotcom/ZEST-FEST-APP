from typing import Dict, Optional
from pydantic import BaseModel, Field


class EventOut(BaseModel):
    """Schema for public event display in list views."""
    id: int
    title: str
    category: str
    description: Optional[str] = None
    event_date: str
    event_time: str
    venue: str
    capacity: int
    fee: int = 0
    icon: Optional[str] = None
    available_seats: int


class EventDetail(EventOut):
    """Schema for single event detail view (same shape as EventOut)."""
    pass


class RegistrationIn(BaseModel):
    """Payload sent by the student to register for an event."""
    student_name: Optional[str] = Field(default="", description="Full name of the student")
    roll_number: Optional[str] = Field(default="", description="Student college roll / ID number")
    email: Optional[str] = Field(default="", description="Valid email address containing @")
    phone: Optional[str] = Field(default=None, description="Optional phone number")


class RegistrationOut(BaseModel):
    """Confirmation payload returned upon successful registration."""
    id: int
    event_id: int
    student_name: str
    registered_at: str


class RegistrationItemOut(BaseModel):
    """Detailed registration record for the admin dashboard."""
    id: int
    event_id: int
    student_name: str
    roll_number: str
    email: str
    phone: Optional[str] = None
    registered_at: str
    event_title: str


class StatsOut(BaseModel):
    """Live aggregated statistics for the admin dashboard."""
    total_events: int
    total_registrations: int
    total_capacity: int
    overall_utilization_pct: float
    upcoming_events_count: int
    events_by_category: Dict[str, int]


class EventCreateIn(BaseModel):
    """Payload for admin creating a new event (Stretch goal)."""
    title: str = Field(..., min_length=1)
    category: str = Field(..., min_length=1)
    description: Optional[str] = ""
    event_date: str = Field(..., min_length=10)
    event_time: str = Field(..., min_length=1)
    venue: str = Field(..., min_length=1)
    capacity: int = Field(..., gt=0)
    fee: int = Field(default=0, ge=0)
    icon: Optional[str] = "🎉"
