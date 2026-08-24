"""
Genera el ícono del launcher de DeciAventuras: una brújula de exploración
(referencia visual al "dron brújula" guía descrito en APP_PROMPT.md) sobre
fondo verde jungla, en todas las densidades mipmap requeridas por Android.

Uso: python3 scripts/gen_launcher_icon.py
Salida: app/src/main/res/mipmap-*/ic_launcher.png y ic_launcher_round.png
        app/src/main/res/drawable-xxxhdpi/ic_launcher_foreground.png (referencia adaptive icon)
"""
import math
import os

from PIL import Image, ImageDraw

JUNGLE_GREEN = (30, 138, 76, 255)
JUNGLE_GREEN_DARK = (15, 92, 49, 255)
FIRE_ORANGE = (244, 97, 30, 255)
PARCHMENT = (255, 251, 242, 255)
SKY_BLUE = (46, 159, 214, 255)
INK_BROWN = (62, 44, 35, 255)

DENSITIES = {
    "mdpi": 48,
    "hdpi": 72,
    "xhdpi": 96,
    "xxhdpi": 144,
    "xxxhdpi": 192,
}

OUT_ROOT = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "res")


def draw_compass(size: int, round_mask: bool, transparent_bg: bool = False, ring_scale: float = 0.66) -> Image.Image:
    scale = 4  # supersample para bordes limpios
    s = size * scale
    img = Image.new("RGBA", (s, s), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    center = s / 2
    radius = s / 2

    # Fondo: círculo o cuadrado redondeado en verde jungla (se omite en el
    # foreground del adaptive icon, que debe ser transparente fuera del motivo)
    if not transparent_bg:
        if round_mask:
            draw.ellipse([0, 0, s, s], fill=JUNGLE_GREEN)
        else:
            corner = s * 0.22
            draw.rounded_rectangle([0, 0, s, s], radius=corner, fill=JUNGLE_GREEN)

    # Anillo exterior de la brújula (papel/pergamino)
    ring_r = radius * ring_scale
    draw.ellipse(
        [center - ring_r, center - ring_r, center + ring_r, center + ring_r],
        fill=PARCHMENT,
        outline=JUNGLE_GREEN_DARK,
        width=max(2, int(s * 0.012)),
    )

    # Marcas cardinales (N/E/S/O) como pequeñas líneas
    tick_r_out = ring_r * 0.94
    tick_r_in = ring_r * 0.80
    for angle_deg in range(0, 360, 45):
        angle = math.radians(angle_deg)
        x1 = center + tick_r_in * math.sin(angle)
        y1 = center - tick_r_in * math.cos(angle)
        x2 = center + tick_r_out * math.sin(angle)
        y2 = center - tick_r_out * math.cos(angle)
        draw.line([x1, y1, x2, y2], fill=INK_BROWN, width=max(2, int(s * 0.01)))

    # Aguja de la brújula: mitad naranja (norte) / mitad azul (sur)
    needle_len = ring_r * 0.62
    needle_w = ring_r * 0.16

    def rotate(px, py, deg):
        rad = math.radians(deg)
        rx = px * math.cos(rad) - py * math.sin(rad)
        ry = px * math.sin(rad) + py * math.cos(rad)
        return center + rx, center + ry

    # Punta norte (naranja fuego)
    north = [
        rotate(0, -needle_len, 0),
        rotate(-needle_w, 0, 0),
        rotate(needle_w, 0, 0),
    ]
    draw.polygon(north, fill=FIRE_ORANGE)

    # Punta sur (azul cielo)
    south = [
        rotate(0, needle_len, 0),
        rotate(-needle_w, 0, 0),
        rotate(needle_w, 0, 0),
    ]
    draw.polygon(south, fill=SKY_BLUE)

    # Eje central
    hub_r = ring_r * 0.09
    draw.ellipse(
        [center - hub_r, center - hub_r, center + hub_r, center + hub_r],
        fill=INK_BROWN,
    )

    img = img.resize((size, size), Image.LANCZOS)
    return img


def main() -> None:
    for density, size in DENSITIES.items():
        mip_dir = os.path.join(OUT_ROOT, f"mipmap-{density}")
        os.makedirs(mip_dir, exist_ok=True)

        square_icon = draw_compass(size, round_mask=False)
        square_icon.save(os.path.join(mip_dir, "ic_launcher.png"))

        round_icon = draw_compass(size, round_mask=True)
        round_icon.save(os.path.join(mip_dir, "ic_launcher_round.png"))

        print(f"Generado mipmap-{density} ({size}x{size})")

    # Foreground TRANSPARENTE de alta resolución para el adaptive icon (API 26+).
    # ring_scale reducido para que el motivo quede dentro de la "zona segura"
    # circular (~66dp de un lienzo de 108dp) y no sea recortado por ninguna
    # máscara de ícono (círculo, squircle, cuadrado redondeado, etc.).
    fg_dir = os.path.join(OUT_ROOT, "drawable")
    os.makedirs(fg_dir, exist_ok=True)
    fg = draw_compass(432, round_mask=False, transparent_bg=True, ring_scale=0.40)
    fg.save(os.path.join(fg_dir, "ic_launcher_foreground.png"))
    print("Generado drawable/ic_launcher_foreground.png (432x432, transparente)")


if __name__ == "__main__":
    main()
