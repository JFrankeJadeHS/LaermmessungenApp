# SettingsActivity.kt

## Beschreibung
Die ⁠ SettingsActivity ⁠ verwaltet die Einstellungen der App, insbesondere den Dunkelmodus. Sie speichert die Einstellungen mit ⁠ SharedPreferences ⁠ und ändert die Hintergrundfarbe entsprechend der Nutzerwahl.

## Funktionen

### Variablen
•⁠  ⁠⁠ PREFS_NAME ⁠: Name der SharedPreferences-Datei.
•⁠  ⁠⁠ DARK_MODE_KEY ⁠: Schlüssel zur Speicherung der Dunkelmodus-Einstellung.
•⁠  ⁠⁠ switchModes ⁠: UI-Element für den Umschalter des Dunkelmodus.

### Methoden

#### ⁠ onCreate(savedInstanceState: Bundle?) ⁠
•⁠  ⁠Lädt das Layout ⁠ activity_settings ⁠.
•⁠  ⁠Initialisiert den ⁠ Switch ⁠ für den Dunkelmodus.
•⁠  ⁠Liest die gespeicherte Dunkelmodus-Einstellung und setzt den ⁠ Switch ⁠ entsprechend.
•⁠  ⁠Reagiert auf Änderungen am ⁠ Switch ⁠, speichert die Einstellung und passt die Hintergrundfarbe an.

#### ⁠ setAppBackgroundColor(isDarkMode: Boolean) ⁠
•⁠  ⁠Setzt die Hintergrundfarbe der App basierend auf dem Dunkelmodus-Zustand.
  - Schwarz für Dunkelmodus.
  - Weiß für normalen Modus.

## Nutzung
Diese Aktivität wird aufgerufen, wenn der Nutzer die App-Einstellungen öffnen möchte, um den Dunkelmodus ein- oder auszuschalten.