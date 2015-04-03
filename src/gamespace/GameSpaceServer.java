package gamespace;

import gamespace.web.Authenticator;
import gamespace.web.games.GamesPage;
import gamespace.web.login.LoginPage;
import gamespace.web.profile.ProfilePage;
import jasonlib.Config;
import jasonlib.Log;
import bowser.WebServer;

public class GameSpaceServer {

  private final Config config = Config.load("gamespace");

  private void run() {
    boolean devMode = config.getBoolean("dev_mode", false);

    int httpPort = config.getInt("port", devMode ? 8082 : 8082);

    Log.info("Starting web server on port " + httpPort);

    new WebServer("GameSpace.us", httpPort, devMode)
        .shortcut("jquery", "//cdnjs.cloudflare.com/ajax/libs/jquery/2.1.1/jquery.min.js")
        .shortcut("bootstrap", "//maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css")
        .shortcut("bootstrap", "//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.2/js/bootstrap.min.js")
        .shortcut("cookie", "//cdnjs.cloudflare.com/ajax/libs/jquery-cookie/1.4.1/jquery.cookie.min.js")
        .shortcut("buzz", "//cdnjs.cloudflare.com/ajax/libs/buzz/1.1.8/buzz.min.js")
        .controller(new LoginPage())
        .add(new Authenticator())
        .controller(new ProfilePage())
        .controller(new GamesPage())
        .start();

    Log.info("Server started.");
  }

  public static void main(String[] args) {
    new GameSpaceServer().run();
  }

}
