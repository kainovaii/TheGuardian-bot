package fr.kainovaii.guardian.web.core;

import org.reflections.Reflections;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ControllerLoader
{
    public static void loadControllers()
    {
        Reflections reflections = new Reflections("fr.kainovaii.guardian.web.controllers");
        Set<Class<?>> controllerClasses = reflections.getTypesAnnotatedWith(Controller.class);
        controllerClasses.stream()
            .map(cls -> {
                try {
                    return cls.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    System.err.println("Impossible d’instancier le controller : " + cls.getName());
                    e.printStackTrace();
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
