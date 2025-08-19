# Database Compatibility Guide

## Problem gelöst: JSONB-Kompatibilität zwischen H2 und PostgreSQL

### Das Problem
Die ursprüngliche Flyway-Migration verwendete PostgreSQL-spezifische Datentypen (`JSONB`, `BIGSERIAL`), die von H2 (Entwicklungsdatenbank) nicht unterstützt werden.

### Die Lösung
Zwei separate Schema-Definitionen wurden erstellt:

1. **H2-kompatible Version**: `V1__Initial_Schema.sql` (für Development)
   - `JSONB` → `CLOB` (für JSON-Strings)
   - `BIGSERIAL` → `BIGINT AUTO_INCREMENT`
   - Keine PostgreSQL Extensions

2. **PostgreSQL-optimierte Version**: `docs/database/postgresql_schema.sql` (für Production)
   - Native `JSONB` mit GIN-Indizes
   - `BIGSERIAL` Auto-Increment
   - UUID Extension aktiviert

### Verwendung in der Anwendung

#### JSON-Handling
In der Anwendungslogik müssen JSON-Felder je nach Datenbank unterschiedlich behandelt werden:

```java
// Entity-Mapping Beispiel
@Entity
public class User {
    @Column(columnDefinition = "CLOB") // H2
    // @Column(columnDefinition = "JSONB") // PostgreSQL
    private String learningProgress; // JSON als String
    
    // Getter/Setter mit JSON-Konvertierung
    public Map<String, Object> getLearningProgressAsMap() {
        return JsonUtils.fromJson(learningProgress, Map.class);
    }
    
    public void setLearningProgressFromMap(Map<String, Object> progress) {
        this.learningProgress = JsonUtils.toJson(progress);
    }
}
```

### Deployment-Strategie

#### Development (H2)
- Verwendet automatisch `V1__Initial_Schema.sql`
- JSON-Daten werden als CLOB gespeichert
- Flyway-Migration läuft automatisch

#### Production (PostgreSQL)
- Schema wird manuell mit `postgresql_schema.sql` erstellt
- Alternativ: Profil-spezifische Flyway-Skripte
- Nutzt native JSONB-Features für bessere Performance

### Empfehlungen

1. **Für zukünftige Migrationen**: 
   - Immer beide Versionen (H2 und PostgreSQL) erstellen
   - Profil-spezifische Migration-Ordner verwenden

2. **JSON-Handling**:
   - Jackson für JSON-Serialisierung verwenden
   - Repository-Layer sollte Datenbankspezifika abstrahieren

3. **Testing**:
   - Unit Tests mit H2
   - Integration Tests auch mit PostgreSQL (Testcontainers)

### Migration nach PostgreSQL

Wenn Sie von H2 zu PostgreSQL wechseln möchten:

```sql
-- 1. PostgreSQL Schema erstellen
\i docs/database/postgresql_schema.sql

-- 2. Daten migrieren (JSON-Konvertierung berücksichtigen)
-- CLOB → JSONB Konvertierung erfolgt automatisch bei gültigen JSON-Strings
```

### Error Logs
Das ursprüngliche Problem wurde in `error.log` dokumentiert:
- Error Code: 50004
- Ursache: "Unknown data type: JSONB" in H2
- Gelöst durch: Kompatible Datentypen in V1__Initial_Schema.sql
