"""
Toolbox for the finance agent: schema discovery and read-only query execution.
All data access goes through the Spring Boot API so the backend enforces security and timeout.
"""
from __future__ import annotations

import json
from typing import Any

import httpx
from langchain_core.tools import tool

from config import FINANCE_API_BASE_URL


def _get_schema() -> list[dict[str, Any]]:
    """Fetch table/column metadata from backend. Used by the tool and by the agent context."""
    with httpx.Client(timeout=15.0) as client:
        r = client.get(f"{FINANCE_API_BASE_URL.rstrip('/')}/api/schema")
        r.raise_for_status()
        return r.json()


def _run_query(sql: str) -> dict[str, Any]:
    """Execute a read-only SELECT via backend. Backend validates SELECT-only and timeout."""
    with httpx.Client(timeout=30.0) as client:
        r = client.post(
            f"{FINANCE_API_BASE_URL.rstrip('/')}/api/query",
            json={"sql": sql},
        )
        r.raise_for_status()
        return r.json()


@tool
def get_finance_schema() -> str:
    """
    Get the schema of all finance tables (account, transaction, customer) with column names and types.
    Call this first when you need to write SQL so you use correct column and table names.
    """
    data = _get_schema()
    return json.dumps(data, indent=2)


@tool
def run_finance_query(sql: str) -> str:
    """
    Run a read-only SELECT query against the finance database. Input must be a single SQL SELECT statement.
    Only SELECT is allowed; no INSERT/UPDATE/DELETE. Use get_finance_schema first to know table and column names.
    """
    result = _run_query(sql)
    return json.dumps(result)
