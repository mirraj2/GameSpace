package teamup.db;

import java.util.UUID;
import ez.Row;
import ez.Table;

public class SessionDB extends TeamUpDB {

  @Override
  protected Table getTable() {
    return new Table("session")
        .primary("token", UUID.class)
        .column("userId", Long.class);
  }

  public String newSession(long userId) {
    UUID token = UUID.randomUUID();

    db.insert("session", new Row().with("token", token).with("userId", userId));

    return token.toString();
  }

  public Long getUserId(String token) {
    Row row = db.selectSingleRow("SELECT userId FROM session WHERE token = ?", token);
    return row == null ? null : row.getLong("userId");
  }

}
