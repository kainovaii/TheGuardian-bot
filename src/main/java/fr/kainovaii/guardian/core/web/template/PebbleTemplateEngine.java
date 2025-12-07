package fr.kainovaii.guardian.core.web.template;

import spark.ModelAndView;
import spark.TemplateEngine;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.ClasspathLoader;

import java.io.StringWriter;
import java.util.Map;

public class PebbleTemplateEngine extends TemplateEngine
{
    private final PebbleEngine engine;

    public PebbleTemplateEngine()
    {
        ClasspathLoader loader = new ClasspathLoader();
        loader.setPrefix("view");
        engine = new PebbleEngine.Builder()
                .loader(loader)
                .cacheActive(true)
                .build();
    }

    @Override
    public String render(ModelAndView modelAndView)
    {
        try {
            var template = engine.getTemplate(modelAndView.getViewName());
            var writer = new StringWriter();
            template.evaluate(writer, (Map<String, Object>) modelAndView.getModel());
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String render(String templateName, Map<String, Object> model) {
        try {
            var template = engine.getTemplate(templateName);
            var writer = new StringWriter();
            template.evaluate(writer, model);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
