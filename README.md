# DeciAventuras: El Camino de tus Decisiones

App educativa Android (offline, 8-12 años) sobre análisis de consecuencias.
El niño arrastra una **Tarjeta de Decisión** hacia la **Brújula de Acción** y
descubre el **Impacto Inmediato** y el **Destino Final** de su elección, en un
mapa de aventuras que va desbloqueando dilemas cotidianos.

Construida bajo la ESPECIFICACIÓN MAESTRA V3 (`MASTER_SPEC.md`) y el diseño
específico en `APP_PROMPT.md`.

## Estado actual del proyecto

🟢 **Paso 6/7: 10 dilemas, Pantalla de Celebración y sonido real — sobre una base confirmada funcionando en dispositivo real.**

Tres funcionalidades nuevas, elegidas por el usuario para que el juego dure
más y se sienta más completo:

1. **5 dilemas nuevos** (total 10): juguete prestado, burla entre
   compañeros, invitación en línea vs. ayudar en casa, partido perdido, y
   florero roto en casa de la abuela. Mismo estándar de calidad que los
   primeros 5 (3 decisiones reales cada uno, consecuencia inmediata + a
   largo plazo distintas entre sí).
2. **Pantalla de Celebración**: al completar el último dilema disponible
   (`RecordChoiceUseCase` devuelve `NoMoreDilemmas`), en vez de volver
   directo al Mapa se muestra una pantalla dedicada con el resumen de
   insignias ganadas — reutiliza `JournalViewModel` en vez de duplicar lógica.
3. **Sonido real** (no decorativo): dos efectos sintetizados localmente sin
   assets de terceros (`scripts/gen_sound_effects.py`, ondas senoidales con
   envolvente) — un "ding" al soltar una decisión con éxito, y una
   fanfarria corta en la Celebración. El toggle "Efectos de sonido" en
   Ajustes, que antes dejamos afuera a propósito por no tener nada que
   controlar, ahora sí es funcional.

En el camino encontré y corregí un bug real: el método `save()` del
repositorio de preferencias no pasaba `soundEnabled` explícitamente, lo que
hubiera reseteado esa preferencia a `true` cada vez que se guardaba
cualquier otro campo del perfil.

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
- **Modelo Room v4**: `DilemmaEntity`, `ChoiceEntity` (FK → dilema, `CASCADE`),
  `UserProgressEntity` (FK → dilema y decisión, `CASCADE`, historial
  *append-only* salvo por la columna `reflection`, la única actualizable),
  y `UserPreferencesEntity` (alias, avatar, vibración, sonido, onboarding
  completado). Migración destructiva documentada (aceptable en esta etapa
  pre-release, no en producción).
- **Los 10 dilemas semilla completos**, cada uno con 3 tarjetas de decisión
  reales, espejadas en `database/sample_data.sql`.
- **Dominio puro y testeable**: `RecordChoiceUseCase`, `GetJournalUseCase`,
  y `ResetProgressUseCase` (coordina borrar progreso + perfil).
- `AppContainer`: contenedor de dependencias manual, conectado a
  `DeciAventurasApp` y expuesto a Compose vía `rememberAppContainer()`.
- **Drag & Drop real y verificado en dispositivo** (`DraggableChoiceCard.kt`):
  `detectDragGestures` + `Animatable` para el resorte de retorno, sin
  contenedores con scroll horizontal (competían por el gesto — bug real
  encontrado y corregido), con pista visual animada, haptic feedback y
  sonido apagables desde Ajustes.
- **`CompassDropZone.kt`**: la Brújula ilustrada con Compose Canvas.
- **`ExplorerAvatar.kt`**: el zorrito explorador ilustrado con Canvas (8
  colores de pelaje), usado en el Onboarding y en el header del Mapa.
