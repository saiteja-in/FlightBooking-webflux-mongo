# Flight Booking API Documentation

## Base URL
```
http://localhost:8080/api/v1.0
```

---

##  Authentication APIs

### Register User
**POST** `/auth/register`

Register a new user account.

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "Password123!"
}
```

**Response:** `201 Created`
```json
{
  "email": "user@example.com",
  "message": "Registration successful"
}
```

---

### Login User
**POST** `/auth/login`

Authenticate user and get access token.

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "Password123!"
}
```

**Response:** `200 OK`
```json
{
  "email": "user@example.com",
  "message": "Login successful"
}
```

---

##  Flight Management APIs (Admin)

### Create Flight
**POST** `/flight/admin/flights`

Create a new flight configuration.

**Request Body:**
```json
{
  "flightNumber": "AI101",
  "airline": "AIR_INDIA",
  "originAirport": "DEL",
  "destinationAirport": "BLR",
  "seatCapacity": 180
}
```

**Response:** `201 Created`
```json
{
  "id": "flight-123",
  "flightNumber": "AI101",
  "airline": "AIR_INDIA",
  "originAirport": "DEL",
  "destinationAirport": "BLR",
  "seatCapacity": 180
}
```

**Available Airlines:** `AIR_INDIA`, `INDIGO`, `SPICEJET`, `VISTARA`, `GOAIR`

---

### Get All Flights
**GET** `/flight/admin/flights`

Retrieve all available flights.

**Response:** `200 OK`
```json
[
  {
    "id": "flight-123",
    "flightNumber": "AI101",
    "airline": "AIR_INDIA",
    "originAirport": "DEL",
    "destinationAirport": "BLR",
    "seatCapacity": 180
  }
]
```

---

### Get Flight by Flight Number
**GET** `/flight/admin/flights/{flightNumber}`

Get flight details by flight number.

**Path Parameters:**
- `flightNumber` - Flight number (e.g., "AI101")

**Response:** `200 OK`
```json
{
  "id": "flight-123",
  "flightNumber": "AI101",
  "airline": "AIR_INDIA",
  "originAirport": "DEL",
  "destinationAirport": "BLR",
  "seatCapacity": 180
}
```

---

### Delete Flight
**DELETE** `/flight/admin/flights/{id}`

Delete a flight by ID.

**Path Parameters:**
- `id` - Flight ID

**Response:** `204 No Content`

---

##  Flight Schedule APIs (Admin)

### Add Flight Inventory
**POST** `/flight/admin/inventory`

Create a flight schedule with available seats and pricing.

**Request Body:**
```json
{
  "flightNumber": "AI101",
  "flightDate": "2025-12-25",
  "departureTime": "10:00:00",
  "arrivalTime": "12:30:00",
  "fare": 12000.00
}
```

**Response:** `201 Created`
```json
{
  "scheduleId": "schedule-123",
  "flightNumber": "AI101",
  "flightDate": "2025-12-25",
  "departureTime": "10:00:00",
  "arrivalTime": "12:30:00",
  "fare": 12000.00,
  "availableSeats": 180,
  "totalSeats": 180
}
```

---

### Search Flights
**POST** `/flight/admin/search`

Search available flights by origin, destination, and date.

**Request Body:**
```json
{
  "originAirport": "DEL",
  "destinationAirport": "BLR",
  "flightDate": "2025-12-25"
}
```

**Response:** `200 OK`
```json
[
  {
    "scheduleId": "schedule-123",
    "flightNumber": "AI101",
    "originAirport": "DEL",
    "destinationAirport": "BLR",
    "flightDate": "2025-12-25",
    "departureTime": "10:00:00",
    "arrivalTime": "12:30:00",
    "fare": 12000.00,
    "availableSeats": 150
  }
]
```

---

##  Booking APIs

### Book Flight
**POST** `/flight/booking/{scheduleId}`

Create a booking for a flight schedule.

**Path Parameters:**
- `scheduleId` - Flight schedule ID

**Request Body:**
```json
{
  "contactEmail": "passenger@example.com",
  "passengers": [
    {
      "fullName": "John Doe",
      "gender": "MALE",
      "age": 30,
      "seatNumber": "1A",
      "mealOption": "VEG"
    }
  ]
}
```

**Response:** `201 Created`
```json
{
  "pnr": "PNR123",
  "bookingId": "booking-123",
  "passengers": [
    {
      "fullName": "John Doe",
      "gender": "MALE",
      "age": 30,
      "seatNumber": "1A",
      "mealOption": "VEG"
    }
  ],
  "issuedAt": "2025-11-24T10:30:00"
}
```

**Available Genders:** `MALE`, `FEMALE`, `OTHER`  
**Available Meal Options:** `VEG`, `NON_VEG`, `VEGAN`, `NO_MEAL`

---

### Cancel Booking
**DELETE** `/flight/booking/cancel/{pnr}`

Cancel a booking using PNR number.

**Path Parameters:**
- `pnr` - Passenger Name Record (PNR) number

**Response:** `200 OK`
```json
{
  "message": "Booking and Ticket Cancelled"
}
```

---

##  Ticket APIs

### Get Ticket by PNR
**GET** `/flight/ticket/{pnr}`

Retrieve ticket details by PNR number.

**Path Parameters:**
- `pnr` - Passenger Name Record (PNR) number

**Response:** `200 OK`
```json
{
  "pnr": "PNR123",
  "bookingId": "booking-123",
  "passengers": [
    {
      "fullName": "John Doe",
      "gender": "MALE",
      "age": 30,
      "seatNumber": "1A",
      "mealOption": "VEG"
    }
  ],
  "issuedAt": "2025-11-24T10:30:00"
}
```


---

## Testing

Run tests with coverage:
```bash
./mvnw verify
```

View coverage report:
```
target/site/jacoco/index.html
```
