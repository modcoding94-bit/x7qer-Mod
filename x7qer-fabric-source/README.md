# x7qer Style (Fabric, Minecraft 1.21.11)

Der Mod öffnet mit **O** den Pack Editor. Die Quellpacks werden aus dem normalen Minecraft-Ordner `resourcepacks` gelesen. Wähle eine Kategorie, dann das Quellpack. „Pack erstellen“ schreibt `x7qer-style.zip` in denselben Ordner; aktiviere es anschließend in Minecraft unter **Optionen → Ressourcenpakete**.

## JAR mit einem Doppelklick bauen

1. Entpacke dieses Projekt.
2. Doppelklicke auf `build.bat` und erlaube bei Bedarf die Internetverbindung. Beim ersten Lauf lädt das Skript ein portables JDK 25 und Gradle herunter. Loom braucht JDK 25 zum Ausführen; der Mod-Code wird weiterhin für Java 21 kompiliert.
3. Nach erfolgreichem Build liegt die Mod-JAR unter `build/libs/x7qer-1.0.0.jar`. Kopiere sie in den `mods`-Ordner einer Fabric-Installation für Minecraft 1.21.11.

Alternativ lässt sich das Projekt weiterhin in IntelliJ IDEA als Gradle-Projekt öffnen und dort mit der Gradle-Aufgabe `build` bauen.

## Online bauen (GitHub Actions)

Du kannst das entpackte Projekt in ein eigenes GitHub-Repository hochladen. Der Workflow in `.github/workflows/build.yml` baut die Mod nach jedem Upload und kann im Reiter **Actions** auch manuell gestartet werden. Öffne danach den erfolgreichen Lauf und lade unter **Artifacts** die Datei `x7qer-fabric-1.21.11` herunter. Darin liegt die Mod-JAR.

Eine Webseite, die eine einzelne Java-Datei in eine passende Fabric-JAR verwandelt, ist dafür nicht geeignet: Gradle muss Minecraft, Fabric Loader, Fabric API und die Ressourcen gemeinsam einbinden. Dieses Projekt enthält deshalb die vollständige Gradle-Struktur; Gradle lädt Abhängigkeiten beim ersten Build aus den offiziellen Repositories.

## Grenzen des ersten Builds

Die Kategorien ordnen Dateien anhand ihrer Pfade zu und kombinieren Texturen aus installierten Packs. Manche Packs haben abweichende Ordnerstrukturen oder mehrere Versionen im ZIP; die Auswahl kann diese Sonderfälle nicht automatisch erkennen. Die Schildtexturen liegen in „Gear“ beziehungsweise „PvP“. Eine echte Änderung der im Spiel gerenderten Schildgröße ist keine Resourcepack-Funktion und ist in diesem ersten Quellstand noch nicht eingebaut.
