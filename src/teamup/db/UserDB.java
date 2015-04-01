package teamup.db;

import java.time.LocalDateTime;
import java.util.function.Function;
import teamup.model.User;
import ez.Row;
import ez.Table;

public class UserDB extends TeamUpDB {

  @Override
  protected Table getTable() {
    return new Table("user")
        .idColumn()
        .column("join_date", LocalDateTime.class)
        .column("facebookId", Long.class)
        .column("first_name", String.class)
        .column("last_name", String.class)
        .column("gender", String.class);
  }

  public User get(long id) {
    Row row = db.selectSingleRow("SELECT * FROM user WHERE id = ?", id);
    return row == null ? null : deserializer.apply(row);
  }

  public User getByFacebookId(long facebookId) {
    Row row = db.selectSingleRow("SELECT * FROM user WHERE facebookId = ?", facebookId);
    return row == null ? null : deserializer.apply(row);
  }

  public void insert(User user) {
    user.id = db.insert("user", serializer.apply(user));
  }

  private final Function<Row, User> deserializer = row -> {
    return new User(row.getLong("id"),
        row.getLong("facebookId"),
        row.get("first_name"),
        row.get("last_name"),
        row.get("gender")
      );
    };

  private final Function<User, Row> serializer = user -> {
    return new Row()
        .with("facebookId", user.facebookId)
        .with("first_name", user.firstName)
        .with("last_name", user.lastName)
        .with("gender", user.gender)
        .with("join_date", LocalDateTime.now());
  };

}
