# DeciAventuras: El Camino de tus Decisiones

App educativa Android (offline, 8-12 años) sobre análisis de consecuencias.
El niño arrastra una **Tarjeta de Decisión** hacia la **Brújula de Acción** y
descubre el **Impacto Inmediato** y el **Destino Final** de su elección, en un
mapa de aventuras que va desbloqueando dilemas cotidianos.

Construida bajo la ESPECIFICACIÓN MAESTRA V3 (`MASTER_SPEC.md`) y el diseño
específico en `APP_PROMPT.md`.

## Estado actual del proyecto

🟢 **Paso 3/7 completado: Drag & Drop real + las 3 pantallas conectadas.**

Lo que existe ya en este repositorio:

- Estructura Clean Architecture (`data/ · domain/ · ui/`) dentro de `app/`.
- Configuración Gradle Kotlin DSL con **version catalog** (`gradle/libs.versions.toml`):
  Kotlin 1.9.24, AGP 8.4.2, Compose BOM 2024.05.00, Room 2.6.1, Navigation
  Compose 2.7.7, Coroutines 1.8.1 — todas versiones fijas y estables.
- `minSdk 24`, `targetSdk`/`compileSdk 34`, JDK 17.
- Tema Material 3 propio ("Diario de Explorador"): verde jungla, naranja
  fuego, azul cielo — sin dynamic color, para mantener identidad visual propia.
- Ícono de launcher propio (brújula), generado como PNG real en las 5
  densidades + adaptive icon (API 26+).
- `AndroidManifest.xml` sin ningún permiso (app 100% offline, sin Internet,
  sin Firebase, sin analítica).
- Gradle Wrapper 8.7 real (jar/scripts oficiales, no simulados).
- Workflow de GitHub Actions (`.github/workflows/build.yml`) que compila,
  testea, lintea y publica el APK de depuración en cada push a `main`.
- **Modelo Room completo**: `DilemmaEntity`, `ChoiceEntity` (FK → dilema,
  `CASCADE`), `UserProgressEntity` (FK → dilema y decisión, `CASCADE`,
  historial *append-only*) + sus DAOs y `DeciAventurasDatabase` con
  `Callback.onCreate` real de precarga.
- **Los 5 dilemas semilla completos**, cada uno con 3 tarjetas de decisión
  reales, espejadas en `database/sample_data.sql`.
- **Dominio puro y testeable**: `RecordChoiceUseCase` (guarda decisión →
  completa dilema → desbloquea el siguiente) y `GetJournalUseCase`.
- `AppContainer`: contenedor de dependencias manual, conectado a
  `DeciAventurasApp` y expuesto a Compose vía `rememberAppContainer()`.
- **Drag & Drop real** (`DraggableChoiceCard.kt`): `detectDragGestures` +
  `Animatable` para el resorte de retorno, seguimiento de posición vía
  `onGloballyPositioned`/`boundsInWindow()`, detección de solapamiento con
  la Brújula, y haptic feedback al soltar con éxito. NO es un botón que
  cambia texto al tocarlo (Sección 45 del spec maestro).
- **`CompassDropZone.kt`**: la Brújula ilustrada con Compose Canvas
  (mismo lenguaje visual que el ícono de launcher), con animación de
  "brillo" cuando una tarjeta está encima.
- **Las 3 pantallas completas y conectadas por Navigation Compose**:
  - `DashboardScreen.kt` — Mapa de Aventuras: camino serpenteante de nodos
    ilustrados (bloqueado/disponible/completado con iconografía real, no
    solo color) y progreso derivado de los datos.
  - `SimulatorScreen.kt` — situación → drag & drop → Impacto Inmediato +
    Destino Final → insignia ganada → "Guardar en mi Diario y Continuar".
  - `JournalScreen.kt` — insignias coleccionables + historial de
    decisiones, con estado vacío ilustrado (no una pantalla en blanco).
