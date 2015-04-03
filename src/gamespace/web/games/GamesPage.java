package gamespace.web.games;

import jasonlib.Json;
import java.util.List;
import java.util.Set;
import bowser.Controller;
import bowser.template.Data;
import com.google.common.collect.Sets;
import ez.Row;
import gamespace.db.AnswerDB;
import gamespace.db.UserDB;
import gamespace.model.User;

public class GamesPage extends Controller {

  private final UserDB userDB = new UserDB();
  private final AnswerDB answerDB = new AnswerDB();

  @Override
  public void init() {
    route("GET", "/games").to("todo.html");
    route("GET", "/chat").to("todo.html");
    route("GET", "/games/*").to("games.html").data(gameData);
  }

  private List<User> getPlayersForGame(String game) {
    game = game.toLowerCase();
    
    
    Set<Long> userIds = Sets.newHashSet();
    for (Row row : answerDB.getForQuestions("boardGames", "digitalGames")) {
      String answer = row.get("answer");
      if (answer.toLowerCase().contains(game)) {
        userIds.add(row.getLong("user"));
      }
    }
    
    return userDB.get(userIds);
  }

  private final Data gameData = context -> {

    // user the original request's path so that we retain the case
    // TODO we'll normalize this in the future to an actual game
    String name = context.request.request.getPath().getSegments()[1];
    
    name = name.replace('-', ' ');

    Json game = Json.object()
        .with("name", name);

    context.put("game", game);

    List<User> players = getPlayersForGame(name);
    context.put("players", players);
  };

}
