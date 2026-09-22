# VCLedger Frontend

React + Tailwind frontend for VCLedger, end to end: sign in / register,
then Voice Ledger, New Customer, All Customers, and Customer detail.

## Design concept

The visual language is a shopkeeper's cloth-bound ledger book (*bahi-khata*):
a maroon cover with brass corner fittings for the sidebar and primary
buttons, warm cream paper for content, and stamped ink-serif headings.
Every color/shadow token lives in `tailwind.config.js` and `src/index.css`
— change them there rather than in individual components.

- **Colors:** maroon (cover/sidebar), brass (accents, primary buttons),
  paper/cream (background, cards), teal (credit balances), rust-red
  (debit balances, the record button).
- **Type:** Fraunces (display serif, for headings) + Manrope (UI sans).
- **Surfaces:** `.surface-card` / `.surface-pressed` utility classes in
  `index.css` give the raised-paper / pressed-inset skeuomorphic look
  consistently across the app.

## Getting started

```bash
npm install
cp .env.example .env   # then edit if your API URL or JWT key differ
npm run dev
```

## Auth flow (now fully wired in)

`App.jsx` has two public routes and four protected ones:

- `/login`, `/register` — your `Login.jsx` / `Register.jsx`, using
  `AuthLayout` + `LedgerField` for the ledger-book look.
- `/voice`, `/customers`, `/customers/new`, `/customers/:id` — wrapped in
  `ProtectedRoute` (`src/components/ProtectedRoute.jsx`), which reads
  `isAuthenticated` from `AuthContext` and bounces to `/login` if there's
  no session.

`src/context/AuthContext.jsx` is new — it implements the `login`,
`register`, `logout`, `isAuthenticated` that your components already call
via `useAuth()`. Token storage is delegated to the same `getToken` /
`setToken` / `clearToken` functions in `src/api/client.js` that the rest
of the app already uses, so there's exactly one JWT storage key for the
whole app (`VITE_JWT_STORAGE_KEY` in `.env`, default `vcledger_token`) —
no separate key for auth vs. the rest of the app.

One small edit was made to your `Login.jsx`: the post-login redirect
default changed from `/dashboard` to `/voice`, since `VoiceLedgerPage` is
the real home screen (spec item 1.4). Your `Dashboard.jsx` placeholder is
kept in `src/pages/` for reference but isn't routed anywhere anymore.

A "Sign out" link was added to the bottom of the sidebar
(`src/components/layout/Sidebar.jsx`), calling `logout()` from
`AuthContext` and sending the shopkeeper back to `/login`.

The `ledger-*` class names your `AuthLayout`/`LedgerField`/`Login`/
`Register` components use (`ledger-page`, `ledger-card`, `ledger-field`,
`ledger-button`, etc.) didn't have any CSS behind them yet — they're now
defined in `src/index.css` under `@layer components`, built from the same
maroon/brass/paper/ink tokens as the rest of the app, so the sign-in
screens look like part of the same ledger book rather than a bolted-on
page.

## Backend assumptions to confirm

Everything below matches the endpoints you specified exactly, **except**
these calls the UI needs but weren't in your original spec — all
isolated with comments so they're easy to find and adjust:

In `src/context/AuthContext.jsx`:
- `POST /api/auth/login` — body `{ email, password }`, expects a `token`
  (or `accessToken`) field back.
- `POST /api/auth/register` — body `{ username, email, password }`,
  expects the created user back (no token — the shopkeeper signs in next).

In `src/api/customerService.js`:
- `DELETE /api/customers/:id` — used by the delete button on the
  Customer page.
- `GET /api/customers/:id/transactions` — used to populate the
  transaction history on the Customer page. Treated as "no transactions
  yet" on a 404 so the page still works before this exists.

The `/api/voicecommand/process` response is intentionally left
unhandled beyond displaying raw JSON, per your note — see
`renderSystemContent()` in `src/pages/VoiceLedgerPage.jsx`.

Customer photo upload is stubbed: the New Customer form lets the
shopkeeper pick a preview image, but it is **not** sent to the backend
yet (only `name`/`phone` are), matching your note that you'll implement
that later.

## Project structure

```
src/
  api/            # axios client + one file per resource (customers, voice)
  context/        # AuthContext (login/register/logout, isAuthenticated)
  components/
    AuthLayout.jsx, LedgerField.jsx, ProtectedRoute.jsx
    layout/       # Sidebar, AppShell
    common/       # Button, Avatar, Pagination, PageLoader, EmptyState
    customers/    # CustomerCard, CustomerGrid, TransactionList
    voice/        # RecordButton, SendButton, MessageBubble
  hooks/          # useAudioRecorder, useCustomers
  pages/          # Login, Register, Dashboard (unrouted), + one file per app route
  utils/          # formatters (currency, date, initials)
```

Every component is small and single-purpose on purpose, so you can find
and change one thing (a color, a card layout, a copy string) without
touching the rest — see comments at the top of each file for what it's
responsible for.
