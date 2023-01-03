package reactive;

import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;

@Path("fruits")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class FruitMutinyResource {

    @Inject
    SessionFactory sf;

    @GET
    public Uni<List<Fruit>> get() {
        return sf.withTransaction((s,t) -> s
                .createNamedQuery("Fruits.findAll", Fruit.class)
                .getResultList()
        );
    }

    @GET
    @Path("{id}")
    public Uni<Fruit> getSingle(Integer id) {
        return sf.withTransaction((s,t) -> s.find(Fruit.class, id));
    }

    @POST
    public Uni<Response> create(Fruit fruit) {
        if (fruit == null || fruit.getId() != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }

        return sf.withTransaction((s,t) -> s.persist(fruit))
                .replaceWith(Response.ok(fruit).status(CREATED)::build);
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(Integer id, Fruit fruit) {
        if (fruit == null || fruit.getName() == null) {
            throw new WebApplicationException("Fruit name was not set on request.", 422);
        }

        return sf.withTransaction((s,t) -> s.find(Fruit.class, id)
                .onItem().ifNull().failWith(new WebApplicationException("Fruit missing from database.", NOT_FOUND))
                // If entity exists then update it
                .invoke(entity -> entity.setName(fruit.getName())))
                .map(entity -> Response.ok(entity).build());
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(Integer id) {
        return sf.withTransaction((s,t) -> s.find(Fruit.class, id)
                .onItem().ifNull().failWith(new WebApplicationException("Fruit missing from database.", NOT_FOUND))
                // If entity exists then delete it
                .call(s::remove))
                .replaceWith(Response.ok().status(NO_CONTENT)::build);    }

}
