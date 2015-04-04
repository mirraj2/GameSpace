package gamespace.web.events;

import static java.lang.Integer.parseInt;
import gamespace.db.EventDB;
import gamespace.model.Event;
import gamespace.model.Event.Privacy;
import gamespace.model.User;
import jasonlib.util.Utils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import bowser.Controller;
import bowser.Handler;
import bowser.template.Data;

public class EventsPage extends Controller {

  private final EventDB eventDB = new EventDB();

  @Override
  public void init() {
    route("GET", "/events").to("events.html").data(eventsData);
    route("GET", "/events/new").to("new-event.html").data(newEvent);
    route("POST", "/events/new").to(createEvent);
  }

  private final Data eventsData = context -> {
    List<Event> events = eventDB.getAll();

    context.put("events", events);
  };

  private final Data newEvent = context -> {
    context.put("date", LocalDate.now().toString());
    context.put("privacies", Privacy.values());
  };

  private final Handler createEvent = (request, response) -> {
    User user = request.get("user");

    String name = request.param("name");

    String datetime = request.param("date") + "T" + request.param("time");
    LocalDateTime date = LocalDateTime.parse(datetime);
    int timezoneOffset = parseInt(request.param("timezoneOffset"));
    date = date.plusMinutes(timezoneOffset);

    int numPeople = parseInt(request.param("num_people"));
    Privacy privacy = Utils.parseEnum(request.param("privacy"), Privacy.class);
    String description = request.param("description");

    Event event = new Event(null, user.id, name, date, numPeople, privacy, description);
    eventDB.save(event);

    response.write("/events/" + event.id);
  };

}
