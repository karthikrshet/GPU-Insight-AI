# Security Policy & Guidelines

## 🛡️ Reporting Vulnerabilities
If you discover a security vulnerability within **GPU Insight AI**, please send an email to `kartikrshet@gmail.com` or create a private disclosure. Do NOT open a public GitHub issue for security vulnerabilities.

## 🔑 Secret & PII Redaction
The Gemini AI Debug Assistant incorporates an automatic privacy regex pipeline (`SecretRedactor`) that sanitizes:
- Bearer tokens, JWTs, and API key patterns
- Internal IPv4 / IPv6 addresses and local hostnames
- AWS access keys (`AKIA...`) and database passwords

---

Copyright © 2026 Karthik Rajesh Shet (@karthikrshet). Released under the Apache-2.0 License.
