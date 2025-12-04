package fr.kainovaii.guardian.web.core;

import static spark.Spark.*;

import fr.kainovaii.guardian.Main;
import fr.kainovaii.guardian.utils.Loader;
import fr.kainovaii.guardian.web.controllers.GlobalAdviceController;
import spark.Request;

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

