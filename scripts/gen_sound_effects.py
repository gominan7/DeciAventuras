"""
Genera efectos de sonido cortos y simples (tonos sintetizados, sin
dependencias externas) para DeciAventuras. Nada de assets bajados de
internet: todo se genera matemáticamente con ondas senoidales, consistente
con que la app es 100% offline y no usa recursos con licencia de terceros.

Salida: app/src/main/res/raw/sfx_success.wav, sfx_celebration.wav
"""
import math
import os
import struct
import wave

SAMPLE_RATE = 22050
OUT_DIR = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "res", "raw")


def note(freq: float, duration_s: float, volume: float = 0.35) -> list[int]:
    """Genera samples de una nota senoidal con envolvente suave (evita clics)."""
    n_samples = int(SAMPLE_RATE * duration_s)
    samples = []
    fade_samples = max(1, int(n_samples * 0.12))
    for i in range(n_samples):
        t = i / SAMPLE_RATE
        raw = math.sin(2 * math.pi * freq * t)
        # Envolvente: fade-in y fade-out lineales para que no truene al empezar/cortar.
        if i < fade_samples:
            envelope = i / fade_samples
        elif i > n_samples - fade_samples:
            envelope = (n_samples - i) / fade_samples
        else:
            envelope = 1.0
        value = raw * envelope * volume
        samples.append(int(value * 32767))
    return samples


def silence(duration_s: float) -> list[int]:
    return [0] * int(SAMPLE_RATE * duration_s)


def write_wav(filename: str, samples: list[int]) -> None:
    path = os.path.join(OUT_DIR, filename)
    with wave.open(path, "w") as f:
        f.setnchannels(1)
        f.setsampwidth(2)  # 16-bit
        f.setframerate(SAMPLE_RATE)
        packed = struct.pack("<%dh" % len(samples), *samples)
        f.writeframes(packed)
    size_kb = os.path.getsize(path) / 1024
    print(f"Generado {filename} ({len(samples) / SAMPLE_RATE:.2f}s, {size_kb:.1f} KB)")


def main() -> None:
    os.makedirs(OUT_DIR, exist_ok=True)

    # sfx_success.wav — al soltar una tarjeta con éxito en la Brújula:
    # dos notas ascendentes cortas (Do6 -> Mi6), tipo "ding-ding" alegre.
    success = note(1046.50, 0.11) + silence(0.02) + note(1318.51, 0.16)
    write_wav("sfx_success.wav", success)

    # sfx_celebration.wav — al completar TODOS los dilemas:
    # arpegio de 3 notas ascendentes (Do5-Mi5-Sol5), tipo "fanfarria" corta.
    celebration = (
        note(523.25, 0.14) + silence(0.02)
        + note(659.25, 0.14) + silence(0.02)
        + note(783.99, 0.28)
    )
    write_wav("sfx_celebration.wav", celebration)


if __name__ == "__main__":
    main()
