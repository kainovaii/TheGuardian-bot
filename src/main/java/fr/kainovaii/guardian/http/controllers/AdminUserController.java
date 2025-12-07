package fr.kainovaii.guardian.http.controllers;

import fr.kainovaii.guardian.core.database.DB;
import fr.kainovaii.guardian.core.web.controller.BaseController;
import fr.kainovaii.guardian.core.web.controller.Controller;
import fr.kainovaii.guardian.domain.user.User;
import fr.kainovaii.guardian.domain.user.UserRepository;
import org.javalite.activejdbc.LazyList;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Map;
import static spark.Spark.get;

@Controller
public class AdminUserController extends BaseController
{
    private final UserRepository userRepository;

    public AdminUserController()
    {
        initRoutes();
        this.userRepository = new UserRepository();
    }

    private void initRoutes() { get("/admin/users", this::homepage);}

    private Object homepage(Request req, Response res)
    {
        requireLogin(req, res);

        List<User> users = DB.withConnection(() -> userRepository.getAll().stream().toList());

        return render("admin/users.html", Map.of( "users", users));
    }
}
