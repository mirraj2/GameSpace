package gamespace.api;

import jasonlib.util.Utils;
import java.util.Set;
import java.util.function.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;

public class GameUtils {

  private static final Set<String> articles = ImmutableSet.copyOf(
      Splitter.on(' ').split("the of a an"));

  public static Function<String, String> prettify = game -> {
    StringBuilder sb = new StringBuilder();

    for (String s : Splitter.on(' ').split(game)) {
      if (articles.contains(s)) {
        sb.append(s);
      } else {
        sb.append(Utils.capitalize(s));
      }
      sb.append(' ');
    }

    sb.deleteCharAt(sb.length() - 1);

    return sb.toString();
  };

}
