# React VITE starter

Vite + React + TypeScript frontend template:

- shadcn/ui base setup
- environment-driven backend and OpenAPI URLs
- typed DTOs matching the current backend contract
- ready API client wrappers for core modules
- starter pages/forms for rapid UI design

## Environment variables

Configured in `.env` and `.env.example`:

```bash
VITE_API_BASE_URL=http://localhost:8080
VITE_OPENAPI_URL=http://localhost:8080/swagger-ui/index.html
VITE_OPENAPI_JSON_URL=http://localhost:8080/v3/api-docs
VITE_GITHUB_LOGIN_URL=http://localhost:8080/oauth2/authorization/github
```

## Run locally

```bash
npm install
npm run dev
```

The app expects the backend API to run on `http://localhost:8080` by default.

## API setup

- DTOs and API wrapper types: `src/types/api.ts`
- Generic HTTP client: `src/api/http-client.ts`
- Endpoint wrappers: `src/api/case-manager-client.ts`

All endpoints return `ApiResponseWrapper<T>`; the client automatically unwraps to `T`.