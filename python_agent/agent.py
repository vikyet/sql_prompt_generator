"""
LangGraph agent that answers natural language questions about finance tables.
Uses get_finance_schema and run_finance_query as tools; returns structured FinanceAnswer (no raw parsing).
"""
from __future__ import annotations

import warnings

from langchain_core.messages import AIMessage
from langchain_openai import ChatOpenAI
from langgraph.prebuilt import create_react_agent

from config import OPENAI_API_KEY, OPENAI_MODEL
from tools import get_finance_schema, run_finance_query
from schemas import FinanceAnswer


TOOLS = [get_finance_schema, run_finance_query]

SYSTEM_PROMPT = """ROLE: You are a Senior Finance Data Architect.

CAPABILITIES:
1. get_finance_schema: Call this first to understand the current DB structure.
2. run_finance_query: Call this to execute a READ-ONLY SELECT statement.

WORKFLOW:
- STEP 1 (THINK): Analyze the user's intent. Which tables might be relevant?
- STEP 2 (EXPLORE): Call `get_finance_schema`. 
- STEP 3 (PLAN): Write a performant MySQL query based on the schema.
- STEP 4 (VALIDATE): Ensure the query is a SELECT and does not contain PII (use customer_ref).
- STEP 5 (EXECUTE): Call `run_finance_query`.
- STEP 6 (RESPOND): Summarize the data in professional business English.

SAFETY RULES:
- If a user asks for anything other than a SELECT, state: "I am only authorized for data retrieval."
- If no data is found, do not hallucinate; state that no records match the criteria."""


def build_agent():
    # Pass API key as a string from config (from .env.local / .env).
    llm = ChatOpenAI(
        model=OPENAI_MODEL,
        temperature=0,
        api_key=OPENAI_API_KEY if OPENAI_API_KEY else None,
    )
    with warnings.catch_warnings():
        warnings.simplefilter("ignore", DeprecationWarning)
        return create_react_agent(llm, TOOLS, prompt=SYSTEM_PROMPT)


def _extract_sql_from_tool_calls(messages: list) -> str | None:
    """Extract the last run_finance_query SQL from tool calls for structured output."""
    for m in reversed(messages):
        if isinstance(m, AIMessage) and m.tool_calls:
            for tc in m.tool_calls:
                name = tc.get("name") if hasattr(tc, "get") else getattr(tc, "name", None)
                if name == "run_finance_query":
                    args = tc.get("args") if hasattr(tc, "get") else getattr(tc, "args", None) or {}
                    return (args.get("sql") if hasattr(args, "get") else getattr(args, "sql", None))
    return None


def answer_question(question: str) -> FinanceAnswer:
    """
    Run the agent and return a structured FinanceAnswer. SQL used is extracted from tool calls.
    """
    agent = build_agent()
    result = agent.invoke({"messages": [("user", question)]})
    messages = result.get("messages", [])
    if not messages:
        return FinanceAnswer(answer="No response from agent.", summary="Error", sql_used=None)
    last = messages[-1]
    content = last.content if hasattr(last, "content") and last.content else str(last)
    sql_used = _extract_sql_from_tool_calls(messages)
    summary = content[:500] + ("..." if len(content) > 500 else "") if content else "No answer."
    return FinanceAnswer(answer=content or "No answer.", sql_used=sql_used, summary=summary)


if __name__ == "__main__":
    import sys
    q = sys.argv[1] if len(sys.argv) > 1 else "How many accounts are there?"
    out = answer_question(q)
    print(out.model_dump_json(indent=2))
