package gamespace.web.event;

import static java.lang.Long.parseLong;
import gamespace.api.UserAPI;
import gamespace.db.EventDB;
import gamespace.model.Event;
import gamespace.model.User;
import jasonlib.Json;
import java.util.List;
import bowser.Controller;
import bowser.Handler;
import bowser.template.Data;
import com.google.common.collect.Lists;

public class EventPage extends Controller {

  private String chatIP;
  private int chatPort;

  private UserAPI userAPI;
  private EventDB eventDB = new EventDB();

  public EventPage(UserAPI userAPI, String chatIP, int chatPort) {
    this.chatIP = chatIP;
    this.chatPort = chatPort;
  }

  @Override
  public void init() {
    route("GET", "/events/*").to("event.html").data(eventData);
    route("POST", "/events/*/invite").to(invite);
  }

  private final Handler invite = (request, response) -> {
    String email = request.param("email");
    if (email == null) {
      long id = parseLong(request.param("id"));
      User target = userAPI.get(id);
    } else {
      // make sure it's actually an email

      // see if this user already exists
    }
  };

  private final Data eventData = context -> {
    long eventId = context.request.getInt(1);
    Event event = eventDB.get(eventId);

    User owner = userAPI.get(event.ownerId);

    List<Json> people = Lists.newArrayList();
    people.add(Json.object().with("id", owner.id).with("name", owner.getFullName()).with("status", "Host"));
    context.put("people", people);

    context.put("event", event);

    context.put("chatIP", chatIP);
    context.put("chatPort", chatPort);
  };

}
