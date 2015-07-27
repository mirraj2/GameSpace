package gamespace.model;

import java.util.UUID;

/**
 * Users can invite other users to an Event.
 */
public class Invite {

  public Long id;
  public final long eventId;

  /**
   * The id of the user who sent the invite.
   */
  public final long fromId;

  // this might be null. If it is null, targetEmail and emailCode will be filled out.
  // once the user accepts the email invite and logs in, this field will be populated.
  public final Long targetUserId;

  public final String targetEmail, emailCode;

  public final Status status;

  public Invite(Long id, long eventId, long fromId, Long targetUserId, String targetEmail, String emailCode,
      Status status) {
    this.id = id;
    this.eventId = eventId;
    this.fromId = fromId;
    this.targetUserId = targetUserId;
    this.targetEmail = targetEmail;
    this.emailCode = emailCode;
    this.status = status;
  }

  public static Invite create(long eventId, long fromId, long targetId) {
    return new Invite(null, eventId, fromId, targetId, null, null, Status.OPEN);
  }

  public static Invite createEmailInvite(long eventId, long fromId, String targetEmail) {
    String emailCode = UUID.randomUUID().toString().replace("-", "");
    return new Invite(null, eventId, fromId, null, targetEmail, emailCode, Status.OPEN);
  }

  public static enum Status {
    ACCEPTED, OPEN, REJECTED;

    @Override
    public String toString() {
      if (this == OPEN) {
        return "Invited";
      } else if (this == ACCEPTED) {
        return "Confirmed";
      } else if (this == REJECTED) {
        return "Unable to make it";
      }
      throw new IllegalStateException();
    };
  }

}
