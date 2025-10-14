package cc.srv;

import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

public class MainApplication extends Application {

    private final Set<Object> singletons = new HashSet<>();
    private final Set<Class<?>> resources = new HashSet<>();

    public MainApplication() {
        System.out.println("main app");
        resources.add(ControlResource.class);
        resources.add(UserResource.class);
        resources.add(LegoSetResource.class);
        singletons.add(new MediaResource());
    }

    @Override
    public Set<Class<?>> getClasses() {
        return resources;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
