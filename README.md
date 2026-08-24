# DeciAventuras: El Camino de tus Decisiones

App educativa Android (offline, 8-12 años) sobre análisis de consecuencias.
El niño arrastra una **Tarjeta de Decisión** hacia la **Brújula de Acción** y
descubre el **Impacto Inmediato** y el **Destino Final** de su elección, en un
mapa de aventuras que va desbloqueando dilemas cotidianos.

Construida bajo la ESPECIFICACIÓN MAESTRA V3 (`MASTER_SPEC.md`) y el diseño
específico en `APP_PROMPT.md`.

## Estado actual del proyecto

🟢 **Paso 5/7: Reflexión propia del niño en el Diario, sobre una base confirmada funcionando en dispositivo real.**

Nuevo en esta tanda: al terminar un dilema, el niño puede escribir
opcionalmente qué aprendió ("¿Qué aprendiste de esta decisión?"), y esa
reflexión queda guardada junto a la decisión en el Diario de Explorador —
no reemplaza el texto fijo de Impacto Inmediato/Destino Final, se suma
como una nota propia. Es 100% opcional: si no escribe nada, todo sigue
funcionando exactamente igual que antes.

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
- **Modelo Room v3**: `DilemmaEntity`, `ChoiceEntity` (FK → dilema, `CASCADE`),
  `UserProgressEntity` (FK → dilema y decisión, `CASCADE`, historial
  *append-only* salvo por la columna `reflection`, la única actualizable),
  y `UserPreferencesEntity` (tabla de una sola fila: alias, avatar,
  vibración, onboarding completado). Migración destructiva documentada
  (aceptable en esta etapa pre-release, no en producción).
- **Los 5 dilemas semilla completos**, cada uno con 3 tarjetas de decisión
  reales, espejadas en `database/sample_data.sql`.
- **Dominio puro y testeable**: `RecordChoiceUseCase` (guarda decisión →
  completa dilema → desbloquea el siguiente), `GetJournalUseCase`, y
  **`ResetProgressUseCase`** (coordina borrar progreso + perfil).
- `AppContainer`: contenedor de dependencias manual, conectado a
  `DeciAventurasApp` y expuesto a Compose vía `rememberAppContainer()`.
- **Drag & Drop real y verificado en dispositivo** (`DraggableChoiceCard.kt`):
  `detectDragGestures` + `Animatable` para el resorte de retorno, sin
  contenedores con scroll horizontal (competían por el gesto — bug real
  encontrado y corregido), con pista visual animada enseñando la mecánica,
  y haptic feedback apagable desde Ajustes.
- **`CompassDropZone.kt`**: la Brújula ilustrada con Compose Canvas, con
  animación de "brillo" cuando una tarjeta está encima.
- **Las 5 pantallas completas, conectadas por Navigation Compose**:
  - `SplashScreen` — decide sin UI visible si mostrar Onboarding o el Mapa.
  - `OnboardingScreen` — 3 páginas: bienvenida, cómo se juega, perfil (alias + avatar).
  - `DashboardScreen` — Mapa de Aventuras: camino serpenteante de nodos con
    estado real y progreso derivado de los datos; ícono de Ajustes en el header.
  - `SimulatorScreen` — situación → drag & drop → Impacto Inmediato +
    Destino Final → insignia ganada → reflexión propia opcional.
  - `JournalScreen` — insignias coleccionables + historial (con la
    reflexión del niño destacada cuando la escribió), estado vacío ilustrado.
  - `SettingsScreen` — vibración (real) + reiniciar todo el progreso (con
    confirmación). Se dejó afuera a propósito un toggle de "sonido de
    efectos": la app no reproduce audio todavía, ese control sería decorativo.
- **ViewModels** conectados vía `viewModelFactory{}` (sin Hilt/Dagger).
- **67 tests** (JUnit4 + Truth): 45 unitarios (dominio, seed data, mappers,
  ViewModels, `ResetProgressUseCase`, reflexión, con `MainDispatcherRule` +
  `FakeDilemmaRepository`/`FakeUserPreferencesRepository`, sin Robolectric)
  + 22 instrumentados sobre Room real en memoria.

