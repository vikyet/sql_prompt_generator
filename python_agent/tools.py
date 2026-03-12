"""
Toolbox for the finance agent: schema discovery and read-only query execution.
All data access goes through the Spring Boot API so the backend enforces security and timeout.
PII in query results is masked before being sent to the LLM.
"""
from __future__ import annotations

import json
import re
from typing import Any

import httpx
from langchain_core.tools import tool

from config import FINANCE_API_BASE_URL

# Column names (case-insensitive) or patterns that indicate PII—values are redacted before the LLM sees them.
# customer_ref is excluded so the LLM can use it for reporting/joins.
PII_KEYS: frozenset[str] = frozenset({
    "name", "email", "phone", "mobile", "address_line1", "address_line2", "address",
    "pincode", "pan", "aadhar", "customer_name", "contact_email", "contact_phone",
})
PII_PATTERN = re.compile(
    r"(\b(name|email|phone|mobile|address|pincode|pan|aadhar)\b)",
    re.IGNORECASE,
)
MASK = "[REDACTED]"


def _is_pii_column(key: str) -> bool:
    if not key:
        return False
    k = key.lower()
    if k in PII_KEYS:
        return True
    return bool(PII_PATTERN.search(k))


def _mask_pii_in_row(row: dict[str, Any]) -> dict[str, Any]:
    return {
        k: MASK if _is_pii_column(k) else v
        for k, v in row.items()
    }


def _mask_pii_in_query_result(result: dict[str, Any]) -> dict[str, Any]:
    """Mask PII in API query response rows so the LLM never sees raw customer data."""
    rows = result.get("rows")
    if not isinstance(rows, list):
        return result
    return {
        **result,
        "rows": [_mask_pii_in_row(r) for r in rows],
    }


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
    PII columns (e.g. name, email, phone) in results are redacted before use.
    """
    result = _run_query(sql)
    result = _mask_pii_in_query_result(result)
    return json.dumps(result)
