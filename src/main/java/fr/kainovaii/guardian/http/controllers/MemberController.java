package fr.kainovaii.guardian.http.controllers;

import fr.kainovaii.guardian.core.helper.MemberHelper;
import fr.kainovaii.guardian.domain.alert.Alert;
import fr.kainovaii.guardian.domain.alert.AlertRepository;
import fr.kainovaii.guardian.domain.penalty.Penalty;
import fr.kainovaii.guardian.domain.penalty.PenaltyRepository;
import fr.kainovaii.guardian.core.database.DB;
import fr.kainovaii.guardian.core.Guardian;
import fr.kainovaii.guardian.core.web.controller.BaseController;
import fr.kainovaii.guardian.core.web.controller.Controller;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import spark.Request;
import spark.Response;
import spark.Session;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static spark.Spark.*;

@Controller
public class MemberController extends BaseController
{
    private final MemberHelper memberHelper;

    public MemberController()
    {
        initRoutes();
        this.memberHelper = new MemberHelper();
    }

    private void initRoutes()
    {
        get("/member", this::members);
        get("/member/:role", this::membersByRole);
        get("/member/profil/:id", this::memberProfile);
        get("/member/:id/kick", this::memberKick);
        post("/member/:id/penalties", this::memberPenalty);
    }

    private Object members(Request req, Response res)
    {
        requireLogin(req, res);

        List<Map<String, Object>> memberData = memberHelper.getAllMembers();
        List<Map<String, Object>> roleData = memberHelper.getAllRoles();

        return render(req, "member.html", Map.of(
            "members", memberData,
            "roles", roleData
        ));
    }

    private Object membersByRole(Request req, Response res)
    {
        requireLogin(req, res);

        String roleName = req.params("role");
        List<Map<String, Object>> filtered = memberHelper.getMembersByRole(roleName);

        return render(req, "member.html", Map.of(
            "members", filtered,
            "roles", memberHelper.getAllRoles()
        ));
    }
    private Object memberProfile(Request req, Response res)
    {
        requireLogin(req, res);

        String memberId = req.params("id");
        Map<String, Object> memberData = memberHelper.getMemberById(memberId);

        if (memberData == null) redirectWithFlash(req, res, "error", "Membre introuvable", "/member");

        List<Alert> memberAlerts = memberHelper.getMemberAlerts(memberId);
        List<Penalty> memberPenalties = memberHelper.getMemberPenalties(memberId);

        return render(req, "member_profile.html", Map.of(
            "member", memberData,
            "alerts", memberAlerts,
            "penalties", memberPenalties
        ));
    }

    private Object memberKick(Request req, Response res)
    {
        requireLogin(req, res);
        Session session = req.session(true);
        String memberId = req.params("id");
        Guild guild = Guardian.getGuild();
        if (guild == null) return "Guild introuvable !";

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String formatted = now.format(formatter);

        try {
            Member member = guild.retrieveMemberById(memberId).complete(); // Bloquant
            if (member == null) setFlash(req, "error", "Membre introuvable !");
            member.kick().complete();
            //PenaltyManager.addPenalty(new Penalty(UUID.randomUUID().toString(), member.getId(), member.getEffectiveName(), "Kick by admin", session.attribute("username"), formatted, "Kick", "pending", 0.90 ));
            setFlash(req, "success", "Membre kické : " + member.getUser().getName());
            res.redirect("/member");
        } catch (Exception e) {
            setFlash(req, "error", e.getMessage());
            res.redirect("/member");
        }
        return null;
    }

    private List<Map<String, Object>> getAllRole()
    {
        List<Role> roles_list = Guardian.getGuild().getRoles();
        List<Map<String, Object>> roleData = roles_list.stream().map(role ->
        {
            Map<String, Object> data = new HashMap<>();
            data.put("id", role.getId());
            data.put("name", role.getName());
            data.put("color", role.getColor() != null ? role.getColor().getRGB() : null);
            return data;
        }).collect(Collectors.toList());

        return roleData;
    }

    private Object memberPenalty(Request req, Response res)
    {
        String memberId = req.params("id");
        String penaltyType = req.queryParams("penalty_type");
        String penaltyAction = req.queryParams("action");
        Guild guild = Guardian.getGuild();

        if (!"mute".equalsIgnoreCase(penaltyType)) {
            return null;
        }

        Member member = guild.retrieveMemberById(memberId).complete();
        if (member == null) {
            setFlash(req, "error", "Membre introuvable !");
            res.redirect("/member/profil/" + memberId);
            return false;
        }

        Role mutedRole = guild.getRolesByName("Muted", true).stream().findFirst().orElse(null);
        if (mutedRole == null) {
            setFlash(req, "error", "Rôle 'Muted' introuvable !");
            res.redirect("/member/profil/" + memberId);
            return false;
        }

        if ("1".equals(penaltyAction)) {
            if (!member.getRoles().contains(mutedRole)) {
                guild.addRoleToMember(member, mutedRole).queue();
            }
            setFlash(req, "success", "Le rôle 'Muted' a été appliqué au membre.");
        } else {
            if (member.getRoles().contains(mutedRole)) {
                guild.removeRoleFromMember(member, mutedRole).queue();
            }
            setFlash(req, "success", "Le rôle 'Muted' a été retiré du membre.");
        }

        res.redirect("/member/profil/" + memberId);
        return true;
    }



}
