-- ============================================================
-- DeciAventuras — Esquema de base de datos (Room / SQLite)
-- ============================================================
-- Documentación humana del esquema real generado por Room a partir de
-- app/src/main/java/com/deciaventuras/app/data/local/entity/*.kt
-- (Sección 33 del spec maestro). Este archivo es descriptivo: Room genera
-- y versiona su propio esquema internamente (ver app/schemas/ una vez que
-- el proyecto compile con exportSchema = true).
--
-- Versión actual de la base de datos: 4
--   v1 -> v2: se agregó la tabla user_preferences.
--   v2 -> v3: se agregó la columna reflection en user_progress.
--   v3 -> v4: se agregó la columna soundEnabled en user_preferences.
-- (Migración destructiva mientras el proyecto no esté publicado — ver
-- DeciAventurasDatabase.kt para el detalle y la advertencia sobre producción.)

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

-- 2) Tarjetas de decisión de cada dilema (3 por dilema)
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

-- 3) Historial de decisiones tomadas por el niño (append-only salvo por la
--    columna `reflection`, que es la única que se actualiza después de
--    insertada — el resto de los campos son inmutables; alimenta el Diario)
CREATE TABLE IF NOT EXISTS `user_progress` (
    `id`              INTEGER NOT NULL,
    `dilemmaId`       INTEGER NOT NULL,
    `chosenChoiceId`  INTEGER NOT NULL,
    `timestampMillis` INTEGER NOT NULL,
    `reflection`      TEXT,             -- opcional: la reflexión propia del niño
    PRIMARY KEY(`id` AUTOINCREMENT),
    FOREIGN KEY(`dilemmaId`) REFERENCES `dilemmas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
    FOREIGN KEY(`chosenChoiceId`) REFERENCES `choices`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS `index_user_progress_dilemmaId` ON `user_progress` (`dilemmaId`);
CREATE INDEX IF NOT EXISTS `index_user_progress_chosenChoiceId` ON `user_progress` (`chosenChoiceId`);

-- 4) Perfil local del explorador (tabla de una sola fila, id siempre = 1).
--    No hay multi-usuario ni cuentas: alias y avatar son locales y ficticios.
CREATE TABLE IF NOT EXISTS `user_preferences` (
    `id`                  INTEGER NOT NULL,
    `alias`               TEXT    NOT NULL,
    `avatarIndex`         INTEGER NOT NULL, -- 0-7, ver AVATAR_COLORS en ExplorerAvatar.kt
    `hapticsEnabled`      INTEGER NOT NULL, -- booleano (0/1)
    `soundEnabled`        INTEGER NOT NULL DEFAULT 1, -- booleano (0/1)
    `onboardingCompleted` INTEGER NOT NULL, -- booleano (0/1)
    PRIMARY KEY(`id`)
);
