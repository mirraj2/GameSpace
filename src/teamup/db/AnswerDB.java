package teamup.db;

import static com.google.common.collect.Iterables.transform;
import java.util.List;
import java.util.Map;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import ez.Row;
import ez.Table;

public class AnswerDB extends TeamUpDB {

  @Override
  protected Table getTable() {
    return new Table("answer")
        .idColumn()
        .column("user", Long.class)
        .column("question", String.class)
        .column("answer", "TEXT");
  }

  public void setAnswers(long userId, Map<String, String> answers) {
    db.update("DELETE FROM answer WHERE user = ? AND "
        + "question IN (" +
        Joiner.on(",").join(transform(answers.keySet(), answer -> '"' + answer + '"')) + ")",
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

  public Map<String, String> getAnswers(long userId) {
    Map<String, String> ret = Maps.newHashMap();
    for (Row row : db.select("SELECT * FROM answer WHERE user = ?", userId)) {
      ret.put(row.get("question"), row.get("answer"));
    }
    return ret;
  }

}
