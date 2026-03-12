"""Load config from env so we never hardcode secrets."""
import os
from pathlib import Path

from dotenv import load_dotenv

# Load .env.local first (local secrets), then .env
_root = Path(__file__).resolve().parent
load_dotenv(_root / ".env.local")
load_dotenv(_root / ".env")

FINANCE_API_BASE_URL: str = os.getenv("FINANCE_API_BASE_URL", "http://localhost:8080")
OPENAI_API_KEY: str = os.getenv("OPENAI_API_KEY", "")
OPENAI_MODEL: str = os.getenv("OPENAI_MODEL", "gpt-4o-mini")
