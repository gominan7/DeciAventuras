-- ============================================================
-- DeciAventuras — Datos semilla (equivalente SQL de SeedData.kt)
-- ============================================================
-- Fuente de verdad real: app/src/main/java/com/deciaventuras/app/data/local/database/SeedData.kt
-- Este archivo es la documentación en SQL puro de esos mismos datos
-- (Sección 33 del spec maestro), útil para inspeccionar el contenido sin
-- abrir Android Studio.

-- 1) Dilemas (el primero empieza desbloqueado; el resto, bloqueado)
INSERT INTO `dilemmas` (`id`, `orderIndex`, `title`, `description`, `isUnlocked`, `isCompleted`) VALUES
(1, 0, 'El tesoro del recreo',
    'Tienes dinero para toda la semana de recreo. Es lunes: ¿qué vas a hacer con él hoy, el primer día?',
    1, 0),
(2, 1, 'El equipo en apuros',
    'Tu equipo tiene que entregar un trabajo escolar mañana, pero uno de tus compañeros no hizo su parte.',
    0, 0),
(3, 2, 'El secreto del amigo',
    'Tu mejor amigo te cuenta, en voz baja, que un chico mayor lo molesta en el camino a casa, y te pide que no se lo cuentes a nadie.',
    0, 0),
(4, 3, 'La trampa del tiempo',
    'Ya es de noche y deberías estar durmiendo, pero la pantalla te tienta con "un video más".',
    0, 0),
(5, 4, 'El botín perdido',
    'En el parque, entre los arbustos, encuentras una mochila con un videojuego increíble adentro. No hay nadie cerca.',
    0, 0);

-- 2) Tarjetas de decisión (3 por dilema = 15 en total)

-- Aventura 1: El tesoro del recreo
INSERT INTO `choices` (`id`, `dilemmaId`, `orderIndex`, `choiceText`, `shortTermEffect`, `longTermEffect`, `personalityTrait`) VALUES
(1, 1, 0, 'Comprar hoy un juguete grande con todo el dinero',
    '¡Hoy disfrutas muchísimo mostrando tu juguete nuevo a todo el mundo en el recreo!',
    'El resto de la semana no te queda nada. Ves a tus amigos comprar algo rico cada día... y tú solo puedes mirar.',
    'Espontaneidad'),
(2, 1, 1, 'Guardar el dinero y repartirlo entre los días',
    'Hoy no compras nada especial, pero sabes que mañana también vas a tener con qué disfrutar.',
    'Toda la semana puedes comer algo rico en el recreo, y hasta te sobra un poco para el fin de semana.',
    'Responsabilidad'),
(3, 1, 2, 'Comprar algo pequeño hoy y guardar el resto',
    'Te compras algo pequeño y rico, sin gastar todo de una vez.',
    'Te alcanza para varios días más, aunque no llega a cubrir la semana completa.',
    'Equilibrio');

-- Aventura 2: El equipo en apuros
INSERT INTO `choices` (`id`, `dilemmaId`, `orderIndex`, `choiceText`, `shortTermEffect`, `longTermEffect`, `personalityTrait`) VALUES
(4, 2, 0, 'Hacer tú solo la parte que falta, sin decir nada',
    'Terminas agotado, pero el trabajo queda completo a tiempo.',
    'Tu compañero no se entera de que algo estuvo mal, y la próxima vez vuelve a dejar su parte sin hacer, esperando que tú la resuelvas otra vez.',
    'Generosidad'),
(5, 2, 1, 'Hablar con tu compañero y terminarlo juntos',
    'Al principio es incómodo decírselo, pero entre los dos terminan el trabajo a tiempo.',
    'Tu compañero entiende que su parte importa, y la próxima vez cumple mejor.',
    'Comunicación'),
(6, 2, 2, 'Avisarle a la profesora sin hablar antes con tu compañero',
    'La profesora se entera del problema y decide qué hacer con la entrega.',
    'Tu compañero se siente acusado sin que le dieras la oportunidad de explicar, y la confianza entre ustedes se enfría por un tiempo.',
    'Honestidad');

-- Aventura 3: El secreto del amigo
INSERT INTO `choices` (`id`, `dilemmaId`, `orderIndex`, `choiceText`, `shortTermEffect`, `longTermEffect`, `personalityTrait`) VALUES
(7, 3, 0, 'Guardar el secreto tal como lo prometiste',
    'Tu amigo se siente aliviado porque cumpliste tu promesa de silencio.',
    'El problema con el chico mayor sigue sin resolverse, y tu amigo sigue sintiendo miedo cada día camino a casa.',
    'Lealtad'),
(8, 3, 1, 'Contárselo a un adulto de confianza, aunque tu amigo se moleste',
    'Tu amigo se enoja un poco al principio, porque sentía que rompiste su confianza.',
    'Un adulto puede ayudar a resolver la situación de verdad, y con el tiempo tu amigo entiende que se lo dijiste para protegerlo.',
    'Valentía'),
(9, 3, 2, 'Acompañar a tu amigo para que él mismo se lo cuente a un adulto',
    'Vas con tu amigo mientras habla con un adulto de confianza, y eso le da valor para contarlo.',
    'El problema empieza a resolverse, y la amistad entre ustedes se hace más fuerte porque lo enfrentaron juntos.',
    'Empatía');

-- Aventura 4: La trampa del tiempo
INSERT INTO `choices` (`id`, `dilemmaId`, `orderIndex`, `choiceText`, `shortTermEffect`, `longTermEffect`, `personalityTrait`) VALUES
(10, 4, 0, 'Ver "un video más" y quedarte despierto',
    'Te ríes un montón con el video extra antes de dormir.',
    'Al día siguiente te cuesta despertar, estás cansado en clase y te cuesta concentrarte.',
    'Impulsividad'),
(11, 4, 1, 'Apagar la pantalla y dormir a tu hora',
    'Te cuesta un poco dejar de ver videos, pero te vas a dormir a tiempo.',
    'Al día siguiente despiertas con energía, rindes mejor en clase y hasta te queda tiempo para jugar en la tarde.',
    'Autocontrol'),
(12, 4, 2, 'Ver el video, pero con una alarma para saber cuándo parar',
    'Ves el video con calma, sabiendo que la alarma te va a avisar cuándo parar.',
    'Te acuestas un poco más tarde de lo ideal, pero de forma controlada, y al otro día estás cansado, no agotado.',
    'Equilibrio');

-- Aventura 5: El botín perdido
INSERT INTO `choices` (`id`, `dilemmaId`, `orderIndex`, `choiceText`, `shortTermEffect`, `longTermEffect`, `personalityTrait`) VALUES
(13, 5, 0, 'Quedarte con el videojuego sin decir nada',
    'Te emociona tener un videojuego nuevo sin haber gastado nada.',
    'Sabes que en algún lugar hay un niño triste porque perdió algo que quería mucho, y ese pensamiento te incomoda cada vez que juegas.',
    'Impulsividad'),
(14, 5, 1, 'Llevar la mochila a un adulto para encontrar a su dueño',
    'Entregas la mochila y te sientes bien por haber hecho lo correcto, aunque no te quedaste con el videojuego.',
    'Si encuentran al dueño, se pone feliz de recuperarla, y tú te ganas la fama de alguien en quien se puede confiar.',
    'Honestidad'),
(15, 5, 2, 'Dejarla ahí sin tocar nada, por si alguien vuelve a buscarla',
    'No te llevas nada ni avisas a nadie, solo sigues tu camino.',
    'Nunca sabes si el dueño la recuperó, y te queda la duda de si podrías haber ayudado más entregándola a un adulto.',
    'Prudencia');
