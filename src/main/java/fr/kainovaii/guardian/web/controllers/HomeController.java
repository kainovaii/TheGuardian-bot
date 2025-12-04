package fr.kainovaii.guardian.web.controllers;

import fr.kainovaii.guardian.Main;
import fr.kainovaii.guardian.web.core.BaseController;
import fr.kainovaii.guardian.web.core.Controller;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import spark.Request;
import spark.Response;

import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;

@Controller
public class HomeController extends BaseController
{
    public HomeController() { initRoutes(); }

    private void initRoutes() { get("/", this::homepage);}

    private Object homepage(Request req, Response res)
    {
        requireLogin(req, res);
        return render(req,"home.html", Map.of());
    }

}
