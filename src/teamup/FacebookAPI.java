package teamup;

import jasonlib.IO;
import jasonlib.Json;

public class FacebookAPI {

  private final String accessToken;

  public FacebookAPI(String accessToken){
    this.accessToken = accessToken;
  }
  
  public Json getMe(){
    return get("/me");
  }
  
  private Json get(String route){
    String url = "https://graph.facebook.com/v2.3"+route+"?access_token="+accessToken;
    return IO.fromURL(url).toJson();
  }
  
}