Lo que **todavía no existe**:

- [ ] Documentación (`docs/MEMORIA_DESCRIPTIVA.md`, manuales, `BUILD_REPORT.md`) — siguiente paso.
- [ ] Verificación en dispositivo real de la reflexión en el Diario (Paso 5):
      pasó la misma auditoría manual de siempre, pero todavía no se probó
      corriendo, a diferencia del Mapa/Simulador/Diario/Onboarding/Ajustes
      que ya están confirmados funcionando.

## ⚠️ Compilación: verificada en GitHub Actions y dispositivo real (con una salvedad)

Este proyecto se generó originalmente en un sandbox sin salida de red hacia
`services.gradle.org` / `dl.google.com` / Maven Central, así que no pudo
compilarse ahí. Eso ya no aplica al estado actual: **el usuario compiló y
corrió la app real** vía GitHub Actions (workflow en verde) e instalación
directa en un celular Android, y encontramos y corregimos 2 bugs reales en
el proceso (un XML sin cerrar, y un conflicto de gestos entre scroll y
drag & drop) — quedó documentado como aprendizaje de que ninguna revisión
manual de código reemplaza probarlo de verdad.

**Salvedad honesta**: el onboarding y Ajustes (Paso 4) pasaron la misma
auditoría manual exhaustiva de siempre (balance de llaves/paréntesis, rutas
de `package`, imports, XML), pero **todavía no se corrieron en un
dispositivo real** al momento de escribir esto. Si al compilarlos aparece
algún error, es información valiosa: mandalo tal cual (captura del log de
Build) para corregirlo.

```bash
./gradlew clean
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew assembleDebug
```

o simplemente `git push` a `main`: el workflow en `.github/workflows/build.yml`
compila en GitHub Actions y publica el APK como artefacto descargable.

## Estructura del repositorio

```
DeciAventuras/
├── app/
│   └── src/
│       ├── main/java/com/deciaventuras/app/
│       │   ├── data/
│       │   │   ├── local/entity/      (Dilemma, Choice, UserProgress, UserPreferences) ✅
│       │   │   ├── local/dao/         (DilemmaDao, ChoiceDao, UserProgressDao, UserPreferencesDao) ✅
│       │   │   ├── local/database/    (DeciAventurasDatabase v2 + SeedData) ✅
│       │   │   └── repository/        (DilemmaRepositoryImpl, UserPreferencesRepositoryImpl) ✅
│       │   ├── domain/
│       │   │   ├── model/             (Dilemma, Choice, UserProgress, JournalEntry, UserPreferences) ✅
│       │   │   ├── repository/        (DilemmaRepository, UserPreferencesRepository) ✅
│       │   │   └── usecase/           (RecordChoiceUseCase, GetJournalUseCase, ResetProgressUseCase) ✅
│       │   ├── ui/
│       │   │   ├── theme/             (Color, Type, Theme) ✅
│       │   │   ├── navigation/        (Routes, DeciAventurasNavHost, SplashScreen) ✅
│       │   │   ├── components/        (DraggableChoiceCard, CompassDropZone,
│       │   │   │                        DilemmaMapNode, ExplorerBadge, BottomBar) ✅
│       │   │   └── screens/
│       │   │       ├── onboarding/    (OnboardingScreen + ViewModel) ✅
│       │   │       ├── dashboard/     (DashboardScreen + ViewModel) ✅
│       │   │       ├── simulator/     (SimulatorScreen + ViewModel) ✅
│       │   │       ├── journal/       (JournalScreen + ViewModel) ✅
│       │   │       └── settings/      (SettingsScreen + ViewModel) ✅
│       │   └── di/                    (AppContainer + acceso desde Compose) ✅
│       ├── test/java/...              (41 tests unitarios: dominio, seed, mappers, ViewModels) ✅
│       └── androidTest/java/...       (19 tests instrumentados: Room real) ✅
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
