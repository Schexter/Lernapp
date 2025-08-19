# Claude Code Instructions für Phase 1.2: Domain Model Design

## 🎯 Ziel
Implementierung aller Domain Models, Repositories und Services für die Fachinformatiker Lernapp.

## 📁 Arbeitsverzeichnis
`C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java\lernapp-core`

## ✅ Bereits vorbereitet:
1. **Package-Struktur** komplett angelegt:
   - `domain.model` - für Entities
   - `domain.repository` - für Repositories  
   - `domain.service` - für Services
   - `domain.enums` - für Enumerations

2. **Base Classes** bereits vorhanden:
   - `BaseEntity.java` - Basis für alle Entities
   - `UserRole.java` - Enum für Benutzerrollen
   - `QuestionType.java` - Enum für Fragentypen
   - `DifficultyLevel.java` - Enum für Schwierigkeitsgrade

3. **Templates** als Vorlage:
   - `TEMPLATE_DOMAIN_MODEL.java` - zeigt TODO-Liste für alle Entities
   - `TEMPLATE_REPOSITORY.java` - zeigt TODO-Liste für alle Repositories
   - `TEMPLATE_SERVICE.java` - zeigt TODO-Liste für alle Services

4. **Flyway Migration** vorbereitet:
   - `V1__Initial_Schema.sql` - für Datenbank-Schema

## 📋 TODO-Liste für Claude Code:

### 1. Domain Models implementieren (in domain.model):
- [ ] `User.java` - Benutzer-Entity
- [ ] `Topic.java` - Themen-Entity mit Hierarchie
- [ ] `Question.java` - Fragen-Entity
- [ ] `Answer.java` - Antworten-Entity
- [ ] `LearningPath.java` - Lernpfad-Entity
- [ ] `Progress.java` - Fortschritts-Entity
- [ ] `Examination.java` - Prüfungs-Entity
- [ ] `ExamSession.java` - Prüfungssitzungs-Entity
- [ ] `Tag.java` - Tag-Entity für Kategorisierung

### 2. Repositories implementieren (in domain.repository):
- [ ] `UserRepository.java`
- [ ] `TopicRepository.java`
- [ ] `QuestionRepository.java`
- [ ] `AnswerRepository.java`
- [ ] `ProgressRepository.java`
- [ ] `ExaminationRepository.java`
- [ ] `ExamSessionRepository.java`

### 3. Services implementieren (in domain.service):
- [ ] `UserService.java` - Benutzerverwaltung
- [ ] `QuestionService.java` - Fragenverwaltung
- [ ] `ProgressService.java` - Fortschrittsverfolgung
- [ ] `LearningService.java` - Hauptservice für Lernlogik
- [ ] `ExaminationService.java` - Prüfungsverwaltung

### 4. Flyway Migration vervollständigen:
- [ ] Komplettes Datenbank-Schema in `V1__Initial_Schema.sql`

## 🔧 Technische Anforderungen:

### Für Entities:
```java
@Entity
@Table(name = "table_name")
@Data // Lombok
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EntityName extends BaseEntity {
    // Fields mit JPA Annotations
}
```

### Für Repositories:
```java
@Repository
public interface EntityRepository extends JpaRepository<Entity, Long>, 
                                         JpaSpecificationExecutor<Entity> {
    // Custom query methods
}
```

### Für Services:
```java
@Service
@Transactional
@Slf4j // für Logging
public class EntityService {
    private final EntityRepository repository;
    
    // Constructor Injection
    public EntityService(EntityRepository repository) {
        this.repository = repository;
    }
}
```

## 🏗️ Best Practices:
1. **Lombok** verwenden für weniger Boilerplate
2. **Constructor Injection** statt @Autowired
3. **Validation Annotations** (@NotNull, @Size, @Email)
4. **Bidirektionale Beziehungen** mit mappedBy
5. **Fetch Types** optimieren (meist LAZY)
6. **Cascade Types** sinnvoll wählen
7. **Deutsche Kommentare** für Business Logic
8. **Englische Namen** für Klassen/Methoden

## 📝 Beispiel-Entity:
```java
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_username", columnList = "username"),
    @Index(name = "idx_email", columnList = "email")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    
    @Column(name = "username", unique = true, nullable = false, length = 50)
    @NotNull
    @Size(min = 3, max = 50)
    private String username;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Progress> progressList = new ArrayList<>();
    
    // Weitere Felder...
}
```

## 🚀 Starte mit:
1. User Entity implementieren
2. UserRepository erstellen
3. UserService implementieren
4. Tests schreiben
5. Dann nächste Entity...

## ⚠️ Wichtig:
- Alle Entities erben von `BaseEntity`
- Keine Spaghetti-Code - saubere Struktur!
- Mindestens 70% Test Coverage anstreben
- Fehler in `error.log` dokumentieren
- Fortschritt in `CHANGELOG.md` protokollieren

---
*Erstellt von Hans Hahn - Alle Rechte vorbehalten*
