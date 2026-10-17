# Atelier 5 : Spring MVC et Exposition de l'API REST

---

## Section A. Inventaire des Endpoints de l'API REST AutoLoc

Tous les contrôleurs sont configurés avec le préfixe d'API global `/api` défini via `server.servlet.context-path=/api`.
Chaque contrôleur est annoté avec `@RestController`, `@RequestMapping("/...")` et utilise l'injection par constructeur via Lombok (`@RequiredArgsConstructor`).

| Contrôleur | Verbe & Route | Service appelé | Code Succès | Codes Erreur | Rôle |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **`ContratController`** | `GET /api/contrats` | `contratService.findAll()` | 200 OK | — | Lister tous les contrats |
| | `GET /api/contrats/{id}` | `contratService.findById(id)` | 200 OK | 404 | Consulter un contrat par ID |
| | `POST /api/contrats` | `contratService.create(contrat)` | 201 Created | 400 | Créer un contrat (cascade vers paiements) |
| | `PUT /api/contrats/{id}` | `contratService.update(id, contrat)`| 200 OK | 400, 404 | Mettre à jour un contrat existant |
| | `DELETE /api/contrats/{id}`| `contratService.deleteById(id)` | 204 No Content | 404 | Supprimer un contrat et ses paiements |
| **`PaiementController`** | `GET /api/paiements` | `paiementService.findAll()` | 200 OK | — | Lister tous les paiements |
| | `GET /api/paiements/{id}`| `paiementService.findById(id)` | 200 OK | 404 | Consulter un paiement par ID |
| *(Paiement POST/PUT/DELETE)* | `POST /api/paiements` | *(non exposé)* | — | 405 | Refusé : lecture seule côté contrôleur |
| **`AgenceController`** | `GET /api/agences` | `agenceService.findAll()` | 200 OK | — | Lister les agences |
| | `GET /api/agences/{id}` | `agenceService.findById(id)` | 200 OK | 404 | Consulter une agence |
| | `POST /api/agences` | `agenceService.create(agence)` | 201 Created | 400 | Créer une agence (nom obligatoire) |
| | `PUT /api/agences/{id}` | `agenceService.update(id, agence)` | 200 OK | 400, 404 | Modifier une agence |
| | `DELETE /api/agences/{id}`| `agenceService.deleteById(id)` | 204 No Content | 404 | Supprimer une agence |
| **`EmployeController`** | `GET /api/employes` | `employeService.findAll()` | 200 OK | — | Lister les employés |
| | `GET /api/employes/{id}` | `employeService.findById(id)` | 200 OK | 404 | Consulter un employé |
| | `POST /api/employes` | `employeService.create(employe)` | 201 Created | 400 | Créer un employé (nom/rôle requis) |
| | `PUT /api/employes/{id}` | `employeService.update(id, employe)`| 200 OK | 400, 404 | Modifier un employé |
| | `DELETE /api/employes/{id}`| `employeService.deleteById(id)` | 204 No Content | 404 | Supprimer un employé |
| **`VehiculeController`** | `GET /api/vehicules` | `vehiculeService.findAll()` | 200 OK | — | Lister les véhicules |
| | `GET /api/vehicules/{id}`| `vehiculeService.findById(id)` | 200 OK | 404 | Consulter un véhicule |
| | `POST /api/vehicules` | `vehiculeService.create(vehicule)` | 201 Created | 400 | Créer un véhicule (tarif non négatif)|
| | `PUT /api/vehicules/{id}`| `vehiculeService.update(id, vehicule)`| 200 OK | 400, 404 | Modifier un véhicule |
| | `DELETE /api/vehicules/{id}`| `vehiculeService.deleteById(id)` | 204 No Content | 404 | Supprimer un véhicule |
| **`EquipementController`**| `GET /api/equipements` | `equipementService.findAll()` | 200 OK | — | Lister les équipements |
| | `GET /api/equipements/{id}`| `equipementService.findById(id)` | 200 OK | 404 | Consulter un équipement |
| | `POST /api/equipements` | `equipementService.create(equipement)` | 201 Created | 400 | Créer un équipement (libellé requis) |
| | `PUT /api/equipements/{id}`| `equipementService.update(id, equipement)` | 200 OK | 400, 404 | Modifier un équipement |
| | `DELETE /api/equipements/{id}` | `equipementService.deleteById(id)` | 204 No Content | 404 | Supprimer un équipement |
| **`ClientController`** | `GET /api/clients` | `clientService.findAll()` | 200 OK | — | Lister les clients |
| | `GET /api/clients/{id}` | `clientService.findById(id)` | 200 OK | 404 | Consulter un client |
| | `POST /api/clients` | `clientService.create(client)` | 201 Created | 400 | Créer un client (nom et email requis)|
| | `PUT /api/clients/{id}` | `clientService.update(id, client)` | 200 OK | 400, 404 | Modifier un client |
| | `DELETE /api/clients/{id}`| `clientService.deleteById(id)` | 204 No Content | 404 | Supprimer un client |
| **`ReservationController`**| `GET /api/reservations` | `reservationService.findAll()` | 200 OK | — | Lister les réservations |
| | `GET /api/reservations/{id}` | `reservationService.findById(id)` | 200 OK | 404 | Consulter une réservation |
| | `POST /api/reservations` | `reservationService.create(reservation)` | 201 Created | 400 | Créer une réservation (date fin >= début) |
| | `PUT /api/reservations/{id}` | `reservationService.update(id, res)` | 200 OK | 400, 404 | Modifier une réservation |
| | `DELETE /api/reservations/{id}` | `reservationService.deleteById(id)` | 204 No Content | 404 | Supprimer une réservation |
| **`MaintenanceController`**| `GET /api/maintenances` | `maintenanceService.findAll()` | 200 OK | — | Lister les maintenances |
| | `GET /api/maintenances/{id}` | `maintenanceService.findById(id)` | 200 OK | 404 | Consulter une maintenance |
| | `POST /api/maintenances` | `maintenanceService.create(maint)` | 201 Created | 400 | Créer une maintenance |
| | `PUT /api/maintenances/{id}` | `maintenanceService.update(id, maint)` | 200 OK | 400, 404 | Modifier une maintenance |
| | `DELETE /api/maintenances/{id}` | `maintenanceService.deleteById(id)` | 204 No Content | 404 | Supprimer une maintenance |

