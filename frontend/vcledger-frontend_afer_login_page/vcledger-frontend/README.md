# VCLedger Frontend

React + Tailwind frontend for the four post-login pages: Voice Ledger,
New Customer, All Customers, and Customer detail.

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

## Integrating with your existing login page

This project only contains the **post-login** app (`App.jsx` routes
`/voice`, `/customers`, `/customers/new`, `/customers/:id`). To merge it
with your existing Login / Create Account pages:

1. Copy `src/` (minus `main.jsx`/`App.jsx` if you already have your own)
   into your existing project, or copy your Login/CreateAccount page
   components into this project's `src/pages/`.
2. Add your login routes to `src/App.jsx` alongside the ones already
   there, e.g. `<Route path="/login" element={<LoginPage />} />`.
3. After a successful login, make sure your login page stores the JWT
   under the **same key** this project reads — see `.env.example`'s
   `VITE_JWT_STORAGE_KEY` (defaults to `vcledger_token`) — then redirect
   to `/voice`.
4. `src/api/client.js` is the only place that reads the token
   (`getToken()`). If your login stores it somewhere other than
   `localStorage`, that's the only function you need to change.

## Backend assumptions to confirm

Everything below matches the endpoints you specified exactly, **except**
two calls the UI needs but weren't in your spec — both isolated in
`src/api/customerService.js` with comments, so they're easy to find and
adjust:

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
  components/
    layout/       # Sidebar, AppShell
    common/       # Button, Avatar, Pagination, PageLoader, EmptyState
    customers/    # CustomerCard, CustomerGrid, TransactionList
    voice/        # RecordButton, SendButton, MessageBubble
  hooks/          # useAudioRecorder, useCustomers
  pages/          # one file per route
  utils/          # formatters (currency, date, initials)
```

Every component is small and single-purpose on purpose, so you can find
and change one thing (a color, a card layout, a copy string) without
touching the rest — see comments at the top of each file for what it's
responsible for.
