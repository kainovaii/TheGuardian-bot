package fr.kainovaii.guardian.core;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class ConfigLoader
{
    private static final String TARGET_DIR = "Guardian";
    private static final String TARGET_FILE = "config.yml";
    private PerspectiveConfig perspectiveConfig;

    public void load()
    {
        try {
            Path targetDir = Paths.get(TARGET_DIR);
            Path targetFile = targetDir.resolve(TARGET_FILE);

            if (!Files.exists(targetDir)) Files.createDirectories(targetDir);
            if (!Files.exists(targetFile)) copyDefaultConfig(targetFile);

            try (InputStream in = Files.newInputStream(targetFile)) {
                Yaml yaml = new Yaml();
                Map<String, Object> data = yaml.load(in);

                Map<String, Object> p = (Map<String, Object>) data.get("perspective");
                double alert = ((Number) p.get("alert_threshold")).doubleValue();
                double mute = ((Number) p.get("mute_threshold")).doubleValue();
                perspectiveConfig = new PerspectiveConfig(alert, mute);
            }

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du chargement du config.yml", e);
        }
    }

    private void copyDefaultConfig(Path targetFile) throws IOException
    {
        try (InputStream in = getClass().getResourceAsStream("/config.yml")) {
            if (in == null) throw new RuntimeException("config.yml introuvable dans /resources !");
            Files.copy(in, targetFile);
        }
    }

    public PerspectiveConfig getPerspectiveConfig() { return perspectiveConfig; }
}
