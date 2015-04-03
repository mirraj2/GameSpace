package gamespace.web.login;

import gamespace.FacebookAPI;
import gamespace.db.SessionDB;
import gamespace.db.UserDB;
import gamespace.model.User;
import jasonlib.Json;
import jasonlib.Log;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import bowser.Controller;
import bowser.Handler;

public class LoginPage extends Controller {

  private final UserDB userDB = new UserDB();
  private final SessionDB sessionDB = new SessionDB();

  @Override
  public void init() {
    route("GET", "/login").to("login.html");
    route("POST", "/login").to(login);
  }

  private final Handler login = (request, response) -> {
    FacebookAPI api = new FacebookAPI(request.param("accessToken"));

    Json me = api.getMe();
    Log.debug(me);

    long facebookId = me.getLong("id");

    User user = userDB.getByFacebookId(facebookId);

    if (user == null) {
      String picUrl = api.getProfilePictureURL();

      // id, email, gender, first_name, last_name, name, timezone, birthday, verified
      LocalDate birthday = null;
      if (me.get("birthday") != null) {
        birthday = LocalDate.parse(me.get("birthday"), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
      }
      user = new User(null, facebookId, me.get("first_name"), me.get("last_name"), me.get("gender"), birthday, picUrl);
      userDB.insert(user);
    }

    String token = sessionDB.newSession(user.id);

    response.cookie("token", token, 14, TimeUnit.DAYS);
  };

}
