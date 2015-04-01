package teamup.web.profile;

import java.util.List;
import java.util.Map;
import teamup.db.AnswerDB;
import teamup.model.User;
import bowser.Controller;
import bowser.Handler;
import bowser.template.Data;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class ProfilePage extends Controller {

  private final AnswerDB answerDB = new AnswerDB();

  @Override
  public void init() {
    route("GET", "/").to("profile.html").data(profileData);
    route("GET", "/edit").to("editprofile.html").data(data);
    route("POST", "/answer").to(saveAnswer);
  }

  private final Handler saveAnswer = (request, response) -> {
    User user = request.get("user");
    answerDB.setAnswers(user.id, ImmutableMap.of(request.param("question"), request.param("answer")));
  };

  private final Data profileData = context -> {
    User user = context.get("user");
    Map<String, String> answers = answerDB.getAnswers(user.id);
    answers.forEach((question, answer) -> {
      if (question.equals("boardGames") || question.equals("digitalGames")) {
        answer = arrayify(answer);
      }
      context.put(question, answer);
    });
  };

  private final Data data = context -> {
    User user = context.get("user");
    Map<String, String> answers = answerDB.getAnswers(user.id);
    answers.forEach((question, answer) -> {
      context.put(question, answer);
    });
  };

  private String arrayify(String answer) {
    List<String> m = Lists.newArrayList();
    for (String s : Splitter.on("\n").trimResults().omitEmptyStrings().split(answer)) {
      m.add('"' + s + '"');
    }
    return m.toString();
  }

}
