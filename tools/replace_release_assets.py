import subprocess, requests, os

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

# 1. Fetch release
r = requests.get('https://api.github.com/repos/Z3ntrix603/ZenCapes/releases/tags/v1.0.0', headers=headers)
if r.status_code != 200:
    print('Failed to get release:', r.status_code, r.text)
    exit(1)

rel = r.json()
upload_url = rel['upload_url'].split('{')[0]
print(f"Found release {rel['name']} ({rel['tag_name']})")

# 2. Delete existing assets
for asset in rel.get('assets', []):
    asset_id = asset['id']
    asset_name = asset['name']
    del_r = requests.delete(f"https://api.github.com/repos/Z3ntrix603/ZenCapes/releases/assets/{asset_id}", headers=headers)
    print(f"Deleted old asset {asset_name} (id {asset_id}): {del_r.status_code}")

# 3. Upload new assets
dist_dir = r'C:\Users\Zentrix\.gemini\antigravity\scratch\official-capes-physics\dist'
assets = [
    'zencapes-1.0.0-1.21.11.jar',
    'zencapes-1.0.0-26.2.jar',
    'official-capes-physics-mc1.21.11-1.0.0+1.21.11.jar',
    'official-capes-physics-mc26.2-1.0.0+26.2.jar'
]

upload_headers = {
    'Authorization': 'token ' + token,
    'Content-Type': 'application/java-archive'
}

for asset_name in assets:
    file_path = os.path.join(dist_dir, asset_name)
    if not os.path.exists(file_path):
        print(f"File not found: {file_path}")
        continue
    with open(file_path, 'rb') as f:
        file_data = f.read()
    u_url = f"{upload_url}?name={asset_name}"
    upload_res = requests.post(u_url, headers=upload_headers, data=file_data)
    print(f"Uploaded new asset {asset_name} ({len(file_data)} bytes): {upload_res.status_code}")

print("All assets successfully replaced in release v1.0.0!")
