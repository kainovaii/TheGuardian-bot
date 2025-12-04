package fr.kainovaii.guardian.core;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.Map;

public class ConfigManager
{
    private static PerspectiveConfig perspectiveConfig;

    @SuppressWarnings("unchecked")
    public static void load()
    {
        Yaml yaml = new Yaml();

        InputStream input = ConfigManager.class
            .getClassLoader()
            .getResourceAsStream("config.yml");

        if (input == null)  throw new IllegalStateException("config.yml introuvable dans resources !");

        Map<String, Object> data = yaml.load(input);

        Map<String, Object> perspective = (Map<String, Object>) data.get("perspective");

        double alert = ((Number) perspective.get("alert_threshold")).doubleValue();
        double penalty  = ((Number) perspective.get("penalty_threshold")).doubleValue();

        perspectiveConfig = new PerspectiveConfig(alert, penalty);
    }

    public static PerspectiveConfig getPerspective() { return perspectiveConfig; }
}
