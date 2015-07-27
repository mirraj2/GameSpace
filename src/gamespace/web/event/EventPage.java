package gamespace.web.event;

import gamespace.api.UserAPI;
import gamespace.db.EventDB;
import gamespace.db.InviteDB;
import gamespace.model.Event;
import gamespace.model.Event.Privacy;
import gamespace.model.Invite;
import gamespace.model.Invite.Status;
import gamespace.model.User;
import jasonlib.Json;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import bowser.Controller;
import bowser.template.Data;
import com.google.common.collect.Lists;

public class EventPage extends Controller {

  private UserAPI userAPI;
  private String chatIP;
  private int chatPort;

  private EventDB eventDB = new EventDB();
  private InviteDB inviteDB = new InviteDB();

  public EventPage(UserAPI userAPI, String chatIP, int chatPort) {
    this.userAPI = userAPI;
    this.chatIP = chatIP;
    this.chatPort = chatPort;
  }

  @Override
  public void init() {
    route("GET", "/events/*").to("event.html").data(eventData);
  }

  private final Data eventData = context -> {
    User user = context.get("user");

    long eventId = context.request.getInt(1);
    Event event = eventDB.get(eventId);

    User owner = userAPI.get(event.ownerId);

    List<Json> people = Lists.newArrayList();
    people.add(Json.object()
        .with("id", owner.id)
        .with("name", owner.getFullName())
        .with("status", "Host"));

    List<Invite> invites = inviteDB.getForEvent(eventId);
    Collections.sort(invites, (a, b) -> {
      return a.status.ordinal() - b.status.ordinal();
    });

    Invite myInvite = null;
    for (Invite invite : invites) {
      if (invite.targetUserId == null) {
        // for public events, don't show email addresses except to the person who invited that email
        if (event.privacy != Privacy.ANYONE || invite.fromId == user.id) {
          people.add(Json.object()
              .with("name", invite.targetEmail)
              .with("status", invite.status));
        }
      } else {
        User target = userAPI.get(invite.targetUserId);
        if (Objects.equals(target.id, user.id)) {
          myInvite = invite;
        }
        people.add(Json.object()
            .with("id", invite.targetUserId)
            .with("name", target.getFullName())
            .with("status", invite.status));
      }
    }

    if (myInvite != null && myInvite.status == Status.OPEN) {
      context.put("myInvite", myInvite);
      context.put("inviteSender", userAPI.get(myInvite.fromId));
    }

    boolean allowInvites = true;
    if (event.privacy == Privacy.FRIENDS || event.privacy == Privacy.INVITE_ONLY) {
      allowInvites = Objects.equals(user, owner);
    }

    context.put("people", people);
    context.put("event", event);
    context.put("chatIP", chatIP);
    context.put("chatPort", chatPort);
    context.put("allow-invites", allowInvites);
    
  };

}
