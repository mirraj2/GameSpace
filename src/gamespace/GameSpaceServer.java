package gamespace;

import gamespace.api.UserAPI;
import gamespace.web.Authenticator;
import gamespace.web.chat.ChatController;
import gamespace.web.chat.ChatServer;
import gamespace.web.event.EventPage;
import gamespace.web.events.EventsPage;
import gamespace.web.games.GamesPage;
import gamespace.web.login.LoginPage;
import gamespace.web.notfound.PageNotFoundHandler;
import gamespace.web.profile.ProfilePage;
import gamespace.web.search.SearchController;
import jasonlib.Config;
import jasonlib.Log;
import bowser.WebServer;

public class GameSpaceServer {

  private final Config config = Config.load("gamespace");

  private void run() {
    boolean devMode = config.getBoolean("dev_mode", false);

    String domain = devMode ? "localhost" : "gamespace.us";
    int httpPort = config.getInt("port", devMode ? 8082 : 80);
    int chatPort = config.getInt("chat_port", 39142);

    Log.info("Starting web server on port " + httpPort);

    UserAPI userAPI = new UserAPI();

    WebServer server = new WebServer("GameSpace.us", httpPort, devMode);
    server.shortcut("jquery", "//cdnjs.cloudflare.com/ajax/libs/jquery/2.1.1/jquery.min.js")
        .shortcut("bootstrap", "//maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css")
        .shortcut("bootstrap", "//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.2/js/bootstrap.min.js")
        .shortcut("cookie", "//cdnjs.cloudflare.com/ajax/libs/jquery-cookie/1.4.1/jquery.cookie.min.js")
        .shortcut("buzz", "//cdnjs.cloudflare.com/ajax/libs/buzz/1.1.8/buzz.min.js")
        .shortcut("dateformat", "//cdnjs.cloudflare.com/ajax/libs/jquery-dateFormat/1.0/jquery.dateFormat.min.js")
        .shortcut("typeahead", "https://cdnjs.cloudflare.com/ajax/libs/typeahead.js/0.10.4/typeahead.bundle.min.js")
        .controller(new LoginPage())
        .add(new Authenticator(userAPI))
        .controller(new ChatController())
        .controller(new ProfilePage())
        .controller(new GamesPage())
        .controller(new EventsPage())
        .controller(new EventPage(userAPI, domain, chatPort))
        .controller(new SearchController(userAPI))
        .add(new PageNotFoundHandler(server.getResourceLoader()))
        .start();
    Log.info("Server started.");

    ChatServer chatServer = new ChatServer(chatPort, userAPI);
    chatServer.start();
    Log.info("Chat server started.");
  }

  public static void main(String[] args) {
    new GameSpaceServer().run();
  }

}
