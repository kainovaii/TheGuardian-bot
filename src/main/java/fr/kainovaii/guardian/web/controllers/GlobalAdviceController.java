package fr.kainovaii.guardian.web.controllers;

import fr.kainovaii.guardian.core.web.controller.BaseController;
import fr.kainovaii.guardian.core.web.WebRenderer;
import spark.Request;
import spark.Session;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GlobalAdviceController extends BaseController
{
    record User(String username, String role) {}

    public static void applyGlobals(Request req)
    {
        Session session = req.session(true);

        String username = Optional.ofNullable(session.attribute("username")).orElse("Invité").toString();
        String role = Optional.ofNullable(session.attribute("role")).orElse("").toString();

        List<User> users = Collections.singletonList(new User(username, role));

        WebRenderer.setGlobal("users", users);
        WebRenderer.setGlobal("isLogged", isLogged(req));
        WebRenderer.setGlobal("title", "The Guardian");
    }
}