import os, struct, zlib

def make_png(width, height, rgba_data):
    def chunk(tag, data):
        return struct.pack('>I', len(data)) + tag + data + struct.pack('>I', zlib.crc32(tag + data) & 0xffffffff)
    header = b'\x89PNG\r\n\x1a\n'
    ihdr = chunk(b'IHDR', struct.pack('>IIBBBBB', width, height, 8, 6, 0, 0, 0))
    raw_lines = bytearray()
    for y in range(height):
        raw_lines.append(0)
        idx = y * width * 4
        raw_lines.extend(rgba_data[idx:idx + width * 4])
    idat = chunk(b'IDAT', zlib.compress(bytes(raw_lines), 9))
    iend = chunk(b'IEND', b'')
    return header + ihdr + idat + iend

class CapeCanvas:
    def __init__(self):
        self.w = 64
        self.h = 32
        self.pixels = {} # (x, y) -> (r, g, b, a)

    def set(self, x, y, r, g, b, a=255):
        if 0 <= x < self.w and 0 <= y < self.h:
            self.pixels[(x, y)] = (r, g, b, a)

    def fill_rect(self, x0, y0, w, h, r, g, b, a=255):
        for y in range(y0, y0 + h):
            for x in range(x0, x0 + w):
                self.set(x, y, r, g, b, a)

    def fill_base(self, bg_color, border_color=None):
        r, g, b = bg_color
        # Fill Front (1,1 to 10,16) and Back (12,1 to 21,16)
        self.fill_rect(1, 1, 10, 16, r, g, b)
        self.fill_rect(12, 1, 10, 16, r, g, b)
        # Edges
        self.fill_rect(0, 1, 1, 16, r, g, b) # right edge
        self.fill_rect(11, 1, 1, 16, r, g, b) # left edge
        self.fill_rect(1, 0, 10, 1, r, g, b) # top edge
        self.fill_rect(11, 0, 10, 1, r, g, b) # bottom edge

        if border_color:
            br, bg, bb = border_color
            for x in range(1, 11):
                self.set(x, 1, br, bg, bb)
                self.set(x, 16, br, bg, bb)
                self.set(x + 11, 1, br, bg, bb)
                self.set(x + 11, 16, br, bg, bb)
            for y in range(1, 17):
                self.set(1, y, br, bg, bb)
                self.set(10, y, br, bg, bb)
                self.set(12, y, br, bg, bb)
                self.set(21, y, br, bg, bb)

    def draw_front_pixel(self, fx, fy, r, g, b, a=255):
        # fx in 0..9, fy in 0..15
        self.set(1 + fx, 1 + fy, r, g, b, a)

    def draw_back_pixel(self, bx, by, r, g, b, a=255):
        # bx in 0..9, by in 0..15
        self.set(12 + bx, 1 + by, r, g, b, a)

    def to_png_bytes(self):
        buf = bytearray(self.w * self.h * 4)
        for y in range(self.h):
            for x in range(self.w):
                idx = (y * self.w + x) * 4
                if (x, y) in self.pixels:
                    r, g, b, a = self.pixels[(x, y)]
                    buf[idx] = r
                    buf[idx+1] = g
                    buf[idx+2] = b
                    buf[idx+3] = a
                else:
                    buf[idx] = 0
                    buf[idx+1] = 0
                    buf[idx+2] = 0
                    buf[idx+3] = 0
        return make_png(self.w, self.h, buf)

def draw_pickaxe(c, metal_color, handle_color=(100, 60, 20)):
    mr, mg, mb = metal_color
    hr, hg, hb = handle_color
    # Pickaxe head (curved top)
    for x in range(2, 8):
        c.draw_front_pixel(x, 4, mr, mg, mb)
    c.draw_front_pixel(1, 5, mr, mg, mb)
    c.draw_front_pixel(8, 5, mr, mg, mb)
    c.draw_front_pixel(2, 3, mr, mg, mb)
    c.draw_front_pixel(7, 3, mr, mg, mb)
    # Handle
    for y in range(5, 12):
        c.draw_front_pixel(4, y, hr, hg, hb)
        c.draw_front_pixel(5, y, hr, hg, hb)

