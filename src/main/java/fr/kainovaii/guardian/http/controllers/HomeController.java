package fr.kainovaii.guardian.http.controllers;

import fr.kainovaii.guardian.core.Guardian;
import fr.kainovaii.guardian.core.database.DB;
import fr.kainovaii.guardian.core.helper.MemberHelper;
import fr.kainovaii.guardian.core.web.controller.BaseController;
import fr.kainovaii.guardian.core.web.controller.Controller;
import fr.kainovaii.guardian.domain.alert.Alert;
import fr.kainovaii.guardian.domain.alert.AlertRepository;
import fr.kainovaii.guardian.domain.penalty.Penalty;
import fr.kainovaii.guardian.domain.penalty.PenaltyRepository;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;

@Controller
public class HomeController extends BaseController
{
    private final MemberHelper memberHelper;
    private final PenaltyRepository penaltyRepository;
    private final AlertRepository alertRepository;

    public HomeController()
    {
        initRoutes();
        this.memberHelper = new MemberHelper();
        this.penaltyRepository = new PenaltyRepository();
        this.alertRepository = new AlertRepository();
    }

    private void initRoutes() { get("/", this::homepage);}

    private Object homepage(Request req, Response res)
    {
        requireLogin(req, res);

        memberHelper.preloadMemberCache();

        int membersCount = Guardian.getMemberCache().size();
        long membersThisWeek = memberHelper.countMembersJoinedInLastDays(7);
        List<Map<String, Object>> latestMembers = memberHelper.getLatestMembers(10);
        List<Penalty> penalties = DB.withConnection(() -> penaltyRepository.getAll().stream().toList());
        List<Alert> alerts = DB.withConnection(() -> alertRepository.getAll().stream().toList());

        int penaltiesCount = DB.withConnection(() -> penaltyRepository.getAll().size());
        int alertsCount = DB.withConnection(() -> alertRepository.getAll().size());

        return render(req, "home.html", Map.of(
            "members_count", membersCount,
            "members_week_count", membersThisWeek,
            "latest_members", latestMembers,
            "penalties", penalties,
            "alerts", alerts,
            "penalties_count", penaltiesCount,
            "alerts_count", alertsCount
        ));
    }

}
