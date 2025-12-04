package fr.kainovaii.guardian.web.core;

import spark.Request;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.javalite.activejdbc.Model;

public class WebRenderer
{
    private static final Map<String, Object> globalVars = new HashMap<>();
    public static void setGlobal(String key, Object value) { globalVars.put(key, value); }
    public static Object getGlobal(String key) { return globalVars.get(key); }
    public static void removeGlobal(String key) { globalVars.remove(key); }
    public static void clearGlobals() { globalVars.clear(); }

    public static String render(Request req, String viewName, Map<String, Object> variables)
    {
        try {
            Map<String, Object> mergedVars = new HashMap<>(globalVars);
            if (variables != null) mergedVars.putAll(variables);

            Map<String, String> flashes = req != null ? BaseController.collectFlashes(req) : new HashMap<>();
            mergedVars.put("flash", flashes);

            String viewContent = loadResource("view/" + viewName);
            String layoutContent = loadResource("view/layout.html");
            String content = layoutContent.replace("{{content}}", viewContent);

            content = processIncludes(content);
            content = processConditionals(content, mergedVars);
            content = processLoops(content, mergedVars);
            content = replaceVariables(content, mergedVars);

            return content;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du rendu de la vue : " + viewName, e);
        }
    }

    private static String loadResource(String path) throws IOException
    {
        try (InputStream in = WebRenderer.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) throw new IOException("Fichier introuvable : " + path);
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static String processIncludes(String content)
    {
        Pattern pattern = Pattern.compile("\\{\\{include ['\"](.*?)['\"]}}");
        Matcher matcher = pattern.matcher(content);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String file = matcher.group(1);
            try {
                String include = loadResource("view/" + file);
                matcher.appendReplacement(sb, Matcher.quoteReplacement(include));
            } catch (IOException e) {
                matcher.appendReplacement(sb, "<!-- include introuvable: " + file + " -->");
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String processConditionals(String content, Map<String, Object> vars)
    {
        Pattern pattern = Pattern.compile("\\{\\{if ([^}]*)}}([\\s\\S]*?)(?:\\{\\{else}}([\\s\\S]*?))?\\{\\{endif}}");
        Matcher matcher = pattern.matcher(content);
        StringBuffer sb = new StringBuffer();
        boolean hasMatch = false;

        while (matcher.find()) {
            hasMatch = true;
            String condition = matcher.group(1).trim();
            String ifBlock = matcher.group(2);
            String elseBlock = matcher.group(3);

            boolean truthy = evaluateCondition(condition, vars);
            String replacement = truthy ? ifBlock : (elseBlock != null ? elseBlock : "");

            replacement = processIncludes(replacement);
            replacement = processLoops(replacement, vars);
            replacement = replaceVariables(replacement, vars);

            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(sb);
        return hasMatch ? processConditionals(sb.toString(), vars) : sb.toString();
    }

    private static boolean evaluateCondition(String condition, Map<String, Object> vars)
    {
        condition = condition.trim();
        try {
            if (condition.contains("==")) {
                String[] parts = condition.split("==", 2);
                Object left = resolveValue(parts[0].trim(), vars);
                String right = parts[1].trim().replaceAll("['\"]", "");
                return left != null && left.toString().equalsIgnoreCase(right);
            }
            if (condition.contains("!=")) {
                String[] parts = condition.split("!=", 2);
                Object left = resolveValue(parts[0].trim(), vars);
                String right = parts[1].trim().replaceAll("['\"]", "");
                return left == null || !left.toString().equalsIgnoreCase(right);
            }
            Object value = resolveValue(condition, vars);
            if (value instanceof Boolean b) return b;
            return value != null && !value.toString().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private static String processLoops(String content, Map<String, Object> vars)
    {
        Pattern pattern = Pattern.compile("\\{\\{for (\\w+) in ([^}]+)}}([\\s\\S]*?)\\{\\{endfor}}");
        Matcher matcher = pattern.matcher(content);
        StringBuffer sb = new StringBuffer();
        boolean found = false;

        while (matcher.find()) {
            found = true;
            String varName = matcher.group(1);
            String listExpr = matcher.group(2).trim();
            String block = matcher.group(3);

            Object listObj = resolveValue(listExpr, vars);
            StringBuilder rendered = new StringBuilder();

            if (listObj instanceof Collection<?> col) {
                for (Object item : col) {
                    Map<String, Object> localVars = new HashMap<>(vars);
                    localVars.put(varName, item);

                    String temp = block;
                    temp = processIncludes(temp);
                    temp = processConditionals(temp, localVars);
                    temp = processLoops(temp, localVars);
                    temp = replaceVariables(temp, localVars);

                    rendered.append(temp);
                }
            }

            matcher.appendReplacement(sb, Matcher.quoteReplacement(rendered.toString()));
        }

        matcher.appendTail(sb);
        return found ? processLoops(sb.toString(), vars) : sb.toString();
    }

    private static String replaceVariables(String content, Map<String, Object> vars)
    {
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)}");
        Matcher matcher = pattern.matcher(content);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String path = matcher.group(1).trim();
            Object value = resolveValue(path, vars);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value != null ? value.toString() : ""));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private static Object resolveValue(String path, Map<String, Object> vars)
    {
        String[] parts = path.split("\\.");
        Object current = vars.get(parts[0]);

        for (int i = 1; i < parts.length && current != null; i++) {
            String part = parts[i];

            if (current instanceof Map<?, ?> map) {
                current = map.get(part);
            } else if (current instanceof Model model) {
                current = model.get(part); // ActiveJDBC : accès via get(String column)
            } else {
                try {
                    var field = current.getClass().getDeclaredField(part);
                    field.setAccessible(true);
                    current = field.get(current);
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return current;
    }
}
