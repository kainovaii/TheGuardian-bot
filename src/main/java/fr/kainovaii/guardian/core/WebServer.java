package fr.kainovaii.guardian.core;

import static spark.Spark.*;

import fr.kainovaii.guardian.web.controllers.GlobalAdviceController;

public class WebServer
{
    public void start()
    {
        port(Loader.getWebPort());
        staticFiles.location("/assets");
        before((req, res) -> { GlobalAdviceController.applyGlobals(req); });
        ControllerLoader.loadControllers();
    }
}

