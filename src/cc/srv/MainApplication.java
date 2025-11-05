package cc.srv;

import java.util.HashSet;
import java.util.Set;

import cc.srv.db.CosmosConnection;
import cc.srv.resources.AuctionResource;
import cc.srv.resources.AuthResource;
import cc.srv.resources.LegoSetResource;
import cc.srv.resources.UserContFunctions;
import cc.utils.EnvLoader;
import jakarta.ws.rs.core.Application;

public class MainApplication extends Application {

    private final Set<Object> singletons = new HashSet<>();
    private final Set<Class<?>> resources = new HashSet<>();

    public MainApplication() {
        System.out.println("main app");
        resources.add(ControlResource.class);
        resources.add(UserResource.class);
        resources.add(LegoSetResource.class);
        resources.add(CosmosConnection.class);
        resources.add(UserContFunctions.class);
        resources.add(AuctionResource.class);
        resources.add(AuthResource.class);
        singletons.add(new MediaResource());

        //initializing .env variables
        EnvLoader.envInit();
        CosmosConnection.dbInit();
        
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
