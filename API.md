# API Documentation

## Gemini AI Integration
GPU Insight AI uses Google Gemini Pro for GPU error analysis.

### Endpoint
```
POST https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key={API_KEY}
```

### Privacy
All stack traces are sanitized before sending:
- AWS keys redacted
- Bearer tokens redacted
- Internal IPs redacted

### Error Handling
All API calls return `Result<String>` — errors are surfaced to UI via ViewModel state.
