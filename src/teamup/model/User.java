package teamup.model;

public class User {

  public Long id;
  public final long facebookId;
  public final String firstName, lastName;
  public final String gender;

  public User(Long id, long facebookId, String firstName, String lastName, String gender) {
    this.id = id;
    this.facebookId = facebookId;
    this.firstName = firstName;
    this.lastName = lastName;
    this.gender = gender;
  }

  public String getFullName() {
    return firstName + " " + lastName;
  }

  @Override
  public String toString() {
    return getFullName();
  }

}
