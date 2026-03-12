"""
Structured outputs for the agent. No raw string parsing—all responses conform to these schemas.
"""
from __future__ import annotations

from pydantic import BaseModel, Field


class FinanceAnswer(BaseModel):
    """Structured answer to a natural language question about finance data."""

    answer: str = Field(description="Clear, concise answer in natural language for the user")
    sql_used: str | None = Field(default=None, description="The SELECT query that was run, if any")
    summary: str = Field(description="One-line summary of what was found or done")
