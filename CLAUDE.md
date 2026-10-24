# LocalGems — Agent Context

## Project

LocalGems is a platform for discovering local, niche, non-chain businesses and attractions. It's explicitly an engineering/learning project, not a growth-oriented product — optimize for sound design and learning value over shipping speed.

## Who you're working with

The user is relearning Spring Boot after stepping away from this project (internships/school). Key working style, see `~/.claude/projects/.../memory/user_teaching_style.md` for the full note, but in short:

- **Build incrementally, never one-shot.** Break work into small steps, explain the Spring Boot concepts involved anchored in the actual code, check in before moving to the next piece.
- **Sessions are split by task** to keep context windows manageable — expect a new conversation to pick up exactly one refactor/feature step at a time. This file exists so each new session has the full picture without re-deriving it.
- Keep `README.md` minimal and public-facing — current features and what the app does, nothing about unimplemented plans or internal architecture detail. Update it only when a feature actually lands, not when it's merely planned. The detailed/speculative planning content belongs here or in the local spec doc, not there.

## Current implementation status (as of 2026-10-24)

Backend-only, Spring Boot 3.5.5 (Java 17), PostgreSQL, Docker. No frontend exists.

- Full CRUD (entity, repository, service, MapStruct mapper, controller) for `User`, `Business`, `Review`, `Category`, `City`, `SavedBusiness`.
- Business filtering by city/state/category/rating works (`BusinessService.filterBusinesses`).
- **No Spring Security / auth.** No `SecurityConfig`, no password hashing — `UserService.createUser` stores the raw password. `jjwt` is in `pom.xml` but unused.
- **Business submission is not wired up.** `BusinessController.submitBusiness()` (`POST /api/businesses/submit`) is a stub that returns `null`. The actual pipeline exists in `BusinessService.submitBusiness()` (Google Places search → city resolution → `OpenAIService` categorization → persist) but nothing calls it.
- **`OpenAIService` still exists and is still the categorization/moderation path** — it is scheduled for removal, see Architecture Redesign below. Don't extend it; any session touching business submission should expect to be replacing it, not patching it.
- **`ml-service/`** — new Python/FastAPI service, scaffolded but not implemented. Currently just a `/health` endpoint. Pinned to Python 3.12 (matches `Dockerfile`; host machine's default `python` may be a different version — check `.python-version`). Wired into `Backend/docker-compose.yml` as a sibling container, not yet called by the backend.
- No tests beyond the default Spring Boot smoke test (`LocalGemsBackendApplicationTests`).
- `data.sql` seeds 60 US cities, 9 categories, 7 sample businesses, 6 users (plaintext passwords), reviews, saved businesses.

## Architecture redesign in progress

The full handoff spec — submission flow, data sourcing, chain gate, uniqueness-scoring model, FastAPI service design, data model, labeling workflow, open items — lives in a **local, gitignored** file at repo root: `LocalGems — Business Submission & Uniqueness Scoring Architecture.md`. It is not committed (deliberately — keep detailed/speculative planning out of the public repo). If it's missing on a given machine, ask the user for it rather than reconstructing it from memory; the summary below may drift from the real doc over time.

**Summary of the key decisions** (read the actual doc for full rationale/data model before implementing):

- No LLM moderation. Replaced by a hard **chain gate** (reject chains) + a classical ML **uniqueness score, 1–5** (ranks everything, never rejects).
- Category assignment is a lookup from source type tags — not ML, not LLM.
- Google Places becomes search + live display only; only the place ID is stored long-term (Google ToS forbids storing/training on Places content). Durable fields come from open sources: Foursquare OS Places or Overture Maps (base layer), OpenStreetMap/Overpass, Wikidata/Wikipedia, possibly Wikivoyage.
- New data model: a merged `Place` record (what sources said) + a separate `FeatureSet` table (what the model sees), so features can be rebuilt without re-calling external APIs.
- The classical ML model (`HistGradientBoostingRegressor` via scikit-learn) is served by the `ml-service` Python/FastAPI service, called from Spring Boot over REST. This is the user's hands-on ML learning vehicle.
- Deployment target moves from AWS Elastic Beanstalk to EC2.
- LLM use is NOT being removed from the project — it's repositioned to conversational retrieval: RAG semantic search over businesses, and a later MCP-based tour-guide agent (search by criteria, place details, find nearby). MCP is also a deliberate hands-on learning goal for the user, independent of whether it's the most minimal design.

**Refactor order** (one task = roughly one session):

1. Remove `OpenAIService` and the LLM-moderation call out of `BusinessService`/`BusinessController`.
2. Rework `GoogleMapsService`: text search returns a candidate list instead of auto-picking the first result; Place Details only called after the user picks; API key moved out of the URL into the `X-Goog-Api-Key` header only; `priceLevel` mapped or dropped from the field mask.
3. New data model: split `Business` into `Place` (merged record) + `FeatureSet` (model input).
4. `EntityMatchingService`: cross-match the user-picked Google anchor against Foursquare/Overture, OSM, Wikidata/Wikipedia, Wikivoyage by coordinates + fuzzy name.
5. `ChainGateService`: reject on brand/chain signals from any matched source.
6. Category assignment by lookup from source type tags.
7. `ml-service`: labeling workflow, `train_uniqueness.py`, then the real `/score` endpoint.
8. Wire `BusinessService` to call the scoring service and persist `Place` + `FeatureSet` + score in one `@Transactional` boundary.
9. Explicit transactional boundaries / isolation review on submission and review/rating writes.
10. Deployment migration to EC2.

After that: RAG semantic search, MCP tour-guide agent, recommendation system (separate ML model from the uniqueness scorer), mobile app — each planned separately when picked up.

## Conventions already established

- Layered architecture: `controller` → `service` → `repository`, with MapStruct `mapper` interfaces converting between `model` entities and `dto/{request,response,update}DTO`. Follow this for any new entity.
- Constructor injection everywhere, no field injection.
- External API clients live in `service/external/`.
