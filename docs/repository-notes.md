# Atelier 3 : Couche Repository Spring Data JPA

## 1. Rappels et choix des interfaces Spring Data JPA

### 1.1 Hiérarchie des interfaces
Spring Data JPA propose une hiérarchie d'interfaces progressives permettant d'abstraire la couche d'accès aux données (DAO) :
- **`Repository<T, ID>`** : Interface marqueur pure, sans aucune méthode. Elle signale uniquement à Spring qu'il s'agit d'un repository.
- **`CrudRepository<T, ID>`** : Fournit les opérations CRUD fondamentales (`save`, `findById`, `existsById`, `findAll`, `count`, `deleteById`, `delete`, etc.). Les méthodes renvoyant plusieurs entités retournent un `Iterable<T>`.
- **`ListCrudRepository<T, ID>`** : Identique à `CrudRepository`, mais remplace les retours `Iterable<T>` par des `List<T>`, facilitant leur manipulation directe (`size()`, indexation, API `Stream`).
- **`PagingAndSortingRepository<T, ID>`** : Introduit la pagination et le tri (`findAll(Sort)`, `findAll(Pageable)`). *Note : Depuis Spring Data 3, cette interface n'étend plus `CrudRepository`.*
- **`ListPagingAndSortingRepository<T, ID>`** : Variante de pagination et tri dont `findAll(Sort)` renvoie une `List<T>`.
- **`JpaRepository<T, ID>`** : Interface la plus complète pour la persistance JPA. Elle cumule `ListCrudRepository`, `ListPagingAndSortingRepository`, `QueryByExampleExecutor`, ainsi que des opérations propres à JPA (`flush()`, `saveAndFlush()`, `saveAllAndFlush()`, `getReferenceById()`, `deleteAllInBatch()`).

### 1.2 Justification du choix de JpaRepository pour AutoLoc
Pour l'ensemble des 9 entités d'AutoLoc, **`JpaRepository<T, Long>`** a été retenu pour les raisons suivantes :
1. **Confort de manipulation** : Les retours natifs en `List<T>` évitent les conversions manuelles depuis `Iterable`.
2. **Support intégré du tri et de la pagination** : Essentiel pour gérer les volumes de données (flotte de véhicules, historique des réservations, facturation).
3. **Contrôle du contexte de persistance** : La méthode `saveAndFlush()` permet de forcer l'écriture immédiate en base avant des traitements ultérieurs.

---

## 2. Inventaire et justification des Repositories

Conformément à la convention de nommage AutoLoc, chaque interface est préfixée par la lettre `I` et est placée dans le package `tn.esprit.autoloc.repository`. Aucune annotation `@Repository` n'est requise : Spring Data détecte les interfaces à l'initialisation du contexte et génère dynamiquement les proxys d'implémentation.

| Interface | Étend | Justification |
| :--- | :--- | :--- |
| **`IContratRepository`** | `JpaRepository<Contrat, Long>` | CRUD complet, retours en `List`, tri/pagination sur les montants, disponibilité de `saveAndFlush()` pour synchroniser immédiatement le contrat avant l'émission des paiements. |
| **`IPaiementRepository`** | `JpaRepository<Paiement, Long>` | Permet la lecture et la consultation paginée des règlements financiers. Dans la logique métier, la création/mise à jour passe par la cascade du `Contrat` (agrégat parent). |
| **`IVehiculeRepository`** | `JpaRepository<Vehicule, Long>` | Gestion du catalogue de véhicules. Nécessite le tri et la pagination par statut, catégorie ou tarif journalier (`findAll(Pageable)`). |
| **`IAgenceRepository`** | `JpaRepository<Agence, Long>` | CRUD complet sur les agences de location, listing direct sous forme de `List<Agence>` pour alimenter les listes déroulantes de l'application. |
| **`IEmployeRepository`** | `JpaRepository<Employe, Long>` | Gestion des utilisateurs internes (agents et managers). Supporte la recherche et le listing des employés rattachés aux agences. |
| **`IClientRepository`** | `JpaRepository<Client, Long>` | Gestion du portefeuille clients, pagination pour les listes de clients et suivi de leurs réservations associées. |
| **`IReservationRepository`** | `JpaRepository<Reservation, Long>` | Gestion des plannings de réservation, tri chronologique par date de début et filtrage paginé selon le statut de la réservation. |
| **`IEquipementRepository`** | `JpaRepository<Equipement, Long>` | Référentiel des options et équipements (GPS, siège bébé...). Retour direct sous forme de `List` adapté à un catalogue de taille modérée. |
| **`IMaintenanceRepository`** | `JpaRepository<Maintenance, Long>` | Suivi des opérations techniques et d'entretien sur les véhicules, avec pagination et tri par date d'intervention. |

---

## 3. Comportement des opérations CRUD & Analyse SQL

### 3.1 Comportement de `save()` : persist vs merge
Le comportement de `save()` dépend de l'état de l'identifiant de l'entité transmise :
- **Identifiant `null` (nouvelle entité)** :
  - Déclenche un `EntityManager.persist()`.
  - Hibernate génère une requête SQL `INSERT INTO ...`.
  - La clé primaire auto-incrémentée est attribuée par MySQL (stratégie `IDENTITY`) et affectée à l'objet retourné.
- **Identifiant renseigné (entité existante)** :
  - Déclenche un `EntityManager.merge()`.
  - Hibernate exécute un `SELECT` pour charger l'état persistant en base, applique les modifications, puis émet un `UPDATE` si l'état des attributs a changé (mécanisme de *dirty checking*).
  - **Bonne pratique** : Toujours réassigner la variable locale à l'objet retourné par `save()`, car l'instance managée retournée peut être différente de l'instance détachée passée en argument.

