# Bahnpilot — Open Source Bahn Navigator

<div align="center">

### **Dein Open-Source Alternative zum DB Navigator. 🚆 Echtzeit. Werbefrei. Ohne Tracking.**

[![Live - Echtzeit](https://img.shields.io/badge/Live-Echtzeit-EC0000?style=for-the-badge)](#) [![MIT Lizenz](https://img.shields.io/badge/Lizenz-MIT-black?style=for-the-badge)](#) [![APK](https://img.shields.io/badge/APK-Automatisch%20via%20GitHub%20Actions-00C853?style=for-the-badge)](#) [![PWA](https://img.shields.io/badge/PWA-installierbar-4285F4?style=for-the-badge)](#)

**[🌐 Live Demo](https://dein-name.github.io/bahnpilot/) • [📲 APK Bauen](./BUILD-APK.md) • [📖 Datenquelle](#-datenquelle)**

</div>

---

**Bahnpilot** nutzt dieselben Live-Daten wie der **DB Navigator** — über die Community-API **v6.db.transport.rest** (Wrapper um DB Navigator / bahn.de Vendo APIs).

- 🔍 **Verbindung suchen** — Bahnhof-Autocomplete, Umstiege, Fußwege, Dauer
- ⏱ **Echtzeit** — Verspätungen pro Halt, Gleiswechsel, Ausfälle
- 📋 **Abfahrtsmonitor** — Live-Abfahrten für jeden Bahnhof (z. B. Hannover Hbf)
- 🔄 **Live aktualisieren** — Refresh-Token holt DB-Neu-Stand in Sekunden
- 📱 **PWA + APK** — Installierbar als App auf Android (Capacitor)
- 🔓 **MIT** — selbst hosten in 2 Minuten, kein Tracking, keine Werbung

<img src="./icons/icon-512.png" width="120" alt="Bahnpilot Icon">

---

## 🚀 Schnellstart — 2 Minuten

### 1) Lokal starten (ohne Build)

```bash
git clone https://github.com/DEIN-NAME/bahnpilot.git
cd bahnpilot
npx serve .
# → http://localhost:3000
```
Kein API-Key — spricht direkt mit `https://v6.db.transport.rest`.

### 2) Auf GitHub bringen + APK automatisch bauen

Siehe **[BUILD-APK.md — Komplette Anleitung](./BUILD-APK.md)**

Kurzfassung:
```bash
# 1. Leeres Repo auf https://github.com/new erstellen (Name: bahnpilot)
# 2. Pushen:
./push-to-github.sh https://github.com/DEIN-NAME/bahnpilot.git

# 3. Auf GitHub: Actions → "Bahnpilot APK Bauen" → 3 Min warten → APK downloaden
```
Jeder Push auf `main` baut automatisch eine neue APK!

### 3) Als PWA installieren (ohne APK)

- Chrome auf Android → Live-Demo öffnen → Menü → **“App installieren”**
- Läuft wie native App mit Icon & Splashscreen.

---

## 📲 APK — Android App

Du willst Bahnpilot als echte **.apk** auf deinem Handy?

**Alles vorbereitet:**
- `capacitor.config.json` → App-ID `de.bahnpilot.app`
- `.github/workflows/android-apk.yml` → Baut Debug + Release APK automatisch
- Icons, Manifest, Service Worker, Permissions

**Download nach Push:** GitHub → Actions → Run → Artifacts → `bahnpilot-debug-apk.zip` → entpacken → auf Handy installieren.

> Ohne Android Studio! GitHub baut für dich in der Cloud.

Lokal bauen (falls du Android Studio hast): siehe [BUILD-APK.md](./BUILD-APK.md)

---

## 🛰 Datenquelle

**v6.db.transport.rest** — sauberer REST-Wrapper um Deutsche Bahn APIs (bahn.de + DB Navigator), gepflegt von der [public-transport Community](https://github.com/public-transport/db-rest) (AGPL).

- Basis: `https://v6.db.transport.rest`
- Endpunkte: `/locations`, `/journeys`, `/journeys/:ref`, `/stops/:id/departures`, `/trips/:id`
- Felder: `departureDelay`, `arrivalDelay`, `cancelled`, `platform`, `remarks`, `stopovers`
- Rate-Limit: 100 Req/Min (Burst 200)
- Abdeckung: Fernverkehr + Regional + teils S-Bahn/Bus — alles was DB Navigator zeigt

> Die alte HAFAS-API wurde Ende 2024 abgeschaltet. Diese App nutzt bereits die neuen Vendo/Movas APIs. Selbst hosten: [derhuerst/db-rest](https://github.com/derhuerst/db-rest).
>
> Fallback: [Transitous/MOTIS](https://transitous.org) (`api.transitous.org`).

---

## 🧩 Architektur

```
index.html              Single-Page App (Tailwind CDN + Vanilla JS)
manifest.json + sw.js   PWA (installierbar, offline-cache)
capacitor.config.json   Native Wrapper (Android WebView)
.github/workflows/
  android-apk.yml       Baut APK in der Cloud (Java 17 + Android SDK)
  pages.yml             Deployed Web-App auf GitHub Pages
```

**Kein Backend.** Alles im Browser. CORS-ready.

API-Nutzung:
```
/locations?query=           Autocomplete
/journeys?from=&to=         Routen
/journeys/:refreshToken     Live-Refresh
/stops/:id/departures       Abfahrtsmonitor
/trips/:id                  Alle Halte einer Fahrt
```

---

## ⚙️ Konfiguration

In `index.html`:
```js
const API = 'https://v6.db.transport.rest';
// Eigener Fork:
// const API = 'https://deine-db-rest.example.com';
```

App-ID / Name in `capacitor.config.json` ändern, dann `npx cap sync android`.

---

## 🤝 Mitmachen

PRs willkommen! Ideen:

- [ ] Live-Karte mit Zugposition (IRIS/RIS)
- [ ] Ticket-Preise & Bestpreis
- [ ] Push bei Verspätung (Web Push / FCM)
- [ ] Barrierefreiheit, EN-Sprache
- [ ] Offline-Fahrplan & Service Worker v2
- [ ] iOS Build (`npx cap add ios`)

`CONTRIBUTING.md` folgt.

---

## 📄 Lizenz

MIT — siehe [LICENSE](./LICENSE). Nutze, forke, hoste, verkaufe — Open Source gehört dir.

---

<div align="center">

**Made with ♥ in Hannover.** Kein Tracking. Keine Werbung. Nur Züge.

*Hannover Hbf → Berlin Hbf in 1:45 Std? Bahnpilot zeigt dir ob’s wirklich klappt — in Echtzeit.*

</div>
