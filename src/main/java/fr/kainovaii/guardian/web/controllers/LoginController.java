package fr.kainovaii.guardian.web.controllers;

import fr.kainovaii.guardian.domain.user.User;
import fr.kainovaii.guardian.domain.user.UserRepository;
import fr.kainovaii.guardian.core.DB;
import fr.kainovaii.guardian.core.BaseController;
import fr.kainovaii.guardian.core.Controller;
import org.mindrot.jbcrypt.BCrypt;
import spark.Request;
import spark.Response;
import spark.Session;

import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;

@Controller
public class LoginController extends BaseController
{
    private final UserRepository userRepository;

    public LoginController()
    {
        initRoutes();
        this.userRepository = new UserRepository();
    }
    private void initRoutes()
    {
        get("/login", this::front);
        post("/login", this::back);
        get("/logout", this::logout);
    }

    private Object front(Request req, Response res)
    {
        if (!isLogged(req)) {
            return render(req,"login.html", Map.of("title", "Bot"));
        } else {
            res.redirect("/");
        }
        return null;
    }

    private Object back(Request req, Response res)
    {
        String usernameParam = req.queryParams("username");
        String passwordParam = req.queryParams("password");
        Session session = req.session(true);

        DB.withConnection(() -> {
            if (UserRepository.userExist(usernameParam))
            {
                User user = userRepository.findByUsername(usernameParam);

                if (BCrypt.checkpw(passwordParam, user.getPassword()))
                {
                    session.attribute("logged", true);
                    session.attribute("username", usernameParam);
                    session.attribute("role", user.getRole());
                    res.redirect("/");
                    return null;
                }
            }

            setFlash(req, "error", "Incorrect login !");
            res.redirect("/login");

            return null;
        });
        return false;
    }

    private Object logout(Request req, Response res)
    {
        Session session = req.session(true);
        if (isLogged(req)) {
            session.invalidate();
        }
        res.redirect("/login");
        return null;
    }
}
