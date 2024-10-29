package org.acme;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        ObservationRegistry registry = ObservationRegistry.create();
        Observation hello = Observation.start("hello", registry);
        return "Hello from RESTEasy Reactive";
    }
}
