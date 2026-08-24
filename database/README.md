# database/

Artefactos SQL de la base de datos Room, exigidos por la Sección 33 de la
Especificación Maestra:

- `schema.sql` — esquema humano-legible de las 3 tablas (`dilemmas`,
  `choices`, `user_progress`), con sus claves foráneas `ON DELETE CASCADE`.
- `sample_data.sql` — los 5 dilemas semilla y sus 15 tarjetas de decisión,
  en SQL puro, espejo exacto de
  `app/src/main/java/com/deciaventuras/app/data/local/database/SeedData.kt`
  (esa clase Kotlin es la única fuente de verdad; este `.sql` es documentación).

Una vez que el proyecto compile con acceso normal a Internet, Room también
exportará su propio JSON de esquema versionado en `app/schemas/` (activado
vía `exportSchema = true` en `DeciAventurasDatabase`).