- **Las 6 pantallas completas, conectadas por Navigation Compose**:
  - `SplashScreen` — decide sin UI visible si mostrar Onboarding o el Mapa.
  - `OnboardingScreen` — 3 páginas: bienvenida, cómo se juega, perfil (alias + avatar).
  - `DashboardScreen` — Mapa de Aventuras: camino serpenteante de nodos con
    estado real y progreso derivado de los datos; header con avatar + alias
    reales del perfil, e ícono de Ajustes.
  - `SimulatorScreen` — situación → drag & drop → Impacto Inmediato +
    Destino Final → insignia ganada → reflexión propia opcional.
  - `JournalScreen` — insignias coleccionables + historial (con la
    reflexión del niño destacada cuando la escribió), estado vacío ilustrado.
  - `SettingsScreen` — sonido (real) + vibración (real) + reiniciar todo
    el progreso (con confirmación).
  - `CelebrationScreen` — al completar el último dilema disponible: resumen
    de insignias + fanfarria, con botón para volver al Mapa.
- **ViewModels** conectados vía `viewModelFactory{}` (sin Hilt/Dagger).
- **75 tests** (JUnit4 + Truth): 53 unitarios (dominio, seed data, mappers,
  ViewModels, `ResetProgressUseCase`, reflexión, perfil en el Mapa,
  Celebración, Ajustes de sonido, con `MainDispatcherRule` +
  `FakeDilemmaRepository`/`FakeUserPreferencesRepository`, sin Robolectric)
  + 22 instrumentados sobre Room real en memoria.

Lo que **todavía no existe**:

- [ ] Documentación (`docs/MEMORIA_DESCRIPTIVA.md`, manuales, `BUILD_REPORT.md`) — siguiente paso.
- [ ] Verificación en dispositivo real de ESTA tanda (10 dilemas, Celebración,
      sonido): pasó la misma auditoría manual de siempre, pero todavía no
      se probó corriendo, a diferencia del resto de la app que ya está
      confirmado funcionando.

`database/schema.sql` y `database/sample_data.sql` sí se pudieron verificar
de verdad en este entorno (a diferencia del resto del proyecto): se
regeneraron automáticamente a partir de `SeedData.kt` con
`scripts/gen_sql_docs.py` (no transcriptos a mano, para que nunca se
desalineen) y se ejecutaron contra un SQLite real en memoria, incluyendo
una prueba de que las claves foráneas rechazan referencias inválidas.

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
│       │   │   ├── local/database/    (DeciAventurasDatabase v4 + SeedData: 10 dilemas) ✅
│       │   │   └── repository/        (DilemmaRepositoryImpl, UserPreferencesRepositoryImpl) ✅
│       │   ├── domain/
│       │   │   ├── model/             (Dilemma, Choice, UserProgress, JournalEntry, UserPreferences) ✅
│       │   │   ├── repository/        (DilemmaRepository, UserPreferencesRepository) ✅
│       │   │   └── usecase/           (RecordChoiceUseCase, GetJournalUseCase, ResetProgressUseCase) ✅
│       │   ├── ui/
│       │   │   ├── theme/             (Color, Type, Theme) ✅
│       │   │   ├── util/              (SoundEffects, sfx_success/sfx_celebration) ✅
│       │   │   ├── navigation/        (Routes, DeciAventurasNavHost, SplashScreen) ✅
│       │   │   ├── components/        (DraggableChoiceCard, CompassDropZone, ExplorerAvatar,
│       │   │   │                        DilemmaMapNode, ExplorerBadge, BottomBar) ✅
│       │   │   └── screens/
│       │   │       ├── onboarding/    (OnboardingScreen + ViewModel) ✅
│       │   │       ├── dashboard/     (DashboardScreen + ViewModel) ✅
│       │   │       ├── simulator/     (SimulatorScreen + ViewModel) ✅
│       │   │       ├── journal/       (JournalScreen + ViewModel) ✅
│       │   │       ├── settings/      (SettingsScreen + ViewModel) ✅
│       │   │       └── celebration/   (CelebrationScreen) ✅
│       │   └── di/                    (AppContainer + acceso desde Compose) ✅
│       ├── test/java/...              (53 tests unitarios: dominio, seed, mappers, ViewModels) ✅
│       └── androidTest/java/...       (22 tests instrumentados: Room real) ✅
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
