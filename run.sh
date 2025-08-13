#!/bin/bash
# Quick Test Script für Fachinformatiker Lernapp
# Erstellt von Hans Hahn - Alle Rechte vorbehalten

echo "🚀 Fachinformatiker Lernapp - Quick Test"
echo "========================================="

# Farben für Output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Projekt-Verzeichnis
cd "C:/SoftwareEntwicklung/Fachinformatiker_Lernapp_Java" || exit

echo ""
echo "📦 Teste Gradle Build..."
if ./gradlew.bat clean build --no-daemon; then
    echo -e "${GREEN}✅ Build erfolgreich!${NC}"
else
    echo -e "${RED}❌ Build fehlgeschlagen!${NC}"
    exit 1
fi

echo ""
echo "🧪 Führe Tests aus..."
if ./gradlew.bat test --no-daemon; then
    echo -e "${GREEN}✅ Tests erfolgreich!${NC}"
else
    echo -e "${YELLOW}⚠️ Einige Tests fehlgeschlagen${NC}"
fi

echo ""
echo "🚀 Starte Anwendung..."
echo -e "${YELLOW}Die Anwendung wird gestartet auf http://localhost:8080${NC}"
echo "Drücke Ctrl+C zum Beenden"
echo ""

./gradlew.bat :lernapp-web:bootRun --no-daemon
