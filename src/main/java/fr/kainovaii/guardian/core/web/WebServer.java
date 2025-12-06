package fr.kainovaii.guardian.core.web;

import static spark.Spark.*;

import fr.kainovaii.guardian.core.Guardian;
import fr.kainovaii.guardian.core.web.controller.ControllerLoader;
import fr.kainovaii.guardian.http.controllers.GlobalAdviceController;

public class WebServer
{
    public void start()
    {
        port(Guardian.getWebPort());
        staticFiles.location("/");
        before((req, res) -> { GlobalAdviceController.applyGlobals(req); });
        ControllerLoader.loadControllers();
    }
}

