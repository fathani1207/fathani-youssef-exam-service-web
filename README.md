# 🍽️ API de Gestion des Restaurants – Projet Backend Spring Boot

## Réalisé par Youssef Fathani

## 📘 Description du projet

Ce projet a été réalisé dans le cadre de l’examen **Backend de la formation Web Services**.  
L’objectif est de concevoir et développer une **API REST** permettant de gérer les **restaurants visités** et leurs **évaluations**, pour le compte du **Guide Michelin**.

L’application a été développée avec **Spring Boot (Java 21)** et repose sur une architecture moderne intégrant la sécurité, le stockage d’images et la recherche textuelle.

---

## ⚙️ Stack technique

| Composant | Description |
|------------|--------------|
| **Langage** | Java 21 |
| **Framework principal** | Spring Boot |
| **Sécurité / Authentification** | Spring Security + JWT (OpenID Connect) via **Keycloak** |
| **Base de données** | PostgreSQL *(ou autre selon configuration)* |
| **Stockage d’images** | **MinIO** (compatible Amazon S3) |
| **Indexation et recherche** | **Apache Lucene** |
| **Documentation API** | Swagger / OpenAPI |
| **Tests unitaires** | JUnit 5 |
| **Gestion du projet** | Git & GitHub |

---

## 🧩 Fonctionnalités implémentées

#### 🚨🚨🚨 Pour utiliser l'ensemble des fonctionnalités, la creation d'un fichier `secret.properties` dans le dossier `src/main/resources` est nécessaire pour la configuration d Keycloak et Minio S3 Bucket
#### si vous avez besoin de contenu de ce fichier, n'hésitez pas à me le demander en m'envoyant un mail sur mon adresse email : **youssef.fathani1207@gmail.com**.
#### pour des raisons d'evaluation de travail, j'ai mis des valeurs par défaut dans le code source.

### Restaurants
- ➕ Création d’un restaurant (réservé aux administrateurs)  
- 🔍 Récupération de tous les restaurants  
- 🔍 Récupération d’un restaurant spécifique  
- ✏️ Mise à jour du nom et de l’adresse d’un restaurant  
- 📊 Calcul automatique de la **moyenne des notes** pour chaque restaurant  
  (valeur par défaut : `-1` si aucune évaluation)

### Évaluations
- ➕ Ajout d’une évaluation sur un restaurant  
- ❌ Suppression d’une évaluation  
- 🔎 Recherche d’évaluations par mot-clé (grâce à **Lucene**)  
- 👤 Récupération de toutes les évaluations créées par un utilisateur  
- 📸 Téléversement et gestion d’images des plats via **MinIO**

### Sécurité
- Authentification basée sur **Keycloak** via **JWT / OpenID Connect**  
- Gestion des rôles :
  - `USER` → peut créer et modifier ses propres évaluations  
  - `ADMIN` → peut créer des restaurants et modifier n’importe quelle évaluation  
- Contrôle d’accès géré au niveau des routes via les annotations Spring Security  

### Erreurs et validation
- Gestion centralisée des exceptions avec des réponses structurées :  
  ```json
  {
    "code": 404,
    "message": "Restaurant non trouvé"
  }
  ```

---

## 🧱 Architecture du projet

```
src/
 ├── main/
 │   ├── java/com/michelin/restaurantapi/
 │   │    ├── controllers/        # Contrôleurs REST
 │   │    ├── services/           # Logique métier
 │   │    ├── repositories/        # Requêtes JPA
 │   │    ├── entities/             # Entités JPA
 │   │    ├── dto/               # Objets de transfert
 │   │    ├── configuration/            # Configuration Keycloak, MinIO, Lucene, Swagger
 │   │    └── exceptions/         # Gestion des erreurs
 │   └── resources/
 │        ├── application.yml    # Configuration (DB, MinIO, Keycloak, etc.)
 │        └── static/uploads/    # Dossier de stockage local (optionnel)
 └── test/                       # Tests unitaires
```

---

## 🚀 Démarrage du projet

### 1️⃣ Prérequis
- Java 21  
- Maven 3.9+  
- Docker *(pour exécuter MinIO et Keycloak facilement)*

### 2️⃣ Lancer les services externes
#### pour un environnement de développement local, sinon , configurer vos propres instances de base des données, **MinIO** et **Keycloak**.

```bash
# Lancer MinIO
docker run -p 9000:9000 -p 9001:9001   -e "MINIO_ROOT_USER=minioadmin"   -e "MINIO_ROOT_PASSWORD=minioadmin"   quay.io/minio/minio server /data --console-address ":9001"

# Lancer Keycloak
docker run -p 8080:8080   -e KEYCLOAK_ADMIN=admin   -e KEYCLOAK_ADMIN_PASSWORD=admin   quay.io/keycloak/keycloak start-dev
```

Configurer ensuite :
- un **Realm** (par ex. `michelin`)
- deux utilisateurs prédéfinis :
  - `lucien.bramard@michelin.fr` (USER)
  - `noel.flantier@michelin.fr` (ADMIN)
- un **Client** configuré comme *confidential resource server* avec support JWT

### 3️⃣ Lancer l’application

```bash
mvn spring-boot:run
```

L’API sera disponible sur :  
👉 [http://localhost:8080](http://localhost:8080)

### 4️⃣ Documentation Swagger

Accessible sur :  
👉 [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## 🧪 Tests unitaires

Les tests ont été implémentés avec **JUnit 5** et couvrent notamment :
- la création de restaurant et d’évaluation  
- le calcul de moyenne des notes  
- la recherche via Lucene  
- la gestion des erreurs (404, 500, etc.)

Pour les exécuter :
```bash
mvn test
```

---

## 🧭 Historique de développement

D’après le journal Git :

| Étape | Description |
|-------|--------------|
| 🟢 Initialisation | Création du projet Spring Boot, configuration `pom.xml` et variables d’environnement |
| 🧱 Modélisation | Création des entités, DTOs et repositories |
| ⚙️ Services | Implémentation des services de base (restaurant, évaluation, indexation) |
| 🔍 Indexation | Intégration du service Lucene pour la recherche |
| 🖼️ Stockage | Ajout du service de téléversement d’images avec MinIO |
| 🔐 Sécurité | Mise en place de la configuration Keycloak et du Resource Server |
| 🚦 Authentification | Implémentation du contrôleur et service d’authentification |
| 🧹 Refactor | Restructuration du code et nettoyage final |
| ✅ Finalisation | Ajout de la documentation Swagger et des tests unitaires |

---

## 👨‍💻 Auteur

**Youssef Fathani**  
📧 [youssef.fathani1207@gmail.com](mailto:youssef.fathani1207@gmail.com)  
🔗 [GitHub](https://github.com/fathani1207)
