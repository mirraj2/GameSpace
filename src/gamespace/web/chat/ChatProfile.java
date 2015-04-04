package gamespace.web.chat;

import static com.google.common.base.Preconditions.checkNotNull;
import gamespace.model.User;
import jasonlib.Json;
import org.java_websocket.WebSocket;

public class ChatProfile {

  public final WebSocket socket;
  public final User user;
  public final String room;

  public ChatProfile(WebSocket socket, User user, String room) {
    this.socket = socket;
    this.user = checkNotNull(user);
    this.room = checkNotNull(room);
  }

  public void send(Json json) {
    send(json.toString());
  }

  public void send(String s) {
    socket.send(s);
  }

}
