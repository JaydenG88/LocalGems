# LocalGems

LocalGems is a platform for discovering local, niche, and non-chain businesses and attractions — the kind of places worth a special trip, submitted and rated by the people who find them.

> **Status:** In development. Backend only (no frontend yet).

## What it does

Users search for a business, submit it to LocalGems, and the submission gets checked and enriched before it's published. Other users can browse, filter, review, rate, and save businesses.

## Current Features

- Business, user, review, category, and city data with full CRUD
- Business search/filtering by city, category, rating, and name
- Reviews and ratings on businesses
- Saving businesses to a personal list
- Google Maps integration for business lookup
- Dockerized backend + Postgres, with a separate ML service in early scaffolding

Still missing: authentication, a wired-up submission flow, and a frontend — all actively being worked on.

## Tech Stack

- **Backend:** Java, Spring Boot
- **Database:** PostgreSQL
- **Deployment:** Docker

## Installation

```bash
git clone https://github.com/JaydenG88/LocalGems.git
cd LocalGems/Backend
docker compose up --build
```

The API comes up on `http://localhost:8080`.
