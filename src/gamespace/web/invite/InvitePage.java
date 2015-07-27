package gamespace.web.invite;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.Long.parseLong;
import ez.Mailbox;
import gamespace.api.UserAPI;
import gamespace.db.EventDB;
import gamespace.db.InviteDB;
import gamespace.model.Event;
import gamespace.model.Invite;
import gamespace.model.Invite.Status;
import gamespace.model.User;
import jasonlib.util.Utils;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import bowser.Controller;
import bowser.Handler;
import bowser.Request;
import bowser.Response;
import bowser.template.Data;

public class InvitePage extends Controller {

  private static final Executor executor = Executors.newSingleThreadExecutor();

  private UserAPI userAPI;
  private String httpDomain;
  private int httpPort;
  private Mailbox mailbox;
  private InviteDB inviteDB = new InviteDB();
  private final EventDB eventDB = new EventDB();

  public InvitePage(UserAPI userAPI, String httpDomain, int httpPort, Mailbox mailbox) {
    this.userAPI = userAPI;
    this.httpDomain = httpDomain;
    this.httpPort = httpPort;
    this.mailbox = mailbox;
  }

  @Override
  public void init() {
    route("GET", "/invite/*").to("invite.html").data(data);
    route("POST", "/events/*/invite").to(invite);
    route("POST", "/events/*/accept").to(accept);
    route("POST", "/events/*/decline").to(decline);
  }

  private final Handler accept = (request, response) -> {
    changeStatus(request, response, Status.ACCEPTED);
  };

  private final Handler decline = (request, response) -> {
    changeStatus(request, response, Status.REJECTED);
  };

  private void changeStatus(Request request, Response response, Status status) {
    User user = request.get("user");
    long eventId = request.getInt(1);
    Invite invite = inviteDB.getInvite(user.id, eventId);
    checkNotNull(invite, "You weren't invited to this event.");

    inviteDB.updateStatus(invite.id, status);
  }

  private final Handler invite = (request, response) -> {
    User from = request.get("user");
    long eventId = request.getInt(1);

    String email = request.param("email");
    if (email == null) {
      long id = parseLong(request.param("id"));
      User target = userAPI.get(id);
      invite(eventId, from, target);
    } else {
      invite(eventId, from, email);
    }
  };

  private void invite(long eventId, User from, String email) {
    checkState(Utils.isValidEmailAddress(email));

    User target = userAPI.getByEmail(email);
    Event event = eventDB.get(eventId);

    if (target != null) {
      // this user is already a member.
      invite(eventId, from, target);
      return;
    }

    // check to see if this email is already invited to this event
    Invite existingInvite = inviteDB.getInvite(email, eventId);
    checkState(existingInvite == null, "This person was already invited to the event.");

    // add this person to the invite DB
    Invite invite = Invite.createEmailInvite(eventId, from.id, email);
    inviteDB.save(invite);

    executor.execute(() -> {
      String link = "http://" + httpDomain + ":" + httpPort + "/invite/" + invite.emailCode;

      StringBuilder sb = new StringBuilder()
          .append(from.getFullName()).append(" invited you to an event!<br><br>")
          .append("<a href='")
          .append(link)
          .append("'>Click here</a> to see more details. You'll be able to see ")
          .append("which of your friends are participating as well as respond to the invitation.")
          .append("<br><br>If the link doesn't work, copy paste this into your browser: ")
          .append(link);

      mailbox.sendEmail("noreply@gamespace.com",
          email,
          from.getFullName() + " invited you to '" + event.name + "'",
          sb.toString());
    });
  }

  private void invite(long eventId, User from, User target) {
    Event event = eventDB.get(eventId);

    checkState(!Objects.equals(from, target), "You can't invite yourself.");
    checkState(!Objects.equals(target.id, event.ownerId), "The host is already invited.");

    // check to see if this user was already invited
    Invite existingInvite = inviteDB.getInvite(target.id, eventId);
    if (existingInvite != null) {
      if (Objects.equals(existingInvite.fromId, from.id)) {
        throw new IllegalStateException("You already invited " + target + " to this event.");
      } else {
        throw new IllegalStateException(target + " was already invited to this event.");
      }
    }

    Invite invite = Invite.create(eventId, from.id, target.id);
    inviteDB.save(invite);

    String email = target.email;
    if (email != null) {
      executor.execute(() -> {
        String link = "http://" + httpDomain + ":" + httpPort + "/events/" + eventId;

        StringBuilder sb = new StringBuilder()
            .append("Hey ").append(target.firstName).append(",<br><br>")
            .append(from.getFullName()).append(" invited you to an event!<br><br>")
            .append("<a href='")
            .append(link)
            .append("'>Click here</a> to see more details. You'll be able to see ")
            .append("which of your friends are participating as well as respond to the invitation.")
            .append("<br><br>If the link doesn't work, copy paste this into your browser: ")
            .append(link);

        mailbox.sendEmail("noreply@gamespace.com",
            email,
            from.getFullName() + " invited you to '" + event.name + "'",
            sb.toString());
      });
    }
  }

  private final Data data = context -> {
    String code = context.request.getSegment(1);
    Invite invite = inviteDB.getInviteForCode(code);
    Event event = eventDB.get(invite.eventId);
    User from = userAPI.get(invite.fromId);

    context.put("event", event);
    context.put("friend", from.getFullName());
    context.put("redirectLocation", "/events/" + event.id);
    context.put("loginPath", "/login?code=" + code);
  };

}
