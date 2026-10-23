# LocalGems ML Scoring Service

Separate Python/FastAPI service that scores business uniqueness (1-5) from open-data features. Called by the Spring Boot backend over REST — see the main repo README's Architecture and Refactor Plan sections for the full design.

Not yet implemented: feature building, `train_uniqueness.py`, the `/score` endpoint, and the saved model pipeline. Currently just a running skeleton with a `/health` check.

## Local dev

Requires **Python 3.12** (matches the Dockerfile's base image; see `.python-version`).

```bash
py -3.12 -m venv .venv      # or: python3.12 -m venv .venv
.venv\Scripts\activate      # or: source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload
```

## Layout

- `app/main.py` — FastAPI app
- `data/` — raw/intermediate datasets (gitignored, not committed)
- `models/` — saved joblib pipelines (gitignored, not committed)
