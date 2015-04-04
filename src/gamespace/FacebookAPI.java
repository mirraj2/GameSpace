package gamespace;

import jasonlib.IO;
import jasonlib.Json;
import jasonlib.Log;
import java.util.List;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class FacebookAPI {

  private final String accessToken;

  public FacebookAPI(String accessToken){
    this.accessToken = accessToken;
  }
  
  public Json getMe(){
    return get("/me");
  }
  
  public String getProfilePictureURL() {
    Json json = get("/me/picture", ImmutableMap.of("type", "large", "fields", "url", "redirect", "false"));
    return json.getJson("data").get("url");
  }

  public List<Json> getFriends() {
    List<Json> ret = Lists.newArrayList();
    Json data = get("/me/friends");
    Log.debug(data);
    return ret;
  }

  private Json get(String route) {
    return get(route, ImmutableMap.of());
  }

  private Json get(String route, Map<String, String> params) {
    params = Maps.newHashMap(params);
    params.put("access_token", accessToken);

    StringBuilder sb = new StringBuilder("https://graph.facebook.com/v2.3");
    sb.append(route).append("?");
    params.forEach((key, value) -> {
      sb.append(key).append('=').append(value).append("&");
    });
    sb.setLength(sb.length() - 1);

    String url = sb.toString();

    Log.debug(url);

    return IO.fromURL(url).toJson();
  }
  
  public static void main(String[] args) {
    FacebookAPI api = new FacebookAPI(
        "CAACEdEose0cBACapS76btSm2AIbelpHKVfpWdXsPHgN72e133eEzFjl9ycprNCz0AvsHmGz0RNtZC4Sz5V9ZAAAyv9CLPyzabrm3p24"
            + "ZC5PmrsymbgMfOIVpBl8dM8G1dCPNZAwknqMRMLaWqrCogZCYIpOn9DZAj7WuZCkBvLHZAfLb5aTiueZCqQZCZAM6WNQ3Wh0zOQSXIS"
            + "LZAbgyLuhZBdzOA16zq6w8pUW8ZD");

    Log.debug(api.getFriends());
  }

}
