from fastapi import FastAPI

app = FastAPI(title="LocalGems Uniqueness Scoring Service")


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}
