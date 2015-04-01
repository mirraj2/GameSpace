package teamup.web.games;

import bowser.Controller;

public class GamesPage extends Controller {

  @Override
  public void init() {
    route("GET", "/games").to("games.html");
  }

}
