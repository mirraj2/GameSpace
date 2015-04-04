package gamespace.web.search;

import gamespace.api.UserAPI;
import gamespace.model.User;
import jasonlib.Json;
import java.util.List;
import bowser.Controller;
import bowser.Handler;

public class SearchController extends Controller {

  private UserAPI userAPI;

  public SearchController(UserAPI userAPI) {
    this.userAPI = userAPI;
  }

  @Override
  public void init() {
    route("GET", "/search/users/*").to(searchUsers);
  }

  private final Handler searchUsers = (request, response) -> {
    List<User> users = userAPI.getAll();

    String query = request.getSegment(2).toLowerCase();

    Json json = Json.array();
    for (User user : users) {
      if (query.equals(user.email) || user.getFullName().toLowerCase().contains(query)) {
        json.add(Json.object()
            .with("id", user.id)
            .with("value", user.getFullName()));
      }
    }

    response.write(json.toString());
  };

}
