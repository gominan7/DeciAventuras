package com.deciaventuras.app.data.local.database

import com.deciaventuras.app.data.local.entity.ChoiceEntity
import com.deciaventuras.app.data.local.entity.DilemmaEntity

/**
 * Los 5 dilemas cotidianos definidos en APP_PROMPT.md §5, con contenido real
 * y completo (no placeholders) para que la app se sienta terminada desde el
 * primer inicio (Sección 24 del spec maestro).
 *
 * Es la ÚNICA fuente de verdad del contenido semilla: tanto el `Callback` de
 * Room como `database/sample_data.sql` (documentación) derivan de este objeto.
 */
object SeedData {

    val dilemmas: List<DilemmaEntity> = listOf(
        DilemmaEntity(
            id = 1,
            orderIndex = 0,
            title = "El tesoro del recreo",
            description = "Tienes dinero para toda la semana de recreo. Es lunes: " +
                "¿qué vas a hacer con él hoy, el primer día?",
            isUnlocked = true,
            isCompleted = false,
        ),
        DilemmaEntity(
            id = 2,
            orderIndex = 1,
            title = "El equipo en apuros",
            description = "Tu equipo tiene que entregar un trabajo escolar mañana, " +
                "pero uno de tus compañeros no hizo su parte.",
            isUnlocked = false,
            isCompleted = false,
        ),
        DilemmaEntity(
            id = 3,
            orderIndex = 2,
            title = "El secreto del amigo",
            description = "Tu mejor amigo te cuenta, en voz baja, que un chico mayor " +
                "lo molesta en el camino a casa, y te pide que no se lo cuentes a nadie.",
            isUnlocked = false,
            isCompleted = false,
        ),
        DilemmaEntity(
            id = 4,
            orderIndex = 3,
            title = "La trampa del tiempo",
            description = "Ya es de noche y deberías estar durmiendo, pero la pantalla " +
                "te tienta con \"un video más\".",
            isUnlocked = false,
            isCompleted = false,
        ),
        DilemmaEntity(
            id = 5,
            orderIndex = 4,
            title = "El botín perdido",
            description = "En el parque, entre los arbustos, encuentras una mochila con " +
                "un videojuego increíble adentro. No hay nadie cerca.",
            isUnlocked = false,
            isCompleted = false,
        ),
    )