---

## Section B. Annotations Jackson et sérialisation JSON

### 1. Tableau des associations et annotations Jackson retenues

| Association | Entité source | Attribut annoté | Annotation retenue | Justification |
| :--- | :--- | :--- | :--- | :--- |
| **Contrat ↔ Paiement** | `Contrat` | `paiements` | `@JsonManagedReference("contrat-paiements")` | Composition forte : les paiements font partie intégrante du JSON du contrat parent. |
| | `Paiement` | `contrat` | `@JsonBackReference("contrat-paiements")` | Évite la boucle infinie de sérialisation (`StackOverflowError`) en masquant le parent lors de la sérialisation de l'enfant. |
| **Contrat ↔ Reservation**| `Contrat` | `reservation` | `@JsonIgnore` | Évite le cycle bidirectionnel avec la réservation sans masquer le contrat dans la réservation. |
| **Agence ↔ Vehicule** | `Agence` | `vehicules` | `@JsonIgnore` | Évite de charger et sérialiser l'intégralité du parc automobile lors de la consultation d'une agence. |
| **Agence ↔ Employe** | `Agence` | `employes` | `@JsonIgnore` | Évite de sérialiser la liste des employés lors de l'accès aux données de l'agence. |
| **Client ↔ Reservation** | `Client` | `reservations` | `@JsonIgnore` | Évite de propager l'historique complet des réservations lors du listing des clients. |
| **Vehicule ↔ Maintenance**| `Vehicule` | `maintenances` | `@JsonIgnore` | Évite d'alourdir le JSON d'un véhicule avec son carnet d'entretien. |
| **Vehicule ↔ Reservation**| `Vehicule` | `reservations` | `@JsonIgnore` | Évite d'inclure toutes les réservations passées et futures dans le JSON du véhicule. |
| **Equipement ↔ Vehicule** | `Equipement` | `vehicules` | `@JsonIgnore` | Évite la boucle ManyToMany : un équipement ne sérialise pas la liste de tous les véhicules équipés. |