- **ViewModels** (`DashboardViewModel`, `SimulatorViewModel`,
  `JournalViewModel`) conectados vía `viewModelFactory{}` (sin Hilt/Dagger).
- **53 tests** (JUnit4 + Truth): 38 unitarios puros en `app/src/test`
  (dominio, seed data, mappers y ViewModels con `MainDispatcherRule` +
  `FakeDilemmaRepository`, sin Robolectric) + 15 instrumentados en
  `app/src/androidTest` sobre Room real en memoria.

Lo que **todavía no existe** (siguientes pasos, según `APP_PROMPT.md` §6):

- [ ] Ilustraciones/insignias adicionales más elaboradas (Compose Canvas).
- [ ] Pantallas de onboarding (máximo 3-4, Sección 16 del spec maestro).
- [ ] Documentación (`docs/MEMORIA_DESCRIPTIVA.md`, manuales, `BUILD_REPORT.md`).
- [ ] Verificación de compilación real (requiere entorno con acceso a
      `dl.google.com` / Maven Central / `services.gradle.org`).

## ⚠️ Compilación: NO VERIFICADA en este entorno

Siguiendo la Sección 37 de la Especificación Maestra ("Honestidad"), se deja
constancia explícita de que **no se pudo ejecutar `./gradlew` en el entorno
de generación de este proyecto**, porque el sandbox no tiene salida de red
hacia `services.gradle.org` ni hacia los repositorios Maven de Google/Android
(`dl.google.com`) o Maven Central, necesarios para descargar la distribución
de Gradle y las dependencias declaradas.

No se ha simulado ningún resultado de build. Para verificar la compilación
real, en una máquina con acceso normal a Internet:

```bash
./gradlew clean
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew assembleDebug
```

o simplemente haz `git push` a `main`: el workflow en `.github/workflows/build.yml`
lo compilará en GitHub Actions y publicará el APK como artefacto descargable.

## Estructura del repositorio

```
DeciAventuras/
├── app/
│   └── src/
│       ├── main/java/com/deciaventuras/app/
│       │   ├── data/
│       │   │   ├── local/entity/      (DilemmaEntity, ChoiceEntity, UserProgressEntity) ✅
│       │   │   ├── local/dao/         (DilemmaDao, ChoiceDao, UserProgressDao) ✅
│       │   │   ├── local/database/    (DeciAventurasDatabase + SeedData) ✅
│       │   │   └── repository/        (DilemmaRepositoryImpl) ✅
│       │   ├── domain/
│       │   │   ├── model/             (Dilemma, Choice, UserProgress, JournalEntry) ✅
│       │   │   ├── repository/        (DilemmaRepository, interfaz) ✅
│       │   │   └── usecase/           (RecordChoiceUseCase, GetJournalUseCase) ✅
│       │   ├── ui/
│       │   │   ├── theme/             (Color, Type, Theme) ✅
│       │   │   ├── navigation/        (Routes, DeciAventurasNavHost) ✅
│       │   │   ├── components/        (DraggableChoiceCard, CompassDropZone,
│       │   │   │                        DilemmaMapNode, ExplorerBadge, BottomBar) ✅
│       │   │   └── screens/
│       │   │       ├── dashboard/     (DashboardScreen + ViewModel) ✅
│       │   │       ├── simulator/     (SimulatorScreen + ViewModel) ✅
│       │   │       └── journal/       (JournalScreen + ViewModel) ✅
│       │   └── di/                    (AppContainer + acceso desde Compose) ✅
│       ├── test/java/...              (38 tests unitarios: dominio, seed, mappers, ViewModels) ✅
│       └── androidTest/java/...       (15 tests instrumentados: Room real) ✅
├── database/
│   ├── schema.sql          ✅
│   └── sample_data.sql     ✅
├── docs/               (memoria, manuales, build report — próximo paso)
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
├── .github/workflows/build.yml
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```
