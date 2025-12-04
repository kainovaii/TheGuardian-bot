package fr.kainovaii.guardian;

import fr.kainovaii.guardian.core.Loader;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        Loader loader = new Loader();
        loader.registerMotd();
        loader.connectDatabase();
        loader.loadConfigAndEnv();
        loader.initUser();
        loader.initBot();
        loader.startWebServer();
    }
}