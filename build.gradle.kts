// build.gradle.kts (raíz del proyecto)
// Declara los plugins usados por los módulos, sin aplicarlos aquí (apply false).
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