### 2. Différence entre `@JsonBackReference` et `@JsonIgnore`
- **`@JsonBackReference`** : Fonctionne en binôme indissociable avec `@JsonManagedReference`. Elle indique que le champ fait partie d'une relation bidirectionnelle parent-enfant. Le champ annoté est ignoré lors de la sérialisation (évite la boucle), mais la relation est **automatiquement rétablie à la désérialisation** : lorsque le parent est désérialisé avec ses enfants, chaque enfant reçoit automatiquement la référence vers son parent.
- **`@JsonIgnore`** : Ignore purement et simplement le champ annoté lors de la sérialisation ET de la désérialisation JSON. C'est l'annotation adaptée pour les collections côté "inverse" (`mappedBy`) lorsque l'on ne souhaite pas exposer la liste d'enfants ou lorsqu'on instancie l'entité seule.

---

## Section C. Utilisation de Postman et Bonnes Pratiques de Test d'API

### 1. Apport de Postman dans le cycle de développement
Postman permet d'exercer directement les endpoints REST de manière reproductible et automatisée :
- **Variables d'environnement (`{{baseUrl}}`, `{{contratId}}`)** : Permettent de faire abstraction de l'URL absolue et d'enchaîner dynamiquement les requêtes (l'identifiant généré par un `POST` est automatiquement mémorisé pour les requêtes `GET`, `PUT` et `DELETE` suivantes).
- **Scripts de test automatisés (*After response*)** : Permettent de vérifier instantanément le statut HTTP (`pm.response.to.have.status(200)`), le format du JSON et la validité des champs retournés.
- **Validation du comportement CRUD et de la cascade** : Permet de constater visuellement qu'un `POST` de contrat avec sa liste de paiements imbriquée persiste les deux entités en une seule requête, et qu'un `DELETE` de ce même contrat supprime également ses paiements par cascade.

---

## Section D. Analyse de la qualité avec SonarQube for IDE (SonarLint)

L'analyse de la couche `web.controller` a permis de s'assurer du respect des règles Clean Code :

| Règle Sonar / Problème | Explication | Correction apportée |
| :--- | :--- | :--- |
| **Contrôleurs minces (*Thin Controllers*)** | Un contrôleur REST ne doit pas contenir de logique métier ni manipuler directement les repositories (`java:S1200`). | Les 9 contrôleurs se contentent de valider la forme de la requête HTTP et délèguent immédiatement le traitement à l'interface de service correspondante (`I...Service`). |
| **Injection par constructeur** | L'injection par champ via `@Autowired` sur les attributs de contrôleur est déconseillée (`java:S6813`). | Utilisation de champs `private final` et de l'annotation Lombok `@RequiredArgsConstructor` sur tous les contrôleurs. |
| **Codes de retour HTTP explicites** | L'absence d'indication de statut par défaut force Spring à renvoyer 200 sur les créations et suppressions. | Utilisation des annotations `@ResponseStatus(HttpStatus.CREATED)` (201) sur les méthodes `create` et `@ResponseStatus(HttpStatus.NO_CONTENT)` (204) sur les méthodes `deleteById`. |
| **Gestion centralisée des exceptions** | Éviter les blocs `try-catch` dispersés dans les méthodes de contrôleur. | Centralisation du traitement des erreurs dans `GlobalExceptionHandler` avec `@RestControllerAdvice`. |

---

## Section E. Questions de compréhension

### 1. Quel composant reçoit toutes les requêtes, et comment retrouve-t-il la méthode du contrôleur à appeler ?
Toutes les requêtes HTTP adressées à l'application sont d'abord interceptées par un point d'entrée unique : la servlet frontale **`DispatcherServlet`** de Spring MVC.
La `DispatcherServlet` interroge le composant **`HandlerMapping`** (notamment `RequestMappingHandlerMapping`), qui maintient une table de correspondance en mémoire entre les URL/verbes HTTP et les méthodes de contrôleurs annotées avec `@RequestMapping`, `@GetMapping`, `@PostMapping`, etc. Une fois la méthode trouvée, elle l'invoque via un `HandlerAdapter`.

