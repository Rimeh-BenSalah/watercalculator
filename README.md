# 💧 Water Calculator — ThinkGreen Project

Application JavaFX éco-responsable pour calculer, analyser et réduire la consommation d'eau.
**Contexte** : Stress hydrique tunisien · Normes OMS · Java 17 + JavaFX 21 + JDBC MySQL

---

## 📁 Structure du projet

```
WaterCalculator/
├── pom.xml                          ← Build Maven
├── sql/
│   └── water_calculator.sql         ← Schéma + données d'exemple
└── src/main/
    ├── java/com/watercalculator/
    │   ├── MainApp.java              ← Point d'entrée JavaFX
    │   ├── model/
    │   │   ├── User.java
    │   │   ├── Consumption.java
    │   │   └── Recommendation.java
    │   ├── dao/
    │   │   ├── UserDAO.java
    │   │   ├── ConsumptionDAO.java
    │   │   └── RecommendationDAO.java
    │   ├── service/
    │   │   └── WaterService.java
    │   ├── controller/
    │   │   ├── LoginController.java
    │   │   ├── RegisterController.java
    │   │   ├── DashboardController.java
    │   │   ├── CalculatorController.java
    │   │   └── HistoryController.java
    │   └── util/
    │       ├── DatabaseConnection.java
    │       ├── SessionManager.java
    │       └── SceneManager.java
    └── resources/
        ├── fxml/
        │   ├── login.fxml
        │   ├── register.fxml
        │   ├── dashboard.fxml
        │   ├── calculator.fxml
        │   └── history.fxml
        └── css/
            └── style.css
```

---

## ⚙️ Installation

### Prérequis
- Java 17+
- Maven 3.8+
- MySQL 8.0+
- (Optionnel) IntelliJ IDEA ou VS Code

### 1. Base de données

```sql
-- Dans MySQL Workbench ou terminal :
mysql -u root -p < sql/water_calculator.sql
```

### 2. Configuration JDBC

Modifier `src/main/java/com/watercalculator/util/DatabaseConnection.java` :

```java
private static final String URL      = "jdbc:mysql://localhost:3306/water_calculator_db?useSSL=false&serverTimezone=UTC";
private static final String USER     = "root";       // ← votre utilisateur
private static final String PASSWORD = "";           // ← votre mot de passe
```

### 3. Compiler et lancer

```bash
# Compiler
mvn clean package

# Lancer (avec JavaFX Maven Plugin)
mvn javafx:run

# Ou lancer le JAR
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     -jar target/water-calculator-1.0.0.jar
```

---

## 🔐 Comptes de test

| Identifiant | Mot de passe | Ville   |
|-------------|-------------|---------|
| ahmed       | password    | Tunis   |
| sonia       | password    | Sfax    |
| mohamed     | password    | Sousse  |

> **Note** : les mots de passe dans la BD sont hachés SHA-256. Pour tester, recréez un compte via l'interface ou réinitialisez le hash dans la table `users`.

---

## 🌍 Normes OMS utilisées

| Usage          | Norme         | Détail                        |
|----------------|--------------|-------------------------------|
| Usage domestique| 50 L/j/pers  | Référence OMS eau potable     |
| Douche         | 40 L          | ~5 min à 8 L/min              |
| Vaisselle      | 15 L          | Bac fermé                     |
| Arrosage       | 5 L/m²        | Estimation standard           |
| Agriculture    | 50 000 L/ha   | Irrigation gravitaire         |

---

## 🏛️ Architecture POO

| Concept       | Implémentation                                  |
|---------------|-------------------------------------------------|
| Encapsulation | Tous les modèles avec getters/setters privés    |
| Singleton     | `DatabaseConnection`, `SessionManager`          |
| DAO Pattern   | `UserDAO`, `ConsumptionDAO`, `RecommendationDAO`|
| Service Layer | `WaterService` — logique métier centralisée     |
| MVC           | Contrôleurs séparés par écran FXML              |

---

## 📊 Critères d'évaluation couverts

- ✅ **Fonctionnalités (40%)** : Login, Register, Calcul, OMS, Recommandations, Historique
- ✅ **Architecture POO (30%)** : Héritage, encapsulation, DAO, Singleton, Service
- ✅ **UI/UX JavaFX (20%)** : FXML + CSS responsive, graphiques BarChart + PieChart
- ✅ **Documentation (10%)** : README, SQL scripté, javadoc inline

---

## 📄 Livrables

- [x] Code source GitHub avec README
- [x] Base de données SQL scriptée (12+ enregistrements)
- [ ] Rapport PDF 8-12 pages (UML + analyse)
- [ ] Démo 10 min

---

*ThinkGreen Project — ISET Tunisie 2024*
"# watercalculator" 
"# watercalculator" 
