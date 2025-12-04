package fr.kainovaii.guardian.core.web;

import static spark.Spark.*;

import fr.kainovaii.guardian.core.Loader;
import fr.kainovaii.guardian.core.web.controller.ControllerLoader;
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

