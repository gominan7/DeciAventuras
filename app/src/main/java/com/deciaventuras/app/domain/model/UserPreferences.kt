package com.deciaventuras.app.domain.model

/**
 * Perfil local del explorador: alias, avatar elegido, preferencias de
 * vibración, y si ya completó el onboarding. Es un único registro por
 * dispositivo (no hay multi-usuario ni cuentas — Sección 23 del spec
 * maestro: 100% offline, sin datos personales reales).
 */
data class UserPreferences(
    val alias: String = "",
    val avatarIndex: Int = 0,
    val hapticsEnabled: Boolean = true,
    val onboardingCompleted: Boolean = false,
)
