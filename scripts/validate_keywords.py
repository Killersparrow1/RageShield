#!/usr/bin/env python3
"""Validates keywords.txt. Run: python3 scripts/validate_keywords.py"""
import sys

MAX_LINES = 5000
MAX_LEN = 60
MIN_LEN = 2

errors = []
seen = {}
count = 0

with open(sys.argv[1] if len(sys.argv) > 1 else "app/src/main/assets/keywords.txt", encoding="utf-8") as f:
    for n, raw in enumerate(f, 1):
        line = raw.rstrip("\n")
        stripped = line.strip()
        if not stripped or stripped.startswith("#"):
            continue
        count += 1
        if line != stripped:
            errors.append(f"line {n}: remove leading/trailing spaces: {line!r}")
        if stripped != stripped.lower():
            errors.append(f"line {n}: use lowercase: {stripped!r}")
        if len(stripped) < MIN_LEN:
            errors.append(f"line {n}: too short (min {MIN_LEN}): {stripped!r}")
        if len(stripped) > MAX_LEN:
            errors.append(f"line {n}: too long (max {MAX_LEN}): {stripped[:20]!r}...")
        if not any(ch.isalnum() for ch in stripped):
            errors.append(f"line {n}: must contain letters or digits: {stripped!r}")
        key = stripped.lower()
        if key in seen:
            errors.append(f"line {n}: duplicate of line {seen[key]}: {stripped!r}")
        else:
            seen[key] = n

if count > MAX_LINES:
    errors.append(f"too many entries ({count}); limit is {MAX_LINES}")

if errors:
    print("keyword list has problems:")
    for e in errors:
        print(" -", e)
    sys.exit(1)

print(f"OK: {count} keywords")
