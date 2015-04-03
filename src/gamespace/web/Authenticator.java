package gamespace.web;

import gamespace.db.SessionDB;
import gamespace.db.UserDB;
import gamespace.model.User;
import bowser.Request;
import bowser.RequestHandler;
import bowser.Response;

public class Authenticator implements RequestHandler {

  private final SessionDB sessionDB = new SessionDB();
  private final UserDB userDB = new UserDB();

  @Override
  public boolean process(Request request, Response response) {
    if (request.isStaticResource()) {
      return false;
    }

    String token = request.cookie("token");

    if (token != null) {
      Long userId = sessionDB.getUserId(token);
      if (userId != null) {
        User user = userDB.get(userId);
        request.put("user", user);
        return false;
      }
    }

    response.redirect("/login");
    return true;
  }

}
