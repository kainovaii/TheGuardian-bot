package fr.kainovaii.guardian.http.controllers;

import fr.kainovaii.guardian.core.web.controller.BaseController;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.FileLoader;
import spark.Request;
import spark.Session;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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

        setGlobal("title", "The Guardian");
        setGlobal("isLogged", true);
        setGlobal("users", users);

        Map<String, String> flashes = collectFlashes(req);
        setGlobal("flashes", flashes);
    }
}