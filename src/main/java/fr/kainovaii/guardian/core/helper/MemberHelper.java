package fr.kainovaii.guardian.core.helper;

import fr.kainovaii.guardian.core.Guardian;
import fr.kainovaii.guardian.core.database.DB;
import fr.kainovaii.guardian.domain.alert.Alert;
import fr.kainovaii.guardian.domain.alert.AlertRepository;
import fr.kainovaii.guardian.domain.penalty.Penalty;
import fr.kainovaii.guardian.domain.penalty.PenaltyRepository;
import net.dv8tion.jda.api.entities.Role;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class MemberHelper
{
    private final PenaltyRepository penaltyRepository;
    private final AlertRepository alertRepository;
    private final List<Map<String, Object>> memberCache = new ArrayList<>();

    public MemberHelper()
    {
        this.penaltyRepository = new PenaltyRepository();
        this.alertRepository = new AlertRepository();
    }

    public void preloadMemberCache()
    {
        memberCache.clear();
        Guardian.getGuild().getMembers().forEach(member -> {
            Map<String, Object> data = new HashMap<>();
            data.put("id", member.getId());
            data.put("username", member.getUser().getName());
            data.put("discriminator", member.getUser().getDiscriminator());
            data.put("nickname", member.getNickname());
            data.put("displayName", member.getEffectiveName());
            data.put("joinDate", member.getTimeJoined());
            data.put("avatar", member.getUser().getAvatarUrl());
            data.put("bot", member.getUser().isBot());

            List<String> allRoles = member.getRoles() != null
                    ? member.getRoles().stream().map(Role::getName).collect(Collectors.toList())
                    : new ArrayList<>();
            data.put("roles", allRoles);

            Role topRole = member.getRoles().stream()
                    .max(Comparator.comparingInt(Role::getPosition))
                    .orElse(null);
            data.put("topRoleName", topRole != null ? topRole.getName() : null);
            data.put("topRoleColor", topRole != null && topRole.getColor() != null ? topRole.getColor().getRGB() : null);

            memberCache.add(data);
        });
    }

    public List<Map<String, Object>> getAllMembers()
    {
        if (memberCache.isEmpty()) preloadMemberCache();
        return memberCache;
    }

    public Map<String, Object> getMemberById(String memberId)
    {
        return getAllMembers().stream()
            .filter(m -> m.get("id").equals(memberId))
            .findFirst()
            .orElse(null);
    }

    public List<Map<String, Object>> getAllRoles()
    {
        return Guardian.getGuild().getRoles().stream()
            .map(role -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", role.getId());
                map.put("name", role.getName());
                map.put("color", role.getColor() != null ? role.getColor().getRGB() : null);
                return map;
            })
            .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getMembersByRole(String roleName)
    {
        return getAllMembers().stream()
            .filter(m -> {
                List<String> roles = (List<String>) m.get("roles");
                return roles != null && roles.contains(roleName);
            })
            .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getLatestMembers(int limit)
    {
        return getAllMembers().stream()
            .filter(m -> m.get("joinDate") != null)
            .sorted((m1, m2) -> {
                OffsetDateTime d1 = (OffsetDateTime) m1.get("joinDate");
                OffsetDateTime d2 = (OffsetDateTime) m2.get("joinDate");
                return d2.compareTo(d1);
            })
            .limit(limit)
            .collect(Collectors.toList());
    }

    public long countMembersJoinedInLastDays(int days)
    {
        OffsetDateTime now = OffsetDateTime.now();
        return getAllMembers().stream()
            .map(m -> (OffsetDateTime) m.get("joinDate"))
            .filter(joinDate -> joinDate != null && joinDate.isAfter(now.minus(days, ChronoUnit.DAYS)))
            .count();
    }

    public List<Alert> getMemberAlerts(String memberId)
    {
        return DB.withConnection(() -> alertRepository.findByMember(memberId).stream().toList());
    }

    public List<Penalty> getMemberPenalties(String memberId)
    {
        return DB.withConnection(() -> penaltyRepository.findByMember(memberId).stream().toList());
    }
}
