package gamespace.db;

import java.time.LocalDateTime;
import java.util.List;
import ez.Row;
import ez.Table;
import gamespace.model.Invite;

public class InviteDB extends GSDB {

  @Override
  protected Table getTable() {
    return new Table("invite")
        .idColumn()
        .column("date", LocalDateTime.class)
        .column("eventId", Long.class)
        .column("fromId", Long.class)
        .column("targetUserId", Long.class)
        .column("targetEmail", String.class)
        .column("emailCode", String.class)
        .column("status", String.class)
        .index("eventId");
  }

  public Invite getInvite(long userId, long eventId) {
    Row row = db.selectSingleRow("SELECT * FROM invite WHERE eventId = ? AND targetUserId = ?", eventId, userId);
    return table.fromRow(row, Invite.class);
  }

  public Invite getInvite(String email, long eventId) {
    Row row = db.selectSingleRow("SELECT * FROM invite WHERE eventId = ? AND targetEmail = ?", eventId, email);
    return table.fromRow(row, Invite.class);
  }

  public Invite getInviteForCode(String code) {
    Row row = db.selectSingleRow("SELECT * FROM invite WHERE emailCode = ?", code);
    return table.fromRow(row, Invite.class);
  }

  public List<Invite> getForEvent(long eventId) {
    List<Row> rows = db.select("SELECT * FROM invite WHERE eventId = ?", eventId);
    return table.fromRows(rows, Invite.class);
  }

  public void updateTargetUserForCode(String emailCode, long id) {
    db.update("UPDATE invite SET targetUserId = ? WHERE emailCode = ?", id, emailCode);
  }

  public void updateStatus(long inviteId, Invite.Status status) {
    db.update("UPDATE invite SET status = ? WHERE id = ?", status, inviteId);
  }

  public void save(Invite invite) {
    invite.id = db.insert("invite", table.toRow(invite).with("date", LocalDateTime.now()));
  }

  public Invite get(long id) {
    Row row = db.selectSingleRow("SELECT * FROM invite WHERE id = ?", id);
    return table.fromRow(row, Invite.class);
  }

}
