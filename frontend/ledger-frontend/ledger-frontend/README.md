# Khatabook — Ledger Frontend

React + Vite frontend for the voice-controlled ledger app. This first slice
covers login and registration against a Spring Boot backend, with JWT
storage and an authenticated API client wired up so future screens (the
transaction ledger, the voice command bar, etc.) can be built directly on
top of it.

## Setup

```bash
npm install
cp .env.example .env   # adjust VITE_API_BASE_URL if the backend isn't on :8080
npm run dev
```

The backend is expected at `http://localhost:8080` with:

- `POST /api/auth/login` — body `{ email, password }` → `{ token, tokenType }`
- `POST /api/auth/register` — body `{ username, email, password }` → `{ id, name, email, createdAt }`

## Structure

```
src/
  api/
    axiosClient.js   axios instance: attaches the JWT to every request,
                      logs the user out automatically on a 401
    authApi.js        login()/register() calls, matching the backend
                       controller contracts exactly
  context/
    AuthContext.jsx   isAuthenticated state + login/register/logout,
                       exposed via useAuth()
  components/
    AuthLayout.jsx    shared ledger-book chrome (masthead, tabs, card)
                       around the login/register forms
    LedgerField.jsx   labelled input used by both forms
    ProtectedRoute.jsx route guard — wrap any page that needs a session
  pages/
    Login.jsx
    Register.jsx
    Dashboard.jsx     placeholder landing page after sign-in — this is
                       where the real ledger UI (transactions, balances,
                       the microphone button) should go next
  utils/
    tokenStorage.js   the only file that touches localStorage; swap this
                       out if you move to httpOnly cookies later
  styles/
    index.css         all styling, as plain CSS custom properties
```

## Auth flow

1. `Login`/`Register` call `useAuth()`, which calls `authApi`.
2. On successful login, the token is saved via `tokenStorage` and
   `isAuthenticated` flips to `true`.
3. `axiosClient` reads the stored token on every request and attaches
   `Authorization: <tokenType> <token>`.
4. A 401 response anywhere in the app clears the session and flips
   `isAuthenticated` back to `false` (see `registerUnauthorizedHandler`),
   so a stale token can't leave the UI stuck in a signed-in state.
5. `ProtectedRoute` redirects to `/login` when there's no session; add new
   authenticated routes by wrapping them the same way `Dashboard` is
   wrapped in `App.jsx`.

## Extending this

- Registration doesn't return a token (per the backend contract), so it
  redirects to `/login` on success rather than signing the user in
  directly. If the backend later returns a token from `/register` too,
  that's a one-line change in `Register.jsx`.
- To add a new authenticated screen: create it under `src/pages/`, add a
  `<Route>` in `App.jsx` wrapped in `<ProtectedRoute>`, and call the
  backend through `apiClient` (from `src/api/axiosClient.js`) so auth
  headers and 401 handling come for free.
- `useAuth()` is the single source of truth for session state — pull it
  into any component that needs to know whether someone's signed in, or
  needs to log them out.
