package gamespace.db;

import static jasonlib.util.Functions.map;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import ez.Row;
import ez.Table;
import gamespace.model.Event;
import gamespace.model.Event.Privacy;

public class EventDB extends GSDB {

  @Override
  protected Table getTable() {
    return new Table("event")
        .idColumn()
        .column("owner", Long.class)
        .column("name", String.class)
        .column("date", LocalDateTime.class)
        .column("max_people", Integer.class)
        .column("privacy", String.class)
        .column("description", "TEXT");
  }

  public Event get(long id) {
    return deserializer.apply(db.selectSingleRow("SELECT * FROM `event` WHERE id = ?", id));
  }

  public List<Event> getAll() {
    return map(db.select("SELECT * FROM `event`"), deserializer);
  }

  public void save(Event event) {
    Row row = serializer.apply(event);
    db.insert("event", row);
    event.id = row.getLong("id");
  }

  private final Function<Event, Row> serializer = event -> {
    return new Row()
        .with("owner", event.ownerId)
        .with("name", event.name)
        .with("date", event.date)
        .with("max_people", event.maxPeople)
        .with("privacy", event.privacy)
        .with("description", event.description);
  };

  private final Function<Row, Event> deserializer = row -> {
    return new Event(row.getLong("id"),
        row.getLong("owner"),
        row.get("name"),
        row.getDateTime("date"),
        row.getInt("max_people"),
        row.getEnum("privacy", Privacy.class),
        row.get("description")
      );
  };

}
