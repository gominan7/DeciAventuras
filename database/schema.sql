-- ============================================================
-- DeciAventuras — Esquema de base de datos (Room / SQLite)
-- ============================================================
-- Documentación humana del esquema real generado por Room a partir de
-- app/src/main/java/com/deciaventuras/app/data/local/entity/*.kt
-- (Sección 33 del spec maestro). Este archivo es descriptivo: Room genera
-- y versiona su propio esquema internamente (ver app/schemas/ una vez que
-- el proyecto compile con exportSchema = true).

PRAGMA foreign_keys = ON;

-- 1) Dilemas cotidianos (nodos del Mapa de Aventuras)
CREATE TABLE IF NOT EXISTS `dilemmas` (
    `id`          INTEGER NOT NULL,
    `orderIndex`  INTEGER NOT NULL,
    `title`       TEXT    NOT NULL,
    `description` TEXT    NOT NULL,
    `isUnlocked`  INTEGER NOT NULL, -- booleano (0/1)
    `isCompleted` INTEGER NOT NULL, -- booleano (0/1)
    PRIMARY KEY(`id`)
);

-- 2) Tarjetas de decisión de cada dilema (2-3 por dilema)
CREATE TABLE IF NOT EXISTS `choices` (
    `id`               INTEGER NOT NULL,
    `dilemmaId`        INTEGER NOT NULL,
    `orderIndex`       INTEGER NOT NULL,
    `choiceText`       TEXT    NOT NULL,
    `shortTermEffect`  TEXT    NOT NULL, -- Impacto Inmediato
    `longTermEffect`   TEXT    NOT NULL, -- Destino Final
    `personalityTrait` TEXT    NOT NULL, -- insignia que otorga (ej. "Responsabilidad")
    PRIMARY KEY(`id`),
    FOREIGN KEY(`dilemmaId`) REFERENCES `dilemmas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS `index_choices_dilemmaId` ON `choices` (`dilemmaId`);

-- 3) Historial de decisiones tomadas por el niño (append-only; alimenta el Diario)
CREATE TABLE IF NOT EXISTS `user_progress` (
    `id`              INTEGER NOT NULL,
    `dilemmaId`       INTEGER NOT NULL,
    `chosenChoiceId`  INTEGER NOT NULL,
    `timestampMillis` INTEGER NOT NULL,
    PRIMARY KEY(`id` AUTOINCREMENT),
    FOREIGN KEY(`dilemmaId`) REFERENCES `dilemmas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
    FOREIGN KEY(`chosenChoiceId`) REFERENCES `choices`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS `index_user_progress_dilemmaId` ON `user_progress` (`dilemmaId`);
CREATE INDEX IF NOT EXISTS `index_user_progress_chosenChoiceId` ON `user_progress` (`chosenChoiceId`);
