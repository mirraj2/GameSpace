package gamespace.web.games;

import static jasonlib.util.Functions.map;
import ez.Row;
import gamespace.api.GameUtils;
import gamespace.db.AnswerDB;
import gamespace.db.UserDB;
import gamespace.model.User;
import jasonlib.Json;
import java.util.List;
import java.util.Map;
import java.util.Set;
import bowser.Controller;
import bowser.template.Data;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

public class GamesPage extends Controller {

  private final UserDB userDB = new UserDB();
  private final AnswerDB answerDB = new AnswerDB();

  @Override
  public void init() {
    route("GET", "/games").to("games.html").data(topGames);
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

  private final Data topGames = context -> {
    for (String type : ImmutableList.of("boardGames", "digitalGames")) {
      Map<String, Integer> counts = Maps.newHashMap();
      for (Row row : answerDB.getForQuestions(type)) {
        String answer = row.get("answer").toLowerCase();
        for (String s : Splitter.on('\n').omitEmptyStrings().split(answer)) {
          Integer i = counts.getOrDefault(s, 0);
          counts.put(s, i + 1);
        }
      }

      List<String> games = Ordering.from((a, b) -> counts.get(b) - counts.get(a)).immutableSortedCopy(counts.keySet());
      games = games.subList(0, Math.min(games.size(), 10));
      games = map(games, GameUtils.prettify);
      context.put(type, games);
    }
  };

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