### 2. Pourquoi `ContratController` dépend-il de `IContratService` et non de `ContratRepository` ni de `ContratServiceImpl` ?
- **Non de `ContratRepository`** : Respect du principe de séparation des responsabilités et de l'architecture multicouche. Le contrôleur s'occupe de la couche web (HTTP, sérialisation JSON), tandis que le service centralise la logique métier (vérifications d'intégrité, règles de gestion). Le contrôleur ne doit jamais court-circuiter le métier pour accéder directement aux données.
- **Non de `ContratServiceImpl`** : Respect du principe d'inversion des dépendances (le D de SOLID). Dépendre d'une interface abstraite assure un couplage faible, permet de substituer facilement l'implémentation (ex: pour des tests unitaires avec des mocks) et permet à Spring d'insérer ses proxys d'infrastructure (transactions, sécurité).

### 3. Pourquoi un POST renvoie-t-il 201 et un DELETE 204 ? Pourquoi une ressource absente donne-t-elle 404 et non 500 ?
- **POST → 201 Created** : La sémantique HTTP stipule qu'une requête ayant pour résultat la création d'une nouvelle ressource sur le serveur doit renvoyer le code `201 Created`, souvent accompagné de l'entité créée ou d'un en-tête `Location`.
- **DELETE → 204 No Content** : Lorsque la suppression est effectuée avec succès et qu'il n'y a plus aucun contenu à renvoyer dans le corps de la réponse, le standard HTTP préconise `204 No Content`.
- **Ressource absente → 404 et non 500** : Le code `500 Internal Server Error` indique un dysfonctionnement inattendu ou un bug interne du serveur. Une ressource non trouvée (`id` inexistant) est une situation normale de l'utilisation de l'API : elle relève d'une erreur côté client qui a demandé un identifiant introuvable, ce qui correspond exactement au code `404 Not Found`.

### 4. Pourquoi `PaiementController` n'expose-t-il que des GET ? Quelle réponse reçoit un POST `/api/paiements` ?
- **Pourquoi uniquement des GET** : `Paiement` est dans une relation de composition stricte avec `Contrat`. La création, modification et suppression des paiements doivent obligatoirement être effectuées à travers le `Contrat` parent (avec cascade et gestion des orphelins).
- **Réponse à un POST `/api/paiements`** : Comme aucune méthode du contrôleur n'est associée au verbe `POST` sur la route `/paiements`, Spring MVC rejette la requête avec le code HTTP **`405 Method Not Allowed`**.

### 5. Quels risques présente l'exposition directe des entités JPA ? Pourquoi `Contrat.paiements` utilise-t-il `@JsonManagedReference` alors que `Agence.vehicules` utilise `@JsonIgnore` ?
- **Risques de l'exposition directe des entités JPA** :
  1. **Couplage fort** entre la structure de la base de données et l'API publique (toute modification de colonne modifie le contrat d'API des clients externes).
  2. **Fuite de données sensibles ou internes** (mots de passe, identifiants techniques, drapeaux internes exposés dans le JSON).
  3. **Boucles infinies de sérialisation** (`StackOverflowError`) dues aux associations bidirectionnelles JPA.
  4. **Problèmes de chargement paresseux (*Lazy Loading*)** générant des requêtes N+1 imprévues ou des erreurs `LazyInitializationException` / `ByteBuddyInterceptor`.
- **Pourquoi `@JsonManagedReference` sur `Contrat.paiements` et `@JsonIgnore` sur `Agence.vehicules` ?** :
  - `Contrat` et `Paiement` forment une **composition** : créer ou afficher un contrat implique naturellement de voir et manipuler ses lignes de paiement dans le même document JSON. `@JsonManagedReference` permet d'inclure les paiements dans le JSON du contrat tout en évitant la boucle de retour vers le contrat.
  - `Agence` et `Vehicule` représentent une relation simple de rattachement : sérialiser une agence ne doit pas embarquer l'intégralité du catalogue de véhicules (ce qui produirait un JSON volumineux et inefficace). `@JsonIgnore` permet de couper la sérialisation descendante.
