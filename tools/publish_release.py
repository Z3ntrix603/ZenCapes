import subprocess, requests, os

# Retrieve token from git credential manager
p = subprocess.Popen(['git', 'credential', 'fill'], stdin=subprocess.PIPE, stdout=subprocess.PIPE, text=True)
out, _ = p.communicate(input='protocol=https\nhost=github.com\n\n')
token = ''
for line in out.splitlines():
    if line.startswith('password='):
        token = line[9:]

if not token:
    print('Error: GitHub token not found')
    exit(1)

headers = {'Authorization': 'token ' + token, 'Accept': 'application/vnd.github.v3+json'}

release_payload = {
    'tag_name': 'v1.0.0',
    'target_commitish': 'main',
    'name': 'ZenCapes v1.0.0',
    'body': """## ZenCapes v1.0.0 - Official Capes & Cloth Physics 🥋

### ✨ What's Included
- **All 36 Official Minecraft Capes:** Complete collection of official capes (Minecon 2011-2016, Founder's, Mojang Classic/Studios/Spil/Developer, 15th Anniversary, MCC 15 Year, Cherry Blossom, TikTok, Twitch, Experience Cape, Vanilla Cape, Translator, Turtle, Cobalt, Scrolls, etc.).
- **Dawn / NoRisk Cloth Physics:** Realistic 16-segment waving cloth physics simulation reacting dynamically to walking, sprinting, sneaking, jumping, and elytra flight.
- **Physics Customization Menu:** Interactive in-game menu to tweak cloth stiffness, gravity, wind sway, and damping in real-time.
- **Authentic Minecraft GUI:** Classic dirt-style GUI with search filters, cape previews, and one-click equip.
- **Controls & Keybinds:** Dedicated *"Official Capes & Physics"* category in Minecraft Key Binds. Open GUI with **`H`**, toggle cape with **`Y`**, or use chat commands **`/cape`** / **`/capes`**.
- **Dual Version Support:** Native builds for Minecraft **1.21.11** and Minecraft **26.2** on Fabric Loader.

### 📦 Downloads & Installation
Download the JAR matching your Minecraft version below and drop it into your `.minecraft/mods` folder:
- **Minecraft 1.21.11:** `zencapes-1.0.0-1.21.11.jar`
- **Minecraft 26.2:** `zencapes-1.0.0-26.2.jar`
""",
    'draft': False,
    'prerelease': False
}

r = requests.post('https://api.github.com/repos/Z3ntrix603/ZenCapes/releases', headers=headers, json=release_payload)
if r.status_code not in (200, 201):
    print('Failed to create release:', r.status_code, r.text)
    exit(1)

release_data = r.json()
upload_url = release_data['upload_url'].split('{')[0]
print('Release created:', release_data['html_url'])

dist_dir = r'C:\Users\Zentrix\.gemini\antigravity\scratch\official-capes-physics\dist'
assets = [
    'zencapes-1.0.0-1.21.11.jar',
    'zencapes-1.0.0-26.2.jar',
    'official-capes-physics-1.0.0-1.21.11.jar',
    'official-capes-physics-1.0.0-26.2.jar'
]

upload_headers = {
    'Authorization': 'token ' + token,
    'Content-Type': 'application/java-archive'
}

for asset_name in assets:
    file_path = os.path.join(dist_dir, asset_name)
    if not os.path.exists(file_path):
        print(f'File not found: {file_path}')
        continue
    with open(file_path, 'rb') as f:
        file_data = f.read()
    u_url = f'{upload_url}?name={asset_name}'
    res = requests.post(u_url, headers=upload_headers, data=file_data)
    if res.status_code in (200, 201):
        print(f'Uploaded asset: {asset_name} ({len(file_data)} bytes)')
    else:
        print(f'Failed to upload {asset_name}:', res.status_code, res.text)

print('Release published successfully!')
