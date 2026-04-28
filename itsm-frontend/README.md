# Case Manager Frontend

Modern service-desk frontend built with Vite, React, TypeScript, and shadcn styles.

## Included features

- Strict auth-guarded workspace (`/app/*`) with GitHub OAuth login flow.
- Professional app shell with sidebar navigation and top command bar.
- Dashboard metrics, ticket queue, ticket details, and comment timeline actions.
- Assignment groups management, admin user/role operations, and audit logs.
- Notifications center and profile/settings screen from `/api/v1/auth/me`.
- API wrappers with typed DTO contracts from backend endpoints.
- Query client setup (`@tanstack/react-query`) and toast notifications (`sonner`).
- Route-level fallback pages and global error boundary.

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

## App routes

- `/login` GitHub sign-in entry
- `/logout` sign-out confirmation
- `/app/dashboard`
- `/app/tickets`
- `/app/tickets/:ticketId`
- `/app/assignment-groups`
- `/app/users`
- `/app/audit`
- `/app/notifications`
- `/app/settings`