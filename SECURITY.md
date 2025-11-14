# Security Policy

## Supported Versions
| Version | Supported |
|---------|-----------|
| 1.x     | Yes       |

## Reporting Vulnerabilities
Email security@gpuinsightai.dev. Do NOT open public issues.

Response time: 72 hours. Critical patch: 7 days.

## Security Practices
- API keys stored in BuildConfig (never in source)
- Stack traces sanitized before Gemini API calls
- SHA-256 hash chains for audit log integrity
- Zero-trust RBAC for all resource access