def draw_creeper_face(c, eye_color=(10, 30, 10)):
    er, eg, eb = eye_color
    # Eyes
    c.draw_front_pixel(2, 5, er, eg, eb)
    c.draw_front_pixel(3, 5, er, eg, eb)
    c.draw_front_pixel(2, 6, er, eg, eb)
    c.draw_front_pixel(3, 6, er, eg, eb)

    c.draw_front_pixel(6, 5, er, eg, eb)
    c.draw_front_pixel(7, 5, er, eg, eb)
    c.draw_front_pixel(6, 6, er, eg, eb)
    c.draw_front_pixel(7, 6, er, eg, eb)
    # Nose & Mouth
    c.draw_front_pixel(4, 7, er, eg, eb)
    c.draw_front_pixel(5, 7, er, eg, eb)
    c.draw_front_pixel(3, 8, er, eg, eb)
    c.draw_front_pixel(4, 8, er, eg, eb)
    c.draw_front_pixel(5, 8, er, eg, eb)
    c.draw_front_pixel(6, 8, er, eg, eb)
    c.draw_front_pixel(3, 9, er, eg, eb)
    c.draw_front_pixel(6, 9, er, eg, eb)

def generate_all_capes(target_dirs):
    capes = {}

    # 1. Minecon 2011
    c = CapeCanvas()
    c.fill_base((155, 27, 27), (110, 15, 15))
    draw_pickaxe(c, (220, 220, 220))
    capes['minecon_2011'] = c.to_png_bytes()

    # 2. Minecon 2012
    c = CapeCanvas()
    c.fill_base((28, 40, 65), (15, 22, 38))
    draw_pickaxe(c, (240, 200, 35))
    capes['minecon_2012'] = c.to_png_bytes()

    # 3. Minecon 2013
    c = CapeCanvas()
    c.fill_base((24, 85, 38), (14, 52, 23))
    # Piston head
    c.fill_rect(1+2, 1+4, 6, 2, 150, 110, 60)
    c.fill_rect(1+3, 1+6, 4, 3, 160, 160, 160)
    c.fill_rect(1+2, 1+9, 6, 4, 110, 110, 110)
    capes['minecon_2013'] = c.to_png_bytes()

    # 4. Minecon 2015
    c = CapeCanvas()
    c.fill_base((30, 75, 145), (15, 45, 95))
    # Iron golem face
    c.fill_rect(1+2, 1+4, 6, 7, 215, 215, 215)
    c.draw_front_pixel(3, 6, 180, 40, 40) # eye left
    c.draw_front_pixel(6, 6, 180, 40, 40) # eye right
    c.fill_rect(1+4, 1+7, 2, 3, 160, 110, 80) # nose
    c.draw_front_pixel(7, 3, 220, 30, 30) # red poppy
    c.draw_front_pixel(7, 4, 40, 140, 40) # stem
    capes['minecon_2015'] = c.to_png_bytes()

    # 5. Minecon 2016
    c = CapeCanvas()
    c.fill_base((26, 18, 42), (14, 9, 24))
    # Enderman eyes
    c.draw_front_pixel(2, 6, 195, 75, 240)
    c.draw_front_pixel(3, 6, 240, 140, 255)
    c.draw_front_pixel(6, 6, 240, 140, 255)
    c.draw_front_pixel(7, 6, 195, 75, 240)
    capes['minecon_2016'] = c.to_png_bytes()

    # 6. Minecon Live 2019 / Founder's Cape
    c = CapeCanvas()
    c.fill_base((45, 55, 62), (28, 35, 40))
    draw_creeper_face(c, (80, 190, 60))
    capes['minecon_2019'] = c.to_png_bytes()

    # 7. Mojang Classic (Staff / Mitarbeiter)
    c = CapeCanvas()
    c.fill_base((135, 20, 40), (85, 10, 25))
    # Classic Mojang Ruby Insignia
    c.fill_rect(1+2, 1+4, 6, 7, 255, 255, 255)
    c.fill_rect(1+3, 1+5, 4, 4, 180, 20, 40)
    c.draw_front_pixel(4, 6, 255, 255, 255)
    c.draw_front_pixel(5, 7, 255, 255, 255)
    capes['mojang_classic'] = c.to_png_bytes()

    # 8. Mojang Studios (Modern Staff / Mitarbeiter)
    c = CapeCanvas()
    c.fill_base((218, 36, 36), (160, 20, 20))
    # Mojang Studios Modern blocks
    c.fill_rect(1+2, 1+4, 2, 6, 255, 255, 255)
    c.fill_rect(1+5, 1+4, 3, 2, 255, 255, 255)
    c.fill_rect(1+5, 1+7, 3, 3, 255, 255, 255)
    c.draw_front_pixel(7, 6, 255, 255, 255)
    capes['mojang_studios'] = c.to_png_bytes()

    # 9. Mojang Spil (Vintage Staff / Mitarbeiter)
    c = CapeCanvas()
    c.fill_base((65, 25, 40), (35, 12, 20))
    c.fill_rect(1+3, 1+4, 4, 8, 220, 180, 80)
    c.draw_front_pixel(4, 6, 65, 25, 40)
    c.draw_front_pixel(5, 6, 65, 25, 40)
    capes['mojang_spil'] = c.to_png_bytes()

    # 10. Mojang Office / Developer (Staff / Mitarbeiter)
    c = CapeCanvas()
    c.fill_base((75, 18, 85), (45, 8, 55))
    c.fill_rect(1+2, 1+5, 6, 5, 240, 200, 50)
    c.draw_front_pixel(3, 4, 240, 200, 50)
    c.draw_front_pixel(6, 4, 240, 200, 50)
    c.draw_front_pixel(4, 7, 75, 18, 85)
    c.draw_front_pixel(5, 7, 75, 18, 85)
    capes['mojang_office'] = c.to_png_bytes()

    # 11. Migrator Cape
    c = CapeCanvas()
    c.fill_base((140, 30, 45), (95, 18, 28))
    # Gold compass rose
    c.fill_rect(1+4, 1+4, 2, 7, 240, 210, 60)
    c.fill_rect(1+2, 1+6, 6, 2, 240, 210, 60)
    c.draw_front_pixel(4, 7, 255, 255, 255)
    c.draw_front_pixel(5, 7, 255, 255, 255)
    capes['migrator'] = c.to_png_bytes()

    # 12. Vanilla Cape
    c = CapeCanvas()
    c.fill_base((20, 25, 50), (12, 15, 32))
    # Cosmic stars & blend
    for y in range(1, 16, 2):
        for x in range(1, 9, 3):
            c.draw_front_pixel(x, y, 70, 90, 160)
    c.draw_front_pixel(3, 5, 255, 255, 255)
    c.draw_front_pixel(6, 9, 255, 255, 255)
    c.draw_front_pixel(4, 12, 220, 230, 255)
    capes['vanilla'] = c.to_png_bytes()

    # 13. Cherry Blossom Cape
    c = CapeCanvas()
    c.fill_base((240, 175, 195), (210, 130, 155))
    # Cherry petals
    petals = [(3, 4), (6, 5), (4, 8), (7, 10), (2, 11), (5, 13)]
    for px, py in petals:
        c.draw_front_pixel(px, py, 255, 230, 240)
        c.draw_front_pixel(px+1, py, 230, 110, 145)
    capes['cherry'] = c.to_png_bytes()

    # 14. 15th Anniversary Cape
    c = CapeCanvas()
    c.fill_base((45, 170, 65), (25, 110, 40))
    draw_creeper_face(c, (15, 60, 20))
    c.draw_front_pixel(1, 14, 255, 255, 255)
    c.draw_front_pixel(2, 14, 255, 255, 255)
    c.draw_front_pixel(3, 14, 255, 255, 255)
    capes['anniversary_15'] = c.to_png_bytes()

    # 15. MCC 15 Year Cape
    c = CapeCanvas()
    c.fill_base((38, 42, 90), (20, 24, 55))
    # Ender Cup golden crown
    c.fill_rect(1+2, 1+6, 6, 3, 245, 195, 40)
    c.draw_front_pixel(2, 5, 245, 195, 40)
    c.draw_front_pixel(4, 4, 255, 225, 90)
    c.draw_front_pixel(5, 4, 255, 225, 90)
    c.draw_front_pixel(7, 5, 245, 195, 40)
    c.draw_front_pixel(3, 7, 230, 50, 50) # Ruby gem
    c.draw_front_pixel(6, 7, 50, 190, 220) # Diamond gem
    capes['mcc_15'] = c.to_png_bytes()

    # 16. TikTok Cape
    c = CapeCanvas()
    c.fill_base((18, 18, 20), (10, 10, 12))
    # TikTok Note
    c.fill_rect(1+4, 1+4, 2, 7, 0, 242, 254)
    c.fill_rect(1+5, 1+4, 2, 7, 254, 9, 121)
    c.fill_rect(1+4, 1+4, 1, 6, 255, 255, 255)
    c.fill_rect(1+2, 1+9, 3, 3, 0, 242, 254)
    c.fill_rect(1+3, 1+9, 2, 2, 255, 255, 255)
    capes['tiktok'] = c.to_png_bytes()

    # 17. Twitch Cape
    c = CapeCanvas()
    c.fill_base((145, 70, 255), (105, 40, 190))
    # Twitch speech heart
    c.fill_rect(1+2, 1+4, 6, 6, 255, 255, 255)
    c.draw_front_pixel(3, 6, 145, 70, 255)
    c.draw_front_pixel(6, 6, 145, 70, 255)
    c.draw_front_pixel(2, 10, 255, 255, 255)
    capes['twitch'] = c.to_png_bytes()

    # 18. Experience Cape
    c = CapeCanvas()
    c.fill_base((60, 28, 95), (35, 15, 60))
    # Amethyst & Nether Portal swirl
    for i in range(10):
        c.draw_front_pixel(i, (i*2)%14 + 1, 175, 95, 245)
        c.draw_front_pixel((9-i), (i*2+1)%14 + 1, 65, 215, 245)
    capes['experience'] = c.to_png_bytes()

    # 19. Turtle Cape
    c = CapeCanvas()
    c.fill_base((24, 85, 92), (12, 50, 55))
    # Turtle shell pattern
    c.fill_rect(1+2, 1+3, 6, 9, 35, 145, 110)
    c.draw_front_pixel(3, 5, 18, 70, 55)
    c.draw_front_pixel(6, 5, 18, 70, 55)
    c.draw_front_pixel(4, 7, 18, 70, 55)
    c.draw_front_pixel(5, 7, 18, 70, 55)
    c.draw_front_pixel(3, 9, 18, 70, 55)
    c.draw_front_pixel(6, 9, 18, 70, 55)
    capes['turtle'] = c.to_png_bytes()

    # 20. Translator Cape
    c = CapeCanvas()
    c.fill_base((32, 75, 160), (18, 45, 105))
    # Crowdin speech bubbles
    c.fill_rect(1+2, 1+4, 4, 3, 240, 240, 240)
    c.fill_rect(1+4, 1+8, 4, 3, 220, 200, 60)
    capes['translator'] = c.to_png_bytes()

    # 21. Prismarine Cape
    c = CapeCanvas()
    c.fill_base((75, 145, 135), (45, 95, 90))
    for y in range(2, 15, 3):
        for x in range(10):
            c.draw_front_pixel(x, y, 50, 105, 98)
    c.draw_front_pixel(3, 6, 120, 200, 180)
    c.draw_front_pixel(7, 11, 120, 200, 180)
    capes['prismarine'] = c.to_png_bytes()

    # 22. Cobalt Cape
    c = CapeCanvas()
    c.fill_base((25, 95, 205), (15, 58, 135))
    # Oxeye Daisy flower
    c.draw_front_pixel(4, 7, 250, 215, 45)
    c.draw_front_pixel(5, 7, 250, 215, 45)
    petals = [(4, 6), (5, 6), (4, 8), (5, 8), (3, 7), (6, 7)]
    for px, py in petals:
        c.draw_front_pixel(px, py, 255, 255, 255)
    capes['cobalt'] = c.to_png_bytes()

    # 23. Scrolls Cape
    c = CapeCanvas()
    c.fill_base((125, 90, 50), (75, 50, 25))
    # Parchment scroll & emerald gem
    c.fill_rect(1+2, 1+3, 6, 10, 215, 185, 130)
    c.fill_rect(1+4, 1+6, 2, 3, 40, 185, 75)
    capes['scrolls'] = c.to_png_bytes()

    # 24. Bacon Cape (Jonas)
    c = CapeCanvas()
    c.fill_base((185, 30, 30), (120, 15, 15))
    # Bacon marbling stripes
    stripes = [(230, 175, 175), (140, 20, 20), (245, 210, 210), (180, 25, 25)]
    for y in range(16):
        col = stripes[y % len(stripes)]
        for x in range(10):
            c.draw_front_pixel(x, y, col[0], col[1], col[2])
    capes['bacon'] = c.to_png_bytes()

    # 25. Millionth Customer
    c = CapeCanvas()
    c.fill_base((25, 55, 130), (12, 30, 80))
    # Golden Star
    c.draw_front_pixel(4, 5, 255, 220, 40)
    c.draw_front_pixel(5, 5, 255, 220, 40)
    for x in range(2, 8):
        c.draw_front_pixel(x, 6, 255, 220, 40)
    c.fill_rect(1+3, 1+7, 4, 2, 255, 220, 40)
    c.draw_front_pixel(2, 9, 255, 220, 40)
    c.draw_front_pixel(7, 9, 255, 220, 40)
    capes['millionth_customer'] = c.to_png_bytes()

    # 26. Danny Style Cape
    c = CapeCanvas()
    c.fill_base((25, 25, 30), (15, 15, 18))
    # Business suit collar & tie
    c.draw_front_pixel(3, 3, 240, 240, 240)
    c.draw_front_pixel(6, 3, 240, 240, 240)
    c.fill_rect(1+4, 1+4, 2, 7, 20, 140, 225)
    c.draw_front_pixel(4, 11, 20, 140, 225)
    capes['danny_style'] = c.to_png_bytes()

    # 27. JulianClark Cape
    c = CapeCanvas()
    c.fill_base((35, 35, 42), (20, 20, 25))
    c.fill_rect(1+4, 1+4, 2, 8, 50, 180, 245)
    c.fill_rect(1+2, 1+7, 6, 2, 50, 180, 245)
    capes['julian_clark'] = c.to_png_bytes()

    # 28. CheapShot Cape
    c = CapeCanvas()
    c.fill_base((220, 40, 40), (150, 25, 25))
    c.fill_rect(1+3, 1+5, 4, 4, 250, 250, 250)
    c.draw_front_pixel(1, 2, 250, 250, 250)
    c.draw_front_pixel(8, 2, 250, 250, 250)
    c.draw_front_pixel(1, 12, 250, 250, 250)
    c.draw_front_pixel(8, 12, 250, 250, 250)
    capes['cheap_shot'] = c.to_png_bytes()

    # 29. MrMessiah Cape
    c = CapeCanvas()
    c.fill_base((22, 22, 26), (12, 12, 15))
    # Lantern flame
    c.fill_rect(1+3, 1+6, 4, 5, 245, 160, 30)
    c.fill_rect(1+4, 1+7, 2, 3, 255, 240, 140)
    c.draw_front_pixel(4, 5, 245, 160, 30)
    c.draw_front_pixel(5, 5, 245, 160, 30)
    capes['mr_messiah'] = c.to_png_bytes()

    # 30. Valentine Cape
    c = CapeCanvas()
    c.fill_base((160, 20, 55), (105, 12, 35))
    # Pixel heart
    c.fill_rect(1+2, 1+5, 2, 2, 255, 75, 105)
    c.fill_rect(1+6, 1+5, 2, 2, 255, 75, 105)
    c.fill_rect(1+2, 1+7, 6, 2, 255, 75, 105)
    c.fill_rect(1+3, 1+9, 4, 2, 255, 75, 105)
    c.fill_rect(1+4, 1+11, 2, 1, 255, 75, 105)
    capes['valentine'] = c.to_png_bytes()

    # 31. New Year 2011 Cape
    c = CapeCanvas()
    c.fill_base((195, 140, 18), (130, 90, 10))
    # Fireworks sparkle
    c.fill_rect(1+4, 1+5, 2, 5, 255, 245, 180)
    c.fill_rect(1+2, 1+7, 6, 2, 255, 245, 180)
    capes['new_year_2011'] = c.to_png_bytes()

    # 32. Birthday Cape
    c = CapeCanvas()
    c.fill_base((235, 220, 205), (180, 160, 145))
    # Birthday cake
    c.fill_rect(1+2, 1+8, 6, 4, 180, 110, 60)
    c.fill_rect(1+2, 1+7, 6, 2, 245, 245, 245)
    # Candles
    c.draw_front_pixel(3, 5, 230, 40, 40)
    c.draw_front_pixel(6, 5, 40, 130, 230)
    c.draw_front_pixel(3, 4, 255, 210, 40)
    c.draw_front_pixel(6, 4, 255, 210, 40)
    capes['birthday'] = c.to_png_bytes()

    # 33. Dinnerbone Cape (Inverted Mojang)
    c = CapeCanvas()
    c.fill_base((135, 20, 40), (85, 10, 25))
    c.fill_rect(1+2, 1+6, 6, 6, 255, 255, 255)
    c.fill_rect(1+3, 1+7, 4, 4, 180, 20, 40)
    c.draw_front_pixel(4, 9, 255, 255, 255)
    c.draw_front_pixel(5, 8, 255, 255, 255)
    capes['dinnerbone'] = c.to_png_bytes()

    # 34. Grumm Cape
    c = CapeCanvas()
    c.fill_base((18, 18, 20), (10, 10, 12))
    c.fill_rect(1+2, 1+6, 6, 6, 240, 200, 40)
    c.fill_rect(1+3, 1+7, 4, 4, 18, 18, 20)
    capes['grumm'] = c.to_png_bytes()

    # 35. Ray Cokes Cape (TV Color Test Pattern)
    c = CapeCanvas()
    c.fill_base((30, 30, 30), (15, 15, 15))
    colors = [
        (230, 230, 230), # White
        (230, 230, 40),  # Yellow
        (40, 230, 230),  # Cyan
        (40, 230, 40),   # Green
        (230, 40, 230),  # Magenta
        (230, 40, 40),   # Red
        (40, 40, 230),   # Blue
    ]
    for x in range(10):
        col = colors[x % len(colors)]
        for y in range(16):
            c.draw_front_pixel(x, y, col[0], col[1], col[2])
    capes['ray_cokes'] = c.to_png_bytes()

    # 36. Pancakes Cape (Pan Cape)
    c = CapeCanvas()
    c.fill_base((210, 130, 40), (150, 85, 20))
    # Pancake stack
    c.fill_rect(1+2, 1+6, 6, 2, 230, 180, 90)
    c.fill_rect(1+2, 1+9, 6, 2, 220, 165, 80)
    c.fill_rect(1+2, 1+12, 6, 2, 210, 150, 70)
    # Melting butter
    c.fill_rect(1+4, 1+4, 2, 2, 255, 240, 80)
    c.draw_front_pixel(3, 7, 180, 75, 20) # syrup
    c.draw_front_pixel(6, 10, 180, 75, 20)
    capes['pan_cape'] = c.to_png_bytes()

    # Save to all target directories
    for d in target_dirs:
        os.makedirs(d, exist_ok=True)
        for name, data in capes.items():
            path = os.path.join(d, f"{name}.png")
            with open(path, "wb") as f:
                f.write(data)
        print(f"Generated {len(capes)} capes in: {d}")

if __name__ == "__main__":
    base = os.path.abspath(r"C:\Users\Zentrix\.gemini\antigravity\scratch\official-capes-physics")
    dirs = [
        os.path.join(base, r"mc-1.21.11\src\main\resources\assets\official_capes\textures\capes"),
        os.path.join(base, r"mc-26.2\src\main\resources\assets\official_capes\textures\capes"),
    ]
    generate_all_capes(dirs)
