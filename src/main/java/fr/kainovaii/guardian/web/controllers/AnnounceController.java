package fr.kainovaii.guardian.web.controllers;

import static spark.Spark.*;

import fr.kainovaii.guardian.core.Loader;
import fr.kainovaii.guardian.core.web.controller.BaseController;
import fr.kainovaii.guardian.core.web.controller.Controller;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import spark.Request;
import spark.Response;
import java.util.Map;

@Controller
public class AnnounceController extends BaseController
{
    public AnnounceController() { initRoutes(); }

    private void initRoutes() { get("/announce", this::homepage); post("/announce", this::announce);}

    private Object homepage(Request req, Response res)
    {
        requireLogin(req, res);
        return render(req,"form.html", Map.of());
    }

    private Object announce(Request req, Response res)
    {
        String message = req.queryParams("message");
        String channelId = req.queryParams("channel");

        if (message == null || message.isBlank()) {
            return "❌ Message vide.";
        }

        TextChannel channel = Loader.getJda().getTextChannelById(channelId);
        if (channel == null) {
            return "❌ Salon introuvable.";
        }

        channel.sendMessage(message).queue();
        res.redirect("/announce");
        return null;
    }
}
