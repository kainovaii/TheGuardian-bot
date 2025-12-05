package fr.kainovaii.guardian.http.controllers;

import fr.kainovaii.guardian.core.Guardian;
import fr.kainovaii.guardian.core.database.DB;
import fr.kainovaii.guardian.core.web.controller.BaseController;
import fr.kainovaii.guardian.core.web.controller.Controller;
import fr.kainovaii.guardian.domain.user.User;
import fr.kainovaii.guardian.domain.user.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import spark.Request;
import spark.Response;
import spark.Session;

import java.util.Map;
import java.util.Optional;

import static spark.Spark.get;
import static spark.Spark.post;

@Controller
public class AccountController extends BaseController
{
    private UserRepository userRepository;

    public AccountController()
    {
        initRoutes();
        this.userRepository = new UserRepository();
    }

    private void initRoutes()
    {
        get("/account", this::settings);
        post("/account", this::settings_back);
    }
    private Object settings(Request req, Response res)
    {
        requireLogin(req, res);
        return render(req,"account/settings.html", Map.of());
    }

    private Object settings_back(Request req, Response res)
    {
        Session session = req.session(true);
        String currentUsername = session.attribute("username");

        String newUsername = req.queryParams("username");
        String newPassword = req.queryParams("password");

        User user = DB.withConnection(() -> userRepository.findByUsername(currentUsername));

        final String finalUsername = (newUsername == null || newUsername.isEmpty()) ? user.getUsername() : newUsername;
        final String finalPassword = (newPassword == null || newPassword.isEmpty()) ? user.getPassword() : BCrypt.hashpw(newPassword, BCrypt.gensalt());

        boolean updateUser = DB.withConnection(() -> userRepository.updateByUsername(currentUsername, finalUsername, finalPassword));

        if (updateUser) {
            session.attribute("username", finalUsername);
            setFlash(req, "success", "Update success");
        } else {setFlash(req, "error", "Update error");}

        res.redirect("/account");
        return null;
    }
}
