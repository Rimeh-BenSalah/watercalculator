-- ============================================
-- Water Calculator - Base de données MySQL
-- ThinkGreen Project - 2024
-- ============================================

CREATE DATABASE IF NOT EXISTS water_calculator_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE water_calculator_db;

-- Table Utilisateurs
CREATE TABLE IF NOT EXISTS users (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    full_name   VARCHAR(100),
    city        VARCHAR(100) DEFAULT 'Tunis',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Table Consommations
CREATE TABLE IF NOT EXISTS consumptions (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT NOT NULL,
    consumption_date DATE NOT NULL DEFAULT (CURRENT_DATE),
    -- Usages (litres)
    shower_liters   DOUBLE DEFAULT 0,
    dishwashing_liters DOUBLE DEFAULT 0,
    watering_liters DOUBLE DEFAULT 0,
    agriculture_liters DOUBLE DEFAULT 0,
    other_liters    DOUBLE DEFAULT 0,
    -- Calculé
    total_liters    DOUBLE GENERATED ALWAYS AS
                    (shower_liters + dishwashing_liters + watering_liters + agriculture_liters + other_liters) STORED,
    -- OMS : norme 50L/personne/jour domestic
    who_norm        DOUBLE DEFAULT 50.0,
    notes           TEXT,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Table Recommandations
CREATE TABLE IF NOT EXISTS recommendations (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    category    VARCHAR(50) NOT NULL,  -- shower, dishwashing, watering, agriculture, general
    threshold   DOUBLE NOT NULL,        -- seuil déclencheur (litres)
    message     TEXT NOT NULL,
    tip         TEXT,
    severity    ENUM('info','warning','danger') DEFAULT 'info'
);

-- =====================
-- Données d'exemple
-- =====================

-- Utilisateurs exemple
INSERT INTO users (username, email, password, full_name, city) VALUES
('ahmed',   'ahmed@example.com',   'hashed_pw_1', 'Ahmed Ben Ali',    'Tunis'),
('sonia',   'sonia@example.com',   'hashed_pw_2', 'Sonia Trabelsi',   'Sfax'),
('mohamed', 'mohamed@example.com', 'hashed_pw_3', 'Mohamed Karray',   'Sousse'),
('leila',   'leila@example.com',   'hashed_pw_4', 'Leila Mansour',    'Bizerte'),
('karim',   'karim@example.com',   'hashed_pw_5', 'Karim Jebali',     'Nabeul');

-- Consommations exemple (12 enregistrements)
INSERT INTO consumptions (user_id, consumption_date, shower_liters, dishwashing_liters, watering_liters, agriculture_liters, other_liters) VALUES
(1, CURDATE(),            60, 20, 30, 0,   5),
(1, DATE_SUB(CURDATE(),INTERVAL 1 DAY), 55, 18, 25, 0,   4),
(1, DATE_SUB(CURDATE(),INTERVAL 2 DAY), 70, 22, 40, 0,   6),
(2, CURDATE(),            45, 15, 10, 0,   3),
(2, DATE_SUB(CURDATE(),INTERVAL 1 DAY), 50, 20, 15, 0,   5),
(3, CURDATE(),            80, 25, 50, 200, 10),
(3, DATE_SUB(CURDATE(),INTERVAL 1 DAY), 75, 22, 45, 180, 8),
(4, CURDATE(),            40, 12,  5, 0,   2),
(4, DATE_SUB(CURDATE(),INTERVAL 1 DAY), 42, 14,  8, 0,   3),
(5, CURDATE(),            65, 20, 20, 100, 5),
(5, DATE_SUB(CURDATE(),INTERVAL 1 DAY), 60, 18, 18, 90,  4),
(1, DATE_SUB(CURDATE(),INTERVAL 3 DAY), 58, 19, 28, 0,   5);

-- Recommandations
INSERT INTO recommendations (category, threshold, message, tip, severity) VALUES
('shower',      40, 'Votre consommation douche est élevée.',          'Limitez à 5 min (30L). Installez une douchette économique.', 'warning'),
('shower',      70, 'Consommation douche très excessive !',            'Prenez des douches courtes. Chaque minute économise 10L.',    'danger'),
('dishwashing', 20, 'Vaisselle : consommation modérée.',              'Utilisez un bac d'eau plutôt que le robinet ouvert.',         'info'),
('dishwashing', 30, 'Vaisselle : consommation élevée.',               'Un lave-vaisselle moderne consomme 10L par cycle.',           'warning'),
('watering',    30, 'Arrosage : consommation notable.',               'Arrosez tôt le matin ou le soir pour éviter l'évaporation.', 'info'),
('watering',    60, 'Arrosage excessif détecté.',                     'Récupérez l'eau de pluie. Utilisez le goutte-à-goutte.',      'warning'),
('agriculture', 150,'Consommation agricole significative.',           'Adoptez l'irrigation au goutte-à-goutte : -50% d'eau.',      'warning'),
('agriculture', 300,'Consommation agricole très élevée.',             'Planifiez les cultures selon les saisons sèches.',            'danger'),
('general',     50, 'Consommation dans les normes OMS.',              'Continuez vos bonnes habitudes !',                           'info'),
('general',     100,'Consommation au-dessus des normes OMS.',         'Objectif : réduire de 10% chaque semaine.',                  'warning'),
('general',     200,'Consommation très excessive par rapport à l'OMS.','Fixez-vous un budget eau quotidien de 50L.',                'danger');
