import re

BACKSLASH = chr(92)
QUOTE = chr(34)

with open('/home/claude/DeciAventuras/app/src/main/java/com/deciaventuras/app/data/local/database/SeedData.kt', encoding='utf-8') as f:
    content = f.read()

def join_str(raw):
    parts = re.findall(r'"((?:[^"\\]|\\.)*)"', raw)
    joined = ''.join(parts)
    joined = joined.replace(BACKSLASH + QUOTE, QUOTE)  # deshace el escape de Kotlin (\" -> ")
    return joined.replace("'", "''")  # escapa comillas simples para SQL ('' es el estándar)

# --- Parse dilemmas ---
dilemma_block = content.split('val choices')[0]
dilemma_pattern = re.compile(
    r'DilemmaEntity\(\s*'
    r'id\s*=\s*(\d+),\s*'
    r'orderIndex\s*=\s*(\d+),\s*'
    r'title\s*=\s*((?:"(?:[^"\\]|\\.)*"\s*\+?\s*)+),\s*'
    r'description\s*=\s*((?:"(?:[^"\\]|\\.)*"\s*\+?\s*)+),\s*'
    r'isUnlocked\s*=\s*(\w+),\s*'
    r'isCompleted\s*=\s*(\w+),',
    re.DOTALL
)

dilemmas = []
for m in dilemma_pattern.finditer(dilemma_block):
    did, order, title_raw, desc_raw, unlocked, completed = m.groups()
    dilemmas.append((
        int(did), int(order), join_str(title_raw), join_str(desc_raw),
        unlocked == 'true', completed == 'true'
    ))

# --- Parse choices ---
choice_block = content.split('val choices')[1]
choice_pattern = re.compile(
    r'ChoiceEntity\(\s*'
    r'id\s*=\s*(\d+),\s*dilemmaId\s*=\s*(\d+),\s*orderIndex\s*=\s*(\d+),\s*'
    r'choiceText\s*=\s*((?:"(?:[^"\\]|\\.)*"\s*\+?\s*)+),\s*'
    r'shortTermEffect\s*=\s*((?:"(?:[^"\\]|\\.)*"\s*\+?\s*)+),\s*'
    r'longTermEffect\s*=\s*((?:"(?:[^"\\]|\\.)*"\s*\+?\s*)+),\s*'
    r'personalityTrait\s*=\s*"([^"]*)",',
    re.DOTALL
)

choices = []
for m in choice_pattern.finditer(choice_block):
    cid, did, order, text_raw, short_raw, long_raw, trait = m.groups()
    choices.append((
        int(cid), int(did), int(order),
        join_str(text_raw), join_str(short_raw), join_str(long_raw),
        trait.replace("'", "''")
    ))

print(f"Dilemas parseados: {len(dilemmas)}")
print(f"Decisiones parseadas: {len(choices)}")
assert len(dilemmas) == 10
assert len(choices) == 30

# Chequeo puntual del caso con comillas escapadas (dilema 4)
d4 = next(d for d in dilemmas if d[0] == 4)
print("Dilema 4 description (repr):", repr(d4[3]))
assert BACKSLASH not in d4[3], "Todavia quedan backslashes sin resolver"
assert '"un video más"' in d4[3], "El contenido esperado no aparece limpio"

# --- Generate sample_data.sql ---
lines = []
lines.append("-- ============================================================")
lines.append("-- DeciAventuras — Datos semilla (equivalente SQL de SeedData.kt)")
lines.append("-- ============================================================")
lines.append("-- Fuente de verdad real: app/src/main/java/com/deciaventuras/app/data/local/database/SeedData.kt")
lines.append("-- Generado automáticamente a partir de ese archivo (no transcripto a mano),")
lines.append("-- para que nunca se desalinee con el contenido real de la app.")
lines.append("")
lines.append("-- 1) Dilemas (el primero empieza desbloqueado; el resto, bloqueado)")
lines.append("INSERT INTO `dilemmas` (`id`, `orderIndex`, `title`, `description`, `isUnlocked`, `isCompleted`) VALUES")
dilemma_rows = []
for did, order, title, desc, unlocked, completed in dilemmas:
    dilemma_rows.append(f"({did}, {order}, '{title}',\n    '{desc}',\n    {1 if unlocked else 0}, {1 if completed else 0})")
lines.append(",\n".join(dilemma_rows) + ";")
lines.append("")

dilemma_titles = {d[0]: d[2] for d in dilemmas}
current_dilemma = None
lines.append("-- 2) Tarjetas de decisión (3 por dilema = 30 en total)")
for idx, (cid, did, order, text, short, long_, trait) in enumerate(choices):
    if did != current_dilemma:
        current_dilemma = did
        lines.append("")
        lines.append(f"-- Aventura {did}: {dilemma_titles[did]}")
        lines.append("INSERT INTO `choices` (`id`, `dilemmaId`, `orderIndex`, `choiceText`, `shortTermEffect`, `longTermEffect`, `personalityTrait`) VALUES")
    is_last_in_group = (idx == len(choices) - 1) or (choices[idx + 1][1] != did)
    sep = ";" if is_last_in_group else ","
    lines.append(f"({cid}, {did}, {order}, '{text}',\n    '{short}',\n    '{long_}',\n    '{trait}'){sep}")

output_text = "\n".join(lines) + "\n"
assert BACKSLASH not in output_text, "El archivo final todavia contiene backslashes sin resolver"

with open('/home/claude/DeciAventuras/database/sample_data.sql', 'w', encoding='utf-8') as f:
    f.write(output_text)

print("database/sample_data.sql regenerado correctamente, sin backslashes residuales.")
