package fr.kainovaii.guardian.core.web.template;

import java.util.HashMap;
import java.util.Map;

public class TemplateManager
{
    private static final PebbleTemplateEngine engine = new PebbleTemplateEngine();
    private static final Map<String, Object> globals = new HashMap<>();

    public static PebbleTemplateEngine get() { return engine; }

    public static Map<String, Object> getGlobals() { return globals; }

    public static void setGlobal(String key, Object value) { globals.put(key, value); }
}
