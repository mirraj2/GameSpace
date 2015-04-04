package gamespace.model;

import gamespace.api.GameUtils;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class Event {

  public Long id;
  public final long ownerId;
  public final String name, description;
  public final LocalDateTime date;
  public final Integer maxPeople;
  public final Privacy privacy;

  public Event(Long id, long ownerId, String name, LocalDateTime date, Integer maxPeople, Privacy privacy,
      String description) {
    this.id = id;
    this.ownerId = ownerId;
    this.name = name;
    this.date = date;
    this.maxPeople = maxPeople;
    this.privacy = privacy;
    this.description = description;
  }

  public String getDateString() {
    return date.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
  }

  public static enum Privacy {
    INVITE_ONLY, FRIENDS, FRIENDS_OF_FRIENDS, ANYONE;

    @Override
    public String toString() {
      return GameUtils.prettify.apply(name().toLowerCase().replace('_', ' '));
    };
  }

}
