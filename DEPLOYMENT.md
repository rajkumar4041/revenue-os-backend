# Deploying RevenueOS (free tier)

A live demo in three free services:

| Piece | Host | Notes |
| ----- | ---- | ----- |
| Frontend (static) | **Vercel** | instant, always on |
| Backend (Spring Boot, Docker) | **Render** | free web service; sleeps after ~15 min idle (cold start ~30–60 s) |
| Database (Postgres) | **Neon** | free, always on, serverless |

Demo login once it's up: **admin@demo.io / Password123!**

---

## 1. Database — Neon

1. Create a project at https://neon.tech → copy the connection string
   (`postgresql://USER:PASSWORD@HOST/revenueos?sslmode=require`).
2. Note these for the backend:
   - `DB_URL` = `jdbc:postgresql://HOST/revenueos?sslmode=require`
   - `DB_USERNAME` = `USER`
   - `DB_PASSWORD` = `PASSWORD`

## 2. Backend — Render

1. https://render.com → **New → Web Service** → connect `revenue-os-backend`.
2. Runtime **Docker** (the repo's `Dockerfile` is used). Health check path: `/actuator/health`.
3. Environment variables:
   ```
   SPRING_PROFILES_ACTIVE=demo        # seeds the demo company on first boot
   DB_URL=<from Neon>
   DB_USERNAME=<from Neon>
   DB_PASSWORD=<from Neon>
   JWT_SECRET=<base64 256-bit secret>  # e.g. `openssl rand -base64 48`
   APP_BASE_URL=https://<your-vercel-app>.vercel.app
   CORS_ALLOWED_ORIGINS=https://<your-vercel-app>.vercel.app
   ```
   (Render injects `PORT` automatically — the app already reads it.)
4. Deploy. Flyway runs the migrations and the demo data is seeded. Copy the
   service URL, e.g. `https://revenue-os-backend.onrender.com`.

## 3. Frontend — Vercel

1. https://vercel.com → **Add New → Project** → import `revenue-os-frontend`.
2. Framework preset **Vite** (build `yarn build`, output `dist`). `vercel.json`
   in the repo handles SPA routing.
3. Environment variable:
   ```
   VITE_API_URL=https://revenue-os-backend.onrender.com
   ```
   (origin only — API paths already include `/api/v1`.)
4. Deploy → copy the app URL.

## 4. Connect the two

Set the backend's `CORS_ALLOWED_ORIGINS` (and `APP_BASE_URL`) to the Vercel URL
from step 3 and redeploy the backend. Done — open the Vercel URL and sign in with
the demo account.

---

### Notes
- **Cold starts:** Render's free backend sleeps when idle; the first request after
  a pause takes ~30–60 s. Fine for a portfolio demo; upgrade the Render plan to
  remove it.
- **Postgres version:** Neon runs a recent Postgres; the bundled Flyway supports
  it. If Flyway ever rejects the version, bump `flyway.version` in `pom.xml`.
- **Secrets:** never reuse the dev `JWT_SECRET` in production — generate a fresh one.
