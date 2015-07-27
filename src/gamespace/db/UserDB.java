package gamespace.db;

import static jasonlib.util.Functions.map;
import jasonlib.Log;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import com.google.common.base.Joiner;
import ez.Row;
import ez.Table;
import gamespace.model.User;

public class UserDB extends GSDB {

  @Override
  protected Table getTable() {
    return new Table("user")
        .idColumn()
        .column("join_date", LocalDateTime.class)
        .column("facebookId", Long.class)
        .column("email", String.class)
        .column("first_name", String.class)
        .column("last_name", String.class)
        .column("gender", String.class)
        .column("birthday", LocalDate.class)
        .varchar("pic_url", 1024);
  }

  public User get(long id) {
    Row row = db.selectSingleRow("SELECT * FROM user WHERE id = ?", id);
    return row == null ? null : deserializer.apply(row);
  }

  public User getByEmail(String email) {
    Row row = db.selectSingleRow("SELECT * FROM user WHERE email = ?", email);
    return row == null ? null : deserializer.apply(row);
  }

  public List<User> getAll() {
    List<Row> rows = db.select("SELECT * FROM user");
    return map(rows, deserializer);
  }

  public List<User> get(Iterable<Long> ids) {
    List<Row> rows = db.select("SELECT * FROM user WHERE id IN (" + Joiner.on(",").join(ids) + ")");
    return map(rows, deserializer);
  }

  public User getByFacebookId(long facebookId) {
    Row row = db.selectSingleRow("SELECT * FROM user WHERE facebookId = ?", facebookId);
    return row == null ? null : deserializer.apply(row);
  }

  public void updateEmail(long userId, String email) {
    Log.debug("Updating " + userId + "'s email to " + email);
    db.update("UPDATE user SET email = ? WHERE id = ?", email, userId);
  }

  public void insert(User user) {
    user.id = db.insert("user", serializer.apply(user));
  }

  private final Function<Row, User> deserializer = row -> {
    return new User(row.getLong("id"),
        row.getLong("facebookId"),
        row.get("email"),
        row.get("first_name"),
        row.get("last_name"),
        row.get("gender"),
        row.getDate("birthday"),
        row.get("pic_url")
      );
    };

  private final Function<User, Row> serializer = user -> {
    return new Row()
        .with("facebookId", user.facebookId)
        .with("email", user.email)
        .with("first_name", user.firstName)
        .with("last_name", user.lastName)
        .with("gender", user.gender)
        .with("join_date", LocalDateTime.now())
        .with("birthday", user.birthday)
        .with("pic_url", user.picUrl)
      ;
    };

}
