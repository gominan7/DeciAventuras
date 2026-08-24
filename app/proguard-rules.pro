# Reglas ProGuard/R8 de DeciAventuras.
# La app no ofusca en release por defecto (isMinifyEnabled = false) para simplificar
# la verificación educativa del código; estas reglas quedan listas para cuando se active.

# Room: mantener entidades y DAOs anotados
-keep class com.deciaventuras.app.data.local.entity.** { *; }
-keep interface com.deciaventuras.app.data.local.dao.** { *; }

# Kotlin metadata
-keepattributes *Annotation*
-keep class kotlin.Metadata { *; }
