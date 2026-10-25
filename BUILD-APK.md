# 📲 Bahnpilot als APK bauen — Anleitung

Bahnpilot ist eine **PWA + Capacitor App**. Du hast 3 Wege, sie als Android-App zu bekommen:

---

## Weg 1: 🚀 Automatisch via GitHub Actions (empfohlen, kein Setup)

**Kein Android Studio nötig! GitHub baut die APK für dich.**

### Schritte:

1. **GitHub Repo erstellen**
   - Gehe auf https://github.com/new
   - Name: `bahnpilot` (oder `bahn-pilot`)
   - Public, **kein** README / .gitignore / Lizenz (wir haben alles)
   - `Create repository`

2. **Code pushen** (im Terminal im `bahnpilot` Ordner):

   ```bash
   ./push-to-github.sh https://github.com/DEIN-NAME/bahnpilot.git
   # oder manuell:
   git remote add origin https://github.com/DEIN-NAME/bahnpilot.git
   git add -A
   git commit -m "Bahnpilot v1.0"
   git branch -M main
   git push -u origin main
   ```

3. **APK bauen lassen**
   - Gehe im neuen Repo auf **Actions** → **“🤖 Bahnpilot APK Bauen”**
   - Der Workflow startet automatisch nach dem Push
   - Warte ~3–5 Minuten (gelber Punkt → grüner Haken)

4. **APK herunterladen**
   - Im Workflow-Run nach unten scrollen → **Artifacts** → `bahnpilot-debug-apk`
   - ZIP herunterladen, entpacken → `bahnpilot-debug-xxxx.apk`
   - Auf Android installieren: Datei öffnen → “Trotzdem installieren” (da Debug-Signatur)

> **Tipp:** Jeder Push auf `main` baut automatisch eine neue APK!

---

## Weg 2: 💻 Lokal mit Android Studio

Wenn du Android Studio installiert hast:

```bash
# 1. Dependencies
npm install

# 2. Android Projekt erstellen (einmalig)
npx cap add android

# 3. Syncen + Bauen
npm run build:apk

# APK liegt dann hier:
# android/app/build/outputs/apk/debug/app-debug.apk
```

**Release-APK (signiert für Play Store):**

1. Keystore erstellen:
   ```bash
   keytool -genkey -v -keystore android/app/bahnpilot.keystore -keyalg RSA -keysize 2048 -validity 10000 -alias bahnpilot
   ```
2. In `android/gradle.properties` eintragen (siehe `.github/workflows/android-apk.yml`)
3. `npm run build:apk:release` → APK in `android/app/build/outputs/apk/release/`

---

## Weg 3: 📱 Direkt als PWA installieren (ohne APK!)

Bahnpilot ist bereits eine **installierbare PWA**:

1. Öffne https://DEIN-NAME.github.io/bahnpilot/ auf Android Chrome
2. Menü → **“Zum Startbildschirm hinzufügen”** → **“App installieren”**
3. Fertig — läuft wie native App, mit Icon, Splashscreen, offline-Cache!

PWA hat dieselbe Funktionalität wie APK, nur ohne Play Store. Für viele Nutzer reicht das!

---

## 🔐 Play Store Release (optional)

Wenn du Bahnpilot in den Play Store bringen willst:

1. In GitHub Repo → **Settings → Secrets** folgende Secrets anlegen:
   - `ANDROID_KEYSTORE_BASE64`: `base64 -w 0 android/app/bahnpilot.keystore`
   - `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`
2. Push einen Tag: `git tag v1.0.0 && git push origin v1.0.0`
3. Workflow baut signierte Release-APK + erstellt GitHub Release
4. APK/AAB im Play Console hochladen (`android/app/build/outputs/bundle/release/app-release.aab` für Play Store → `bundleRelease` statt `assembleRelease`)

---

## ❓ Häufige Fragen

**“App nicht installiert” auf Android?**
→ Bei Debug-APK: Unbekannte Quellen erlauben (Einstellungen → Apps → Chrome → Unbekannte Apps installieren)

**APK zu groß?**
→ Debug-APK ~8 MB, Release-APK ~5 MB (Capacitor WebView)

**Echtzeit funktioniert nicht in APK?**
→ In `capacitor.config.json` ist `allowMixedContent:true` gesetzt — Internet-Berechtigung ist in `AndroidManifest.xml` automatisch drin.

**Automatische Updates?**
→ Bei PWA: automatisch. Bei APK: neue APK bauen & installieren, oder Play Store nutzen.

---

Made with ♥ in Hannover — Viel Spaß mit Bahnpilot!
