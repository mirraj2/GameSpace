package gamespace.db;

import static jasonlib.util.Functions.map;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import ez.Row;
import ez.Table;

public class AnswerDB extends GSDB {

  @Override
  protected Table getTable() {
    return new Table("answer")
        .idColumn()
        .column("user", Long.class)
        .column("question", String.class)
        .column("answer", "TEXT");
  }

  public List<Row> getForQuestions(String... questions) {
    return db.select("SELECT user, answer FROM answer WHERE "
        + "question IN (" + Joiner.on(",").join(map(questions, quote)) + ")");
  }

  public void setAnswers(long userId, Map<String, String> answers) {
    db.update("DELETE FROM answer WHERE user = ? AND "
        + "question IN (" +
        Joiner.on(",").join(map(answers.keySet(), quote)) + ")",
        userId);

    List<Row> rows = Lists.newArrayList();
    answers.forEach((question, answer) -> {
      rows.add(new Row()
          .with("user", userId)
          .with("question", question)
          .with("answer", answer));
    });
    db.insert("answer", rows);
  }

  private Function<String, String> quote = s -> '"' + s + '"';

  public Map<String, String> getAnswers(long userId) {
    Map<String, String> ret = Maps.newHashMap();
    for (Row row : db.select("SELECT * FROM answer WHERE user = ?", userId)) {
      ret.put(row.get("question"), row.get("answer"));
    }
    return ret;
  }

}
