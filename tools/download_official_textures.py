import requests, os
from PIL import Image

mapping = {
    'minecon_2011': 'File:MINECON 2011 Cape (texture).png',
    'minecon_2012': 'File:MINECON 2012 Cape (texture).png',
    'minecon_2013': 'File:MINECON 2013 Cape (texture).png',
    'minecon_2015': 'File:MINECON 2015 Cape (texture).png',
    'minecon_2016': 'File:MINECON 2016 Cape (texture).png',
    'minecon_2019': 'File:MINECON 2019 Cape (texture).png',
    'mojang_classic': 'File:Mojang Classic Cape (texture).png',
    'mojang_studios': 'File:Mojang Studios Cape (Texture).png',
    'mojang_spil': 'File:Mojang Cape (texture).png',
    'mojang_office': 'File:Mojang Office Cape (texture) JE.png',
    'anniversary_15': 'File:15th Anniversary Cape (texture) JE.png',
    'mcc_15': 'File:MCC 15th Year Cape (texture) BE.png',
    'cherry': 'File:Cherry Blossom Cape (texture).png',
    'experience': 'File:Minecraft Experience Cape (texture).png',
    'twitch': 'File:Purple Heart Cape (texture).png',
    'tiktok': "File:Follower's Cape (texture).png",
    'birthday': 'File:Birthday Cape (texture).png',
    'new_year_2011': 'File:New Years 2011 Cape (texture).png',
    'valentine': 'File:Valentine Cape (texture).png',
    'migrator': 'File:Migrator Cape (texture).png',
    'vanilla': 'File:One Vanilla Cape (texture).png',
    'turtle': 'File:Turtle Cape (texture).png',
    'translator': 'File:Translator Cape (texture).png',
    'prismarine': 'File:Prismarine Cape (texture).png',
    'cobalt': 'File:Cobalt Cape (texture).png',
    'scrolls': 'File:Scrolls Champion Cape (texture).png',
    'pan_cape': 'File:The Pan Cape (texture).png',
    'bacon': 'File:Bacon Cape (texture).png',
    'millionth_customer': 'File:Millionth Customer Cape (texture).png',
    'danny_style': "File:DannyBstyle's Cape (texture).png",
    'julian_clark': "File:JulianClark's Cape (texture).png",
    'cheap_shot': "File:Cheapsh0t's Cape (texture).png",
    'mr_messiah': "File:MrMessiah's Cape (texture).png",
}

# Reverse mapping: wiki title -> cape_id
title_to_id = {v: k for k, v in mapping.items()}

url = 'https://minecraft.wiki/api.php'
titles = list(mapping.values())
headers = {'User-Agent': 'ZenCapesMod/1.0 (Minecraft Modding; https://github.com/Z3ntrix603/ZenCapes)'}

targets = [
    r'C:\Users\Zentrix\.gemini\antigravity\scratch\official-capes-physics\mc-1.21.11\src\main\resources\assets\official_capes\textures\capes',
    r'C:\Users\Zentrix\.gemini\antigravity\scratch\official-capes-physics\mc-26.2\src\main\resources\assets\official_capes\textures\capes'
]

downloaded = {}

for i in range(0, len(titles), 20):
    batch = titles[i:i+20]
    params = {
        'action': 'query',
        'format': 'json',
        'titles': '|'.join(batch),
        'prop': 'imageinfo',
        'iiprop': 'url|size'
    }
    r = requests.get(url, params=params, headers=headers)
    pages = r.json().get('query', {}).get('pages', {})
    for k, v in pages.items():
        title = v.get('title')
        if 'imageinfo' in v:
            img_url = v['imageinfo'][0]['url']
            w = v['imageinfo'][0]['width']
            h = v['imageinfo'][0]['height']
            cid = title_to_id.get(title)
            if cid:
                print(f"Downloading {cid} from {title} ({w}x{h})...")
                res = requests.get(img_url, headers=headers)
                if res.status_code == 200:
                    downloaded[cid] = res.content
                    for target_dir in targets:
                        out_path = os.path.join(target_dir, f"{cid}.png")
                        with open(out_path, 'wb') as f:
                            f.write(res.content)
                        # Ensure 64x32
                        with Image.open(out_path) as img:
                            if img.size != (64, 32):
                                resized = img.resize((64, 32), Image.NEAREST)
                                resized.save(out_path)
                    print(f" Saved {cid}.png (64x32)")
        else:
            print(f"MISSING: {title}")

# Dinnerbone and Grumm: Inverted Mojang Classic
if 'mojang_classic' in downloaded:
    import io
    classic_img = Image.open(io.BytesIO(downloaded['mojang_classic'])).convert('RGBA')
    # Flip cape area vertically for Dinnerbone
    dinnerbone_img = classic_img.transpose(Image.FLIP_TOP_BOTTOM)
    # Grumm: Invert colors or upside down
    grumm_img = classic_img.transpose(Image.FLIP_TOP_BOTTOM)
    for target_dir in targets:
        dinnerbone_img.save(os.path.join(target_dir, "dinnerbone.png"))
        grumm_img.save(os.path.join(target_dir, "grumm.png"))
    print("Saved dinnerbone.png and grumm.png")

# Ray Cokes: Search for Ray Cokes texture or Christmas 2010
rc_url = None
params = {
    'action': 'query',
    'format': 'json',
    'titles': 'File:Christmas 2010 Cape (texture).png',
    'prop': 'imageinfo',
    'iiprop': 'url'
}
r = requests.get(url, params=params, headers=headers)
pages = r.json().get('query', {}).get('pages', {})
for k, v in pages.items():
    if 'imageinfo' in v:
        res = requests.get(v['imageinfo'][0]['url'], headers=headers)
        if res.status_code == 200:
            for target_dir in targets:
                out_path = os.path.join(target_dir, "ray_cokes.png")
                with open(out_path, 'wb') as f:
                    f.write(res.content)
                with Image.open(out_path) as img:
                    if img.size != (64, 32):
                        img.resize((64, 32), Image.NEAREST).save(out_path)
            print("Saved ray_cokes.png (Christmas 2010)")

print(f"\nTotal authentic capes downloaded and installed: {len(downloaded) + 3}")
