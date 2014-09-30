package conference.rest;

import conference.Session;
import conference.Speaker;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Service
@Path("/")
public class ConferenceService {

    //list of conference speakers available
    private List<Speaker> speakers;
    //list of conference sessions available
    private List<Session> sessions;

    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "test";
    }

    @GET
    @Path("/speakers")
    @Produces(MediaType.APPLICATION_XML)
    public List<Speaker> getSpeakers() {
        return speakers;
    }

    @GET
    @Path("/speakers/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Speaker getSpeaker(@PathParam("id") Long id) {
        for (Speaker s : speakers) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }

    @GET
    @Path("/sessions")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Session> getSessions() {
        return sessions;
    }

    @GET
    @Path("/sessions/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Session getSession(String id) {
        for (Session s : sessions) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }

    @GET
    @Path("/trusted/message")
    @Produces(MediaType.TEXT_PLAIN)
    @PreAuthorize("#oauth2.clientHasRole('ROLE_CLIENT')")
    public String getTrustedClientMessage() {
        return "Hello, Trusted Client";
    }

    @PostConstruct
    public void init() {
        loadSpeakers();
        loadSessions();
    }

    public void loadSpeakers() {
        if (speakers == null) {
            //mock all the conference speakers data
            speakers = new ArrayList<Speaker>();
            speakers.add(new Speaker(1l, "Rodrigo Silva", "Software Architect",
                    "https://oracleus.activeevents.com/published/oracleus2014/files/15133/Profile.jpg",
                    "http://twitter.com/rcandidosilva", null, "Integritas"));
            speakers.add(new Speaker(2l, "George Gastaldi", "Senior Software Engineer",
                    "https://oracleus.activeevents.com/published/oracleus2014/files/13315/me.jpeg",
                    null, null, "Red Hat, Inc."));
            speakers.add(new Speaker(3l, "Michel Graciano", "Software Architect",
                    "https://oracleus.activeevents.com/published/oracleus2014/files/13547/IMG_20120908_170401.jpg",
                    null, null, "Betha"));
            speakers.add(new Speaker(4l, "Bruno Borges", "Principal Product Manager",
                    "https://oracleus.activeevents.com/published/oracleus2014/files/8594/profile_facebook.jpg",
                    null, null, "Oracle"));
            speakers.add(new Speaker(5l, "Leonardo Zanivan", "Software Architect",
                    "https://oracleus.activeevents.com/published/oracleus2014/files/13168/foto.png",
                    null, null, "Trier"));
        }
    }

    public void loadSessions() {
        if (sessions == null) {
            //mock all the conference sessions data
            sessions = new ArrayList<Session>();
            sessions.add(new Session("CON4990", "Securing RESTful Resources with OAuth2", null));
            sessions.add(new Session("CON2122", "Tweet for Beer! Beer Tap Powered by Java Goes Internet of Things and JavaFX", null));
            sessions.add(new Session("BOF2695", "Fast-Developing CRUD-like Applications with Java EE 7", null));
            sessions.add(new Session("CON6423", "Scalable JavaScript Applications with Project Nashorn", null));

        }
    }

}
