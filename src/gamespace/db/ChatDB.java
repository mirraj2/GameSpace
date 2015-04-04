package gamespace.db;

import java.util.List;
import ez.Row;
import ez.Table;

public class ChatDB extends GSDB {

  @Override
  protected Table getTable() {
    return new Table("chat")
        .idColumn()
        .varchar("room", 16)
        .column("timestamp", Long.class)
        .column("sender", Long.class)
        .varchar("message", 8000)
        .index("room");
  }

  public void addMessage(String room, long senderId, String message) {
    db.insert("chat", new Row()
        .with("room", room)
        .with("timestamp", System.currentTimeMillis())
        .with("sender", senderId)
        .with("message", message)
        );
  }

  public List<Row> getMessages(String room) {
    return db.select("SELECT * FROM chat WHERE room = ?", room);
  }

}
