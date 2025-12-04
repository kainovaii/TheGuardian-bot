package fr.kainovaii.guardian.core;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EnvLoader
{
    private static final String TARGET_DIR = "Guardian";
    private static final String TARGET_FILE = ".env";
    private Dotenv dotenv;

    public void load()
    {
        try {
            Path targetDir = Paths.get(TARGET_DIR);
            Path targetFile = targetDir.resolve(TARGET_FILE);

            if (!Files.exists(targetDir)) Files.createDirectories(targetDir);
            if (!Files.exists(targetFile)) copyDefaultEnv(targetFile);

            dotenv = Dotenv.configure()
                .directory(TARGET_DIR)
                .filename(TARGET_FILE)
                .load();

        } catch (IOException e) { throw new RuntimeException("Erreur lors du chargement du fichier .env", e); }
    }

    private void copyDefaultEnv(Path targetFile) throws IOException
    {
        try (InputStream in = getClass().getResourceAsStream("/example.env")) {
            if (in == null) throw new RuntimeException("example.old.env introuvable dans /resources !");
            Files.copy(in, targetFile);
        }
    }

    public String get(String key) {  return dotenv.get(key); }
}
