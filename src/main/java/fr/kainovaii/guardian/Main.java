package fr.kainovaii.guardian;

import fr.kainovaii.guardian.core.config.ConfigManager;
import fr.kainovaii.guardian.core.Guardian;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        Guardian app = new Guardian();
        ConfigManager.load();
        app.registerMotd();
        app.connectDatabase();
        app.loadConfigAndEnv();
        app.initUser();
        app.initBot();
        app.startWebServer();
    }
}