#!/usr/bin/env python3
"""Regenerates the Wretched's textures (needs Pillow). Run from the project root:
    python3 tools/generate_textures.py
Edit the colours / eye positions below and re-run to restyle the creature.
"""
import random
from PIL import Image

random.seed(7)
OUT = "src/main/resources/assets/wretched/textures"

# Palette sampled from the reference art.
TOP      = (78, 76, 77)
SIDE     = (64, 62, 63)
BOTTOM   = (44, 42, 43)
OUTLINE  = (20, 19, 20)
CRACK    = (34, 32, 33)
LEG      = (47, 43, 44)
JAW      = (92, 92, 96)
METAL    = (112, 113, 112)
METAL_DK = (82, 83, 84)
EYE_DIM  = (105, 24, 24)    # socket colour in the normal (lit) texture
EYE_GLOW = (235, 45, 45)    # fullbright colour in the eyes layer

# name -> (u, v), (w, h, d) -- must match WretchedEntityModel
PARTS = {
    "head":   ((0, 0),   (8, 8, 8)),
    "seg1":   ((32, 0),  (7, 7, 7)),
    "seg2":   ((0, 16),  (6, 6, 6)),
    "seg3":   ((24, 16), (6, 6, 6)),
    "seg4":   ((0, 32),  (5, 5, 5)),
    "jaw":    ((48, 16), (3, 3, 2)),
    "drill1": ((20, 32), (4, 4, 3)),
    "drill2": ((34, 32), (3, 3, 3)),
    "drill3": ((46, 32), (1, 1, 2)),
    "leg":    ((52, 32), (2, 6, 2)),
}

base = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
glow = Image.new("RGBA", (64, 64), (0, 0, 0, 0))


def rects(name):
    (u, v), (w, h, d) = PARTS[name]
    return {
        "top":    (u + d,         v,     w, d),
        "bottom": (u + d + w,     v,     w, d),
        "right":  (u,             v + d, d, h),
        "front":  (u + d,         v + d, w, h),
        "left":   (u + d + w,     v + d, d, h),
        "back":   (u + 2 * d + w, v + d, w, h),
    }


def jitter(c, n):
    return tuple(max(0, min(255, ch + random.randint(-n, n))) for ch in c) + (255,)


def paint_face(rect, color, noise=3, outline=OUTLINE):
    x0, y0, fw, fh = rect
    for y in range(fh):
        for x in range(fw):
            edge = x in (0, fw - 1) or y in (0, fh - 1)
            base.putpixel((x0 + x, y0 + y),
                          jitter(outline, 1) if (outline and edge) else jitter(color, noise))


def paint_box(name, top=TOP, side=SIDE, bottom=BOTTOM, noise=3, outline=OUTLINE):
    r = rects(name)
    colors = {"top": top, "bottom": bottom, "right": side, "left": side, "front": side, "back": side}
    for face, rect in r.items():
        paint_face(rect, colors[face], noise, outline)


def crack(name, count=2):
    x0, y0, fw, fh = rects(name)["top"]
    for _ in range(count):
        x, y = random.randint(1, fw - 2), 1
        for _ in range(fw + fh):
            if 1 <= x < fw - 1 and 1 <= y < fh - 1:
                base.putpixel((x0 + x, y0 + y), CRACK + (255,))
            if random.random() < 0.55:
                y += 1
            else:
                x += random.choice((-1, 1))


def eye(name, face, fx, fy, ew, eh, mirror=False):
    x0, y0, fw, fh = rects(name)[face]
    if mirror:
        fx = fw - fx - ew
    for y in range(eh):
        for x in range(ew):
            p = (x0 + fx + x, y0 + fy + y)
            base.putpixel(p, EYE_DIM + (255,))
            glow.putpixel(p, EYE_GLOW + (255,))


# --- head ---------------------------------------------------------------
paint_box("head")
crack("head", 2)
x0, y0, _, _ = rects("head")["front"]
for y in (6, 7):                       # mandible plates painted under the eyes
    for x in range(1, 7):
        base.putpixel((x0 + x, y0 + y), jitter(JAW, 2))
eye("head", "front", 1, 3, 1, 2)       # three eyes, like the reference
eye("head", "front", 3, 2, 2, 2)
eye("head", "front", 6, 3, 1, 2)

paint_box("jaw", top=JAW, side=JAW, bottom=(60, 60, 64))

# --- body segments: one small + one big eye on each flank ----------------
for name, small, big in (("seg1", (1, 2, 1, 1), (3, 3, 2, 2)),
                         ("seg2", (1, 2, 1, 1), (3, 3, 2, 2)),
                         ("seg3", (1, 3, 1, 1), (3, 2, 2, 2)),
                         ("seg4", (1, 1, 1, 1), (2, 2, 2, 2))):
    paint_box(name)
    crack(name, 2)
    for face in ("left", "right"):
        mirror = face == "right"
        eye(name, face, *small, mirror=mirror)
        eye(name, face, *big, mirror=mirror)

# --- drill: grey metal with grooves ---------------------------------------
for name in ("drill1", "drill2", "drill3"):
    paint_box(name, top=METAL, side=METAL, bottom=METAL_DK, noise=2, outline=None)
    for face, rect in rects(name).items():
        x0, y0, fw, fh = rect
        for y in range(fh):
            for x in range(fw):
                groove = (x % 2 == 1) if face in ("left", "right") else (y % 2 == 1) if face in ("top", "bottom") else False
                if groove:
                    base.putpixel((x0 + x, y0 + y), jitter(METAL_DK, 2))

# --- legs ------------------------------------------------------------------
paint_box("leg", top=LEG, side=LEG, bottom=OUTLINE, noise=2, outline=None)

base.save(f"{OUT}/entity/wretched.png")
glow.save(f"{OUT}/entity/wretched_eyes.png")

# --- 16x16 item icon: vertical drill cone ---------------------------------
icon = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
for y in range(1, 13):
    half = (y - 1) // 2 + 1                      # widens down the cone
    left, right = 8 - half, 7 + half
    for x in range(left, right + 1):
        t = (x - left) / max(1, right - left)
        c = (150, 151, 150) if t < 0.3 else (112, 113, 112) if t < 0.7 else (80, 81, 82)
        if y % 3 == 0:                           # spiral groove
            c = tuple(int(ch * 0.78) for ch in c)
        if x in (left, right) or y == 1:
            c = (28, 28, 30)
        icon.putpixel((x, y), c + (255,))
for x in range(2, 14):                           # dark base plate
    for y in (13, 14):
        icon.putpixel((x, y), (28, 28, 30, 255) if (y == 14 or x in (2, 13)) else (58, 56, 57, 255))
icon.save(f"{OUT}/item/wretched_drill_tip.png")

# 8x upscaled previews for eyeballing (not shipped)
for nm, im in (("base", base), ("glow", glow), ("icon", icon)):
    im.resize((im.width * 8, im.height * 8), Image.NEAREST).save(f"/home/claude/preview_{nm}.png")
print("textures written")
