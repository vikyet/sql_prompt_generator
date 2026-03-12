"""
Basic evaluation: agent returns structured FinanceAnswer and tools are callable.
RAG/semantic accuracy can be extended with golden Q&A pairs.
"""
from __future__ import annotations

import os
import sys
from pathlib import Path
from unittest.mock import patch

import pytest

# Add parent dir so imports like schemas, tools work when running pytest from python_agent
sys.path.insert(0, str(Path(__file__).resolve().parent.parent))

# Ensure config is loaded before importing agent/tools
os.environ.setdefault("OPENAI_API_KEY", "test-key")
os.environ.setdefault("FINANCE_API_BASE_URL", "http://localhost:8080")


def test_finance_answer_schema():
    """Structured output schema is valid and serializable."""
    from schemas import FinanceAnswer

    a = FinanceAnswer(
        answer="There are 2 accounts.",
        sql_used="SELECT COUNT(*) FROM account",
        summary="2 accounts",
    )
    assert a.answer == "There are 2 accounts."
    assert a.sql_used == "SELECT COUNT(*) FROM account"
    assert a.model_dump()


def test_tools_get_schema_mocked():
    """get_finance_schema returns a string (schema JSON)."""
    from tools import get_finance_schema

    with patch("tools._get_schema") as m:
        m.return_value = [
            {"tableName": "account", "description": "Accounts", "columns": []}
        ]
        out = get_finance_schema.invoke({})
    assert "account" in out


def test_tools_run_query_mocked():
    """run_finance_query accepts sql and returns string (result JSON)."""
    from tools import run_finance_query

    with patch("tools._run_query") as m:
        m.return_value = {"columns": ["id"], "rows": [{"id": 1}], "rowCount": 1}
        out = run_finance_query.invoke({"sql": "SELECT id FROM customer LIMIT 1"})
    assert "rowCount" in out or "id" in out


def test_pii_masking_in_query_result():
    """PII columns (e.g. email) are redacted; customer_ref is left unmasked for reporting."""
    from tools import _mask_pii_in_query_result

    result = {
        "columns": ["id", "customer_ref", "email", "balance"],
        "rows": [
            {"id": 1, "customer_ref": "CUST001", "email": "user@example.com", "balance": 1000},
        ],
        "rowCount": 1,
    }
    masked = _mask_pii_in_query_result(result)
    assert masked["rowCount"] == 1
    row = masked["rows"][0]
    assert row["id"] == 1
    assert row["balance"] == 1000
    assert row["customer_ref"] == "CUST001"  # not masked
    assert row["email"] == "[REDACTED]"
