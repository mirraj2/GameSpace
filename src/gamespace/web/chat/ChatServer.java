package gamespace.web.chat;

import ez.Row;
import gamespace.api.UserAPI;
import gamespace.db.ChatDB;
import gamespace.db.SessionDB;
import gamespace.model.User;
import jasonlib.Json;
import jasonlib.Log;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class ChatServer extends WebSocketServer {

  private final Map<WebSocket, ChatProfile> connections = Maps.newConcurrentMap();
  private final Multimap<String, ChatProfile> roomPeople = LinkedListMultimap.create();

  private final SessionDB sessionDB = new SessionDB();
  private final ChatDB chatDB = new ChatDB();
  private final UserAPI userAPI;

  public ChatServer(int port, UserAPI userAPI) {
    super(new InetSocketAddress(port));

    this.userAPI = userAPI;
  }

  @Override
  public void onMessage(WebSocket conn, String message) {
    ChatProfile profile = connections.get(conn);

    Json json = new Json(message);

    String command = json.get("command");

    if (command.equals("login")) {
      String token = json.get("token");
      Long userId = sessionDB.getUserId(token);
      User user = userAPI.get(userId);
      connections.put(conn, profile = new ChatProfile(conn, user, json.get("room")));
      synchronized (roomPeople) {
        roomPeople.put(profile.room, profile);
      }
      sendAllMessages(profile);
    } else if (command.equals("chat")) {
      json.with("from", profile.user.getFullName());
      json.with("fromId", profile.user.id);
      Log.debug(profile.user.getFullName() + "[" + profile.room + "]: " + json.get("text"));
      sendToRoom(profile.room, json);
      chatDB.addMessage(profile.room, profile.user.id, json.get("text"));
    } else {
      Log.warn("Unknown command: " + command);
      Log.warn(json);
    }
  }

  private void sendToRoom(String room, Json json) {
    String s = json.toString();

    List<ChatProfile> people;
    synchronized (roomPeople) {
      people = ImmutableList.copyOf(roomPeople.get(room));
    }

    for (ChatProfile person : people) {
      person.send(s);
    }
  }

  private void sendAllMessages(ChatProfile profile) {
    Json array = Json.array();
    for (Row row : chatDB.getMessages(profile.room)) {
      long senderId = row.getLong("sender");
      String message = row.get("message");
      User user = userAPI.get(senderId);

      array.add(Json.object()
          .with("command", "chat")
          .with("from", user.getFullName())
          .with("fromId", user.id)
          .with("text", message)
          );
    }
    profile.send(array);
  }

  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
  }

  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    ChatProfile profile = connections.remove(conn);
    synchronized (roomPeople) {
      roomPeople.remove(profile.room, profile);
    }
  }

  @Override
  public void onError(WebSocket conn, Exception e) {
    e.printStackTrace();
  }

}
