package fr.kainovaii.guardian.web.core;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebUtils
{
    public static List<Map<String, Object>> toTemplateMembers(List<Member> members)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Member member : members)
        {
            Map<String, Object> data = new HashMap<>();
            data.put("id", member.getId());
            data.put("username", member.getUser().getName());
            data.put("discriminator", member.getUser().getDiscriminator());
            data.put("displayName", member.getEffectiveName());
            data.put("avatar", member.getUser().getEffectiveAvatarUrl());
            data.put("isBot", member.getUser().isBot());

            if (!member.getRoles().isEmpty()) {
                data.put("role", member.getRoles().get(0).getName());
            } else {
                data.put("role", "No role");
            }

            List<String> roles = new ArrayList<>();
            for (Role role : member.getRoles()) {
                roles.add(role.getName());
            }
            data.put("roles", roles);

            list.add(data);
        }
        return list;
    }
}