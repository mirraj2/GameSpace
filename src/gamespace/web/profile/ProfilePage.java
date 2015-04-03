package gamespace.web.profile;

import gamespace.db.AnswerDB;
import gamespace.db.UserDB;
import gamespace.model.User;
import java.util.List;
import java.util.Map;
import bowser.Controller;
import bowser.Handler;
import bowser.template.Context;
import bowser.template.Data;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class ProfilePage extends Controller {

  private final UserDB userDB = new UserDB();
  private final AnswerDB answerDB = new AnswerDB();

  @Override
  public void init() {
    route("GET", "/").to("profile.html").data(profileData);
    route("GET", "/players/*").to("profile.html").data(playerData);
    route("POST", "/answer").to(saveAnswer);
  }

  private final Handler saveAnswer = (request, response) -> {
    User user = request.get("user");
    answerDB.setAnswers(user.id, ImmutableMap.of(request.param("question"), request.param("answer")));
  };

  private final Data playerData = context -> {
    User user = context.get("user");
    User player = userDB.get(context.request.getInt(1));
    fillContext(user, player, context);
  };

  private final Data profileData = context -> {
    User user = context.get("user");
    fillContext(user, user, context);
  };

  private void fillContext(User user, User player, Context context) {
    Map<String, String> answers = answerDB.getAnswers(player.id);
    context.put("boardGames", arrayify(answers.getOrDefault("boardGames", "")));
    context.put("digitalGames", arrayify(answers.getOrDefault("digitalGames", "")));
    context.put("aboutMe", answers.getOrDefault("aboutMe", "").replace("\n", "\\n"));
    context.put("isMe", user.equals(player));
  }

  private String arrayify(String answer) {
    List<String> m = Lists.newArrayList();
    for (String s : Splitter.on("\n").trimResults().omitEmptyStrings().split(answer)) {
      m.add('"' + s + '"');
    }
    return Joiner.on(",").join(m);
  }

}
