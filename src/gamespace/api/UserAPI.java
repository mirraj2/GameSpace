package gamespace.api;

import gamespace.db.UserDB;
import gamespace.model.User;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Maps;

public class UserAPI {

  private final Map<Long, User> cache = Maps.newConcurrentMap();
  private final UserDB userDB = new UserDB();

  public User get(long id) {
    return cache.computeIfAbsent(id, key -> userDB.get(key));
  }

  public User getByEmail(String email) {
    return userDB.getByEmail(email);
  }

  public List<User> getAll() {
    return userDB.getAll();
  }

}