### 3.2 Comportement de `findById()`
- Retourne un objet `Optional<T>`.
- Cela force le développeur à traiter explicitement l'éventuelle absence de l'enregistrement (`isPresent()`, `orElseThrow()`, `ifPresent()`), évitant ainsi les erreurs `NullPointerException`.

### 3.3 Comportement de `deleteById()`
- En Spring Data 3, la méthode `deleteById(ID id)` vérifie l'existence avant suppression et ne lève plus d'exception (`EmptyResultDataAccessException`) si l'identifiant n'existe pas en base.

### 3.4 Le fil rouge Contrat ↔ Paiement : Cascade et `orphanRemoval`
Dans le modèle AutoLoc, la relation `Contrat` (1) vers `Paiement` (N) illustre une relation de composition :
- **Cascade (`CascadeType.ALL`)** : La persistance ou la suppression d'un `Contrat` se répercute automatiquement sur ses lignes de `Paiement`.
- **`orphanRemoval = true`** : Si un objet `Paiement` est retiré de la collection `contrat.getPaiements()`, Hibernate détecte qu'il n'est plus rattaché à son parent et émet un `DELETE FROM paiement WHERE id_paiement = ?`.
- **Règle métier** : Le code métier modifie les paiements en passant obligatoirement par le parent `Contrat`.

### 3.5 Limites et dangers des suppressions en lot (`deleteAllInBatch`)
- Les méthodes `deleteAllInBatch()` et `deleteAllByIdInBatch(Iterable<ID>)` exécutent une unique requête SQL directe (`DELETE FROM table WHERE id IN (...)`), sans charger préalablement les entités dans le contexte de persistance d'Hibernate.
- **Conséquence critique** : Les règles de cycle de vie JPA (`cascade = CascadeType.REMOVE`) et l'orphelinat (`orphanRemoval`) sont **ignorés**.
- **Risque** : Tenter de supprimer des contrats via `deleteAllInBatch` sans supprimer manuellement les paiements associés déclenche immédiatement une violation de clé étrangère MySQL (`Cannot delete or update a parent row: a foreign key constraint fails`). Pour préserver l'intégrité relationnelle avec cascade, il faut privilégier `delete()` ou `deleteById()`.

---

## 4. Anomalies SonarQube for IDE (SonarLint) et Clean Code

L'analyse statique du code à l'aide de SonarQube for IDE a permis d'identifier et de corriger plusieurs non-conformités :

| Anomalie SonarQube for IDE | Règle / explication | Correction apportée |
| :--- | :--- | :--- |
| **Nom de package non conforme** | **`java:S120`** : Les noms de packages Java doivent respecter une convention en minuscules (`^[a-z_]+(\.[a-z_][a-z0-9_]*)*$`). Les dossiers s'appelaient initialement `Entities` et `Repositories` avec une majuscule. | Renommage des packages en minuscules : `tn.esprit.autoloc.domain` pour les entités et `tn.esprit.autoloc.repository` pour les interfaces, conformément aux conventions Java et à l'architecture de l'Atelier 1. |
| **Imports avec joker (wildcards)** | **`java:S2208` / `java:S1128`** : L'utilisation d'imports avec astérisque (`import jakarta.persistence.*;`, `import lombok.*;`) masque les dépendances réelles et peut provoquer des conflits de nommage. | Remplacement systématique de tous les imports joker par des imports explicites ciblés dans toutes les entités et interfaces. |
| **Code mort et classe inachevée** | **`java:S1144` / `java:S2094`** : Présence d'un package prématuré `Services` contenant une interface `IClient.java` syntaxiquement invalide (bloquant la compilation Maven) et une classe vide `ClientServiceImpl.java`. | Suppression du dossier `Services` hors périmètre de l'Atelier 3. Le projet compile de manière autonome et propre avant l'Atelier 4 dédié aux services. |
| **Annotation `@Repository` redondante** | **Bonnes pratiques Spring Data / `java:S1186`** : L'annotation `@Repository` avait été ajoutée sur les interfaces repository. Spring Data crée automatiquement les proxys dynamiques à l'exécution pour toute interface étendant `JpaRepository`. | Suppression de l'annotation `@Repository` inutile sur les 9 interfaces de la couche repository. |
| **Évitement de l'annotation `@Data` sur les entités JPA** | **Bonnes pratiques JPA / SonarLint** : L'annotation `@Data` de Lombok génère automatiquement `equals()`, `hashCode()` et `toString()` sur tous les champs, ce qui crée des boucles infinies de récursion sur les relations bidirectionnelles et des anomalies dans les collections avec les IDs auto-générés. | Utilisation d'annotations ciblées (`@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@ToString`) avec exclusion systématique des relations de navigation via `@ToString.Exclude`. |

---

## 5. Validation de l'exécution

Au démarrage de l'application Spring Boot (`mvn test` ou lancement de `AutolocApiApplication`), la détection automatique de Spring Data JPA confirme la configuration correcte des 9 interfaces :

```text
INFO --- [autoloc-api] [main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
INFO --- [autoloc-api] [main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 43 ms. Found 9 JPA repository interfaces.
```

Toutes les tables ont été synchronisées sur MySQL (`autoloc_db`) et les opérations CRUD élémentaires (INSERT, SELECT, UPDATE, DELETE) fonctionnent avec la cascade et la gestion transactionnelle appropriées.
