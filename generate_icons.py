import os
from PIL import Image, ImageDraw

densities = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
}

base_res = "app/src/main/res"

def draw_tv_icon(size, is_round=False):
    scale = 4
    canvas_size = size * scale
    img = Image.new("RGBA", (canvas_size, canvas_size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # Background shape
    bg_color = (13, 15, 23, 255) # #0D0F17
    border_color = (45, 51, 70, 255) # #2D3346

    pad = int(2 * scale)
    if is_round:
        draw.ellipse([pad, pad, canvas_size - pad, canvas_size - pad], fill=bg_color, outline=border_color, width=int(1.5 * scale))
    else:
        radius = int(canvas_size * 0.22)
        draw.rounded_rectangle([pad, pad, canvas_size - pad, canvas_size - pad], radius=radius, fill=bg_color, outline=border_color, width=int(1.5 * scale))

    # Center coords
    cx = canvas_size / 2
    cy = canvas_size / 2

    # Antennas
    antenna_color = (255, 204, 0, 255) # #FFCC00
    ant_w = int(2.5 * scale)
    # Left antenna
    draw.line([(cx, cy - 14 * scale), (cx - 10 * scale, cy - 26 * scale)], fill=antenna_color, width=ant_w)
    draw.ellipse([cx - 11.5 * scale, cy - 27.5 * scale, cx - 8.5 * scale, cy - 24.5 * scale], fill=(255, 225, 102, 255))
    # Right antenna
    draw.line([(cx, cy - 14 * scale), (cx + 10 * scale, cy - 26 * scale)], fill=antenna_color, width=ant_w)
    draw.ellipse([cx + 8.5 * scale, cy - 27.5 * scale, cx + 11.5 * scale, cy - 24.5 * scale], fill=(255, 225, 102, 255))

    # Legs
    leg_color = (212, 154, 0, 255)
    draw.polygon([(cx - 14 * scale, cy + 18 * scale), (cx - 18 * scale, cy + 24 * scale), (cx - 13 * scale, cy + 24 * scale), (cx - 9 * scale, cy + 18 * scale)], fill=leg_color)
    draw.polygon([(cx + 14 * scale, cy + 18 * scale), (cx + 18 * scale, cy + 24 * scale), (cx + 13 * scale, cy + 24 * scale), (cx + 9 * scale, cy + 18 * scale)], fill=leg_color)

    # TV Housing (Yellow Rounded Rect)
    tv_w = 48 * scale
    tv_h = 36 * scale
    tv_left = cx - tv_w / 2
    tv_top = cy - tv_h / 2 + 2 * scale
    tv_right = tv_left + tv_w
    tv_bottom = tv_top + tv_h
    tv_radius = int(8 * scale)

    # TV Body
    draw.rounded_rectangle([tv_left, tv_top, tv_right, tv_bottom], radius=tv_radius, fill=(255, 204, 0, 255), outline=(224, 160, 0, 255), width=int(1.5 * scale))

    # Bevel highlight on top
    draw.line([(tv_left + tv_radius, tv_top + 1.5 * scale), (tv_right - tv_radius, tv_top + 1.5 * scale)], fill=(255, 240, 150, 220), width=int(2 * scale))

    # Inner Screen (Dark Inset)
    screen_w = 34 * scale
    screen_h = 28 * scale
    sc_left = tv_left + 4 * scale
    sc_top = tv_top + 4 * scale
    sc_right = sc_left + screen_w
    sc_bottom = sc_top + screen_h
    sc_radius = int(5 * scale)

    draw.rounded_rectangle([sc_left, sc_top, sc_right, sc_bottom], radius=sc_radius, fill=(18, 22, 32, 255), outline=(35, 42, 60, 255), width=int(1 * scale))

    # Clock in center of screen
    clk_cx = (sc_left + sc_right) / 2
    clk_cy = (sc_top + sc_bottom) / 2
    clk_r = 9.5 * scale

    # Clock dial
    draw.ellipse([clk_cx - clk_r, clk_cy - clk_r, clk_cx + clk_r, clk_cy + clk_r], outline=(255, 204, 0, 255), width=int(2 * scale))

    # Clock hands: hour hand up, minute hand right
    draw.line([(clk_cx, clk_cy), (clk_cx, clk_cy - 6 * scale)], fill=(255, 255, 255, 255), width=int(2 * scale))
    draw.line([(clk_cx, clk_cy), (clk_cx + 5 * scale, clk_cy)], fill=(255, 204, 0, 255), width=int(2 * scale))
    draw.ellipse([clk_cx - 1.5 * scale, clk_cy - 1.5 * scale, clk_cx + 1.5 * scale, clk_cy + 1.5 * scale], fill=(255, 255, 255, 255))

    # Right controls
    ctrl_x = tv_right - 5 * scale
    draw.ellipse([ctrl_x - 1.8 * scale, tv_top + 9 * scale, ctrl_x + 1.8 * scale, tv_top + 12.6 * scale], fill=(18, 22, 32, 255), outline=(255, 204, 0, 255), width=int(1 * scale))
    draw.ellipse([ctrl_x - 1.8 * scale, tv_top + 16 * scale, ctrl_x + 1.8 * scale, tv_top + 19.6 * scale], fill=(18, 22, 32, 255), outline=(255, 204, 0, 255), width=int(1 * scale))

    # Downsample with high-quality Lanczos antialiasing
    final_img = img.resize((size, size), Image.Resampling.LANCZOS)
    return final_img

for folder, size in densities.items():
    dir_path = os.path.join(base_res, folder)
    os.makedirs(dir_path, exist_ok=True)
    
    # Save standard icon
    icon = draw_tv_icon(size, is_round=False)
    icon.save(os.path.join(dir_path, "ic_launcher.webp"), "WEBP")
    
    # Save round icon
    round_icon = draw_tv_icon(size, is_round=True)
    round_icon.save(os.path.join(dir_path, "ic_launcher_round.webp"), "WEBP")
    print(f"Generated {folder}: {size}x{size}")

print("All raster mipmaps successfully generated!")
