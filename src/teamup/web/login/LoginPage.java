package teamup.web.login;

import jasonlib.Json;
import java.util.concurrent.TimeUnit;
import teamup.FacebookAPI;
import teamup.db.SessionDB;
import teamup.db.UserDB;
import teamup.model.User;
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

    long facebookId = me.getLong("id");

    User user = userDB.getByFacebookId(facebookId);

    if (user == null) {
      // id, email, gender, first_name, last_name, name, timezone, verified
      user = new User(null, facebookId, me.get("first_name"), me.get("last_name"), me.get("gender"));
      userDB.insert(user);
    }

    String token = sessionDB.newSession(user.id);

    response.cookie("token", token, 14, TimeUnit.DAYS);
  };

}
