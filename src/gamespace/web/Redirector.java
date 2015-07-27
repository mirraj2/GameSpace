package gamespace.web;

import gamespace.model.User;
import java.util.concurrent.TimeUnit;
import bowser.Request;
import bowser.RequestHandler;
import bowser.Response;

public class Redirector implements RequestHandler {

  @Override
  public boolean process(Request request, Response response) {
    if (request.isStaticResource()) {
      return false;
    }

    User user = request.get("user");

    if (user != null) {
      return false;
    }

    String path = request.path;
    if (!request.getQuery().isEmpty()) {
      path += "?" + request.getQuery();
    }
    response.cookie("redirect", path, 60, TimeUnit.MINUTES);
    response.redirect("/login");
    return true;
  }

}
