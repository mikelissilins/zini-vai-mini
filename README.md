# Zini vai mini

Pilna LV/EN nometnes viktorīnas lietotne ar spēļu redaktoru, vadītāja paneli un reāllaikā sinhronizētu projektora skatu.

## Iespējas

- Iebūvēts kristīgās nometnes templates un tukšs templates.
- Neierobežotas sadaļas ar fiksētām 10, 20, 30, 40 un 50 punktu vietām.
- Brīvās atbildes un 2–4 variantu jautājumi, attēli līdz 5 MB.
- 2–12 komandas, automātiska punktu skaitīšana, gājienu rotācija un undo.
- Publisks projektora URL ar SSE sinhronizāciju, pilniem rezultātiem un TOP 3 + 4. vietu.
- Clerk viena īpašnieka autentifikācija un PostgreSQL progress, kas saglabājas pēc restartiem.

## Lokālā palaišana

Prasības: Java 21, Node 24.15+ un Docker.

1. Nokopē `.env.example` uz `.env` un ievadi sava Clerk lietotāja ID un aplikācijas parametrus.
2. Palaid visu steku:

```bash
docker compose up --build
```

3. Atver [http://localhost:8080](http://localhost:8080).

Izstrādei atsevišķi:

```bash
docker compose up postgres -d
cd backend && ./mvnw spring-boot:run
cd frontend && npm install && npm start
```

Frontend tad darbojas `http://localhost:4200` un pārsūta `/api` uz backend `:8080`.

## Konfigurācija

Obligātie production mainīgie:

- `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`
- `CLERK_PUBLISHABLE_KEY`, `CLERK_ISSUER`, `CLERK_JWKS_URL`
- `APP_OWNER_CLERK_USER_ID`

Ja `APP_OWNER_CLERK_USER_ID` nav iestatīts, privātie API pieprasījumi tiek noraidīti.

## Testi

```bash
npm --prefix frontend test -- --watch=false
./backend/mvnw -f backend/pom.xml test
docker build -t zini-vai-mini .
```

Backend integrācijas tests izmanto Testcontainers PostgreSQL, palaiž Flyway migrācijas un MockMvc pieprasījumus ar mock Clerk JWT.

## Render

`render.yaml` izveido bezmaksas Docker web service un PostgreSQL datubāzi Frankfurtē, ja bezmaksas plāni kontā ir pieejami. Blueprint izveides laikā ievadi četrus Clerk/owner mainīgos. Pēc pirmā deploy Render URL jāpievieno Clerk allowed origins un redirect URL sarakstam.

Render savienojuma `postgresql://` URL aplikācija startā pārveido uz JDBC formu. Flyway migrācijas izpildās automātiski, un health check ir `/actuator/health/readiness`.

## Tehnoloģijas

Spring Boot 4.1, Java 21, Angular 22 standalone/signals, Bulma 1.0.4, PostgreSQL 17, Flyway, Clerk un Server-Sent Events.
