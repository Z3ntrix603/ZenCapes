package net.officialcapes.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.officialcapes.cape.CapeRegistry;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;

public class OfficialCapesConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_FILE_NAME = "official_capes.json";
    private static OfficialCapesConfig INSTANCE;

    public boolean enabled = true;
    public String selectedCapeId = "minecon_2011";
    public boolean physicsEnabled = true;
    public float windStrength = 1.0f;
    public float gravity = 1.0f;
    public float waveIntensity = 1.0f;
    public boolean idleBreeze = true;

    public static OfficialCapesConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    public static OfficialCapesConfig load() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME);
        File configFile = configPath.toFile();

        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                OfficialCapesConfig config = GSON.fromJson(reader, OfficialCapesConfig.class);
                if (config != null) {
                    config.validate();
                    return config;
                }
            } catch (Exception e) {
                System.err.println("[OfficialCapes] Failed to load config, restoring defaults: " + e.getMessage());
            }
        }

        OfficialCapesConfig defaultConfig = new OfficialCapesConfig();
        defaultConfig.save();
        return defaultConfig;
    }

    public void save() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME);
        try (FileWriter writer = new FileWriter(configPath.toFile())) {
            GSON.toJson(this, writer);
        } catch (Exception e) {
            System.err.println("[OfficialCapes] Failed to save config: " + e.getMessage());
        }
    }

    public void validate() {
        if (selectedCapeId == null || CapeRegistry.get(selectedCapeId) == null) {
            selectedCapeId = "minecon_2011";
        }
        windStrength = Math.max(0.1f, Math.min(3.0f, windStrength));
        gravity = Math.max(0.1f, Math.min(3.0f, gravity));
        waveIntensity = Math.max(0.1f, Math.min(3.0f, waveIntensity));
    }

    public boolean toggleCape() {
        this.enabled = !this.enabled;
        save();
        return this.enabled;
    }

    public void selectCape(String capeId) {
        if (CapeRegistry.get(capeId) != null) {
            this.selectedCapeId = capeId;
            save();
        }
    }
}
