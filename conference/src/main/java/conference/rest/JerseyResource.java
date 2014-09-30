package conference.rest;

import conference.Session;
import conference.Speaker;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("hello")
public class JerseyResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getHello() {
        return "hello Jersey";
    }

}