    val choices: List<ChoiceEntity> = listOf(
        // --- Aventura 1: El tesoro del recreo ---
        ChoiceEntity(
            id = 1, dilemmaId = 1, orderIndex = 0,
            choiceText = "Comprar hoy un juguete grande con todo el dinero",
            shortTermEffect = "¡Hoy disfrutas muchísimo mostrando tu juguete nuevo a todo el mundo en el recreo!",
            longTermEffect = "El resto de la semana no te queda nada. Ves a tus amigos comprar algo rico " +
                "cada día... y tú solo puedes mirar.",
            personalityTrait = "Espontaneidad",
        ),
        ChoiceEntity(
            id = 2, dilemmaId = 1, orderIndex = 1,
            choiceText = "Guardar el dinero y repartirlo entre los días",
            shortTermEffect = "Hoy no compras nada especial, pero sabes que mañana también vas a tener con qué disfrutar.",
            longTermEffect = "Toda la semana puedes comer algo rico en el recreo, y hasta te sobra un poco para el fin de semana.",
            personalityTrait = "Responsabilidad",
        ),
        ChoiceEntity(
            id = 3, dilemmaId = 1, orderIndex = 2,
            choiceText = "Comprar algo pequeño hoy y guardar el resto",
            shortTermEffect = "Te compras algo pequeño y rico, sin gastar todo de una vez.",
            longTermEffect = "Te alcanza para varios días más, aunque no llega a cubrir la semana completa.",
            personalityTrait = "Equilibrio",
        ),

        // --- Aventura 2: El equipo en apuros ---
        ChoiceEntity(
            id = 4, dilemmaId = 2, orderIndex = 0,
            choiceText = "Hacer tú solo la parte que falta, sin decir nada",
            shortTermEffect = "Terminas agotado, pero el trabajo queda completo a tiempo.",
            longTermEffect = "Tu compañero no se entera de que algo estuvo mal, y la próxima vez vuelve a " +
                "dejar su parte sin hacer, esperando que tú la resuelvas otra vez.",
            personalityTrait = "Generosidad",
        ),
        ChoiceEntity(
            id = 5, dilemmaId = 2, orderIndex = 1,
            choiceText = "Hablar con tu compañero y terminarlo juntos",
            shortTermEffect = "Al principio es incómodo decírselo, pero entre los dos terminan el trabajo a tiempo.",
            longTermEffect = "Tu compañero entiende que su parte importa, y la próxima vez cumple mejor.",
            personalityTrait = "Comunicación",
        ),
        ChoiceEntity(
            id = 6, dilemmaId = 2, orderIndex = 2,
            choiceText = "Avisarle a la profesora sin hablar antes con tu compañero",
            shortTermEffect = "La profesora se entera del problema y decide qué hacer con la entrega.",
            longTermEffect = "Tu compañero se siente acusado sin que le dieras la oportunidad de explicar, " +
                "y la confianza entre ustedes se enfría por un tiempo.",
            personalityTrait = "Honestidad",
        ),

        // --- Aventura 3: El secreto del amigo ---
        ChoiceEntity(
            id = 7, dilemmaId = 3, orderIndex = 0,
            choiceText = "Guardar el secreto tal como lo prometiste",
            shortTermEffect = "Tu amigo se siente aliviado porque cumpliste tu promesa de silencio.",
            longTermEffect = "El problema con el chico mayor sigue sin resolverse, y tu amigo sigue sintiendo " +
                "miedo cada día camino a casa.",
            personalityTrait = "Lealtad",
        ),
        ChoiceEntity(
            id = 8, dilemmaId = 3, orderIndex = 1,
            choiceText = "Contárselo a un adulto de confianza, aunque tu amigo se moleste",
            shortTermEffect = "Tu amigo se enoja un poco al principio, porque sentía que rompiste su confianza.",
            longTermEffect = "Un adulto puede ayudar a resolver la situación de verdad, y con el tiempo tu " +
                "amigo entiende que se lo dijiste para protegerlo.",
            personalityTrait = "Valentía",
        ),
        ChoiceEntity(
            id = 9, dilemmaId = 3, orderIndex = 2,
            choiceText = "Acompañar a tu amigo para que él mismo se lo cuente a un adulto",
            shortTermEffect = "Vas con tu amigo mientras habla con un adulto de confianza, y eso le da valor para contarlo.",
            longTermEffect = "El problema empieza a resolverse, y la amistad entre ustedes se hace más fuerte " +
                "porque lo enfrentaron juntos.",
            personalityTrait = "Empatía",
        ),

        // --- Aventura 4: La trampa del tiempo ---
        ChoiceEntity(
            id = 10, dilemmaId = 4, orderIndex = 0,
            choiceText = "Ver \"un video más\" y quedarte despierto",
            shortTermEffect = "Te ríes un montón con el video extra antes de dormir.",
            longTermEffect = "Al día siguiente te cuesta despertar, estás cansado en clase y te cuesta concentrarte.",
            personalityTrait = "Impulsividad",
        ),
        ChoiceEntity(
            id = 11, dilemmaId = 4, orderIndex = 1,
            choiceText = "Apagar la pantalla y dormir a tu hora",
            shortTermEffect = "Te cuesta un poco dejar de ver videos, pero te vas a dormir a tiempo.",
            longTermEffect = "Al día siguiente despiertas con energía, rindes mejor en clase y hasta te queda " +
                "tiempo para jugar en la tarde.",
            personalityTrait = "Autocontrol",
        ),
        ChoiceEntity(
            id = 12, dilemmaId = 4, orderIndex = 2,
            choiceText = "Ver el video, pero con una alarma para saber cuándo parar",
            shortTermEffect = "Ves el video con calma, sabiendo que la alarma te va a avisar cuándo parar.",
            longTermEffect = "Te acuestas un poco más tarde de lo ideal, pero de forma controlada, y al otro " +
                "día estás cansado, no agotado.",
            personalityTrait = "Equilibrio",
        ),

        // --- Aventura 5: El botín perdido ---
        ChoiceEntity(
            id = 13, dilemmaId = 5, orderIndex = 0,
            choiceText = "Quedarte con el videojuego sin decir nada",
            shortTermEffect = "Te emociona tener un videojuego nuevo sin haber gastado nada.",
            longTermEffect = "Sabes que en algún lugar hay un niño triste porque perdió algo que quería mucho, " +
                "y ese pensamiento te incomoda cada vez que juegas.",
            personalityTrait = "Impulsividad",
        ),
        ChoiceEntity(
            id = 14, dilemmaId = 5, orderIndex = 1,
            choiceText = "Llevar la mochila a un adulto para encontrar a su dueño",
            shortTermEffect = "Entregas la mochila y te sientes bien por haber hecho lo correcto, aunque no " +
                "te quedaste con el videojuego.",
            longTermEffect = "Si encuentran al dueño, se pone feliz de recuperarla, y tú te ganas la fama de " +
                "alguien en quien se puede confiar.",
            personalityTrait = "Honestidad",
        ),
        ChoiceEntity(
            id = 15, dilemmaId = 5, orderIndex = 2,
            choiceText = "Dejarla ahí sin tocar nada, por si alguien vuelve a buscarla",
            shortTermEffect = "No te llevas nada ni avisas a nadie, solo sigues tu camino.",
            longTermEffect = "Nunca sabes si el dueño la recuperó, y te queda la duda de si podrías haber " +
                "ayudado más entregándola a un adulto.",
            personalityTrait = "Prudencia",
        ),
    )
}
