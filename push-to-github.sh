#!/bin/bash
set -e

# Bahnpilot - GitHub Push Helper
# Führe dieses Skript aus, nachdem du ein leeres GitHub Repo erstellt hast.

if [ -z "$1" ]; then
  echo "📋 Nutzung: ./push-to-github.sh <github-repo-url>"
  echo ""
  echo "Beispiel:"
  echo "  ./push-to-github.sh https://github.com/DEIN-NAME/bahnpilot.git"
  echo ""
  echo "Schritte vorher:"
  echo "  1. Gehe auf https://github.com/new"
  echo "  2. Name: bahnpilot (Public, kein README, kein .gitignore)"
  echo "  3. Create Repository"
  echo "  4. Kopiere die URL und führe dieses Skript aus"
  echo ""
  exit 1
fi

REPO_URL="$1"

echo "🚆 Bahnpilot → GitHub Push"
echo "Repo: $REPO_URL"
echo ""

# Remote setzen (falls vorhanden, überschreiben)
git remote remove origin 2>/dev/null || true
git remote add origin "$REPO_URL"

echo "📤 Pushe zu GitHub..."
git add -A
git commit -m "feat: Bahnpilot v1.0 — Echtzeit Bahn Navigator + Android APK" || echo "Commit existiert bereits"
git branch -M main
git push -u origin main

echo ""
echo "✅ Fertig! Dein Repo ist live:"
echo "   $REPO_URL"
echo ""
echo "🤖 APK Bauen:"
echo "   Gehe auf GitHub → Actions → 'Bahnpilot APK Bauen' → Der Build startet automatisch"
echo "   Nach ca. 3 Min kannst du die APK unter 'Artifacts' herunterladen"
echo ""
echo "🌐 Web Deploy:"
echo "   GitHub → Settings → Pages → Source: GitHub Actions → Deine Web-App ist live!"
echo ""
