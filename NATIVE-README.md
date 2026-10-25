# Bahnpilot Native — 100% Kotlin Android App (Kein WebView!)

> **Diese APK ist kein Wrapper um die Webseite.** Es ist eine echte native Android-App — geschrieben in **Kotlin + Jetpack Compose + Material 3**, mit direkten API-Calls zu `v6.db.transport.rest`.

## 🆚 Hybrid vs. Native

|  | **Hybrid `android/`** (Capacitor) | **Native `android-native/`** (Kotlin) |
|---|---|---|
| **Was ist es?** | WebView lädt lokales `www/index.html` (dein aktuelles Web) + native Shell | Echte Android-App, jede Pixel nativ gezeichnet |
| **Technik** | HTML/CSS/JS → WebView | Kotlin, Compose, Retrofit, Coroutines |
| **APK Größe** | ~8 MB | ~6-9 MB |
| **Performance** | Gut, aber Web | Sehr flüssig, 120Hz, native Animationen |
| **Offline** | Service Worker Cache | Eigener Room-Cache möglich (geplant) |
| **Play Store** | Ja, als Web-App | Ja, als „echte“ App preferred |
| **Zu finden** | `android/app/build/outputs/apk/debug/app-debug.apk` | `android-native/app/build/outputs/apk/debug/app-debug.apk` |
| **App-ID** | `de.bahnpilot.app` | `de.bahnpilot.app.native` (parallel installierbar!) |

**Beide sind Open Source & kostenlos.** Du kannst beide behalten — Hybrid für schnelles Web-Update, Native für bestes Android-Feeling.

---

## 📲 Native Highlights

- **Kein WebView:** Jeder Screen ist Compose — `SearchScreen`, `ResultsScreen`, `DeparturesScreen`, `DetailSheet`
- **Direkte API:** `Retrofit2` + `OkHttp` + `Gson` → `https://v6.db.transport.rest/locations|journeys|departures`
- **Echtzeit:** `departureDelay`, `arrivalDelay`, `cancelled`, `platform` + Refresh-Token (`/journeys/{refreshToken}`)
- **Native Komponenten:** `JourneyCard` mit Material-Chips, `BadgeDelay` (grün/gelb/rot), Bottom-Sheet für Details
- **Navigation:** `navigation-compose` mit BottomBar (Suche / Verbindungen / Abfahrten)
- **Dark Theme:** `Theme.Bahnpilot` — DB-Rot `#EC0000` auf `#07070A` wie im Web

---

## 🚀 Nativ bauen

### Lokal (Android Studio)

1. Android Studio öffnen → **Open → `android-native` Ordner**
2. Warten bis Gradle Sync fertig
3. Run → Device auswählen → Play → APK wird gebaut & installiert

Oder Terminal:
```bash
cd android-native
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

### Via GitHub Actions (automatisch)

Workflow `.github/workflows/android-native-apk.yml` baut bei jedem Push auf `main` (wenn `android-native/**` geändert):

1. Pushe: `git add android-native && git commit -m "native" && git push`
2. GitHub → Actions → “🚀 Bahnpilot Native APK” → 4 Min warten
3. Artifacts → `bahnpilot-native-apk` → APK herunterladen
4. Auf Handy installieren — läuft **parallel** zur Hybrid-APK!

> **Tipp:** Beide APKs können gleichzeitig installiert sein — unterschiedliche App-IDs!

---

## 🧩 Code-Struktur

```
android-native/
  app/src/main/java/de/bahnpilot/app/
    MainActivity.kt              ← Entry + Navigation + BottomBar
    BahnpilotViewModel.kt        ← State + API-Logik (Live journeys/departures)
    data/
      Models.kt                  ← FPTF Journey/Leg/Departure
      ApiService.kt              ← Retrofit Interface (locations, journeys, departures)
      Repository.kt              ← Retrofit Builder (OkHttp + Logging)
    ui/
      theme/Theme.kt             ← Dark/Light Bahnpilot Colors
      components/JourneyCard.kt  ← Native Card (kein HTML!)
      screens/SearchScreen.kt    ← Autocomplete + Swap + Suche
      screens/...                ← Results, Departures, Detail
```

**Wichtig:** Alle Netzwerk-Calls sind **nativ** — kein `window.fetch` aus WebView, sondern `OkHttp`.

---

## 🔧 Anpassen

- API wechseln in `data/Repository.kt`: `BASE_URL = "https://v6.db.transport.rest/"` → eigener Fork
- Icons: `app/src/main/res/mipmap-*/ic_launcher.png` (wie Hybrid)
- Farben: `ui/theme/Theme.kt` → `Color(0xFFEC0000)`
- Navigation: `MainActivity.kt` → `NavHost` Routen ändern

---

## 📋 Roadmap Native v2

- [ ] Paging für Journeys (früher/später)
- [ ] Room-Cache für letzte Suchen & Favorites
- [ ] FCM Push bei Verspätung (WorkManager polling)
- [ ] Karten-Integration (OSM) mit Live-Zugposition (IRIS/RIS)
- [ ] Widgets & Live Activity

---

**Made with Kotlin + ♥ — Kein WebView, nur Züge.**
