package gamespace.web;

import gamespace.api.UserAPI;
import gamespace.db.SessionDB;
import gamespace.model.User;
import bowser.Request;
import bowser.RequestHandler;
import bowser.Response;

public class Authenticator implements RequestHandler {

  private final SessionDB sessionDB = new SessionDB();
  private final UserAPI userAPI;

  public Authenticator(UserAPI userAPI) {
    this.userAPI = userAPI;
  }

  @Override
  public boolean process(Request request, Response response) {
    if (request.isStaticResource()) {
      return false;
    }

    String token = request.cookie("token");

    if (token != null) {
      Long userId = sessionDB.getUserId(token);
      if (userId != null) {
        User user = userAPI.get(userId);
        request.put("user", user);
        return false;
      }
    }

    response.redirect("/login");
    return true;
  }

}
